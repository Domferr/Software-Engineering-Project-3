package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffMemberTest {

    /** DS members propose DS only projects. */
    private StaffMember dsOnlyStaffMember;
    /** Non-DS members propose CS or CS+DS projects, but not DS-only projects. */
    private StaffMember csStaffMember;

    @BeforeEach
    void setUp() {
        String[] researchAreas = {"Acting","Hollywood","Serious Acting"};
        String[] researchActivities = {"performing serious acting","winning Oscars"};

        dsOnlyStaffMember = new StaffMember("Name1", researchActivities, researchAreas, true);
        csStaffMember = new StaffMember("Name2", researchActivities, researchAreas, false);
    }

    @Test
    public void testFromCSVRow_withSpecialFocus() {
        StaffMember staffMember = StaffMember.fromCSVRow("The Joker,\"causing mayhem, spreading fear, laughing maniacally\",\"DC Comics, Gotham City\",Dagon Studies");
        assertEquals("The Joker", staffMember.getName());
        assertArrayEquals(new String[]{"causing mayhem", "spreading fear", "laughing maniacally"}, staffMember.getResearchActivities());
        assertArrayEquals(new String[]{"DC Comics", "Gotham City"}, staffMember.getResearchAreas());
        assertTrue(staffMember.isSpecialFocus());
    }

    @Test
    public void testFromCSVRow_withoutSpecialFocus() {
        StaffMember staffMember = StaffMember.fromCSVRow("The Joker,\"causing mayhem, spreading fear, laughing maniacally\",\"DC Comics, Gotham City\"");
        assertEquals("The Joker", staffMember.getName());
        assertArrayEquals(new String[]{"causing mayhem", "spreading fear", "laughing maniacally"}, staffMember.getResearchActivities());
        assertArrayEquals(new String[]{"DC Comics", "Gotham City"}, staffMember.getResearchAreas());
        assertFalse(staffMember.isSpecialFocus());
    }

    @Test
    public void testFromCSVRow_invalidNumberOfColumns() {
        assertThrows(IllegalArgumentException.class, () -> StaffMember.fromCSVRow("The Joker,\"causing mayhem, spreading fear, laughing maniacally\",\"DC Comics, Gotham City\",\"Dagon Studies\",\"Something\""));
    }

    @Test
    public void testFromCSVRow_noName() {
        assertThrows(IllegalArgumentException.class, () -> StaffMember.fromCSVRow(",\"causing mayhem, spreading fear, laughing maniacally\",\"DC Comics, Gotham City\""));
        assertThrows(IllegalArgumentException.class, () -> StaffMember.fromCSVRow("\"\",\"causing mayhem, spreading fear, laughing maniacally\",\"DC Comics, Gotham City\""));
    }

    @Test
    public void testFromCSVRow_wrongStudies() {
        assertThrows(IllegalArgumentException.class, () -> StaffMember.fromCSVRow("The Joker,\"causing mayhem, spreading fear, laughing maniacally\",\"DC Comics, Gotham City\",Wrong Studies"));
        assertThrows(IllegalArgumentException.class, () -> StaffMember.fromCSVRow("The Joker,\"causing mayhem, spreading fear, laughing maniacally\",\"DC Comics, Gotham City\",\" \""));
    }

    @Test
    void getResearchActivities() {
        assertNotNull(dsOnlyStaffMember.getResearchActivities());
    }

    @Test
    void getResearchAreas() {
        assertNotNull(dsOnlyStaffMember.getResearchAreas());
    }

    @Test
    void addDSProject_dsOnlyStaff() {
        Project p1 = new Project("Project1", dsOnlyStaffMember, Project.Type.DS);
        assertDoesNotThrow(() -> dsOnlyStaffMember.addProposedProject(p1), "DS staff can propose DS projects");
        assertTrue(dsOnlyStaffMember.getProposedProjects().contains(p1));
    }

    @Test
    void addCSProject_dsOnlyStaff() {
        Project p1 = new Project("Project1", new StaffMember(), Project.Type.CS);
        assertThrows(IllegalArgumentException.class, () -> dsOnlyStaffMember.addProposedProject(p1), "DS staff cannot propose CS projects");
        assertFalse(dsOnlyStaffMember.getProposedProjects().contains(p1));
    }

    @Test
    void addDsProject_csStaff() {
        Project p1 = new Project("Project1", csStaffMember, Project.Type.DS);
        assertThrows(IllegalArgumentException.class, () -> csStaffMember.addProposedProject(p1), "Non-Ds members cannot propose dsOnly projects");
        assertFalse(csStaffMember.getProposedProjects().contains(p1));
    }

    @Test
    void addCsProject_csStaff() {
        Project p1 = new Project("Project1", csStaffMember, Project.Type.CS);
        assertDoesNotThrow(() -> csStaffMember.addProposedProject(p1), "CS staff can propose cs projects");
        assertTrue(csStaffMember.getProposedProjects().contains(p1));
    }

    @Test
    void addCsDsProject_csStaff() {
        Project p1 = new Project("Project1", csStaffMember, Project.Type.CSDS);
        assertDoesNotThrow(() -> csStaffMember.addProposedProject(p1), "CS staff can propose cs+ds projects");
        assertTrue(csStaffMember.getProposedProjects().contains(p1));
    }
}