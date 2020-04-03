package ie.ucdconnect.sep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** This class reads the testcases and generates a random solution.
 *  It saves each solution into a file inside the testcase dir.
 *  Each row of the solution file has a project and the assigned student. */
public class SolutionGenerator {

	private static Config config;

	public static void main(String[] args) throws IOException {
		config = Config.getInstance();
		List<StaffMember> staffMembers = StaffMember.fromCSV(Utils.readFile(config.getStaffMembersFile().toPath()));
		int[] testSetsSize = config.getTestSetsStudentsSize();
		for (int i = 0; i < testSetsSize.length; i++) {
			Solution solution = generateSolution(testSetsSize[i], staffMembers);

			saveSolution(solution, testSetsSize[i]);
			System.out.println("Generated solution for "+testSetsSize[i]+" students.");
		}
	}

	/** Write the given solution into a file.  */
	private static void saveSolution(Solution solution, int size) {
		String dirName = config.getTestcaseDirName();
		File testCaseDir = new File(dirName);
		if (!testCaseDir.exists())
			testCaseDir.mkdir();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dirName+"solutionFor"+size+"Students.csv"));
			writer.write(solution.toCSVRow());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Given a testset size, reads the right projects and students files and returns a random solution */
	private static Solution generateSolution(int testSetSize, List<StaffMember> staffMembers) throws IOException {
		File projectsFile = Utils.getProjectFile(config, testSetSize);
		File studentsFile = Utils.getStudentsFile(config, testSetSize);

		List<Project> projects = Project.fromCSV(Utils.readFile(projectsFile.toPath()), staffMembers);
		Map<String, Project> projectsMap = projects.stream().collect(Collectors.toMap(Project::getTitle, Function.identity()));
		List<Student> students = Student.fromCSV(Utils.readFile(studentsFile.toPath()), projectsMap);

		return Solution.createRandom(projects, students);
	}
}
