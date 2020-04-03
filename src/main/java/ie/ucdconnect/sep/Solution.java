package ie.ucdconnect.sep;

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

		int currentNoStudents = students.size();
		while (currentNoStudents > 0){
			Student student = students.remove(rand.nextInt(currentNoStudents));
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
			currentNoStudents--;
		}

		while (!unmappedStudents.empty()) {
			Student student = unmappedStudents.pop();
			for (Project project : projects) {
				if (solution.isAvailable(project)) {
					solution.safeMap(student, project);
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

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Project project: projectMapping.keySet()){
			s.append("Project: ").append(project.getTitle()).append(" -> Student: ").append(projectMapping.get(project).toString()).append("\n");
		}
		return s.toString();
	}

	/** Returns this solution in CSV format */
	@Override
	public String toCSVRow() {
		StringBuilder s = new StringBuilder();
		for (Project project: projectMapping.keySet()) {
			s.append(project.toCSVRow()).append(";").append(projectMapping.get(project).toCSVRow()).append("\n");
		}
		return s.toString();
	}

	public static Solution fromCSV(String csvFile) {
		Solution solution = new Solution();

		//TODO work in progress

		return solution;
	}
}
