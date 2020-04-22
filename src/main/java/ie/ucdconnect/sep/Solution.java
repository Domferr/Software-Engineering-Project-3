package ie.ucdconnect.sep;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.opencsv.CSVParser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represent a solution. A solution can map projects to students. It has energy and fitness values.
 */
public class Solution {

	/** Factory class for the Solution class */
	public static class SolutionFactory {
		/** Creates a solution object without evaluating energy and fitness.
		 *  This is the most efficient and should be used when the energy and fitness values are not needed. */
		public static Solution create(ImmutableMultimap<Project, Student> projectMapping) {
			return new Solution(projectMapping);
		}

		/** Creates a solution object and calculates its energy and fitness. */
		public static Solution createAndEvaluate(ImmutableMultimap<Project, Student> projectMapping) {
			Solution solution = new Solution(projectMapping);
			solution.evaluate();
			return solution;
		}

		/** Creates a new Solution by mutating a given solution. It also evaluates the resulting solution. */
		public static Solution createByMutating(Solution solution, List<Project> projects) {
			Random random = new Random();
			ImmutableCollection<Map.Entry<Project, Student>> entries = solution.getEntries();
			ImmutableMultimap.Builder<Project, Student> mapBuilder = ImmutableMultimap.builder();
			int randomIndex = random.nextInt(entries.size());
			int index = 0;
			for (Map.Entry<Project, Student> entry : entries) {
				if (index == randomIndex) {
					Project newProject = projects.get(random.nextInt(projects.size()));
					mapBuilder.put(newProject, entry.getValue());
				} else {
					mapBuilder.put(entry);
				}
				index++;
			}

			return createAndEvaluate(mapBuilder.build());
		}

		/** Create a new solution by taking the best from the two given solutions */
		public static Solution createByMating(Solution first, Solution second, List<Project> projects) {
			//TODO implement the way to take the best from two given solutions to create a new solution
			return createByMutating(first, projects);
		}
	 }

	//Penalties for hard constraint violation
	private static final int CONSTRAINT_VIOLATION_PENALTY = 100;
	private static final int NONPREFERENCE_PROJECT_VIOLATION_PENALTY = 15;

	private ImmutableMultimap<Project, Student> projectMapping;
	private double energy, fitness;
	private double gpaImportance;

	private Solution(ImmutableMultimap<Project, Student> projectMapping) {
		this.projectMapping = projectMapping;
		setGpaImportance(1.0);
	}

	/**
	 * This method calculates the energy and the fitness of this solution.
	 * If gpaImportance tends to 1 than the gpa is very important while if gpaImportance tends to 0
	 * then the gpa is less important.
	 */
	public void evaluate() {
		energy = fitness = 0;
		for (Project project : projectMapping.keySet()) {
			ImmutableCollection<Student> assignedStudents = projectMapping.get(project);
			if (assignedStudents.size() > 1) {
				energy += CONSTRAINT_VIOLATION_PENALTY;
			}
			for (Student student : assignedStudents.asList()) {
				int i = 0;
				boolean found = false;
				while (!found && i < 10) {
					if (student.getPreferences().get(i).equals(project)) {
						int fitnessDelta = 10 - i;
						double gpaWeight = student.getGpa() * getGpaImportance();
						fitness += fitnessDelta + fitnessDelta * gpaWeight;
						energy += i + i * gpaWeight;
						found = true;
					}
					i++;
				}
				if (!found) {
					energy += NONPREFERENCE_PROJECT_VIOLATION_PENALTY;
				}
			}
		}
	}

	/**
	 * Returns the project assigned to a given student. Returns null if the student has no project assigned
	 */
	public Project getAssignedProject(Student student) {
		for (Project project : projectMapping.keySet()) {
			if (projectMapping.containsEntry(project, student))
				return project;
		}
		return null;
	}

	/**
	 * Returns the student assigned to a given project. Returns null if the project has no student assigned
	 */
	public Collection<Student> getAssignedStudents(Project project) {
		return projectMapping.get(project);
	}

	public Set<Project> getProjects() {
		return projectMapping.keySet();
	}

	public ArrayList<Student> getStudents() {
		return new ArrayList<Student>(projectMapping.values());
	}

	/**
	 * Returns this solution as a String in CSV format.
	 * Each file's row has the project title and then
	 * the list of student numbers assigned to that project
	 */
	public String toCSV() {
		StringBuilder s = new StringBuilder();
		for (Project project : projectMapping.keySet()) {
			String studentNumbers = projectMapping.get(project).stream().map(Student::getStudentNumber).collect(Collectors.joining(","));
			s.append(project.getTitle()).append(",\"").append(studentNumbers).append("\"\n");
		}
		return s.toString();
	}

	/**
	 * Returns the solution from a given csvfile content.
	 *
	 * @throws IllegalStateException if a student cannot be mapped
	 * @throws IOException           if an error occurs while parsing the CSV
	 */
	public static Solution fromCSV(String csvFile, List<Student> students, Map<String, Project> projectsMap) throws IllegalStateException, IOException {
		CSVParser csvParser = new CSVParser();
		ImmutableMultimap.Builder<Project, Student> mapBuilder = ImmutableMultimap.builder();
		String[] rows = csvFile.split("\n");
		for (String row : rows) {
			String[] columns = csvParser.parseLine(row);
			if (columns.length != 2)
				throw new IllegalArgumentException("The row [" + row + "] must have two columns");
			Project project = projectsMap.get(columns[0]);
			String[] studentIds = columns[1].split(",");
			List<Student> projectStudents = findStudents(studentIds, students);
			for (Student projectStudent : projectStudents) {
				mapBuilder.put(project, projectStudent);
			}
		}
		return SolutionFactory.createAndEvaluate(mapBuilder.build());
	}

	/**
	 * Given an array of studentIDs and a list of all the students, returns a list of all the student objects
	 * that have an ID from the given array
	 */
	private static List<Student> findStudents(String[] studentIds, List<Student> students) {
		LinkedList<Student> returnedStudents = new LinkedList<>();
		for (Student student : students) {
			for (String studentNumber : studentIds) {
				if (student.getStudentNumber().equals(studentNumber)) {
					returnedStudents.add(student);
				}
			}
		}
		return returnedStudents;
	}

	public double getEnergy() { return energy; }

	public double getFitness() {
		return fitness;
	}

	public double getGpaImportance() {
		return gpaImportance;
	}

	public void setGpaImportance(double gpaImportance) {
		this.gpaImportance = gpaImportance;
	}

	public ImmutableCollection<Map.Entry<Project, Student>> getEntries() {
		return projectMapping.entries();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Project project : projectMapping.keySet()) {
			s.append("Project: ").append(project.getTitle()).append(" -> Student: ").append(projectMapping.get(project).toString()).append("\n");
		}

		return s.toString();
	}
}
