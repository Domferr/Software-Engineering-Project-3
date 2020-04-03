package ie.ucdconnect.sep;

import java.util.*;

public class Solution {

	private Map<Project, Student> projectMapping = new HashMap<>();

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
	public String toCSV() {
		StringBuilder s = new StringBuilder();
		for (Project project: projectMapping.keySet()) {
			s.append(project.getTitle()).append(";").append(projectMapping.get(project).toCSVRow()).append("\n");
		}
		return s.toString();
	}
}
