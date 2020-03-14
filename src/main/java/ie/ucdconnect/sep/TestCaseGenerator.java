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
	private static final String DAGON_STUDIES = "Dagon Studies";

	private static Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) throws IOException {
		//Config.getInstance().save("resources", "names.txt", "Miskatonic Staff Members.csv", "prefixes.txt");
		generate();
		generateStudents();
	}

	/**
	 * Generates test cases and outputs the generated tests.
	 */
	public static void generate() throws IOException {
		ArrayList<String> prefixes = loadPrefixes();
		ArrayList<StaffMember> staffMembers = loadStaffMembers();
		List<Project> projects = generateProjects(staffMembers, prefixes);
		for (Project project : projects) {
			System.out.println(project.getSupervisor() + ":" + project.getTitle() + ":" + project.getType());
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

	private static ArrayList<Student> generateStudents() {
		ArrayList<Student> students = new ArrayList<>();
		try{
			Scanner scanner = new Scanner(Config.getInstance().getNamesFile());
			while(scanner.hasNext()){
				Student student = new Student();
				student.setName(scanner.nextLine());
				int sNumber = (random.nextInt((90000000-10000000)+1)+10000000);
				student.setStudentNumber(Integer.toString(sNumber));
				student.setFocus(studentFocus());

				System.out.println(student.getName() + " " + student.getStudentNumber() + " " + student.getFocus());
				students.add(student);
			}
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
			System.err.println("Could not find file");
		}
		return students;
	}

	/* 60% to be CS and 40% for DS */
	private static Student.Focus studentFocus(){
		int r = random.nextInt(100);
		if(r <= 60){
			return Student.Focus.CS;
		}
		else{
			return Student.Focus.DS;
		}
	}
}
