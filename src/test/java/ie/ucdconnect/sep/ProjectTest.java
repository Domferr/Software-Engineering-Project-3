package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private StaffMember DSstaffMember, CSstaffMember;
    private Project project;
    private String[] researchActivities = {"researchActivity1", "researchActivity2"};
    private String[] researchArea = {"researchArea1", "researchArea2"};

    @BeforeEach
    void setUp() {
        DSstaffMember = new StaffMember("staff1", researchActivities, researchArea, null, true);
        CSstaffMember = new StaffMember("staff2", researchActivities, researchArea, null, false);
        DSstaffMember.addProposedProject(project);
    }

    @Test
    void matchesFocus_DS() {
        boolean matches = project.matchesFocus(Student.Focus.DS);
        assertTrue(matches);
    }

    @Test
    void matchesFocus_CS(){
        boolean matches = project.matchesFocus(Student.Focus.CS);
        assertFalse(matches);
    }

    @Test
    void matchesFocus_CSDS(){
        project.setType(Project.Type.CSDS);
        boolean matches = project.matchesFocus(Student.Focus.DS) && project.matchesFocus(Student.Focus.CS);
        assertTrue(matches);
    }

    @Test
    void validate(){
        project.setType(Project.Type.CS);
        assertThrows(IllegalArgumentException.class, () -> project.validate());
    }

    @Test
    void toCSVRow() {
        assertEquals("staff1,DS,project1", project.toCSVRow(), "Incorrect CSV row output");
    }

    @Test
    void fromCSVRow() {
        LinkedList<StaffMember> list = new LinkedList<>();
        list.add(CSstaffMember);
        list.add(DSstaffMember);
        Project parsedProject = Project.fromCSVRow("staff1,DS,project1", list);
        assertEquals("project1", parsedProject.getTitle());
        assertEquals("staff1", parsedProject.getSupervisor().getName());
        assertEquals(Project.Type.DS, parsedProject.getType());
    }

    @Test
    void fromCSV() {
        LinkedList<StaffMember> list = new LinkedList<>();
        list.add(CSstaffMember);
        list.add(DSstaffMember);
        List<Project> parsedProjects = Project.fromCSV("staff1,DS,project1\nstaff2,CS,project2", list);
        assertEquals(2, parsedProjects.size());
        Project project1 = parsedProjects.get(0);
        Project project2 = parsedProjects.get(1);
        assertEquals("project1", project1.getTitle());
        assertEquals("staff1", project1.getSupervisor().getName());
        assertEquals(Project.Type.DS, project1.getType());
        assertEquals("project2", project2.getTitle());
        assertEquals("staff2", project2.getSupervisor().getName());
        assertEquals(Project.Type.CS, project2.getType());
    }
}