package ie.ucdconnect.sep.generators;

import ie.ucdconnect.sep.*;
import ie.ucdconnect.sep.Solution.SolutionFactory;

import java.util.List;

/** This class implements the Simulated Annealing algorithm.
 *  It implements SolutionGenerationStrategy interface
 *  so call the generate() method to run the algorithm.
 *  */
public class SimulatedAnnealing implements SolutionGenerationStrategy {
    // How much the starting temperature should be higher or less than the maxEnergyDelta
    private static final double STARTING_TEMPERATURE_MODIFIER = 400;
    // How much the temperature will be lowered
    private static final double COOLING_RATE = 0.42;
    // The lowest value that the temperature can reach
    private static final double MINIMUM_TEMPERATURE = 1;

    /** Runs the Simulated Annealing Algorithm and returns the generated solution */
    @Override
    public Solution generate(List<Project> projects, List<Student> students, double GPA_IMPORTANCE) {
        //Calculate max energy delta in 100 random solutions
        List<Solution> randomSolutions = Utils.getRandomSolutionList(projects, students, 100, GPA_IMPORTANCE);
        double maxEnergyDelta = calculateMaxEnergyDelta(randomSolutions);
        double startingTemperature = maxEnergyDelta + STARTING_TEMPERATURE_MODIFIER;

        Solution currentSolution = new RandomGeneration().generate(projects, students, GPA_IMPORTANCE);
        Solution bestSolution = currentSolution;
        int n = 1;
        double temperature = startingTemperature;
        while(temperature > MINIMUM_TEMPERATURE) {
            Solution mutatedSolution = SolutionFactory.createByMutating(currentSolution, projects, GPA_IMPORTANCE);

            double deltaEnergy = Math.abs(mutatedSolution.getEnergy() - currentSolution.getEnergy());

            if (mutatedSolution.getEnergy() < bestSolution.getEnergy())
                bestSolution = mutatedSolution;

            if (mutatedSolution.getEnergy() <= currentSolution.getEnergy()) {
                currentSolution = mutatedSolution;
            } else {
                if (Math.random() <= Math.exp(-deltaEnergy/temperature)) {
                    currentSolution = mutatedSolution;
                }
                System.out.printf("%d - Current energy: %.2f Best energy: %.2f Temperature: %.3f\n", n, currentSolution.getEnergy(), bestSolution.getEnergy(), temperature);
                //Lower the temperature
                temperature = startingTemperature * (Math.pow(1-COOLING_RATE, Math.log(n)));
            }
            n++;
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
