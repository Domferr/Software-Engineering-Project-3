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

	private static final int[] TEST_SETS_STUDENTS_SIZE = {60, 120, 240, 500};
	private static final int CS_FREQUENCY = 60;
	private static final int AVERAGE_PROPOSAL = 3;	//each staff member proposes, on average, 3 projects
    private static final int MAX_STUDENT_PREFERENCES = 10;
	private static final String DAGON_STUDIES = "Dagon Studies";
	private static final double STUDENTS_STAFF_RATIO = 0.5;
	private static HashMap<Integer, Integer> studentNumbers = new HashMap<>();

    private static Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) throws IOException {
		//Config.getInstance().save("resources", "testcases", "names.txt", "Miskatonic Staff Members.csv", "prefixes.txt");
		ArrayList<String> prefixes = loadPrefixes();
		ArrayList<StaffMember> staffMembers = loadStaffMembers();
		List<List<Student>> studentsTestData = new ArrayList<>();
		List<List<Project>> projectsTestData = new ArrayList<>();
		/** Generating the different number of required students i.e. 60, 120, 240 and 500*/
		for (int i = 0; i < TEST_SETS_STUDENTS_SIZE.length; i++) {
			/**
			 * Generates test cases and outputs the generated tests.
			 */
			int numberOfStaffMembers = (int)(TEST_SETS_STUDENTS_SIZE[i]*STUDENTS_STAFF_RATIO);
			ArrayList<StaffMember> staffCopy = new ArrayList<>();
			for (int j = 0; j < numberOfStaffMembers; j++) {
				int randomIndex = random.nextInt(staffMembers.size());
				staffCopy.add(staffMembers.get(randomIndex));
			}
			List<Project> projects = generateProjects(staffCopy, prefixes, TEST_SETS_STUDENTS_SIZE[i]);
			System.out.println("Generating "+projects.size()+" projects for " + TEST_SETS_STUDENTS_SIZE[i] + " students.");
			List<Student> students = generateStudents(TEST_SETS_STUDENTS_SIZE[i], projects);
			studentsTestData.add(students);
			projectsTestData.add(projects);
		}

		String[] studentsTestSets = Arrays.stream(TEST_SETS_STUDENTS_SIZE).mapToObj(i -> String.format("students%d.csv", i)).toArray(String[]::new);
		for (int i = 0; i < studentsTestSets.length; i++) {
			saveGeneratedTestcase(studentsTestSets[i], studentsTestData.get(i));
		}

		String[] projectsTestSets = Arrays.stream(TEST_SETS_STUDENTS_SIZE).mapToObj(i -> String.format("projectsFor%dStudents.csv", i)).toArray(String[]::new);
		for (int i = 0; i < studentsTestSets.length; i++) {
			saveGeneratedTestcase(projectsTestSets[i], projectsTestData.get(i));
			//printProjectFrequency(projectsTestData.get(i));
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
		String dirName = Config.getInstance().getTestcaseDirName();
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

	public static List<Project> generateProjects(ArrayList<StaffMember> staffMembers, ArrayList<String> prefixes, int howManyStudents) {
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

		return new Project(prefix+" "+title, staffMember, type);
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

	private static ArrayList<StaffMember> loadStaffMembers() throws IOException {
		ArrayList<StaffMember> staffMembers = new ArrayList<StaffMember>();
		CSVReader csvReader = new CSVReader(new FileReader(Config.getInstance().getStaffMembersFile()));
		List<String[]> rows = csvReader.readAll();
		for (String[] row : rows) {
			StaffMember staffMember = new StaffMember();
			staffMember.setName(row[0]);
			staffMember.setResearchActivities(row[1].split(", "));
			staffMember.setResearchAreas(row[2].split(", "));
			staffMember.setSpecialFocus(row.length >= 4 && row[3].equals(DAGON_STUDIES));
			staffMembers.add(staffMember);
		}
		return staffMembers;
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

	private static ArrayList<Student> generateStudents(int noStudents, List<Project> projects) {
		final int MAX_NUM = 90000000;
		final int MIN_NUM = 10000000;

		ArrayList<Student> students = new ArrayList<>(noStudents);
		try{
			Scanner scanner = new Scanner(Config.getInstance().getNamesFile());
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

	private static List<Project> assignPreferences(List<Project> projects, Student.Focus studentFocus) {
		List<Project> projectPreferences = new ArrayList<>();
		while (projectPreferences.size() < MAX_STUDENT_PREFERENCES) {
			// https://stackoverflow.com/questions/54712600/what-is-the-true-maximum-and-minimum-value-of-random-nextgaussian
			// This stackoverflow answer calculated the min/max values of nextGaussian().
			// There is a slight inaccuracy here, to fix use z-scores to find probability P, the calculate index as 'P * projects.size()'.
			double sd = 1;
			double maxOutput = 8 * sd;
			double randDistribution = random.nextGaussian() * sd; // Generates a randomly distributed double, with mean zero and SD 1.
			double positiveRandDistribution = maxOutput + randDistribution;
			int projectIndex = (int)(positiveRandDistribution / (maxOutput * 2) * projects.size());
			Project randomProject = projects.get(projectIndex);
			if (randomProject.matchesFocus(studentFocus)) {
				randomProject.totalPicks++;
				projectPreferences.add(randomProject);
			}
		}

		return projectPreferences;
	}
}
