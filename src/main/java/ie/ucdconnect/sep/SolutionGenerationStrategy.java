package ie.ucdconnect.sep;

import java.util.List;

public interface SolutionGenerationStrategy {
    /** Runs an algorithm to generate a solution */
    Solution generate(List<Project> projects, List<Student> students);
}
