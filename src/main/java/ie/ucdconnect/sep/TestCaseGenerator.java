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

	private static final int NUM_PROJECTS = 500;
	private static final int CS_FREQUENCY = 60;
    private static final int MAX_STUDENT_PREFERENCES = 10;
	private static final String DAGON_STUDIES = "Dagon Studies";

    private static Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) throws IOException {
		//Config.getInstance().save("resources", "testcases", "names.txt", "Miskatonic Staff Members.csv", "prefixes.txt");

		/**
		 * Generates test cases and outputs the generated tests.
		 */
		ArrayList<String> prefixes = loadPrefixes();
        ArrayList<StaffMember> staffMembers = loadStaffMembers();
        List<Project> projects = generateProjects(staffMembers, prefixes);

		for (Project project : projects) {
			System.out.println(project.getSupervisor() + ":" + project.getTitle() + ":" + project.getType());
		}

        /** Generating the different number of required students i.e. 60, 120, 240 and 500*/
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

		Function <Student, String> studentPrinter = student -> student.toString();
		String[] testSets = {"students60.txt", "students120.txt", "students240.txt", "students500.txt"};
        for(int i = 0; i < testSets.length; i++){
			saveGeneratedTestcase(testSets[i], studentsTestData.get(i), "Couldn't write into students file", studentPrinter);
		}
        
		Function <Project, String> projectPrinter = project -> project.getSupervisor().getName()+" "+project.getTitle()+" "+project.getType();
        saveGeneratedTestcase("projects.txt", projects, "Couldn't write into projects file", projectPrinter);
	}

	/** Write the given list into specified file.  */
    private static <T> void saveGeneratedTestcase(String filename, List<T> list, String err, Function<T, String> rowFun) {
		String dirName = Config.getInstance().getTestcaseDirName();
	    //Create dir if it doesn't exist
	    File testCaseDir = new File(dirName);
	    if (!testCaseDir.exists())
	    	testCaseDir.mkdir();
        //Finally write into file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dirName+filename));
            for (T elem : list) {
                writer.append(rowFun.apply(elem)+"\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.print(err);
            e.printStackTrace();
        }
    }

	public static List<Project> generateProjects(ArrayList<StaffMember> staffMembers, ArrayList<String> prefixes) {
		ArrayList<Project> projects = new ArrayList<>(NUM_PROJECTS);
		ArrayList<StaffMember> staffCopy = new ArrayList<>(staffMembers);
		Map<Boolean, List<StaffMember>> partition = staffCopy.stream().collect(Collectors.partitioningBy(s -> s.isSpecialFocus()));
		List<StaffMember> csAndDs = partition.get(false); // Proposes CS or CS+DS
		List<StaffMember> dsOnly = partition.get(true); // Proposes DS
		/**
		 * CS vs. CS+DS = 60 vs. 40
		 * As such, I'll generate projects as follows:
		 * 0-49: CS
		 * 50-69: CS+DS
		 * 70-100: DS
		 */
		for (int i = 0; i < NUM_PROJECTS; i++) {
			Project.Type randomType = pickRandomType();
			Project newProject;
			if (randomType == Project.Type.DS) {
				newProject = generateOneProject(dsOnly, prefixes, randomType);
			} else {
				newProject = generateOneProject(csAndDs, prefixes, randomType);
			}

			projects.add(newProject);
		}
		System.out.println(csAndDs.size());
		System.out.println(dsOnly.size());
		return projects;
	}

	/** Returns a project and ensures that the staff member will not be picked in the future if it has proposed
	 *  all of its researches.
	 * */
	private static Project generateOneProject(List<StaffMember> staffList, List<String> prefixes, Project.Type type) {
		int staffIndex = random.nextInt(staffList.size());
		StaffMember supervisor = staffList.get(staffIndex);
		String title = supervisor.proposeResearch();
		if (!supervisor.hasMoreToPropose())
			staffList.remove(staffIndex);
		String prefix = prefixes.get(random.nextInt(prefixes.size()));

		return new Project(prefix+" "+title, supervisor, type);
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
				student.setName(scanner.nextLine());
				int sNumber = (random.nextInt((MAX_NUM-MIN_NUM)+1)+MIN_NUM);
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
		List<Project> projectsCopy = new ArrayList<>(projects);

		int i = 0;
		while (i < MAX_STUDENT_PREFERENCES) {
			int randIndex = random.nextInt(projectsCopy.size());
			Project randomProject = projectsCopy.get(randIndex);
			if ((randomProject.getType().equals(Project.Type.CS) && studentFocus.equals(Student.Focus.CS))
					|| (randomProject.getType().equals(Project.Type.DS) && studentFocus.equals(Student.Focus.DS))
					|| randomProject.getType().equals(Project.Type.CSDS)) {

				projectPreferences.add(randomProject);
				projectsCopy.remove(randIndex);
				i++;
			}
		}

		return projectPreferences;
	}
}
