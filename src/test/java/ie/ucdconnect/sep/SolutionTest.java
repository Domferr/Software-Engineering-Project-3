package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    private Student student;
    private Project csProject;
    private Project csdsProject;
    private Solution solution;

    @BeforeEach
    void setup(){
        solution = new Solution();
        List<Project> preferences = new ArrayList<Project>();

        StaffMember csStaffMember = new StaffMember("Name2", null, null, null, false);

        csProject = new Project("Creating a web interface for running a movie studio", csStaffMember, Project.Type.CS);
        csdsProject = new Project("Building a service to help writing modern fiction", csStaffMember, Project.Type.CSDS);

        preferences.add(csProject);
        preferences.add(csdsProject);

        student = new Student("Nigel", "Mooney", "12345678", Student.Focus.CS, preferences);
    }

    @Test
    void safeMap_fail() {
        solution.safeMap(student, csProject);

        Student newStudent = new Student("Davis ", "Kemp", "14562995", Student.Focus.CS, null);
        assertThrows(IllegalStateException.class, () -> solution.safeMap(newStudent,csProject));
    }

    @Test
    void safeMap_success() {
        solution.safeMap(student, csProject);
        Student newStudent = new Student("Davis ", "Kemp", "14562995", Student.Focus.CS, null);
        solution.safeMap(newStudent, csdsProject);
        assertEquals(newStudent, solution.getAssignedStudent(csdsProject));
        assertEquals(student, solution.getAssignedStudent(csProject));
        assertEquals(csdsProject, solution.getAssignedProject(newStudent));
        assertEquals(csProject, solution.getAssignedProject(student));
        assertEquals(2, solution.getProjects().size());
    }

    @Test
    void isAvailable() {
        assertTrue(solution.isAvailable(csdsProject));
        solution.safeMap(student, csdsProject);
        assertFalse(solution.isAvailable(csdsProject));
    }

    @Test
    void getAssignedProject() {
        solution.safeMap(student, csdsProject);
        assertEquals(csdsProject, solution.getAssignedProject(student));
        assertNotEquals(csProject, solution.getAssignedProject(student));
    }

    @Test
    void getAssignedStudent() {
        solution.safeMap(student, csProject);
        assertEquals(student, solution.getAssignedStudent(csProject));
        assertNotEquals(student, solution.getAssignedStudent(csdsProject));
    }
}