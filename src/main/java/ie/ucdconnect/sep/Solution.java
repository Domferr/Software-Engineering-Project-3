package ie.ucdconnect.sep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Solution {

	private Map<Project, Student> projectMapping = new HashMap<>();

	public static Solution createRandom(List<Project> projects, List<Student> students) {
		Solution solution = new Solution();
		Stack<Student> unmappedStudents = new Stack<>();
		for (Student student : students) {
			boolean matched = false;
			List<Project> preferences = student.getPreferences();
			for (int i = preferences.size() - 1; i >= 0; i--) {
				Project project = preferences.get(i);
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
}
