package ie.ucdconnect.sep;

import ie.ucdconnect.sep.generators.GeneticAlgorithm;
import ie.ucdconnect.sep.generators.SimulatedAnnealing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class reads the testcases and generates a solution.
 * It saves each solution into a file inside the testcase dir.
 * Each row of the solution file has a project and the assigned student's ID.
 */
public class SolutionGenerator {

    private SolutionGenerationStrategy generationStrategy;

	public static void main(String[] args) throws IOException {
		int[] testSetsStudentsSize = Config.getInstance().getTestSetsStudentsSize();
		int test_size = testSetsStudentsSize[1];

		//Read test set
		List<StaffMember> staffMembers = Utils.readStaffMembers();
		List<Project> projects = Utils.readProjects(staffMembers, test_size);
		List<Student> students = Utils.readStudents(Utils.generateProjectsMap(projects), test_size);

		//Run simulated annealing
		Solution solution = new GeneticAlgorithm().generate(projects, students);
		System.out.printf("Final energy: %.2f. Final fitness: %.2f\n", solution.getEnergy(), solution.getFitness());

		//Save generated solution into resources dir
	//	saveSolution(solution, test_size);
	}

	/**
	 * Write the given solution into a file.
	 */
	public static void saveSolution(Solution solution, int size) throws IOException {
		String dirName = Config.getInstance().getTestcaseDirName();
		File testCaseDir = new File(dirName);
		if (!testCaseDir.exists())
			testCaseDir.mkdir();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dirName + "solutionFor" + size + "Students.csv"));
			writer.write(solution.toCSV());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
