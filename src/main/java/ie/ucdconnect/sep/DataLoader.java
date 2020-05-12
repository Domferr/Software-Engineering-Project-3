package ie.ucdconnect.sep;

import com.opencsv.CSVParser;
import javafx.scene.control.Alert;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public void loadData(File file) throws IOException {
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
	}

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
		if (headerInformation[2].isBound()) { // second name
			studentNumber = headerInformation[2].get(line);
		}
		if (headerInformation[4].isBound()) { // Student or proposer differentiator. If not bound is assumed to be a student.
			// TODO: Load a staff member rather than a student
		}
		List<String> preferences = new ArrayList<>();
		for (int i = 5; i < 15; i++) { // Preferences 1 - 10;
			preferences.add(headerInformation[i].get(line));
		}
		students.add(new Student(name, studentNumber, gpa, Student.Focus.UNKNOWN, preferences));
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

	private void checkHeaders() {
		for (HeaderInfo headerInfo : headerInformation) {
			if (headerInfo.isRequired && !headerInfo.isBound()) {
				throw new IllegalArgumentException("Header not found: " + Arrays.toString(headerInfo.possibleNames));
			}
		}
	}

	private void parseHeaders() {
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

	public List<Student> getStudents() {
		return students;
	}

	public void displayWarnings() {
		if (unusedHeaders.size() > 0) {
			Alert unusedHeadersAlert = new Alert(Alert.AlertType.INFORMATION);
			String joinedText = String.join("\n", unusedHeaders);
			unusedHeadersAlert.setTitle("Unused headers");
			unusedHeadersAlert.setHeaderText("Data contained unused headers:");
			unusedHeadersAlert.setContentText(joinedText);
			unusedHeadersAlert.showAndWait();
		}
		if (blankHeaders > 0) {
			Alert unusedHeadersAlert = new Alert(Alert.AlertType.INFORMATION);
			String joinedText = String.join("\n", unusedHeaders);
			unusedHeadersAlert.setTitle("Blank headers");
			unusedHeadersAlert.setHeaderText("Data contained " + blankHeaders + " blank headers.");
			unusedHeadersAlert.showAndWait();
		}
		if (blankLines > 0) {
			Alert unusedHeadersAlert = new Alert(Alert.AlertType.INFORMATION);
			String joinedText = String.join("\n", unusedHeaders);
			unusedHeadersAlert.setTitle("Blank lines");
			unusedHeadersAlert.setHeaderText("Data contained " + blankHeaders + " blank lines.");
			unusedHeadersAlert.showAndWait();
		}
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

		public void bind(int index) {
			if (boundIndex != -1) {
				throw new IllegalArgumentException("Header bound multiple times. Header #" + (boundIndex + 1) + " and #" + (index + 1) + ".");
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
