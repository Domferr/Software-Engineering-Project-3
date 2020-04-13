package ie.ucdconnect.sep;

import ie.ucdconnect.sep.Solution.SolutionFactory;

import java.util.List;

/** This class implements the Simulated Annealing algorithm.
 *  It implements SolutionGenerationStrategy interface
 *  so call the generate() method to run the algorithm.
 *  */
public class SimulatedAnnealing implements SolutionGenerationStrategy {
    // The number of iterations that SA algorithm will do before lowering the temperature
    private static final double MINIMA_NUM_ITERATIONS = 100;
    // How much the starting temperature should be higher than the maxEnergyDelta
    private static final double MAX_TEMPERATURE_INCREASE = 1000;
    // How much the temperature will be lowered
    private static final double COOLING_RATE = 0.005;
    // The lowest value that the temperature can reach
    private static final double MINIMUM_TEMPERATURE = 0.006;

    /** Runs the Simulated Annealing Algorithm and returns the generated solution */
    @Override
    public Solution generate(List<Project> projects, List<Student> students, double GPA_IMPORTANCE) {
        //Calculate max energy delta in 100 random solutions
        List<Solution> randomSolutions = Utils.getRandomSolutionList(projects, students, 100, GPA_IMPORTANCE);
        double maxEnergyDelta = calculateMaxEnergyDelta(randomSolutions);
        double temperature = maxEnergyDelta + MAX_TEMPERATURE_INCREASE;

        Solution currentSolution = new RandomGeneration().generate(projects, students, GPA_IMPORTANCE);
        Solution bestSolution = currentSolution;

        while(temperature > MINIMUM_TEMPERATURE) {
            System.out.printf("Current energy: %.2f Best energy: %.2f Temperature: %.2f\n", currentSolution.getEnergy(), bestSolution.getEnergy(), temperature);

            //Find a local minima
            for (int i = 0; i < MINIMA_NUM_ITERATIONS; i++) {
                Solution mutatedSolution = SolutionFactory.createByMutating(currentSolution, projects);
                double deltaEnergy = Math.abs(mutatedSolution.getEnergy() - currentSolution.getEnergy());

                if (mutatedSolution.getEnergy() < currentSolution.getEnergy())
                    currentSolution = mutatedSolution;
                else if (Math.random() <= Math.exp(-deltaEnergy/temperature))
                    currentSolution = mutatedSolution;
            }

            if (currentSolution.getEnergy() < bestSolution.getEnergy())
                bestSolution = currentSolution;

            //Lower the temperature based on the cooling rate
            temperature *= 1 - COOLING_RATE;
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
