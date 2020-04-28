package ie.ucdconnect.sep.generators;

import ie.ucdconnect.sep.*;

import java.util.ArrayList;
import java.util.List;

/** This class implements the Genetic algorithm. It implements SolutionGenerationStrategy interface,
 *  so call the generate() method to run the algorithm.
 *  */

public class AsexualGeneticAlgorithm implements SolutionGenerationStrategy {
    // The number of solutions in each generation.
    private static final int GENERATION_SIZE = 1000;    //P
    // The number of "good" solutions that are allowed to mate and reproduce
    private static final int TOP_SOLUTIONS = 100;       //N
    // The number of "bad" solutions that will be removed at the end of each generation.
    private static final int GENERATION_CULL = 200;     //M
    // Number of no consecutive improvements to terminate the algorithm
    private static final int MAX_PLATEAU = 180;          //R
    public static final String DISPLAY_NAME = "Asexual Genetic Algorithm";

    @Override
    public Solution generate(List<Project> projects, List<Student> students) {
        List<Solution> solutions = Utils.getRandomSolutionList(projects, students, GENERATION_SIZE);
        int plateau = 0;
        int genCounter = 0;
        Solution lastBest = solutions.get(0);            //The best solution of the last generation
        while (plateau < MAX_PLATEAU) {
            solutions.sort(SolutionAcceptor::compareByEnergy); // Energy low to high
            cullBottom(solutions);  //Cull the bottom - Culling
            solutions.sort(SolutionAcceptor::compareByFitness); // Fitness high to low
            mutate(solutions, projects);

            Solution currentBest = solutions.get(0); //Best solution of this generation
            plateau = lastBest.getFitness() >= currentBest.getFitness() ? plateau+1 : 0;
            lastBest = currentBest;
            genCounter++;
            System.out.printf("Running generation: %d. Energy: %.2f. Fitness: %.2f. plateau: %d \n", genCounter, currentBest.getEnergy(), currentBest.getFitness(), plateau);
        }

        solutions.sort(SolutionAcceptor::compareByEnergy);
        return solutions.get(0);
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /** Replace the worst solutions with a given list best solutioms */
    private void cullBottom(List<Solution> solutions) {
        for (int i = GENERATION_SIZE - GENERATION_CULL; i < GENERATION_SIZE; i++) {
            solutions.remove(solutions.size() - 1);
        }
    }

    /** Mutates the given list of solutions */
    private void mutate(List<Solution> solutions, List<Project> projects) {
        while (solutions.size() < GENERATION_SIZE) {
            int randomIndex = (int) (Math.random() * TOP_SOLUTIONS);
            Solution randomSolution = solutions.get(randomIndex);
            Solution mutatedSolution = Solution.SolutionFactory.createByMutating(randomSolution, projects);

            solutions.add(mutatedSolution);
        }
    }
}
