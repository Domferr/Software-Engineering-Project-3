package ie.ucdconnect.sep;

import com.opencsv.CSVParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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

	public StaffMember(String name, String[] researchActivities, String[] researchAreas, boolean specialFocus) {
		this(name, researchActivities, researchAreas, new ArrayList<Project>(), specialFocus);
	}

	public StaffMember(String name, String[] researchActivities, String[] researchAreas, List<Project> proposedProjects, boolean specialFocus) {
		this.name = name;
		this.researchActivities = researchActivities;
		this.researchAreas = researchAreas;
		this.specialFocus = specialFocus;
		this.proposedProjects = proposedProjects;
	}

	/**
	 * Creates a list of {@link StaffMember} from {@code csvFile}.
	 */
	public static List<StaffMember> fromCSV(String csvFile) {
		List<StaffMember> staffMembers = new LinkedList<>();
		String[] rows = csvFile.split("\n");
		for (String row : rows) {
			staffMembers.add(fromCSVRow(row));
		}
		return staffMembers;
	}

	/**
	 * Creates a {@link StaffMember} from {@code row}.
	 * {@code row} must not end with a newline.
	 *
	 * @return the created {@link StaffMember}
	 */
	public static StaffMember fromCSVRow(String row) {
		try {
			String[] parts = new CSVParser().parseLine(row);
			if (parts.length < 3 || parts.length > 4) {
				throw new IllegalArgumentException("Invalid number of columns");
			}
			String name = parts[0];
			if (name.trim().length() == 0)
				throw new IllegalArgumentException("Staff member must have a name");
			String[] researchActivities = parts[1].split(", ");
			String[] researchAreas = parts[2].split(", ");
			boolean specialFocus = false;
			if (parts.length == 4) {
				specialFocus = parts[3].equals("Dagon Studies");
			}
			if (parts.length == 4 && !specialFocus && !parts[3].equals(""))
				throw new IllegalArgumentException("Dagon Studies is the only special focus allowed: " + row);

			return new StaffMember(name, researchActivities, researchAreas, specialFocus);
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Could not parse: " + row);
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
			throw new IllegalArgumentException("This staffmember cannot propose this project. Staff special focus: " + specialFocus + ", Project type: " + proposedProject.getType());
		this.proposedProjects.add(proposedProject);
	}

	@Override
	public String toString() {
		return name;
	}
}
