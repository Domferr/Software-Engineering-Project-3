package ie.ucdconnect.sep.generators;

import ie.ucdconnect.sep.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** This class implements the Genetic algorithm. It implements SolutionGenerationStrategy interface,
 *  so call the generate() method to run the algorithm.
 *  */

public class GeneticAlgorithm implements SolutionGenerationStrategy {

    // The number of generations to be run before a solution is returned.
    private static final int NUM_GENERATIONS = 10000;
    // The number of solutions in each generation.
    private static final int GENERATION_SIZE = 250;
    // The number of "bad" solutions that will be removed at the end of each generation.
    private static final int GENERATION_CULL = 150;

    @Override
    public Solution generate(List<Project> projects, List<Student> students, double GPA_IMPORTANCE) {
        List<Solution> solutions = Utils.getRandomSolutionList(projects, students, GENERATION_SIZE, GPA_IMPORTANCE);
        for (int i = 0; i < NUM_GENERATIONS; i++) {
            System.out.println("Running generation: " + i);
            SolutionAcceptor.screenSolutions(solutions, GENERATION_CULL);
            mutate(solutions, projects);
        }
        SolutionAcceptor.screenSolutions(solutions, GENERATION_CULL);
        return solutions.get(0);
    }

    /** Mutates the given list of solutions */
    private static void mutate(List<Solution> solutions, List<Project> projects) {
        while (solutions.size() < GENERATION_SIZE) {
            int randomIndex = (int) (Math.random() * solutions.size());
            Solution randomSolution = solutions.get(randomIndex);
            Solution mutatedSolution = Solution.SolutionFactory.createByMutating(randomSolution, projects);

            solutions.add(mutatedSolution);
        }
    }
}
