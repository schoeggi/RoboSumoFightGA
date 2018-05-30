package org.dyn4j.fhnw;

public class Algorithm {

	/* GA parameters */
	private static final double uniformRate = 0.5;
	private static double mutationRate = 0.015; // initial Value 0.015
	private static boolean elitism = true;

	// Evolve a population
	public static RobotPopulation evolvePopulation(RobotPopulation oldPopulation) {
		RobotPopulation newPopulation = new RobotPopulation(oldPopulation.size(), true);

		// System.out.println("mutation Rate: " + mutationRate);

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

		//elitistCrossover(elitismOffset, oldPopulation, newPopulation);
		rouletWheelCrossover(elitismOffset, oldPopulation, newPopulation);

		// Mutate population
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getRobot(i));
		}

		for (int i = 0; i < newPopulation.size(); i++) {
			newPopulation.getRobot(i).setRobotNr(i);
			newPopulation.getRobot(i).setPopulation(newPopulation);
		}

		// oldPopulation.deletePopulation();
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

	public static void elitistCrossover(int elitismOffset, RobotPopulation oldPopulation,
			RobotPopulation newPopulation) {
		// Loop over the population size and pass genes to robots
		for (int i = elitismOffset; i < oldPopulation.size(); i++) {
			byte[] oldGenesRobot1 = oldPopulation.getFittest().getAllGenes();
			byte[] oldGenesRobot2 = oldPopulation.getSecondFittest().getAllGenes();
			// byte[] oldGenesRobot2 = oldPopulation.getThirdFittest().getAllGenes();
			byte[] newGenes = crossover(oldGenesRobot1, oldGenesRobot2);
			newPopulation.getRobot(i).setAllGenes(newGenes);
		}
	}

	public static void rouletWheelCrossover(int elitismOffset, RobotPopulation oldPopulation,
			RobotPopulation newPopulation) {
		int sumFitness = 0;
		for (int i = elitismOffset; i < oldPopulation.size(); i++) {
			sumFitness = sumFitness + oldPopulation.getRobot(i).getFitness();
		}

		double upperProbabilityBound = 0;
		double lowerProbabilityBound = 0;
		double robotFitnessValue;
		double sumFitnessReduced = sumFitness;

		for (int i = elitismOffset; i < oldPopulation.size(); i++) {
			robotFitnessValue = oldPopulation.getRobot(i).getFitness();
			upperProbabilityBound = sumFitnessReduced;
			lowerProbabilityBound = sumFitnessReduced - robotFitnessValue;
			oldPopulation.getRobot(i).setUpperProbabilityBound(upperProbabilityBound);
			oldPopulation.getRobot(i).setLowerProbabilityBound(lowerProbabilityBound);
			sumFitnessReduced = sumFitnessReduced - robotFitnessValue;
		}
		
		if (sumFitnessReduced == 0) {
			System.out.println("Splitting up fitness values worked out, sumFitnessReduced = 0");
		}
		// Loop over the population size and pass genes to robots
		
		byte[] oldGenesRobot1;
		byte[] oldGenesRobot2;

		for (int robot1 = elitismOffset; robot1 < oldPopulation.size(); robot1++) {
			double randomCrossoverValue = Math.random() * sumFitness;				
			oldGenesRobot1 = oldPopulation.getRobot(robot1).getAllGenes();
			oldGenesRobot2 = oldPopulation.getRobot(robot1).getAllGenes();
			for (int robot2 = elitismOffset; robot2 < oldPopulation.size(); robot2++) {
				if (robot1 != robot2) {
					lowerProbabilityBound = oldPopulation.getRobot(robot2).getLowerProbabilityBound();
					upperProbabilityBound = oldPopulation.getRobot(robot2).getUpperProbabilityBound();
					if (randomCrossoverValue >= lowerProbabilityBound && randomCrossoverValue < upperProbabilityBound) {
						oldGenesRobot2 = oldPopulation.getRobot(robot2).getAllGenes();
						double probability = (double) (oldPopulation.getRobot(robot2).getFitness()) / sumFitness;
						System.out.println("Crossover the following robots: " +oldPopulation.getRobot(robot1).getRobotNr() + " and " +oldPopulation.getRobot(robot2).getRobotNr() + " probability striked: " +probability);

						//System.out.println("Roulet Wheel selection matched! " +randomCrossoverValue);
					}
					else {
						//System.out.println("NOT //// Roulet Wheel selection matched! " +randomCrossoverValue);
					}
				}
			}
			byte[] newGenes = crossover(oldGenesRobot1, oldGenesRobot2);
			newPopulation.getRobot(robot1).setAllGenes(newGenes);
		}
		
	}

}