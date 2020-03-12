package ie.ucdconnect.sep;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class TestCaseGenerator {

	public static void main(String[] args) throws IOException {
		new TestCaseGenerator().generate();
	}

	/**
	 * Generates test cases and outputs the generated tests.
	 */
	public void generate() throws IOException {
		ArrayList<String> prefixes = loadPrefixes();
		ArrayList<StaffMember> staffMembers = loadStaffMembers();
		for (StaffMember staffMember : staffMembers) {
			System.out.println(staffMember.name + ":" + staffMember.specialFocus);
		}
	}

	private ArrayList<StaffMember> loadStaffMembers() throws IOException {
		File file = new File("./Miskatonic Staff Members.csv");
		ArrayList<StaffMember> staffMembers = new ArrayList<StaffMember>();
		CSVReader csvReader = new CSVReader(new FileReader(file));
		List<String[]> rows = csvReader.readAll();
		for (String[] row : rows) {
			StaffMember staffMember = new StaffMember();
			staffMember.name = row[0];
			staffMember.researchActivities = row[1].split(", ");
			staffMember.researchAreas = row[2].split(", ");
			staffMember.specialFocus = row.length >= 4 && row[3].equals("Dagon Studies");
			staffMembers.add(staffMember);
		}
		return staffMembers;
	}

	private ArrayList<String> loadPrefixes() throws IOException {
		File file = new File("./prefixes.txt");
		ArrayList<String> prefixes = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = reader.readLine()) != null) {
			prefixes.add(line);
		}
		return prefixes;
	}

	private static class StaffMember {
		public String name;
		public String[] researchActivities, researchAreas;
		// True if is only DS, otherwise false
		public boolean specialFocus;
	}
}
