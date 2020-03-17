package ie.ucdconnect.sep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class StaffMember {
    private String name;
    private String[] researchActivities;
    private String[] researchAreas;
    private boolean specialFocus;   // True if is only DS, otherwise false
    private ArrayList<String> canBeProposed;

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
        this.canBeProposed = new ArrayList<String>(Arrays.asList(researchActivities));
    }

    public String[] getResearchAreas() {
        return researchAreas;
    }

    public void setResearchAreas(String[] researchAreas) {
        this.researchAreas = researchAreas;
    }

    public String proposeResearch() {
        Random random = new Random(System.currentTimeMillis());
        String randomResearch = canBeProposed.remove(random.nextInt(canBeProposed.size()));
        return randomResearch;
    }

    public boolean hasMoreToPropose() {
        return canBeProposed.size() > 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
