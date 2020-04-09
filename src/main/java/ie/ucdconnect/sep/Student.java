package ie.ucdconnect.sep;

import com.opencsv.CSVParser;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class Student implements CSVRow {
    public enum Focus {
        CS,
        DS
    }

    private String firstName;
    private String lastName;
    private String studentNumber;
    private Focus focus;
    private List<Project> preferences;
    private double gpa;

    public Student(){

    }

    public Student(String firstName, String lastName, String studentNumber, Focus focus, List<Project> preferences) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentNumber = studentNumber;
        this.focus = focus;
        this.preferences = preferences;
    }

    @Override
    public String toCSVRow() {
        return String.join(",", studentNumber, firstName, lastName, focus.toString(), createPreferencesCSVEntry());
    }

    private String createPreferencesCSVEntry() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for (int i = 0; i < preferences.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(preferences.get(i).getTitle());
        }
        sb.append("\"");
        return sb.toString();
    }

    /**
     * Creates a list of {@link Project} from {@code csvFile}.
     */
    public static List<Student> fromCSV(String csvFile, Map<String, Project> projects) {
        List<Student> students = new LinkedList<>();
        String[] rows = csvFile.split("\n");
        for (String row : rows) {
            students.add(fromCSVRow(row, projects));
        }
        return students;
    }

    /**
     * Creates a {@link Student} from {@code row}.
     * {@code row} must not end with a newline.
     * @return the created {@link Student}, or null if an error occurred.
     */
    public static Student fromCSVRow(String row, Map<String, Project> projects) {
        try {
            String[] parts = new CSVParser().parseLine(row);

            List<Project> projectPreferences = new ArrayList<>();
            if (parts.length != 5) {
                throw new IllegalArgumentException("Expected 5 values, found " + parts.length);
            }
            String[] preferences = parts[4].split(",");
            for(String preference : preferences){
                projectPreferences.add(projects.get(preference));
            }
            return new Student(parts[1], parts[2], parts[0], Focus.valueOf(parts[3]), projectPreferences);
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

    public void generateGpa(){
        Random rand = new Random();
        double randGpa = (rand.nextInt((int)((4.2)*10+1))) / 10.0;
        setGpa(randGpa);
    }

    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    public List<Project> getPreferences() {
        return preferences;
    }

    public boolean hasPreference(Project project) {
        return preferences.contains(project);
    }

    public boolean hasPreference(String projectTitle) {
        for (Project preference : preferences) {
            if (preference.getTitle().equals(projectTitle))
                return true;
        }
        return false;
    }
    public void setPreferences(List<Project> preferences) {
        this.preferences = preferences;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    @Override
    public String toString() {
        return firstName+" "+lastName+" "+studentNumber+" "+focus+" "+ preferences.toString();
    }
}