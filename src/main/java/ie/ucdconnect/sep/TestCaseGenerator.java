package ie.ucdconnect.sep;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class TestCaseGenerator {

	private static final int NUM_PROJECTS = 500;

	private Random random = new Random(System.currentTimeMillis());

	private ArrayList<Student> students;


	public static void main(String[] args) throws IOException {
		TestCaseGenerator testCaseGenerator = new TestCaseGenerator();
		testCaseGenerator.generate();
		testCaseGenerator.generateStudents();
	}

	/**
	 * Generates test cases and outputs the generated tests.
	 */
	public void generate() throws IOException {
		ArrayList<String> prefixes = loadPrefixes();
		ArrayList<StaffMember> staffMembers = loadStaffMembers();
		List<Project> projects = generateProjects(staffMembers, prefixes);
		for (Project project : projects) {
			System.out.println(project.getSupervisor() + ":" + project.getTitle() + ":" + project.getType());
		}
	}

	private ArrayList<Project> generateProjects(ArrayList<StaffMember> staffMembers, ArrayList<String> prefixes) {
		// DS projects can only be made by CS supervisors. We need to separate the staff members.
		ArrayList<Project> projects = new ArrayList<>(NUM_PROJECTS);
		Map<Boolean, List<StaffMember>> partition = staffMembers.stream().collect(Collectors.partitioningBy(s -> s.specialFocus));
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
			} else if (projectType < 70) {
				project.setType(Project.Type.CSDS);
				supervisor = pickRandomElement(csOnly);
			} else {
				project.setType(Project.Type.DS);
				supervisor = pickRandomElement(dsOnly);
			}
			project.setSupervisor(supervisor.name);
			project.setTitle(pickRandomElement(prefixes) + " " + pickRandomElement(supervisor.researchActivities));
			projects.add(project);
		}
		return projects;
	}

	private <T> T pickRandomElement(T[] haystack) {
		return haystack[random.nextInt(haystack.length)];
	}

	private <T> T pickRandomElement(List<T> haystack) {
		return haystack.get(random.nextInt(haystack.size()));
	}

	private ArrayList<StaffMember> loadStaffMembers() throws IOException {
		File file = new File("./Miskatonic Staff Members.csv");
		ArrayList<StaffMember> staffMembers = new ArrayList<StaffMember>();
		CSVReader csvReader = new CSVReader(new FileReader(file));
		List<String[]> rows = csvReader.readAll();
		for (String[] row : rows) {
			StaffMember staffMember = new StaffMember();
			staffMember.name = row[0];
			staffMember.researchActivities = row[1].split(", ");
			staffMember.researchAreas = row[2].split(", ");
			staffMember.specialFocus = row.length >= 4 && row[3].equals("Dagon Studies");
			staffMembers.add(staffMember);
		}
		return staffMembers;
	}

	private ArrayList<String> loadPrefixes() throws IOException {
		File file = new File("./prefixes.txt");
		ArrayList<String> prefixes = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = reader.readLine()) != null) {
			prefixes.add(line);
		}
		return prefixes;
	}

	private ArrayList<Student> generateStudents() {
		students = new ArrayList<>();
		File names = new File("./names.txt");
		try{
			Scanner scanner = new Scanner(names);
			while(scanner.hasNext()){
				Student student = new Student();
				student.name = scanner.nextLine();
				int sNumber = (random.nextInt((90000000-10000000)+1)+10000000);
				student.studentNumber = Integer.toString(sNumber);
				student.focus = studentFocus();

				System.out.println(student.name + " " + student.studentNumber + " " + student.focus);
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
	private String studentFocus(){
		int r = random.nextInt(100);
		if(r <= 60){
			return "CS";
		}
		else{
			return "DS";
		}
	}

	private static class StaffMember {
		public String name;
		public String[] researchActivities, researchAreas;
		// True if is only DS, otherwise false
		public boolean specialFocus;
	}

	private static class Student {
		public String name;
		public String studentNumber;
		public String focus;
		public String[] preferences;
	}
}
