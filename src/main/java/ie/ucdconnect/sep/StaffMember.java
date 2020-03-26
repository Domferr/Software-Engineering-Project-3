package ie.ucdconnect.sep;

public class StaffMember {

    private String name;
    private String[] researchActivities;
    private String[] researchAreas;
    private boolean specialFocus;   // True if is only DS, otherwise false

    public StaffMember() {}

    public StaffMember(String name, String[] researchActivities, String[] researchAreas, boolean specialFocus) {
        this.name = name;
        this.researchActivities = researchActivities;
        this.researchAreas = researchAreas;
        this.specialFocus = specialFocus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSpecialFocus() {
        return specialFocus;
    }

    public void setSpecialFocus(boolean specialFocus) {
        this.specialFocus = specialFocus;
    }

    public String[] getResearchActivities() {
        return researchActivities;
    }

    public void setResearchActivities(String[] researchActivities) {
        this.researchActivities = researchActivities;
    }

    public String[] getResearchAreas() {
        return researchAreas;
    }

    public void setResearchAreas(String[] researchAreas) {
        this.researchAreas = researchAreas;
    }

    @Override
    public String toString() {
        return name;
    }
}
