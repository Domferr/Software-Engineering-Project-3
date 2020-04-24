package ie.ucdconnect.sep.generators;

import ie.ucdconnect.sep.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** This class implements the Genetic algorithm. It implements SolutionGenerationStrategy interface,
 *  so call the generate() method to run the algorithm.
 *  */

public class GeneticAlgorithm implements SolutionGenerationStrategy {

    // The number of solutions in each generation.
    private static final int GENERATION_SIZE = 600;    //P
    // The number of "good" solutions that are allowed to mate and reproduce
    private static final int TOP_SOLUTIONS = 180;       //N
    // The number of "bad" solutions that will be removed at the end of each generation.
    private static final int GENERATION_CULL = 360;     //M
    // Number of no consecutive improvements to terminate the algorithm
    private static final int MAX_PLATEAU = 100;          //R

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
            List<Solution> reproduced = reproduce(solutions, projects, students);
            solutions.addAll(reproduced);

            Solution currentBest = solutions.get(0); //Best solution of this generation
            plateau = lastBest.getFitness() >= currentBest.getFitness() ? plateau+1 : 0;
            lastBest = currentBest;
            genCounter++;
            System.out.printf("Running generation: %d. Energy: %.2f. Fitness: %.2f. plateau: %d \n", genCounter, currentBest.getEnergy(), currentBest.getFitness(), plateau);
        }

        solutions.sort(SolutionAcceptor::compareByEnergy);
        return solutions.get(0);
    }

    /** Replace the worst solutions with a given list best solutioms */
    private void cullBottom(List<Solution> solutions) {
        for (int i = GENERATION_SIZE - GENERATION_CULL; i < GENERATION_SIZE; i++) {
            solutions.remove(solutions.size() - 1);
        }
    }

    private List<Solution> reproduce(List<Solution> solutions, List<Project> projects, List<Student> students) {
        List<Solution> reproduced = reproduceTop(solutions, projects, students);
        //Randomly mate and reproduce
        while (reproduced.size() < GENERATION_CULL) {
            reproduced.add(reproduceRandomly(solutions, projects, students));
        }
        return reproduced;
    }

    /** Reproduces the TOP_SOLUTIONS and returns the created list */
    private List<Solution> reproduceTop(List<Solution> solutions, List<Project> projects, List<Student> students) {
        List<Solution> reproduced = new ArrayList<>(GENERATION_CULL);
        int firstIndex = 0;
        int secondIndex = 1;
        //Mate and reproduce consecutive couples from the top solutions
        while (secondIndex < TOP_SOLUTIONS-1 && reproduced.size() < GENERATION_CULL) {
            Solution firstSolution = solutions.get(firstIndex);
            Solution secondSolution = solutions.get(secondIndex);
            reproduced.add(Solution.SolutionFactory.createByMating(firstSolution, secondSolution, projects, students));

            firstIndex++;
            secondIndex++;
        }

        return reproduced;
    }

    private Solution reproduceRandomly(List<Solution> solutions, List<Project> projects, List<Student> students) {
        int secondIndex;
        int firstIndex = getRandomInteger(0, TOP_SOLUTIONS);
        if (firstIndex == TOP_SOLUTIONS - 1 || Math.random() < 0.5)
            secondIndex = getRandomInteger(0, firstIndex);
        else
            secondIndex = getRandomInteger(firstIndex+1, TOP_SOLUTIONS);

        return Solution.SolutionFactory.createByMating(solutions.get(firstIndex), solutions.get(secondIndex), projects, students);
    }

    /** Returns a random integer between min (included) and max (excluded) */
    private int getRandomInteger(int min, int max) {
        return (int) (Math.random() * (max-min)) + min;
    }

    /** Mutates the given list of solutions */
    private void mutate(List<Solution> solutions, List<Project> projects) {
        while (solutions.size() < GENERATION_SIZE) {
            int randomIndex = (int) (Math.random() * solutions.size());
            Solution randomSolution = solutions.get(randomIndex);
            Solution mutatedSolution = Solution.SolutionFactory.createByMutating(randomSolution, projects);

            solutions.add(mutatedSolution);
        }
    }
}
