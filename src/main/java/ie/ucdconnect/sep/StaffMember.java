package ie.ucdconnect.sep;

import java.util.ArrayList;
import java.util.List;

public class StaffMember {

    private String name;    //Full name
    private String[] researchActivities;
    private String[] researchAreas;
    private List<Project> proposedProjects; //What project this staff member has proposed
    private boolean specialFocus;   // True if is only DS, otherwise false

    public StaffMember() {
        proposedProjects = new ArrayList<>();
    }

    public StaffMember(String name, String[] researchActivities, String[] researchAreas, List<Project> proposedProjects, boolean specialFocus) {
        this.name = name;
        this.researchActivities = researchActivities;
        this.researchAreas = researchAreas;
        this.specialFocus = specialFocus;
        if (proposedProjects != null)
            this.proposedProjects = proposedProjects;
        else
            this.proposedProjects = new ArrayList<>();
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

    public List<Project> getProposedProjects() {
        return proposedProjects;
    }

    public void addProposedProject(Project proposedProject) {
        if (specialFocus && proposedProject.getType() != Project.Type.DS
            || !specialFocus && proposedProject.getType() == Project.Type.DS)
            throw new IllegalArgumentException();
        this.proposedProjects.add(proposedProject);
    }

    @Override
    public String toString() {
        return name;
    }
}
