package ie.ucdconnect.sep.generators;

import com.google.common.collect.ImmutableMultimap;
import ie.ucdconnect.sep.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedAnnealingTest {

    private Solution randSolution;
    private Solution annealingSolution;
    private List<Project> projects;
    private List<Student> students;

    @BeforeEach
    void setUp() throws IOException {
        int[] testSetsStudentsSize = Config.getInstance().getTestSetsStudentsSize();
        int test_size = testSetsStudentsSize[1];
        List<StaffMember> staffMembers = Utils.readStaffMembers();
        projects = Utils.readProjects(staffMembers, test_size);
        students = Utils.readStudents(Utils.generateProjectsMap(projects), test_size);
    }

    @Test
    void generate() {
        randSolution = new RandomGeneration().generate(projects, students, 1.0);
        annealingSolution = new SimulatedAnnealing().generate(projects, students, 1.0);
        assertTrue(annealingSolution.getEnergy() < randSolution.getEnergy());
        assertNotEquals(randSolution, annealingSolution);
        randSolution = new RandomGeneration().generate(projects, students, 0);
        annealingSolution = new SimulatedAnnealing().generate(projects, students, 0);
        assertTrue(annealingSolution.getEnergy() < randSolution.getEnergy());
    }


}