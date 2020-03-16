package ie.ucdconnect.sep;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class generates a test case
 */
public class TestCaseGenerator {

	private static final int NUM_PROJECTS = 500;
	private static final int CS_FREQUENCY = 60;
    private static final int MAX_STUDENT_PREFERENCES = 10;
	private static final String DAGON_STUDIES = "Dagon Studies";

    private static Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) throws IOException {
		Config.getInstance().save("resources", "testcases", "names.txt", "Miskatonic Staff Members.csv", "prefixes.txt");

		/**
		 * Generates test cases and outputs the generated tests.
		 */
		ArrayList<String> prefixes = loadPrefixes();
        ArrayList<StaffMember> staffMembers = loadStaffMembers();
        List<Project> projects = generateProjects(staffMembers, prefixes);

		for (Project project : projects) {
			System.out.println(project.getSupervisor() + ":" + project.getTitle() + ":" + project.getType());
		}

        /* Generating the different number of required students i.e. 60, 120, 240 and 500*/
		int[] testSetsStudents = {60, 120, 240, 500};
		List<List<Student>>  studentsTestData = new ArrayList<>();
		for (int i = 0; i < testSetsStudents.length; i++){
			List<Student> students = generateStudents(testSetsStudents[i], projects);
			studentsTestData.add(students);
		}

		for(List<Student> student : studentsTestData){
			System.out.println("Students Test Set: " + student.size());
		}

        for (List<Student> students: studentsTestData) {
			System.out.println("Students: " + students.size());
        	for(Student student : students){
				System.out.println(student);
			}
        }

        System.out.println("Projects: " + projects.size());
        System.out.println("Staff: " + staffMembers.size());

		saveGeneratedTestcase("students60.txt", studentsTestData.get(0), "Couldn't write into students file");
		saveGeneratedTestcase("students120.txt", studentsTestData.get(1), "Couldn't write into students file");
		saveGeneratedTestcase("students240.txt", studentsTestData.get(2), "Couldn't write into students file");
		saveGeneratedTestcase("students500.txt", studentsTestData.get(3), "Couldn't write into students file");
        saveGeneratedTestcase("projects.txt", projects, "Couldn't write into projects file");
	}

	/** Write the given list into specified file.  */
    private static void saveGeneratedTestcase(String filename, List list, String err) {
		String dirName = Config.getInstance().getTestcaseDirName();
	    //Create dir if it doesn't exist
	    File testCaseDir = new File(dirName);
	    if (!testCaseDir.exists())
	    	testCaseDir.mkdir();
        //Finally write into file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dirName+filename));
            for (Object elem : list) {
                writer.append(elem.toString()+"\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.print(err);
            e.printStackTrace();
        }
    }

	private static ArrayList<Project> generateProjects(ArrayList<StaffMember> staffMembers, ArrayList<String> prefixes) {
		// DS projects can only be made by CS supervisors. We need to separate the staff members.
		ArrayList<Project> projects = new ArrayList<>(NUM_PROJECTS);
		Map<Boolean, List<StaffMember>> partition = staffMembers.stream().collect(Collectors.partitioningBy(s -> s.isSpecialFocus()));
		List<StaffMember> csOnly = partition.get(false); // Proposes CS or CS+DS
		List<StaffMember> dsOnly = partition.get(true); // Proposes DS
		/**
		 * CS vs. CS+DS = 60 vs. 40
		 * As such, I'll generate projects as follows:
		 * 0-49: CS
		 * 50-69: CS+DS
		 * 70-100: DS
		 */
		for (int i = 0; i < NUM_PROJECTS; i++) {
			double projectType = random.nextDouble();
			Project project = new Project();
			StaffMember supervisor;
			if (projectType < 0.5) {
				supervisor = pickRandomElement(csOnly);
				project.setType(Project.Type.CS);
			} else if (projectType < 0.7) {
				project.setType(Project.Type.CSDS);
				supervisor = pickRandomElement(csOnly);
			} else {
				project.setType(Project.Type.DS);
				supervisor = pickRandomElement(dsOnly);
			}
			project.setSupervisor(supervisor.getName());
			project.setTitle(pickRandomElement(prefixes) + " " + pickRandomElement(supervisor.getResearchActivities()));
			projects.add(project);
		}
		return projects;
	}

	private static <T> T pickRandomElement(T[] haystack) {
		return haystack[random.nextInt(haystack.length)];
	}

	private static <T> T pickRandomElement(List<T> haystack) {
		return haystack.get(random.nextInt(haystack.size()));
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
				student.setName(scanner.nextLine());
				int sNumber = (random.nextInt((MAX_NUM-MIN_NUM)+1)+MIN_NUM);
				student.setStudentNumber(Integer.toString(sNumber));
				student.setFocus(studentFocus());
				student.setPreferences(assignPreferences(projects));
				//System.out.println(student.getName() + " " + student.getStudentNumber() + " " + student.getFocus() + " " + student.getPreferences());
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

	private static List<Project> assignPreferences(List<Project> projects) {
		List<Project> projectPreferences = new ArrayList<>();
		for (int i = 0; i < MAX_STUDENT_PREFERENCES; i++){
			int rand = random.nextInt(NUM_PROJECTS);
			projectPreferences.add(projects.get(rand));
		}
		return projectPreferences;
	}
}
