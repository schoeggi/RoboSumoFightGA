package org.dyn4j.fhnw;

public class Algorithm {

	/* GA parameters */
	private static final double uniformRate = 0.5;
	private static double mutationRate = 0.015; // initial Value 0.015
	private static boolean elitism = true;



	// Evolve a population
	public static RobotPopulation evolvePopulation(RobotPopulation oldPopulation) {
		RobotPopulation newPopulation = new RobotPopulation(oldPopulation.size(), true);
		
		//System.out.println("mutation Rate: " + mutationRate);

		// Keep our best individual
		if (elitism) {
			newPopulation.getRobot(0).setAllGenes(oldPopulation.getFittest().getAllGenes());
		}

		// Crossover population
		int elitismOffset;
		if (elitism) {
			elitismOffset = 1;
		} else {
			elitismOffset = 0;
		}
		// Loop over the population size and pass genes to robots
		for (int i = elitismOffset; i < oldPopulation.size(); i++) {
			byte[] oldGenesRobot1 = oldPopulation.getFittest().getAllGenes();
			byte[] oldGenesRobot2 = oldPopulation.getSecondFittest().getAllGenes();
			//byte[] oldGenesRobot2 = oldPopulation.getThirdFittest().getAllGenes();
			byte[] newGenes = crossover(oldGenesRobot1, oldGenesRobot2);
			newPopulation.getRobot(i).setAllGenes(newGenes);
		}

		// Mutate population
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getRobot(i));
		}

		for (int i = 0; i < newPopulation.size(); i++) {
		newPopulation.getRobot(i).setRobotNr(i);
		newPopulation.getRobot(i).setPopulation(newPopulation);
		}
		
		//oldPopulation.deletePopulation();
		return newPopulation;
	}

	// Crossover individuals
	private static byte[] crossover(byte[] robot1, byte[] robot2) {
		byte[] newSol = new byte[robot1.length];
		// Loop through genes
		for (int i = 0; i < robot1.length; i++) {
			// Crossover
			if (Math.random() <= uniformRate) {
				newSol[i] = robot1[i];
			} else {
				newSol[i] = robot2[i];
			}
		}
		return newSol;
	}

	// Mutate an individual
	private static void mutate(Robot robot) {
		// Loop through genes
		for (int i = 0; i < robot.size(); i++) {
			if (Math.random() <= mutationRate) {
				// Create random gene
				byte gene = (byte) (1 + Math.round(Math.random() * (robot.getMaxGeneValue() - 1)));
				robot.setGene(i, gene);
			}
		}
	}
	
	/* Public methods */

	public static boolean getElitism() {
		return elitism;
	}
	
	public static void setElitism(boolean useElitism) {
		elitism = useElitism;
	}
	
	public static void setMutationRate(double newRate) {
		mutationRate = newRate / 100;
	}
}