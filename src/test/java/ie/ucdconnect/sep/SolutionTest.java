package ie.ucdconnect.sep;

import com.google.common.collect.ImmutableMultimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SolutionTest {

	private Student student;
	private Project csProject;
	private Project csdsProject;
	private Solution solution;

	@BeforeEach
	void setup() {
		List<Project> preferences = new ArrayList<Project>();
		StaffMember csStaffMember = new StaffMember("Name2", null, null, null, false);
		csProject = new Project("Creating a web interface for running a movie studio", csStaffMember, Project.Type.CS);
		csdsProject = new Project("Building a service to help writing modern fiction", csStaffMember, Project.Type.CSDS);
		preferences.add(csProject);
		preferences.add(csdsProject);
		student = new Student("Nigel", "Mooney", "12345678", 3.8, Student.Focus.CS, preferences);
		ImmutableMultimap<Project, Student> projectMapping = new ImmutableMultimap.Builder<Project, Student>().put(csdsProject, student).build();
		solution = Solution.SolutionFactory.createAndEvaluate(projectMapping, 1.0);
	}

	@Test
	void evaluate(){
		assertEquals(4.8, solution.getEnergy(), 0.01);
		assertEquals(43.2, solution.getFitness(), 0.01);
	}

	@Test
	void getAssignedProject() {
		assertEquals(csdsProject, solution.getAssignedProject(student));
		assertNotEquals(csProject, solution.getAssignedProject(student));
	}

	@Test
	void getAssignedStudent() {
		Collection<Student> students = solution.getAssignedStudents(csdsProject);
		assertEquals(1, students.size());
		assertEquals(student, students.iterator().next());
		assertEquals(0, solution.getAssignedStudents(csProject).size());
	}
}