package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    private Student student;
    private List<Project> preferences;
    private StaffMember csStaffMember;
    private Project csProject;
    private Project csdsProject;

    @BeforeEach
    void setup(){
        preferences = new ArrayList<Project>();

        String[] researchAreas = {"Acting","Hollywood","Serious Acting"};
        String[] researchActivities = {"performing serious acting","winning Oscars"};

        csStaffMember = new StaffMember("Name2", researchActivities, researchAreas, null, false);

        csProject = new Project("Creating a web interface for promoting evolutionary theory", csStaffMember, Project.Type.CS);
        csdsProject = new Project("Creating a web interface for promoting healthy eating", csStaffMember, Project.Type.CSDS);

        preferences.add(csProject);
        preferences.add(csdsProject);

        student = new Student("Nigel", "Mooney", "12345678", Student.Focus.CS, preferences);
    }

    @Test
    void toCSVRow() {
        assertEquals("12345678,Nigel,Mooney,CS,\"Creating a web interface for promoting evolutionary theory,Creating a web interface for promoting healthy eating\"", student.toCSVRow(), "Incorrect CSV row output");
    }

    @Test
    void setPreferences(){
        assertNotNull(preferences);
    }

    @Test
    void validate() {
        csStaffMember.setSpecialFocus(true);
        assertThrows(IllegalArgumentException.class, () -> csProject.validate());
    }
}