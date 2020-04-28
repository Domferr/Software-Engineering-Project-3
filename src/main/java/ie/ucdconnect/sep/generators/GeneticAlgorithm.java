package ie.ucdconnect.sep.generators;

import ie.ucdconnect.sep.*;

import java.util.ArrayList;
import java.util.List;

/** This class implements the Genetic algorithm. It implements SolutionGenerationStrategy interface,
 *  so call the generate() method to run the algorithm.
 *  */

public class GeneticAlgorithm implements SolutionGenerationStrategy {

    // The number of solutions in each generation.
    private static final int GENERATION_SIZE = 1000;    //P
    // The number of "good" solutions that are allowed to mate and reproduce
    private static final int TOP_SOLUTIONS = 150;       //N
    // The number of "bad" solutions that will be removed at the end of each generation.
    private static final int GENERATION_CULL = 450;     //M
    // Number of no consecutive improvements to terminate the algorithm
    private static final int MAX_PLATEAU = 120;          //R
    // The probability of a gene to be mutated
    private static final double MUTATION_PROBABILITY = 0.005;
    public static final String DISPLAY_NAME = "Genetic Algorithm";

    @Override
    public Solution generate(List<Project> projects, List<Student> students) {
        List<Solution> solutions = Utils.getRandomSolutionList(projects, students, GENERATION_SIZE);
        int plateau = 0;
        int genCounter = 0;
        Solution lastBest = solutions.get(0);            //The best solution of the last generation
        while (plateau < MAX_PLATEAU) {
            solutions.sort(SolutionAcceptor::compareByEnergy); // Sorts best to worst
            // Reproduces the top solution and substitutes the bad solutions
            reproduce(solutions, projects, students);

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

    /** Reproduces and then substitutes the bad solutions with the new generation */
    private void reproduce(List<Solution> solutions, List<Project> projects, List<Student> students) {
        int index = 1;
        //Randomly mate and reproduce
        while (index <= GENERATION_CULL && index < solutions.size()) {
            Solution children = reproduceRandomly(solutions, projects, students);
            //Replace bad solution with the new generation
            solutions.set(solutions.size()-index, children);
            index++;
        }
    }

    private Solution reproduceRandomly(List<Solution> solutions, List<Project> projects, List<Student> students) {
        int secondIndex;
        int firstIndex = Utils.getRandomInteger(0, TOP_SOLUTIONS);
        if (firstIndex == TOP_SOLUTIONS - 1 || Math.random() < 0.5)
            secondIndex = Utils.getRandomInteger(0, firstIndex);
        else
            secondIndex = Utils.getRandomInteger(firstIndex+1, TOP_SOLUTIONS);

        return Solution.SolutionFactory.createByMating(solutions.get(firstIndex), solutions.get(secondIndex), projects, students, MUTATION_PROBABILITY);
    }
}
