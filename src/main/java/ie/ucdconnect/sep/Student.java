package ie.ucdconnect.sep;

import java.util.List;

public class Student {
    public enum Focus {
        CS,
        DS
    }

    private String firstName;
    private String lastName;
    private String studentNumber;
    private Focus focus;
    private List<Project> preferences;

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