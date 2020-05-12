package ie.ucdconnect.sep;

import ie.ucdconnect.sep.generators.RandomGeneration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class with some important methods
 */
public class Utils {

	/** Returns a random integer between min (included) and max (excluded) */
	public static int getRandomInteger(int min, int max) {
		return (int) (Math.random() * (max-min)) + min;
	}

	/** Returns a list of the given size o=f random generated solutions */
	public static List<Solution> getRandomSolutionList(List<Project> projects, List<Student> students, int size) {
		List<Solution> solutions = new ArrayList<>();
		//Create a list of solutions and find the max energy delta
		for (int i = 0; i < size; i++) {
			solutions.add(new RandomGeneration().generate(projects, students));
		}
		return solutions;
	}

	/**
	 * Takes a list of projects and returns a map title -> project
	 */
	public static Map<String, Project> generateProjectsMap(List<Project> projects) {
		return projects.stream().collect(Collectors.toMap(Project::getTitle, Function.identity()));
	}

	/**
	 * Reads the solution test set file with the given test set size
	 */
	public static Solution readSolution(Map<String, Project> projectsMap, List<Student> students, int testSetSize) throws IOException {
		File solutionFile = getSolutionFile(testSetSize);
		String fileContent = readFile(solutionFile.toPath());

		return Solution.fromCSV(fileContent, students, projectsMap);
	}

	/**
	 * Reads the staff members file
	 */
	public static List<StaffMember> readStaffMembers() throws IOException {
		String fileContent = readFile(Config.getInstance().getStaffMembersFile().toPath());

		return StaffMember.fromCSV(fileContent);
	}

	/**
	 * Reads the projects test set file with the given test set size
	 */
	public static List<Project> readProjects(List<StaffMember> staffMembers, int test_size) throws IOException {
		File projectsFile = getProjectFile(test_size);
		String fileContent = readFile(projectsFile.toPath());

		return Project.fromCSV(fileContent, staffMembers);
	}

	/**
	 * Reads the students test set file with the given test set size
	 */
	public static List<Student> readStudents(int test_size) throws IOException {
		File studentsFile = getStudentsFile(test_size);
		String fileContent = readFile(studentsFile.toPath());

		return Student.fromCSV(fileContent);
	}

	/**
	 * Returns the solution file based on test set size
	 */
	public static File getSolutionFile(int testSetSize) throws IOException {
		String fileName = "solutionFor" + testSetSize + "Students.csv";
		return new File(Config.getInstance().getTestcaseDirName() + fileName);
	}

	/**
	 * Returns the project file based on test set size
	 */
	public static File getProjectFile(int testSetSize) throws IOException {
		String fileName = "projectsFor" + testSetSize + "Students.csv";
		return new File(Config.getInstance().getTestcaseDirName() + fileName);
	}

	/**
	 * Returns the students file based on test set size
	 */
	public static File getStudentsFile(int testSetSize) throws IOException {
		String fileName = "students" + testSetSize + ".csv";
		return new File(Config.getInstance().getTestcaseDirName() + fileName);
	}

	/**
	 * Returns the entire content of a file described by the given path.
	 */
	public static String readFile(Path path) throws IOException {
		return String.join("\n", Files.readAllLines(path));
	}
}
