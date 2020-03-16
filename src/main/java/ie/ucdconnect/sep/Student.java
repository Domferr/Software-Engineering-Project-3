package ie.ucdconnect.sep;

import java.util.List;

public class Student {
    public enum Focus {
        CS,
        DS
    }

    private String name;
    private String studentNumber;
    private Focus focus;
    private List<Project> preferences;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return name+" "+studentNumber+" "+focus+" "+preferences.toString();
    }
}