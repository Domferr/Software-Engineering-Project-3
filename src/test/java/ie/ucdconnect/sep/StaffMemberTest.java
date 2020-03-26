package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffMemberTest {

    private StaffMember staffMember;

    @BeforeEach
    void setUp() {
        String[] researchAreas = {"Acting","Hollywood","Serious Acting"};
        String[] researchActivities = {"performing serious acting","winning Oscars"};
        staffMember = new StaffMember("Name", researchActivities, researchAreas, true);
    }

    @Test
    void getResearchActivities() {
        assertNotNull(staffMember.getResearchActivities());
        assertTrue(staffMember.getResearchActivities().length > 0);
    }

    @Test
    void getResearchAreas() {
        assertNotNull(staffMember.getResearchAreas());
        assertTrue(staffMember.getResearchAreas().length > 0);
    }
}