package ie.ucdconnect.sep;

public class Student {
    public enum Focus {
        CS,
        DS
    }

    private String name;
    private String studentNumber;
    private Focus focus;
    private String[] preferences;

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

    public String[] getPreferences() {
        return preferences;
    }

    public void setPreferences(String[] preferences) {
        this.preferences = preferences;
    }
}