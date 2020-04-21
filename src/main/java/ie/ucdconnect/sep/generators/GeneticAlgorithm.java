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
    private static final int GENERATION_SIZE = 1000;    //P
    // The number of "good" solutions that are allowed to mate and reproduce
    private static final int TOP_SOLUTIONS = 100;       //N
    // The number of "bad" solutions that will be removed at the end of each generation.
    private static final int GENERATION_CULL = 200;     //M
    // Number of no consecutive improvements to terminate the algorithm
    private static final int MAX_PLATEAU = 180;          //R

    @Override
    public Solution generate(List<Project> projects, List<Student> students, double GPA_IMPORTANCE) {
        List<Solution> solutions = Utils.getRandomSolutionList(projects, students, GENERATION_SIZE, GPA_IMPORTANCE);
        int plateau = 0;
        int genCounter = 1;
        Solution currentBest = solutions.get(0);    //The best solution of the current generation
        Solution lastBest = currentBest;            //The best solution of the last generation
        while (plateau < MAX_PLATEAU) {
            solutions.sort(SolutionAcceptor::compareByEnergy); //Sorts from best to worst - Ranking
            currentBest = solutions.get(0); //Best solution of this generation
            System.out.printf("Running generation: %d. Energy: %.2f. Fitness: %.2f. plateau: %d \n", genCounter, currentBest.getEnergy(), currentBest.getFitness(), plateau);
            List<Solution> reproduced = reproduceTop(solutions, projects, GPA_IMPORTANCE);  //Mate and reproduce the top - Mating
            //Randomly mate and reproduce
            while (reproduced.size() < GENERATION_CULL) {
                reproduced.add(reproduceRandomly(solutions, projects, GPA_IMPORTANCE));
            }
            cullBottom(solutions, reproduced);  //Cull the bottom - Culling

            plateau = lastBest.getFitness() >= currentBest.getFitness() ? plateau+1 : 0;
            lastBest = currentBest;
            genCounter++;
        }

        solutions.sort(SolutionAcceptor::compareByEnergy);
        return solutions.get(0);
    }

    /** Replace the worst solutions with a given list best solutioms */
    private void cullBottom(List<Solution> allSolutions, List<Solution> reproducedSolutions) {
        int i = 1;
        while (i < GENERATION_CULL) {
            int badSolutionIndex = allSolutions.size() - i;
            allSolutions.set(badSolutionIndex, reproducedSolutions.get(i-1));
            i++;
        }
    }

    /** Reproduces the TOP_SOLUTIONS and returns the created list */
    private List<Solution> reproduceTop(List<Solution> solutions, List<Project> projects, double gpaImportance) {
        List<Solution> reproduced = new ArrayList<>(GENERATION_CULL);
        int firstIndex = 0;
        int secondIndex = 1;
        //Mate and reproduce consecutive couples from the top solutions
        while (secondIndex < TOP_SOLUTIONS-1 && reproduced.size() < GENERATION_CULL) {
            Solution firstSolution = solutions.get(firstIndex);
            Solution secondSolution = solutions.get(secondIndex);
            reproduced.add(Solution.SolutionFactory.createByMating(firstSolution, secondSolution, projects, gpaImportance));

            firstIndex++;
            secondIndex++;
        }

        return reproduced;
    }

    private Solution reproduceRandomly(List<Solution> solutions, List<Project> projects, double gpaImportance) {
        int secondIndex;
        int firstIndex = getRandomInteger(0, TOP_SOLUTIONS);
        if (firstIndex == TOP_SOLUTIONS - 1 || Math.random() < 0.5)
            secondIndex = getRandomInteger(0, firstIndex);
        else
            secondIndex = getRandomInteger(firstIndex+1, TOP_SOLUTIONS);

        return Solution.SolutionFactory.createByMating(solutions.get(firstIndex), solutions.get(secondIndex), projects, gpaImportance);
    }

    /** Returns a random integer between min (included) and max (excluded) */
    private int getRandomInteger(int min, int max) {
        return (int) (Math.random() * (max-min)) + min;
    }

    /** Mutates the given list of solutions */
    private void mutate(List<Solution> solutions, List<Project> projects, double gpaImportance) {
        while (solutions.size() < GENERATION_SIZE) {
            int randomIndex = (int) (Math.random() * solutions.size());
            Solution randomSolution = solutions.get(randomIndex);
            Solution mutatedSolution = Solution.SolutionFactory.createByMutating(randomSolution, projects, gpaImportance);

            solutions.add(mutatedSolution);
        }
    }
}
