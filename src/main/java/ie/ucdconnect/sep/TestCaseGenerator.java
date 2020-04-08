package ie.ucdconnect.sep;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class generates a test case
 */
public class TestCaseGenerator {

	private static final int CS_FREQUENCY = 60;
	private static final int AVERAGE_PROPOSAL = 3;	//each staff member proposes, on average, 3 projects
    private static final int MAX_STUDENT_PREFERENCES = 10;
	private static final double STUDENTS_STAFF_RATIO = 0.5;
	private static HashMap<Integer, Integer> studentNumbers = new HashMap<>();

    private static Random random = new Random(System.currentTimeMillis());
	private static Config config;

	public static void main(String[] args) {
		try {
			config = Config.getInstance();
		} catch (IOException e) {
			System.out.print("Unable to load Config file: ");
			System.out.println(e.getMessage());
			return;
		}
		try {
			config.save("resources", "testcases", "names.txt", "Miskatonic Staff Members.csv", "prefixes.txt");
		} catch (IOException e) {
			System.out.print("Unable to save config file: ");
			System.out.println(e.getMessage());
		}

		ArrayList<String> prefixes = null;
		List<StaffMember> allStaffMembers = null;
		try {
			prefixes = loadPrefixes();
			allStaffMembers = Utils.readStaffMembers();
		} catch (IOException e) {
			System.out.print("Unable to read from resources: ");
			System.out.println(e.getMessage());
			return;
		}

		List<List<Student>> studentsTestData = new ArrayList<>();
		List<List<Project>> projectsTestData = new ArrayList<>();
		List<List<StaffMember>> pickedStaffTestData = new ArrayList<>();

		/** Generating the different number of required students i.e. 60, 120, 240 and 500*/
		for (int i = 0; i < config.getTestSetsStudentsSize().length; i++) {
			int size = config.getTestSetsStudentsSize()[i];
			/**
			 * Generates test cases and outputs the generated tests.
			 */
			ArrayList<StaffMember> pickedMembers = new ArrayList<>();
			int numberOfStaffMembers = (int)(size*STUDENTS_STAFF_RATIO);
			for (int j = 0; j < numberOfStaffMembers; j++) {
				int randomIndex = random.nextInt(allStaffMembers.size());
				StaffMember randomStaff = allStaffMembers.get(randomIndex);
				//Create a copy of the randomly picked staff member
				StaffMember finalMember = new StaffMember();
				finalMember.setName(randomStaff.getName());
				finalMember.setSpecialFocus(randomStaff.isSpecialFocus());
				finalMember.setResearchAreas(randomStaff.getResearchAreas());
				finalMember.setResearchActivities(randomStaff.getResearchActivities());

				pickedMembers.add(finalMember);
			}

			List<Project> projects = generateProjects(pickedMembers, prefixes);
			System.out.println("Generating "+projects.size()+" projects for " + size + " students.");
			List<Student> students = generateStudents(size, projects);

			studentsTestData.add(students);
			projectsTestData.add(projects);
			pickedStaffTestData.add(pickedMembers);
		}

		String[] studentsTestSets = Arrays.stream(config.getTestSetsStudentsSize()).mapToObj(i -> String.format("students%d.csv", i)).toArray(String[]::new);
		for (int i = 0; i < studentsTestSets.length; i++) {
			saveGeneratedTestcase(studentsTestSets[i], studentsTestData.get(i));
		}

		String[] projectsTestSets = Arrays.stream(config.getTestSetsStudentsSize()).mapToObj(i -> String.format("projectsFor%dStudents.csv", i)).toArray(String[]::new);
		for (int i = 0; i < studentsTestSets.length; i++) {
			saveGeneratedTestcase(projectsTestSets[i], projectsTestData.get(i));
			printProjectFrequency(projectsTestData.get(i));
		}
	}

	private static void printProjectFrequency(List<Project> projects) {
		Map<Integer, Long> frequency = projects.stream().map(project -> project.totalPicks).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		List<Map.Entry<Integer, Long>> sortedEntries = new ArrayList<>(frequency.entrySet());
		sortedEntries.sort(new Comparator<Map.Entry<Integer, Long>>() {
			@Override
			public int compare(Map.Entry<Integer, Long> integerLongEntry, Map.Entry<Integer, Long> t1) {
				return Integer.compare(integerLongEntry.getKey(), t1.getKey());
			}
		});
		for (Map.Entry<Integer, Long> entry : sortedEntries) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}

	/** Write the given list into specified file.  */
	private static void saveGeneratedTestcase(String filename, List<? extends CSVRow> list) {
		String dirName = config.getTestcaseDirName();
		File testCaseDir = new File(dirName);
		if (!testCaseDir.exists())
			testCaseDir.mkdir();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dirName+filename));
			for (CSVRow row : list) {
				writer.write(row.toCSVRow());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Project> generateProjects(ArrayList<StaffMember> staffMembers, ArrayList<String> prefixes) {
		int numberOfProjects = staffMembers.size()*AVERAGE_PROPOSAL;
		Map<Boolean, List<StaffMember>> partition = staffMembers.stream().collect(Collectors.partitioningBy(s -> s.isSpecialFocus()));
		List<StaffMember> csAndDs = partition.get(false); // Proposes CS or CS+DS
		List<StaffMember> dsOnly = partition.get(true); // Proposes DS
		/**
		 * CS vs. CS+DS = 60 vs. 40
		 * As such, I'll generate projects as follows:
		 * 0-49: CS
		 * 50-69: CS+DS
		 * 70-100: DS
		 */
        int i = 0;
		ArrayList<Project> projects = new ArrayList<>(numberOfProjects);
		HashMap<String, Boolean> titlesMap = new HashMap<>();
		while (i < numberOfProjects) {
			Project.Type randomType = pickRandomType();
			Project newProject;
			if (randomType == Project.Type.DS) {
				newProject = generateOneProject(dsOnly, prefixes, randomType);
			} else {
				newProject = generateOneProject(csAndDs, prefixes, randomType);
			}
            if (!titlesMap.containsKey(newProject.getTitle())) {
				titlesMap.put(newProject.getTitle(), true);
                projects.add(newProject);
                i++;
            }
		}
		
		return projects;
	}

	/** Returns a project */
	private static Project generateOneProject(List<StaffMember> staffList, List<String> prefixes, Project.Type type) {
		int staffIndex = random.nextInt(staffList.size());
		StaffMember staffMember = staffList.get(staffIndex);
		String[] research = staffMember.getResearchActivities();
		String prefix = prefixes.get(random.nextInt(prefixes.size()));
		String title = research[random.nextInt(research.length)];

		Project generated = new Project(prefix+" "+title, staffMember, type);
		staffMember.addProposedProject(generated);

		return generated;
	}

	private static Project.Type pickRandomType() {
		double projectType = random.nextDouble();
		if (projectType < 0.5) {
			return Project.Type.CS;
		} else if (projectType < 0.7) {
			return Project.Type.CSDS;
		}
		return Project.Type.DS;
	}

	private static ArrayList<String> loadPrefixes() throws IOException {
		ArrayList<String> prefixes = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(Config.getInstance().getPrefixesFile()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			prefixes.add(line);
		}
		return prefixes;
	}

	/** Generate list of students*/
	private static ArrayList<Student> generateStudents(int noStudents, List<Project> projects) {
		final int MAX_NUM = 90000000;
		final int MIN_NUM = 10000000;

		ArrayList<Student> students = new ArrayList<>(noStudents);
		try{
			Scanner scanner = new Scanner(config.getNamesFile());
			while(scanner.hasNext() && students.size() < noStudents){
				Student student = new Student();
				String first = scanner.next();
				String last = scanner.next();
				student.setFullName(first, last);

				int sNumber = (random.nextInt((MAX_NUM-MIN_NUM)+1)+MIN_NUM);
				while(studentNumbers.containsKey(sNumber)){
					sNumber = (random.nextInt((MAX_NUM-MIN_NUM)+1)+MIN_NUM);
				}
				studentNumbers.put(sNumber, 1);

				student.setStudentNumber(Integer.toString(sNumber));
				student.setFocus(studentFocus());
				student.setPreferences(assignPreferences(projects, student.getFocus()));
				students.add(student);
			}
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
			System.err.println("Could not find names file");
		}
		return students;
	}

	/* 60% to be CS and 40% for DS */
	private static Student.Focus studentFocus(){
		int r = random.nextInt(100);
		if (r <= CS_FREQUENCY) {
			return Student.Focus.CS;
		} else {
			return Student.Focus.DS;
		}
	}

	/** Assign list of project preferences to students*/
	private static List<Project> assignPreferences(List<Project> projects, Student.Focus studentFocus) {
		List<Project> projectPreferences = new ArrayList<>();
		while (projectPreferences.size() < MAX_STUDENT_PREFERENCES) {
			// https://stackoverflow.com/questions/54712600/what-is-the-true-maximum-and-minimum-value-of-random-nextgaussian
			// This stackoverflow answer calculated the min/max values of nextGaussian().
			// There is a slight inaccuracy here, to fix use z-scores to find probability P, the calculate index as 'P * projects.size()'.
			double maxOutput = 8;
			double randDistribution = random.nextGaussian() * 3;
			double positiveRandDistribution = Math.max(0, Math.min(16, maxOutput + randDistribution));
			int projectIndex = (int)(positiveRandDistribution / (maxOutput * 2 + 0.01) * projects.size());
			Project randomProject = projects.get(projectIndex);
			if (randomProject.matchesFocus(studentFocus)) {
				randomProject.totalPicks++;
				projectPreferences.add(randomProject);
			}
		}

		return projectPreferences;
	}
}
