package ie.ucdconnect.sep.generators;

import com.google.common.collect.ImmutableMultimap;
import ie.ucdconnect.sep.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneticAlgorithmTest {

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
    void generate_no_constraint_violation() {
        Solution solution = new GeneticAlgorithm().generate(projects, students);
        for(Project project : projects){
            if(solution.getAssignedStudents(project).size() > 1){
                fail("A constraint in the genetic algorithm has been violated");
            }
        }
    }
    


}