package ie.ucdconnect.sep;

import ie.ucdconnect.sep.Solution.SolutionFactory;

import java.util.List;
import java.util.Random;

/** This class implements the Simulated Annealing algorithm.
 *  It implements SolutionGenerationStrategy interface
 *  so call the generate() method to run the algorithm.
 *  */
public class SimulatedAnnealing implements SolutionGenerationStrategy {

    // The number of iterations that SA algorithm will do before lowering the temperature
    private static final double MINIMA_NUM_ITERATIONS = 100;
    // How much the starting temperature should be higher than the maxEnergyDelta
    private static final double MAX_TEMPERATURE_INCREASE = 10;

    /** Runs the Simulated Annealing Algorithm and returns the generated solution */
    @Override
    public Solution generate(List<Project> projects, List<Student> students, double GPA_IMPORTANCE) {
        //Calculate max energy delta in 100 random solutions
        List<Solution> randomSolutions = Utils.getRandomSolutionList(projects, students, 100, GPA_IMPORTANCE);
        double maxEnergyDelta = calculateMaxEnergyDelta(randomSolutions);
        //Set the initial temperature as higher than MaxEnergyDelta
        double temperature = maxEnergyDelta + (MAX_TEMPERATURE_INCREASE * Math.random());
        //Generate one random currentSolution
        Solution currentSolution = new RandomGeneration().generate(projects, students, GPA_IMPORTANCE);
        Solution bestSolution = currentSolution;
        while(temperature > 0) {
            //Find a local minima
            for (int i = 0; i < MINIMA_NUM_ITERATIONS; i++) {
                Solution mutatedSolution = SolutionFactory.createByMutating(currentSolution, projects);
                double deltaEnergy = Math.abs(mutatedSolution.getEnergy() - currentSolution.getEnergy());
                //Accepting if the changes have better energy
                if (mutatedSolution.getEnergy() < currentSolution.getEnergy())
                    currentSolution = mutatedSolution;
                //Otherwise accept with Boltzmann probability
                else if (Math.random() <= Math.exp(-deltaEnergy/temperature))
                    currentSolution = mutatedSolution;
            }
            //Remember the global minima
            if (currentSolution.getEnergy() < bestSolution.getEnergy())
                bestSolution = currentSolution;
            //Lower the temperature based on how much high it is
            temperature -= temperature*Math.random(); //The higher it is the more it is lowered
        }

        return bestSolution;
    }

    /** Returns the max energy delta calculated from the given list of solutions */
    private static double calculateMaxEnergyDelta(List<Solution> solutions) {
        double maxDelta = 0;

        for (Solution thisSolution :solutions) {
            for (Solution otherSolution :solutions) {
                if (!thisSolution.equals(otherSolution)) {
                    double delta = Math.abs(thisSolution.getEnergy() - otherSolution.getEnergy());
                    if (delta > maxDelta)
                        maxDelta = delta;
                }
            }
        }

        return maxDelta;
    }
}
