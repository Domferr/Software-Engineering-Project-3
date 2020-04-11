package ie.ucdconnect.sep;

import java.util.List;

public class SolutionAcceptor {

	/**
	 * Sorts the solutions by energy, then removes a given number of "bad" ones. Returns the solutions sorted by fitness
	 */
	public static List<Solution> screenSolutions(List<Solution> solutions, int numToBeRemoved) {
		solutions.sort(SolutionAcceptor::compareByEnergy);
		List<Solution> newSolutions = solutions.subList(0, solutions.size() - numToBeRemoved);
		newSolutions.sort(SolutionAcceptor::compareByFitness);
		return newSolutions;
	}

	/**
	 * Sorts low to high
	 */
	private static int compareByEnergy(Solution a, Solution b) {
		return Double.compare(a.getEnergy(), b.getEnergy());
	}

	/**
	 * Sorts high to low
	 */
	private static int compareByFitness(Solution a, Solution b) {
		return Double.compare(b.getFitness(), a.getFitness());
	}

	/**
	 * Compares the two solutions and returns the better one in terms of energy
	 */
	private static Solution getBetterByEnergy(Solution a, Solution b) {
		if (a.getEnergy() < b.getEnergy())
			return a;
		return b;
	}

	/**
	 * Compares the two solutions and returns the better one in terms of fitness
	 */
	private static Solution getBetterByFitness(Solution a, Solution b) {
		if (a.getFitness() > b.getFitness())
			return a;
		return b;
	}
}
