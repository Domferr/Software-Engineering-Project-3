package ie.ucdconnect.sep;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** This class reads the testcases and generates a random solution.
 *  It saves each solution into a file inside the testcase dir.
 *  Each row of the solution file has a project and the assigned student. */
public class SolutionGenerator {

	// The test set to use.
	private static final int TEST_SIZE = 120;
	// The number of generations to be run before a solution is returned.
	private static final int NUM_GENERATIONS = 10000;
	// The number of solutions in each generation.
	private static final int GENERATION_SIZE = 250;
	// The number of "bad" solutions that will be removed at the end of each generation.
	private static final int GENERATION_CULL = 150;

	private static Random random;
	private static List<Project> projects;

	public static void main(String[] args) throws IOException {
		random = new Random(System.currentTimeMillis());
		Solution solution = createGeneticSolution();
		System.out.println("Final energy: " + solution.getEnergy() + ". Final fitness: " + solution.getFitness() + ".");
	}

	/**
	 * Runs a genetic algorithm and returns the best result.
	 */
	private static Solution createGeneticSolution() throws IOException {
		List<Solution> solutions = generateSolutions();
		for (int i = 0; i < NUM_GENERATIONS; i++) {
			System.out.println("Running generation: " + i);
			solutions = SolutionAcceptor.screenSolutions(solutions, GENERATION_CULL);
			solutions = mutate(solutions);
		}
		return solutions.get(0);
	}

	/**
	 * Static method that takes a list of projects and students and then generates a random solution.
	 */
	public static Solution createRandomSolution(List<Project> projects, List<Student> students) {
		ImmutableMultimap.Builder<Project, Student> mapBuilder = ImmutableMultimap.builder();
		Random rand = new Random();

		List<Student> studentsCopy = new ArrayList<>(students);
		Collections.shuffle(studentsCopy);

		for (Student student : studentsCopy) {
			int randomIndex = rand.nextInt(projects.size());
			mapBuilder.put(projects.get(randomIndex), student);
		}
		return new Solution(mapBuilder.build());
	}

	private static List<Solution> mutate(List<Solution> solutions) {
		List<Solution> newSolutions = new ArrayList<>(solutions);
		while (newSolutions.size() < GENERATION_SIZE) {
			Solution randomSolution = solutions.get(random.nextInt(solutions.size()));
			newSolutions.add(mutate(randomSolution));
		}
		return newSolutions;
	}

	private static Solution mutate(Solution solution) {
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
		return new Solution(mapBuilder.build());
	}

	/** Given a testset size, reads the right projects and students files and returns a random solution */
	private static List<Solution> generateSolutions() throws IOException {
		List<StaffMember> staffMembers = Utils.readStaffMembers();
		File projectsFile = Utils.getProjectFile(SolutionGenerator.TEST_SIZE);
		File studentsFile = Utils.getStudentsFile(SolutionGenerator.TEST_SIZE);

		projects = Project.fromCSV(Utils.readFile(projectsFile.toPath()), staffMembers);
		Map<String, Project> projectsMap = projects.stream().collect(Collectors.toMap(Project::getTitle, Function.identity()));
		List<Student> students = Student.fromCSV(Utils.readFile(studentsFile.toPath()), projectsMap);

		List<Solution> solutions = new ArrayList<>();
		for (int i = 0; i < GENERATION_SIZE; i++) {
			solutions.add(createRandomSolution(projects, students));
		}
		return solutions;
	}

	/** Write the given solution into a file.  */
	private static void saveSolution(Solution solution, int size) throws IOException {
		String dirName = Config.getInstance().getTestcaseDirName();
		File testCaseDir = new File(dirName);
		if (!testCaseDir.exists())
			testCaseDir.mkdir();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dirName+"solutionFor"+size+"Students.csv"));
			writer.write(solution.toCSV());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
