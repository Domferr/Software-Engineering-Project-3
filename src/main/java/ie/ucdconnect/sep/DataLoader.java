package ie.ucdconnect.sep;

import com.opencsv.CSVParser;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** Class that loads and parses data that is choosen by the user. */
public class DataLoader {

	// ONLY ADD NEW ENTRIES TO THE BOTTOM OF THE ARRAY!
	// This will keep the index's constant and avoid annoying bugs.
	private final HeaderInfo[] headerInformation = new HeaderInfo[]{
		new HeaderInfo(true, new String[]{"name", "first name", "forename", "student"}),
		new HeaderInfo(false, new String[]{"surname", "second name"}),
		new HeaderInfo(false, new String[]{"student_number", "student number", "id", "sid"}),
		new HeaderInfo(false, new String[]{"gpa", "grade point average", "grade_point_average"}),
		new HeaderInfo(false, new String[]{"proposer", "type"}),
		new HeaderInfo(true, new String[]{"1", "1st", "first"}),
		new HeaderInfo(true, new String[]{"2", "2nd", "second"}),
		new HeaderInfo(true, new String[]{"3", "3rd", "third"}),
		new HeaderInfo(true, new String[]{"4", "4th", "forth"}),
		new HeaderInfo(true, new String[]{"5", "5th", "fifth"}),
		new HeaderInfo(true, new String[]{"6", "6th", "sixth"}),
		new HeaderInfo(true, new String[]{"7", "7th", "seventh"}),
		new HeaderInfo(true, new String[]{"8", "8th", "eighth"}),
		new HeaderInfo(true, new String[]{"9", "9th", "ninth"}),
		new HeaderInfo(true, new String[]{"10", "10th", "tenth"}),
		new HeaderInfo(false, new String[]{"11", "11th", "eleventh"}),
		new HeaderInfo(false, new String[]{"12", "12th", "twelfth"}),
		new HeaderInfo(false, new String[]{"13", "13th", "thirteenth"}),
		new HeaderInfo(false, new String[]{"14", "14th", "fourteenth"}),
		new HeaderInfo(false, new String[]{"15", "15th", "fifteenth"}),
		new HeaderInfo(false, new String[]{"16", "16th", "sixteenth"}),
		new HeaderInfo(false, new String[]{"17", "17th", "seventeenth"}),
		new HeaderInfo(false, new String[]{"18", "18th", "eighteenth"}),
		new HeaderInfo(false, new String[]{"19", "19th", "nineteenth"}),
		new HeaderInfo(false, new String[]{"20", "20th", "twentieth"}),
	};

	private String[] rawHeaders;
	private List<String> unusedHeaders = new ArrayList<>();
	private int blankHeaders = 0;
	private int blankLines = 0;
	private List<Student> students = new ArrayList<>();
	private Vector<String> uniqueProjects = new Vector<>();
	private List<Project> projects = new ArrayList<>();

	/** Loads data from a given file. */
	public void loadData(File file) throws IOException, DataLoaderException {
		CSVParser parser = new CSVParser();
		String content = Utils.readFile(file.toPath());
		String[] lines = content.split("\n");
		this.rawHeaders = parser.parseLine(lines[0]);
		parseHeaders();
		checkHeaders();
		lines = Arrays.copyOfRange(lines, 1, lines.length);
		for (String line : lines) {
			parseLine(parser.parseLine(line));
		}
		// checkProjects();
		System.out.println("Loaded " + students.size() + " students.");
		System.out.println("Loaded " + projects.size() + " projects.");
	}

	/** Parses one line of the data file */
	private void parseLine(String[] line) {
		if (checkLine(line)) {
			blankLines++;
			return;
		}
		String name = headerInformation[0].get(line);
		String studentNumber = "N/A";
		double gpa = 1;
		if (headerInformation[1].isBound()) { // second name
			name += " " + headerInformation[1].get(line);
		}
		if (headerInformation[2].isBound()) { // student number
			studentNumber = headerInformation[2].get(line);
		}

		List<String> preferences = new ArrayList<>();
		if (headerInformation[4].isBound() && isStudentProposed(headerInformation[4].get(line))) {
			for (int i = 5; i < 25; i++) { // Preferences 1 - 20;
				String title = headerInformation[i].get(line);
				if (title.length() > 1) {
					if(!uniqueProjects.contains(title)){
						uniqueProjects.add(title);
						projects.add(new Project(title, name));
					}

				}
			}
		//	return;
		} else {
			for (int i = 5; i < 25; i++) { // Preferences 1 - 20;
				String title = headerInformation[i].get(line);
				if (title.length() > 1) {
					if(!uniqueProjects.contains(title)){
						uniqueProjects.add(title);
						projects.add(new Project(title));
					}

				}
			}
		}
		for (int i = 5; i < 25; i++) { // Preferences 1 - 20;
			preferences.add(headerInformation[i].get(line));
		}
		students.add(new Student(name, studentNumber, gpa, Student.Focus.UNKNOWN, preferences));
	}

	private boolean isStudentProposed(String s) {
		return s.equalsIgnoreCase("student") || s.equalsIgnoreCase("self");
	}

	private void checkProjects() throws DataLoaderException {
		Map<String, Project> projectMap = Utils.generateProjectsMap(projects);
		for (Student student : students) {
			for (String preference : student.getPreferences()) {
				if (!projectMap.containsKey(preference)) {
					throw new DataLoaderException("Preference of student '" + student.getName() + "' '" + preference + "' does not exist.");
				}
			}
		}
	}

	/**
	 * @return true if the line is invalid and should not be parsed
	 */
	private boolean checkLine(String[] line) {
		for (String part : line) {
			if (part.length() > 0) {
				return false;
			}
		}
		return true;
	}

	private void checkHeaders() throws DataLoaderException {
		for (HeaderInfo headerInfo : headerInformation) {
			if (headerInfo.isRequired && !headerInfo.isBound()) {
				throw new DataLoaderException("Header not found. Possible names: " + Arrays.toString(headerInfo.possibleNames));
			}
		}
	}

	private void parseHeaders() throws DataLoaderException {
		for (int i = 0; i < rawHeaders.length; i++) {
			String header = rawHeaders[i];
			boolean found = false;
			if (header.trim().length() == 0) {
				blankHeaders++;
				continue;
			}
			for (HeaderInfo headerInfo : headerInformation) {
				if (headerInfo.matches(header)) {
					headerInfo.bind(i);
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println(header);
				unusedHeaders.add(header);
			}
		}
	}

	/** Returns loaded students */
	public List<Student> getStudents() {
		return students;
	}

	/** Returns loaded projects */
	public List<Project> getProjects() {
		return projects;
	}

	/** Shows warnings as an alert */
	public void displayWarnings() {
		String content = "";
		if (unusedHeaders.size() > 0) {
			String joinedText = String.join(", ", unusedHeaders);
			content += "Data contained unused headers: " + joinedText + "\n";
		}
		if (blankHeaders > 0) {
			content += "Data contained " + blankHeaders + " blank headers." + "\n";
		}
		if (blankLines > 0) {
			content += "Data contained " + blankLines + " blank lines." + "\n";
		}
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Data Warning");
		alert.setHeaderText("Data was loaded successfully but threw warnings");
		alert.setContentText(content);
		alert.showAndWait();
	}

	private static class HeaderInfo {

		private boolean isRequired;
		private int boundIndex = -1;
		private String[] possibleNames;

		public HeaderInfo(boolean isRequired, String[] possibleNames) {
			this.isRequired = isRequired;
			this.possibleNames = possibleNames;
		}

		public String get(String[] parts) {
			return parts[boundIndex]; // Allow out of bounds to be thrown, as if they are it's not a user error.
		}

		public void bind(int index) throws DataLoaderException {
			if (boundIndex != -1) {
				throw new DataLoaderException("Header bound multiple times. Header #" + (boundIndex + 1) + " and #" + (index + 1) + ".");
			}
			boundIndex = index;
		}

		public boolean isBound() {
			return boundIndex != -1;
		}

		public boolean matches(String header) {
			for (String possibleHeader : possibleNames) {
				if (header.equalsIgnoreCase(possibleHeader)) {
					return true;
				}
			}
			return false;
		}
	}
}
