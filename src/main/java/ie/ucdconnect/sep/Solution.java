package ie.ucdconnect.sep;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.opencsv.CSVParser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represent a solution.
 */
public class Solution {

	//Penalties for hard constraint violation
	private static final int CONSTRAINT_VIOLATION_PENALTY = 100;
	private static final int NONPREFERENCE_PROJECT_VIOLATION_PENALTY = 15;

	private ImmutableMultimap<Project, Student> projectMapping;
	private int energy;
	private int fitness;

	public Solution(ImmutableMultimap<Project, Student> projectMapping) {
		this.projectMapping = projectMapping;
		evaluate();
	}

	/** This method calculates the energy and the fitness of this solution */
	private void evaluate() {
		energy = fitness = 0;
		for (Project project : projectMapping.keySet()) {
			ImmutableCollection<Student> assignedStudents = projectMapping.get(project);
			if (assignedStudents.size() > 1)
				energy += CONSTRAINT_VIOLATION_PENALTY;

			for (Student student : assignedStudents.asList()) {
				int i = 0;
				boolean found = false;
				while (!found && i < 10) {
					if (student.getPreferences().get(i).equals(project)) {
						fitness += 10 - i;
						energy += i;
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
		return new Solution(mapBuilder.build());
	}

	/** Given an array of studentIDs and a list of all the students, returns a list of all the student objects
	 *  that have an ID from the given array */
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

	public int getEnergy() {
		return energy;
	}

	public int getFitness() {
		return fitness;
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
