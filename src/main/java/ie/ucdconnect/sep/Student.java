package ie.ucdconnect.sep;

import com.opencsv.CSVParser;

import java.io.IOException;
import java.sql.PseudoColumnUsage;
import java.util.*;

public class Student implements CSVRow {
	public enum Focus {
		UNKNOWN,
		CS,
		DS
	}

	private String name;
	private String studentNumber;
	private Focus focus;
	private List<String> preferences;
	private double gpa;

	private boolean gotPreference = true;

	public Student() {

	}

	public Student(String name, String studentNumber, double gpa, Focus focus, List<String> preferences) {
		this.name = name;
		this.studentNumber = studentNumber;
		this.gpa = gpa;
		this.focus = focus;
		this.preferences = preferences;
	}

	public Student(String firstName, String lastName, String studentNumber, double gpa, Focus focus, List<String> preferences) {
		this(firstName + " " + lastName, studentNumber, gpa, focus, preferences);
	}

	@Override
	public String toCSVRow() {
		return String.join(",", studentNumber, name, Double.toString(gpa), focus.toString(), createPreferencesCSVEntry());
	}

	private String createPreferencesCSVEntry() {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		for (int i = 0; i < preferences.size(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(preferences.get(i));
		}
		sb.append("\"");
		return sb.toString();
	}

	/**
	 * Creates a list of {@link Project} from {@code csvFile}.
	 */
	public static List<Student> fromCSV(String csvFile) {
		List<Student> students = new LinkedList<>();
		String[] rows = csvFile.split("\n");
		for (String row : rows) {
			students.add(fromCSVRow(row));
		}
		return students;
	}

	/**
	 * Creates a {@link Student} from {@code row}.
	 * {@code row} must not end with a newline.
	 *
	 * @return the created {@link Student}, or null if an error occurred.
	 */
	public static Student fromCSVRow(String row) {
		try {
			String[] parts = new CSVParser().parseLine(row);

			if (parts.length != 5) {
				throw new IllegalArgumentException("Expected 5 values, found " + parts.length);
			}
			String[] preferences = parts[4].split(",");
			List<String> projectPreferences = new ArrayList<>();
			for (String preference : preferences) {
				projectPreferences.add(preference);
			}
			return new Student(parts[1], parts[0], Double.parseDouble(parts[2]), Focus.valueOf(parts[3]), projectPreferences);
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Could not parse: " + row);
	}

	private static StaffMember findStaffMember(String name, List<StaffMember> staffMembers) {
		for (StaffMember staffMember : staffMembers) {
			if (staffMember.getName().equals(name)) {
				return staffMember;
			}
		}
		return null;
	}

	/**
	 * Generates a random gpa between 0 to 4.2
	 */
	public void generateGpa() {
		Random rand = new Random();
		double randGpa = (rand.nextInt((int) ((4.2) * 10 + 1))) / 10.0;
		setGpa(randGpa);
	}

	public void setFullName(String firstName, String lastName) {
		this.name = firstName + " " + lastName;
	}

	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public Focus getFocus() {
		return focus;
	}

	public void setFocus(Focus focus) {
		this.focus = focus;
	}

	public List<String> getPreferences() {
		return preferences;
	}

	public boolean hasPreference(Project project) {
		return hasPreference(project.getTitle());
	}

	public boolean hasPreference(String projectTitle) {
		for (String preference : preferences) {
			if (projectTitle.equals(preference))
				return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setPreferences(List<String> preferences) {
		this.preferences = preferences;
	}

	public double getGpa() {
		return gpa;
	}

	public void setGpa(double gpa) {
		this.gpa = gpa;
	}

	public boolean isGotPreference() {
		return gotPreference;
	}

	public void setGotPreference(boolean gotPreference) {
		this.gotPreference = gotPreference;
	}

	@Override
	public String toString() {
		return name +  " " + studentNumber + " " + focus + " " + preferences.toString();
	}
}