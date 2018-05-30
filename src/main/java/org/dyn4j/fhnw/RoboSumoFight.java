/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.fhnw;

// OBI for RAVENDUN

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

public final class RoboSumoFight extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -8518496343422955267L;
	private static RobotPopulation myPop;
	private static RobotPopulation initialPop;
	private static int RobotPopulationSize = 10;
	int generationCount = 0;

	public RoboSumoFight() {
		super("RoboSumoFight", 300.0);
	}

	@Override
	protected void initializeWorld() {
		// no gravity on a top-down view of a billiards game
		this.world.setGravity(World.ZERO_GRAVITY);

		Arena.createOctagon();
		initialPop = new RobotPopulation(RobotPopulationSize, true);

		createRobots(initialPop);

		for (int iWall = 0; iWall < Arena.getNumberOfWalls(); iWall++) {
			this.world.addBody(Arena.getWall(iWall));
		}

		for (int iWall = 0; iWall < Arena.getNumberOfWalls(); iWall++) {
			for (int iRobot = 0; iRobot < initialPop.size(); iRobot++) {
				this.world.addListener(
						new ArenaContactListener(initialPop.getRobot(iRobot), Arena.getWall(iWall), world));
			}
		}

		myPop = initialPop;
	}

	protected void resetSimulation() {
		clearContactListeners();
		clearRobots();
		initialPop = new RobotPopulation(RobotPopulationSize, true);
		createRobots(initialPop);
		myPop = initialPop;
		RobotPopulation.resetGenerationCounter();
	}

	protected void clearRobots() {
		for (int iRobot = 0; iRobot < myPop.size(); iRobot++) {

			if (myPop.getRobot(iRobot).isActive()) {
				myPop.getRobot(iRobot).setActive(false);
				this.world.removeBody(myPop.getRobot(iRobot));
				System.out.println("Removed Active Robot Nr:" + myPop.getRobot(iRobot).getRobotNr());
			}

		}
		// myPop.deleteRobot(myPop.getFittest().getRobotNr());
		// myPop.deleteRobot(iRobot);

		myPop.deletePopulation();
	}

	protected void changeMaxSpeedRobots() {
		for (int iRobot = 0; iRobot < myPop.size(); iRobot++) {
			if (myPop.getRobot(iRobot).isActive()) {
				myPop.getRobot(iRobot).resetSpeed();
				System.out.println("Reset Speed of active Robot Nr:" + myPop.getRobot(iRobot).getRobotNr());
			}
		}
	}

	protected void speedAccelrationForGUI(int speed) {
		for (int iRobot = 0; iRobot < myPop.size(); iRobot++) {
			if (myPop.getRobot(iRobot).isActive()) {
				myPop.getRobot(iRobot).speedAccelerationForGUIlocal(speed);
				//System.out.println("changed gui speed");
			}
		}
		Robot.speedAccelerationForGUI(speed);

	}

	protected void changeDirectionOfRobots() {
		for (int iRobot = 0; iRobot < myPop.size(); iRobot++) {
			if (myPop.getRobot(iRobot).isActive()) {
				myPop.getRobot(iRobot).setRobotMode("escape");
				myPop.getRobot(iRobot).setRobotStepsLocked(40);
				System.out.println("Set Robot to cruise mode, Nr:" + myPop.getRobot(iRobot).getRobotNr());
			}
		}
	}

	protected void clearContactListenersForPupulation(RobotPopulation oldPopulation) {
		for (int iWall = 0; iWall < Arena.getNumberOfWalls(); iWall++) {
			for (int iRobot = 0; iRobot < oldPopulation.size(); iRobot++) {
				this.world.removeListener(
						new ArenaContactListener(oldPopulation.getRobot(iRobot), Arena.getWall(iWall), world));
			}
		}
	}

	protected void clearContactListeners() {
		for (int iWall = 0; iWall < Arena.getNumberOfWalls(); iWall++) {
			for (int iRobot = 0; iRobot < myPop.size(); iRobot++) {
				this.world
						.removeListener(new ArenaContactListener(myPop.getRobot(iRobot), Arena.getWall(iWall), world));
			}
		}
	}

	protected void clearOldPopulationRobots(RobotPopulation oldPopulation) {
		clearContactListenersForPupulation(oldPopulation);
		// this.world.removeBody(oldPopulation.getFittest());
		// this.world.removeBody(oldPopulation.getSecondFittest());
		// oldPopulation.deletePopulation();
		for (int iRobot = 0; iRobot < oldPopulation.size(); iRobot++) {

			if (oldPopulation.getRobot(iRobot).isActive()) {
				oldPopulation.getRobot(iRobot).setActive(false);
				this.world.removeBody(oldPopulation.getRobot(iRobot));
				// System.out.println("RoboSumoFight: Removed Active Robot Nr:" +
				// oldPopulation.getRobot(iRobot).getRobotNr());
			}

		}
	}

	protected void printInactiveRobots() {
		for (int iRobot = 0; iRobot < myPop.size(); iRobot++) {
			if (!myPop.getRobot(iRobot).isActive()) {
				System.out.println("Inactive Robot Nr:" + myPop.getRobot(iRobot).getRobotNr());
			}
		}
		System.out.println("--------------------------------------");

	}

	protected void createRobots(RobotPopulation newPopulation) {
		for (int i = 0; i < newPopulation.size(); i++) {
			this.world.addBody(newPopulation.getRobot(i));
		}

		for (int iWall = 0; iWall < Arena.getNumberOfWalls(); iWall++) {
			for (int iRobot = 0; iRobot < newPopulation.size(); iRobot++) {
				this.world.addListener(
						new ArenaContactListener(newPopulation.getRobot(iRobot), Arena.getWall(iWall), world));
			}
		}
	}

	protected void moveRobots() {
		for (int i = 0; i < myPop.size(); i++) {
			if (myPop.getRobot(i).isActive()) {
				myPop.getRobot(i).moveRobot();
			}
		}
	}

	protected boolean fightEnded() {
		return myPop.fightEnded();
	}

	protected RobotPopulation getPopulation() {
		return initialPop;
	}

	protected void setPopulation(RobotPopulation myPop) {
		this.myPop = myPop;
		// this.myPop.increaseGeneration();
	}

	protected int getGenerationCounter() {
		return generationCount;
	}

	protected void increaseGeneration() {
		RobotPopulation.increaseGeneration();
		// System.out.println("Generation increased");

	}

	protected void setGenerationCounter(int generationCount) {
		this.generationCount = generationCount;
	}

	protected int getFittest() {
		return myPop.getFittest().getRobotNr();
	}

	protected int getSecondFittest() {
		return myPop.getSecondFittest().getRobotNr();
	}

	protected int getThirdFittest() {
		return myPop.getThirdFittest().getRobotNr();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		// move the view a bit
		g.translate(-200, 0);

		super.render(g, elapsedTime);
	}

	/**
	 * Entry point for the example application.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		RoboSumoFight simulation = new RoboSumoFight();
		simulation.run();
	}

	public static int getPopulationSize() {
		return RobotPopulationSize;
	}

}
