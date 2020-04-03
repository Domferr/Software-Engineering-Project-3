package ie.ucdconnect.sep;

import com.opencsv.CSVParser;

import java.io.IOException;
import java.util.*;

/** This class represent a solution. It maps each project to a student */
public class Solution implements CSVRow {

	private Map<Project, Student> projectMapping = new HashMap<>();

	/** Static method that takes a list of projects and students and then generates a random solution.
	 *  The algorithm is the following:
	 *  Assign every student their LEAST preferable project, if that project isn't available, try
	 *  to give them more preferable projects. Any student who can not be matched to a project
	 *  (all their preferences are already taken) will be assigned a random project from the list. */
	public static Solution createRandom(List<Project> projects, List<Student> students) {
		Random rand = new Random();
		Solution solution = new Solution();
		Stack<Student> unmappedStudents = new Stack<>();

		List<Student> studentsCopy = new ArrayList<>(students);
		Collections.shuffle(studentsCopy);

		for (Student student : studentsCopy) {
			boolean matched = false;
			List<Project> preferences = student.getPreferences();
			int startIndex = rand.nextInt(preferences.size());
			for (int i = startIndex; i < startIndex + preferences.size(); i++) {
				Project project = preferences.get(i % preferences.size());
				if (solution.isAvailable(project)) {
					solution.safeMap(student, project);
					matched = true;
					break;
				}
			}
			if (!matched) {
				unmappedStudents.push(student);
			}
		}

		while (!unmappedStudents.empty()) {
			Student student = unmappedStudents.pop();
			for (Project project : projects) {
				if (solution.isAvailable(project)) {
					solution.safeMap(student, project);
					break;
				}
			}
		}
		return solution;
	}

	/**
	 * Maps a student to a project, and checks that we do not unmap a different student in the process.
	 * @throws IllegalStateException if the operation causes another student to be unmapped.
	 */
	public void safeMap(Student student, Project project) throws IllegalStateException {
		Student oldStudent = projectMapping.put(project, student);
		if (oldStudent != null && oldStudent != student) {
			throw new IllegalStateException("Assigned a student to an already assigned project");
		}
	}

	public boolean isAvailable(Project project) {
		return !projectMapping.containsKey(project);
	}

	/** Returns the project assigned to a given student. Returns null if the student has no project assigned */
	public Project getAssignedProject(Student student) {
		for (Project project : projectMapping.keySet()) {
			if (projectMapping.get(project).equals(student))
				return project;
		}
		return null;
	}

	/** Returns the student assigned to a given project. Returns null if the project has no student assigned */
	public Student getAssignedStudent(Project project) {
		return projectMapping.get(project);
	}

	public Set<Project> getProjects() {
		return projectMapping.keySet();
	}

	public ArrayList<Student> getStudents() {
		return new ArrayList<Student>(projectMapping.values());
	}

	/** Returns this solution in CSV format.
	 *  Each file's row has two columns. The first column is the project
	 *  while the second one is the assigned student. */
	@Override
	public String toCSVRow() {
		StringBuilder s = new StringBuilder();
		for (Project project: projectMapping.keySet()) {
			s.append(project.getTitle()).append(",").append(projectMapping.get(project).getStudentNumber()).append("\n");
		}
		return s.toString();
	}

	/** Returns the solution from a given csvfile content.
	 * @throws IllegalStateException if a student cannot be mapped
	 * @throws IOException if an error occurs while parsing the CSV
	 */
	public static Solution fromCSV(String csvFile, List<Student> students, Map<String, Project> projectsMap) throws IllegalStateException, IOException {
		CSVParser csvParser = new CSVParser();
		Solution solution = new Solution();
		String[] rows = csvFile.split("\n");
		for (String row : rows) {
			String[] columns = csvParser.parseLine(row);
			if (columns.length != 2)
				throw new IllegalArgumentException("The row ["+ row +"] must have two columns");
			Project project = projectsMap.get(columns[0]);
			Student student = findStudent(columns[1], students);
			if (solution.isAvailable(project)) {
				solution.safeMap(student, project);
			} else {
				throw new IllegalStateException("The project "+project.getTitle()+" has been mapped with two or more students");
			}
		}
		return solution;
	}

	private static Student findStudent(String studentNumber, List<Student> students) {
		for (Student student : students) {
			if (student.getStudentNumber().equals(studentNumber)) {
				return student;
			}
		}
		throw new IllegalArgumentException("Student '" + studentNumber + "' not found.");
	}


	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Project project: projectMapping.keySet()){
			s.append("Project: ").append(project.getTitle()).append(" -> Student: ").append(projectMapping.get(project).toString()).append("\n");
		}
		return s.toString();
	}
}
