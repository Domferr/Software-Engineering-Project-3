package ie.ucdconnect.sep;

public class Project {
    public enum Type {
        CS,
        CSDS,
        DS;
    }

    private StaffMember supervisor;
    private String title;
    private Type type;

    public Project(String title, StaffMember supervisor, Type type) {
        this.title = title;
        this.supervisor = supervisor;
        this.type = type;
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
        return supervisor.getName()+" "+title+" "+type;
    }
}
