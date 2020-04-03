package ie.ucdconnect.sep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** This class reads the testcases and generates a random solution.
 *  For each project it prints the assigned student. */
public class SolutionGenerator {

	private static final int CASE_NUM = 500;
	private static Config config;

	public static void main(String[] args) throws IOException {
		config = Config.getInstance();
		List<StaffMember> staffMembers = StaffMember.fromCSV(readFile(config.getStaffMembersFile().toPath()));
		int[] testSetsSize = config.getTestSetsStudentsSize();
		for (int i = 0; i < testSetsSize.length; i++) {
			Solution solution = generateSolution(testSetsSize[i], staffMembers);

			saveSolution(solution, testSetsSize[i]);
			System.out.println("Generated solution for "+testSetsSize[i]+" students.");
		}
	}

	/** Write the given list into specified file.  */
	private static void saveSolution(Solution solution, int size) {
		String dirName = config.getTestcaseDirName();
		File testCaseDir = new File(dirName);
		if (!testCaseDir.exists())
			testCaseDir.mkdir();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dirName+"solutionFor"+size+"students.txt"));
			String fileContent = solution.toString();
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Given a testset size, reads the right projects and students files and returns a random solution */
	private static Solution generateSolution(int testSetSize, List<StaffMember> staffMembers) throws IOException {
		List<Project> projects = Project.fromCSV(readFile(getProjectFile(testSetSize).toPath()), staffMembers);
		Map<String, Project> projectsMap = projects.stream().collect(Collectors.toMap(Project::getTitle, Function.identity()));
		List<Student> students = Student.fromCSV(readFile(getStudentsFile(testSetSize).toPath()), projectsMap);

		return Solution.createRandom(projects, students);
	}

	private static File getProjectFile(int testSetSize) {
		String fileName = "projectsFor" + testSetSize + "Students.csv";
		return new File(config.getTestcaseDirName() + fileName);
	}

	private static File getStudentsFile(int testSetSize) {
		String fileName = "students" + testSetSize + ".csv";
		return new File(config.getTestcaseDirName() + fileName);
	}

	private static String readFile(Path path) throws IOException {
		return String.join("\n", Files.readAllLines(path));
	}
}
