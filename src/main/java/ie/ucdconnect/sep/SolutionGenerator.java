package ie.ucdconnect.sep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SolutionGenerator {

	private static final int CASE_NUM = 500;
	private static Config config;

	public static void main(String[] args) throws IOException {
		config = Config.getInstance();
		List<StaffMember> staffMembers = StaffMember.fromCSV(readFile(config.getStaffMembersFile().toPath()));
		List<Project> projects = Project.fromCSV(readFile(getProjectFile().toPath()), staffMembers);
		Map<String, Project> projectsMap = projects.stream().collect(Collectors.toMap(Project::getTitle, Function.identity()));
		List<Student> students = Student.fromCSV(readFile(getStudentsFile().toPath()), projectsMap);
		Solution solution = Solution.createRandom(projects, students);

		System.out.println(solution);
	}

	private static File getProjectFile() {
		String fileName = "projectsFor" + CASE_NUM + "Students.csv";
		return new File(config.getTestcaseDirName() + fileName);
	}

	private static File getStudentsFile() {
		String fileName = "students" + CASE_NUM + ".csv";
		return new File(config.getTestcaseDirName() + fileName);
	}

	private static String readFile(Path path) throws IOException {
		return String.join("\n", Files.readAllLines(path));
	}
}
