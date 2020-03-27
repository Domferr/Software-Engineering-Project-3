package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

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

        csProject = new Project("Creating a web interface for running a movie studio", csStaffMember, Project.Type.CS);
        csdsProject = new Project("Building a service to help writing modern fiction", csStaffMember, Project.Type.CSDS);

        preferences.add(csProject);
        preferences.add(csdsProject);

        student = new Student("Nigel", "Mooney", "12345678", Student.Focus.CS, preferences);
    }

    @Test
    void toCSVRow() {
        assertEquals("12345678,Nigel,Mooney,CS,\"Creating a web interface for running a movie studio,Building a service to help writing modern fiction\"", student.toCSVRow(), "Incorrect CSV row output");
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

    @Test
    void fromCSVRow() {
        HashMap<String, Project> map = new HashMap<>();
        map.put("Creating a web interface for running a movie studio", csProject);
        map.put("Building a service to help writing modern fiction", csdsProject);
        Student parsedStudent = Student.fromCSVRow("89457781,Michel,Owen,CS,\"Creating a web interface for running a movie studio,Building a service to help writing modern fiction\"", map);
        assertEquals("89457781", parsedStudent.getStudentNumber());
        assertEquals(Student.Focus.CS, parsedStudent.getFocus());
        assertEquals("[Creating a web interface for running a movie studio, Building a service to help writing modern fiction]", parsedStudent.getPreferences().toString());
    }

    @Test
    void fromCSVRow_tooFewColumns() {
        HashMap<String, Project> map = new HashMap<>();
        map.put("Creating a web interface for running a movie studio", csProject);
        map.put("Building a service to help writing modern fiction", csdsProject);
        assertThrows(IllegalArgumentException.class, () -> Student.fromCSVRow("89457781,\"Michel,Owen\",CS,\"Creating a web interface for running a movie studio,Building a service to help writing modern fiction\"", map));
    }

    @Test
    void fromCSVRow_tooManyColumns() {
        HashMap<String, Project> map = new HashMap<>();
        map.put("Creating a web interface for running a movie studio", csProject);
        map.put("Building a service to help writing modern fiction", csdsProject);
        assertThrows(IllegalArgumentException.class, () -> Student.fromCSVRow("89457781,Michel,Owen,CS,Creating a web interface for running a movie studio,Building a service to help writing modern fiction", map));
    }
    @Test
    void fromCSV() {
        HashMap<String, Project> map = new HashMap<>();
        map.put("Creating a web interface for running a movie studio", csProject);
        map.put("Building a service to help writing modern fiction", csdsProject);
        List<Student> parsedStudents = Student.fromCSV("89457781,Michel,Owen,CS,\"Creating a web interface for running a movie studio\"\n28859293,Nigel,Mooney,CS,\"Building a service to help writing modern fiction\"", map);
        assertEquals(2, parsedStudents.size());
        Student student1 = parsedStudents.get(0);
        Student student2 = parsedStudents.get(1);
        assertEquals("89457781", student1.getStudentNumber());
        assertEquals("28859293", student2.getStudentNumber());
        assertEquals(Student.Focus.CS, student1.getFocus());
        assertEquals(Student.Focus.CS, student2.getFocus());
    }
}