package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private StaffMember staffMember;
    private Project project;
    private String[] researchActivities = {"researchActivity1", "researchActivity2"};
    private String[] researchArea = {"researchArea1", "researchArea2"};

    @BeforeEach
    void setUp() {
        staffMember = new StaffMember("staff1", researchActivities, researchArea , true);
        project = new Project("project1", staffMember, Project.Type.DS);
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
    void toCSVRow() {
        assertEquals("staff1,DS,project1", project.toCSVRow(), "Incorrect CSV row output");
    }

    //TODO TEST THAT IF SPECIAL FOCUS IS TRUE THEN PROJECT PROPOSED MUST BE DS
}