package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

	private Student student;
	private List<String> preferences;
	private StaffMember csStaffMember;
	private Project csProject;
	private Project csdsProject;

	@BeforeEach
	void setup() {
		preferences = new ArrayList<String>();

		String[] researchAreas = {"Acting", "Hollywood", "Serious Acting"};
		String[] researchActivities = {"performing serious acting", "winning Oscars"};

		csStaffMember = new StaffMember("Name2", researchActivities, researchAreas, null, false);

		csProject = new Project("Creating a web interface for running a movie studio", csStaffMember, Project.Type.CS);
		csdsProject = new Project("Building a service to help writing modern fiction", csStaffMember, Project.Type.CSDS);

		preferences.add(csProject.getTitle());
		preferences.add(csdsProject.getTitle());

		student = new Student("Nigel", "Mooney", "12345678", 3.8, Student.Focus.CS, preferences);
	}

	@Test
	void toCSVRow() {
		assertEquals("12345678,Nigel,Mooney,3.8,CS,\"Creating a web interface for running a movie studio,Building a service to help writing modern fiction\"", student.toCSVRow(), "Incorrect CSV row output");
	}

	@Test
	void setPreferences() {
		assertNotNull(preferences);
	}

	@Test
	void validate() {
		csStaffMember.setSpecialFocus(true);
		assertThrows(IllegalArgumentException.class, () -> csProject.validate());
	}

	@Test
	void fromCSVRow() {
		Student parsedStudent = Student.fromCSVRow("89457781,Michel,Owen,4.2,CS,\"Creating a web interface for running a movie studio,Building a service to help writing modern fiction\"");
		assertEquals("89457781", parsedStudent.getStudentNumber());
		assertEquals(Student.Focus.CS, parsedStudent.getFocus());
		assertEquals("[Creating a web interface for running a movie studio, Building a service to help writing modern fiction]", parsedStudent.getPreferences().toString());
	}

	@Test
	void fromCSVRow_tooFewColumns() {
		assertThrows(IllegalArgumentException.class, () -> Student.fromCSVRow("89457781,\"Michel,Owen\",1,CS,\"Creating a web interface for running a movie studio,Building a service to help writing modern fiction\""));
	}

	@Test
	void fromCSVRow_tooManyColumns() {
		assertThrows(IllegalArgumentException.class, () -> Student.fromCSVRow("89457781,Michel,Owen,0.2,CS,Creating a web interface for running a movie studio,Building a service to help writing modern fiction"));
	}

	@Test
	void fromCSV() {
		List<Student> parsedStudents = Student.fromCSV("89457781,Michel,Owen,1,CS,\"Creating a web interface for running a movie studio\"\n28859293,Nigel,Mooney,2,CS,\"Building a service to help writing modern fiction\"");
		assertEquals(2, parsedStudents.size());
		Student student1 = parsedStudents.get(0);
		Student student2 = parsedStudents.get(1);
		assertEquals("89457781", student1.getStudentNumber());
		assertEquals("28859293", student2.getStudentNumber());
		assertEquals(Student.Focus.CS, student1.getFocus());
		assertEquals(Student.Focus.CS, student2.getFocus());
	}
}