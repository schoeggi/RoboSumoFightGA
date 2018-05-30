package org.dyn4j.fhnw;

import java.util.LinkedList;

import org.dyn4j.dynamics.World;
import org.dyn4j.samples.framework.SimulationBody;

public class RobotPopulation {

	Robot[] robots;
	Robot[] robotsPreviousGeneration;
	Robot fittest;
	Robot secondFittest;
	Robot thirdFittest;
	private int numberOfActiveRobots;
	private static int generation = 0;
	// private static LinkedList<Robot> robots = new LinkedList<Robot>();
	// private static LinkedList<Robot> robotsToDelete = new LinkedList<Robot>();

	public RobotPopulation(int populationSize, boolean initialise) {
		robots = new Robot[populationSize];
		if (initialise) {
			// Loop and create individuals
			for (int i = 0; i < populationSize; i++) {
				Robot robot = new Robot();
				robot.setRobotNr(i);
				robot.setPopulation(this);
				saveRobot(i, robot);
			}
		}

		// System.out.println("Generation: " +generation);
	}

	/* Getters */
	public Robot getRobot(int index) {
		return robots[index];
	}

	private Robot calcFittest() {
		this.fittest = getLastActiveRobot();
		return this.fittest;
	}

	private Robot calcSecondFittest() {
		int lowestFitnessValue = 0;
		int robotIDtoReturn = 99;
		// Loop through individuals to find fittest
		for (int i = 0; i < size(); i++) {
			if (fittest.getRobotNr() != i) {		
				if (getRobot(i).getFitness() >= lowestFitnessValue) {
					lowestFitnessValue = getRobot(i).getFitness();
					robotIDtoReturn = i;
				}
			}
		}
		this.secondFittest = getRobot(robotIDtoReturn);
		return this.secondFittest;
	}

	private Robot calcThirdFittest() {
		int lowestFitnessValue = 0;
		int robotIDtoReturn = 99;
		// Loop through individuals to find fittest
		for (int i = 0; i < size(); i++) {
			if (fittest.getRobotNr() != i && secondFittest.getRobotNr() != i) {		
				if (getRobot(i).getFitness() >= lowestFitnessValue) {
					lowestFitnessValue = getRobot(i).getFitness();
					robotIDtoReturn = i;
				}
			}
		}
		this.thirdFittest = getRobot(robotIDtoReturn);
		return this.thirdFittest;
	}

	public int getNumberOfActiveRobots() {
		int counter = 0;
		for (int i = 0; i < size(); i++) {
			if (getRobot(i).isActive()) {
				counter++;
			}
		}
		return counter;
	}
	
	public Robot getLastActiveRobot() {
		int robotID = 0;
		for (int i = 0; i < size(); i++) {
			if (getRobot(i).isActive()) {
				robotID = getRobot(i).getRobotNr();
			}
		}
		if(getNumberOfActiveRobots() > 1) {
			System.out.println("Error, more than 1 lastActiveRobot");
		}
		
		return getRobot(robotID);
	}


	public boolean fightEnded() {
		if (getNumberOfActiveRobots() <= 1) {
			calcFittest();
			calcSecondFittest();
			calcThirdFittest();
			return true;
		}
		return false;
	}

	/* Public methods */
	// Get population size
	public int size() {
		return robots.length;
	}

	// Save individual
	public void saveRobot(int index, Robot indiv) {
		robots[index] = indiv;
	}

	/*
	 * public void inactivateRobotPopulation() { for (int i = 0; i < size(); i++) {
	 * world.removeBody(getRobot(i)); } robotsPreviousGeneration = robots; robots =
	 * null; }
	 */

	public void deleteRobot(int index) {
		robots[index] = null;
	}

	public void deletePopulation() {
		for (int i = 0; i < size(); i++) {
			robots[i] = null;
		}
	}

	public static synchronized int getGeneration() {
		return generation;
	}

	public synchronized static void setGeneration(int Newgeneration) {
		generation = Newgeneration;
	}

	public synchronized Robot getFittest() {
		
		return fittest;
	}

	public synchronized Robot getSecondFittest() {
		return secondFittest;
	}

	public synchronized Robot getThirdFittest() {
		return thirdFittest;
	}

	public synchronized static void increaseGeneration() {
		generation++;
	}

	public synchronized static void resetGenerationCounter() {
		generation = 0;
	}

}
