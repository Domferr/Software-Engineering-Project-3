package ie.ucdconnect.sep;

import com.opencsv.CSVParser;

import java.io.IOException;
import java.util.List;

public class Project implements CSVRow {
	public enum Type {
        CS,
        CSDS,
        DS
    }
    private StaffMember supervisor;

    private String title;
    private Type type;
    public int totalPicks = 0;

    public Project(String title, StaffMember supervisor, Type type) {
        this.title = title;
        this.supervisor = supervisor;
        this.type = type;
        validate();
    }

    void validate(){
        if(supervisor == null || supervisor.isSpecialFocus() && !type.equals(Type.DS)){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a {@link Project} from {@code row}.
     * {@code row} must not end with a newline.
     * @return the created {@link Project}, or null if an error occurred.
     */
    public static Project fromCSVRow(String row, List<StaffMember> staffMembers) {
        try {
            String[] parts = new CSVParser().parseLine(row);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Expected 3 values, found " + parts.length);
            }
            return new Project(parts[2], findStaffMember(parts[0], staffMembers), Type.valueOf(parts[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static StaffMember findStaffMember(String name, List<StaffMember> staffMembers) {
        for (StaffMember staffMember : staffMembers) {
            if (staffMember.getName().equals(name)) {
                return staffMember;
            }
        }
        return null;
    }

    @Override
    public String toCSVRow() {
        return String.join(",", supervisor.getName(), type.toString(), title);
    }

    public boolean matchesFocus(Student.Focus studentFocus) {
        return (type.equals(Project.Type.CS) && studentFocus.equals(Student.Focus.CS))
                || (type.equals(Project.Type.DS) && studentFocus.equals(Student.Focus.DS))
                || type.equals(Project.Type.CSDS);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public StaffMember getSupervisor() { return supervisor; }

    public void setSupervisor(StaffMember supervisor) { this.supervisor = supervisor; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return title;
    }
}
