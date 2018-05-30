package org.dyn4j.fhnw;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;

public class Robot extends SimulationBody {
	private double maxRadius = 0.028575;
	private double radius = maxRadius;

	private double density = 217.97925;
	private double friction = 0.0; // before 0.08
	private double restitution = 0.0; // before 0.9
	private double positionX;
	private double positionY;
	private int targetedOpponent = 9999;
	private final int maxGeneValue = 9;
	private double upperProbabilityBound = 0;	
	private double lowerProbabilityBound = 0;	

	private int robotGenerationNr = 0;
	private boolean hasTarget = false;
	private String robotMode = "undefined";
	static int defaultGeneLength = 6;
	private byte[] genes = new byte[defaultGeneLength];

	private double wallDetectionRange = 0.03;
 
	private static double speedAccelrationForGUI = 5;
	private double maxGeneticSpeed = 1;
	private double speedAccelerationForGUIlocal = speedAccelrationForGUI;
	private double maxSpeed = maxGeneticSpeed * speedAccelerationForGUIlocal;
	private double aggressiveness = 2;
	private double maxSpeedVariability = maxSpeed / 3;
	private double maxSearchRange = 0.5;
	private double drivingAccuracy = 9;
	
	private int robotModeStepsLocked = 0;
	private int robotModeStepsLockedDefault = 20;

	private int fitness = 0;
	RobotPopulation myPop;
	private int robotNr;

	public Robot() {
		for (int i = 0; i < size(); i++) {
			byte gene = (byte) (1 + (Math.round(Math.random() * (maxGeneValue -1))));
			genes[i] = gene;
			
			 switch (i) {
	            case 0:  maxGeneticSpeed = Math.round(gene * 0.5);
	                     break;
	            case 1:  maxSearchRange = gene / 5;
	                     break;
	            case 2:  wallDetectionRange = gene / 20;
                		 break;
	            case 3:	 radius = radius - ((double) gene) / 1000;
                		 //System.out.println("radius: " +radius);
	            		 break;
	            case 4:  robotModeStepsLockedDefault = gene * 3;
	            		 //System.out.println("StepsLockedDefault: " +robotModeStepsLockedDefault);
       		 			 break;		 
	            case 5:  //drivingAccuracy = maxSpeed * (gene / 2);
	            		 drivingAccuracy = gene;
	            	     // this.getFixtureCount() .setDensity(density);
	            		 //System.out.println("Density: " +this.getFixture(0).getDensity());

		 			 	 break;		 
			}
			
		}
		this.addFixture(Geometry.createCircle(radius), density, friction, restitution);
		this.setMass(MassType.NORMAL);
		this.positionX = getRandomStartX();
		this.positionY = getRandomStartY();
		this.maxSpeed = maxSpeed - (Math.random() * maxSpeedVariability);
		//System.out.println("Pos X: " + positionX);
		//System.out.println("Pos Y: " + positionY);
		this.translate(positionX, positionY);
		this.robotGenerationNr = RobotPopulation.getGeneration();
		// this.translate(Arena.getCenterX(), Arena.getCenterY());
		//System.out.println("maxSpeed robot Nr: " + robotNr + " " + maxSpeed);
	}

	public static double getRandomStartX() {
		double startX;
		double safetySpace = 0.15;
		// startX = Arena.getWallXmin() * 2 + (Math.random() * (Arena.getWallXDelta() /
		// 2));
		startX = Arena.getWallXmin() + safetySpace + (Math.random() * (Arena.getWallXDelta() - (2 * safetySpace)));
		return startX;
	}
	
	public void resetSpeed() {
		this.maxSpeed = maxSpeed - (Math.random() * maxSpeedVariability);
	}

	public static double getRandomStartY() {
		double startY;
		//double safetySpace = 0.15;
		//startY = Arena.getWallYmin() + safetySpace + (Math.random() * (Arena.getWallYDelta() - (2 * safetySpace)));
		startY = Arena.getWallYmin() / 2.5 + (Math.random() * (Arena.getWallYDelta() / 2.5));
		return startY;
	}

	/* Getters and setters */
	// Use this if you want to create individuals with different gene lengths
	public static void setDefaultGeneLength(int length) {
		defaultGeneLength = length;
	}

	public byte getGene(int index) {
		return genes[index];
	}
	
	public byte[] getAllGenes() {
		return genes;
	}

	public void setGene(int index, byte value) {
		genes[index] = value;
		fitness = 0;
	}
	
	public void setAllGenes(byte[] allGenes) {
		genes = allGenes;
		fitness = 0;
	}

	private void setCurrentPosition() {
		positionX = this.getTransform().getTranslationX();
		positionY = this.getTransform().getTranslationY();
		// this.getTransform().getTransformed(vector, destination);

	}

	public void moveRobot() {
	//	System.out.println("in contact of robot " + robotNr + " :" + this.getInContactBodies(false).size()
	//			+ " robotModeStepCounter: " + robotModeStepsLocked);

		setCurrentPosition();
		this.fitness++;

		if (checkIfArenaBoarderIsClose() && !robotMode.equals("attack") && robotModeStepsLocked <= 0) {
			robotMode = "survive";
			robotModeStepsLocked = robotModeStepsLockedDefault;
		}
		
		if (robotMode.equals("undefined") && robotModeStepsLocked <= 0) {
			robotMode = "cruise";
			robotModeStepsLocked = robotModeStepsLockedDefault;
		}

		if (this.getInContactBodies(false).size() >= aggressiveness && robotModeStepsLocked <= 0) {
			robotMode = "escape";
			robotModeStepsLocked = robotModeStepsLockedDefault;

		}

		if (!this.isActive() && robotModeStepsLocked <= 0) {
			robotMode = "delete";
		}

		

		if (hasTarget && robotModeStepsLocked <= 0) {
			robotMode = "attack";
			robotModeStepsLocked = robotModeStepsLockedDefault;
		}

		if (!hasTarget && robotMode.equals("cruise") && robotModeStepsLocked <= 0) {
			robotMode = "search";
			robotModeStepsLocked = robotModeStepsLockedDefault;
		}

		if (!hasTarget && robotMode.equals("survive") && robotModeStepsLocked <= 0) {
			robotMode = "cruise";
			robotModeStepsLocked = robotModeStepsLockedDefault;
		}

		switch (robotMode) {

		case "cruise":
			//System.out.println(+this.robotNr + ": Cruise Mode");
			cruise();
			robotModeStepsLocked--;
			break;
		case "escape":
			//System.out.println(+this.robotNr + ": Escape Mode");
			escape();
			robotModeStepsLocked--;
			break;

		case "attack":
			//System.out.println(+this.robotNr + ": Attack Mode");
			attackOpponent();
			robotModeStepsLocked--;
			break;

		case "search":
			//System.out.println(+this.robotNr + ": Search Mode");
			searchOpponent();
			robotModeStepsLocked--;
			break;

		case "survive":
			//System.out.println(+this.robotNr + ": survive Mode");
			driveToCenter();
			robotModeStepsLocked--;
			break;

		case "initial":
			this.setLinearVelocity(-0.1 + Math.random(), -0.1 + Math.random());
			robotModeStepsLocked--;

			break;

		case "delete":
			//System.out.println(+this.robotNr + ": delete Mode");
			robotModeStepsLocked--;

			break;
		}
	}

	private void moveToCenter() {
		// this.setLinearVelocity(this.getWorldCenter());
		//
		this.getTransform();
	}

	private void escape() {
		cruise();
		/*
		 * for (Body robot : this.getInContactBodies(true)) { if (); }
		 */
		// System.out.println("Espaced " + robotNr + " :" +
		// this.getInContactBodies(true).size());

	}

	public void searchOpponent() {
		// System.out.println("");
		// System.out.println("Entered searchOpponent for RobotNr: " + this.robotNr);

		double deltaX;
		double deltaY;
		double distance = 99;
		double distanceShortest = 99;
		hasTarget = false;

		for (int i = 0; i < RoboSumoFight.getPopulationSize(); i++) {
			if (this.robotNr != i && myPop.getRobot(i).isActive()) {
				deltaX = myPop.getRobot(i).getPositionX() - this.positionX;
				deltaY = myPop.getRobot(i).getPositionY() - this.positionY;
				distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
				//System.out.println("Distance:" + distance);

				if (distance < maxSearchRange && distance <= distanceShortest) {
					targetedOpponent = i;
					hasTarget = true;
					// System.out.println("Target of Robot: " + robotNr + " is: " +
					// targetedOpponent);
					distanceShortest = distance;
					// System.out.println("DistanceShortest:" + distanceShortest);
				}
			}
		}

		if (hasTarget == false) {
			// System.out.println(+this.robotNr + " ------------- no opponent found");
		}
	}

	private void driveToCenter() {
		driveToTarget(Arena.getCenterX(), Arena.getCenterY());
	}

	private void cruise() {
		int directionX = 1;
		int directionY = 1;
		if (Math.random() < 0.5) {
			directionX = -1;
		}
		;
		if (Math.random() < 0.5) {
			directionY = -1;
		}
		;

		double speedX = Math.random() * maxSpeed * directionX;
		double speedY = Math.sqrt(Math.pow(maxSpeed, 2) - Math.pow(speedX, 2)) * directionY;
		this.setLinearVelocity(speedX, speedY);
	}

	private void attackOpponent() {
		if (myPop.getRobot(targetedOpponent).isActive()) {
			double targetPositionX = myPop.getRobot(targetedOpponent).getPositionX();
			double targetPositionY = myPop.getRobot(targetedOpponent).getPositionY();
			driveToTarget(targetPositionX, targetPositionY);
		} else {
			//System.out.println(this.robotNr + " ---------------------------------- not active");
			// robotMode = "undefinded";
			hasTarget = false;
			robotMode = "survive";
			robotModeStepsLocked = robotModeStepsLockedDefault;
		}
	}

	public void driveToTarget(double targetPositionX, double targetPositionY) {

		double positionDeltaToOpponentX = Math.abs(this.positionX - targetPositionX);
		double positionDeltaToOpponentY = Math.abs(this.positionY - targetPositionY);
		double absoluteDistance = Math
				.sqrt(Math.pow(positionDeltaToOpponentX, 2) + Math.pow(positionDeltaToOpponentY, 2));
		double attackingDirectionX;
		double attackingDirectionY;
		int plusOrMinus;
		
		if (Math.round(Math.random()) == 0) {
			plusOrMinus = -1;
		}
		else {
			plusOrMinus = 1;
		}
		
		//double randomSpeedDelta = (Math.random() / (maxSpeed * 4)) * plusOrMinus;
		double randomSpeedDelta = (Math.random() / (drivingAccuracy)) * plusOrMinus;

		
		if (this.positionX < targetPositionX) {
			attackingDirectionX = 1 + randomSpeedDelta;
		} else {
			attackingDirectionX = -1 + randomSpeedDelta;
		}

		if (this.positionY < targetPositionY) {
			attackingDirectionY = 1 + randomSpeedDelta;
		} else {
			attackingDirectionY = -1 + randomSpeedDelta;
		}

		double fractionOverallSpeed = maxSpeed
				/ (Math.abs(positionDeltaToOpponentX) + Math.abs(positionDeltaToOpponentY));
		double speedX = fractionOverallSpeed * positionDeltaToOpponentX * attackingDirectionX;
		double speedY = fractionOverallSpeed * positionDeltaToOpponentY * attackingDirectionY;

		//speedX = speedX * (attackingDirectionX * (1 - randomSpeedDelta)); 
		//speedY = speedY * (attackingDirectionY * (1 - randomSpeedDelta)); 

		
		
		// double speedX = Math.sqrt(Math.pow(absoluteDistance, 2) -
		// Math.pow(positionDeltaToOpponentY,2)) * attackingDirectionX;
		// double speedY = Math.sqrt(Math.pow(absoluteDistance, 2) -
		// Math.pow(positionDeltaToOpponentX,2)) * attackingDirectionY;
		// double speedX = maxSpeed * (positionDeltaToOpponentX /
		// positionDeltaToOpponentY) * attackingDirectionX;
		// double speedY = maxSpeed * (positionDeltaToOpponentY /
		// positionDeltaToOpponentX) * attackingDirectionY;

		this.setLinearVelocity(speedX, speedY);
	}

	private boolean checkIfArenaBoarderIsClose() {
		boolean boarderIsClose = false;
		//System.out.println("Check boarder is close function");
		boarderIsClose = Arena.checkOutsideArena(this.positionX, this.positionY, this.radius + this.wallDetectionRange);
		//System.out.println("Value: " +boarderIsClose);

		return boarderIsClose;
	}

	/* Public methods */
	public int size() {
		return genes.length;
	}

	public int getFitness() {
		return fitness;
	}
	
	public void increaseGeneration() {
		robotGenerationNr++;
	}

	public int getRobotNr() {
		return robotNr;
	}

	public void setRobotNr(int robotNr) {
		this.robotNr = robotNr;
	}
	
	public void setRobotGenerationNr(int robotGenerationNr) {
		this.robotGenerationNr = robotGenerationNr;
	}
	
	public int getRobotGenerationNr() {
		return robotGenerationNr;
	}
	
	public void setRobotMode(String robotMode) {
		this.robotMode = robotMode;
		robotModeStepsLocked = robotModeStepsLockedDefault;
	}
	
	public void setRobotStepsLocked(int steps) {
		robotModeStepsLocked = steps;
	}
	
	public void setPopulation(RobotPopulation myPop) {
		this.myPop = myPop;
	}
	
	public static void speedAccelerationForGUI(int speed) {
		speedAccelrationForGUI = speed;
	}
	
	public void speedAccelerationForGUIlocal(int speed) {
		this.speedAccelerationForGUIlocal = speed;
		maxSpeed = maxGeneticSpeed * speedAccelerationForGUIlocal;
	}
	

	public double getPositionX() {
		return positionX;
	}

	public double getPositionY() {
		return positionY;
	}
	
	public int getMaxGeneValue() {
		return maxGeneValue;
	}
	
	public double getUpperProbabilityBound() {
		return upperProbabilityBound;
	}
	
	public double getLowerProbabilityBound() {
		return lowerProbabilityBound;
	}
	
	public void setUpperProbabilityBound(double value) {
		this.upperProbabilityBound = value;
	}
	
	public void setLowerProbabilityBound(double value) {
		this.lowerProbabilityBound = value;
	}
	
	

	@Override
	public String toString() {
		String geneString = "";
		for (int i = 0; i < size(); i++) {
			geneString += getGene(i);
		}
		return geneString;
	}

}