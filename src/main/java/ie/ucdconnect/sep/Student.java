package ie.ucdconnect.sep;

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

    public void setPreferences(List<Project> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return firstName+" "+lastName+" "+studentNumber+" "+focus+" "+ preferences.toString();
    }
}