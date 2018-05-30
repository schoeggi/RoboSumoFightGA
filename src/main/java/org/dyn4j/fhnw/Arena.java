package org.dyn4j.fhnw;

import java.awt.Color;
import java.util.LinkedList;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.samples.framework.SimulationBody;

public class Arena {

	private static double wallLength = 0.591;
	private static double wallWide = 0.02;
	private static double wallXmax = 1.41;
	private static double wallXmin = -0.01;
	private static double wallXDelta = Math.abs(wallXmax) + Math.abs(wallXmin);
	private static double wallYmax = 0.71;
	private static double wallYmin = -0.71;
	private static double wallYDelta = Math.abs(wallYmax) + Math.abs(wallYmin);
	private static double ArenaRadius = (wallXDelta + wallYDelta) / 4;
	private static int numberOfWalls = 8;
	private static SimulationBody[] walls = new SimulationBody[numberOfWalls];
	private static Color arenaColor = Color.BLUE;
	
	private static double centerX = (wallXmin + wallXmax) / 2;
	private static double centerY = (wallYmin + wallYmax) / 2;

	public static double getWallLength() {
		return wallLength;
	}

	public static void setWallLength(double wallLength) {
		Arena.wallLength = wallLength;
	}

	public static double getWallWide() {
		return wallWide;
	}

	public static void setWallWide(double wallWide) {
		Arena.wallWide = wallWide;
	}

	public static double getWallXmax() {
		return wallXmax;
	}

	public static void setWallXmax(double wallXmax) {
		Arena.wallXmax = wallXmax;
	}

	public static double getWallXmin() {
		return wallXmin;
	}

	public static void setWallXmin(double wallXmin) {
		Arena.wallXmin = wallXmin;
	}

	public static double getWallXDelta() {
		return wallXDelta;
	}

	public static void setWallXDelta(double wallXDelta) {
		Arena.wallXDelta = wallXDelta;
	}

	public static double getWallYmax() {
		return wallYmax;
	}

	public static void setWallYmax(double wallYmax) {
		Arena.wallYmax = wallYmax;
	}

	public static double getWallYmin() {
		return wallYmin;
	}

	public static void setWallYmin(double wallYmin) {
		Arena.wallYmin = wallYmin;
	}

	public static double getWallYDelta() {
		return wallYDelta;
	}

	public static void setWallYDelta(double wallYDelta) {
		Arena.wallYDelta = wallYDelta;
	}

	public static Color getArenaColor() {
		return arenaColor;
	}

	public static int getNumberOfWalls() {
		return numberOfWalls;
	}
	
	public static double getCenterX() {
		return centerX;
	}
	
	public static double getCenterY() {
		return centerY;
	}
	
	public static void setArenaColor(Color arenaColor) {
		Arena.arenaColor = arenaColor;
	}

	public static SimulationBody getWall(int i) {
		return walls[i];
	}

	public static void setWallsArena(SimulationBody[] walls) {
		Arena.walls = walls;
	}

	public Arena() {
	}
	
	public static void removeOctagon() {
		walls = null;
	}
	
	public static boolean checkOutsideArena(double x, double y, double robotRadius) {
		double distanceXfromCenter;
		double distanceYfromCenter;
		boolean returnCode;
		
		distanceXfromCenter =  -1 * (centerX - x);
		distanceYfromCenter =  -1 * (centerY - y);
		//System.out.println("distanceXfromCenter: " +distanceXfromCenter);
		//System.out.println("distanceYfromCenter: " +distanceYfromCenter);
		//System.out.println("x: " +x);
		//System.out.println("ArenaRadius: "  +ArenaRadius);


		
		if (Math.sqrt(Math.pow(distanceXfromCenter, 2) + Math.pow(distanceYfromCenter, 2)) > ArenaRadius - robotRadius) {
			returnCode = true;
			//System.out.println("return code True:");

			
		}
		else {
			returnCode = false;
			//System.out.println("return code false:");

		}
			
		return returnCode;
	}

	public static void createOctagon() {
		SimulationBody wall1 = new SimulationBody();
		wall1.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall1.setColor(arenaColor);
		wall1.rotateAboutCenter(Math.PI / 2);
		wall1.translate(0.7, wallYmax);
		wall1.setMass(MassType.INFINITE);
		walls[0] = wall1;

		SimulationBody wall2 = new SimulationBody();
		wall2.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall2.setColor(arenaColor);
		wall2.rotateAboutCenter(Math.PI / 4);
		wall2.translate(1.2, 0.5);
		wall2.setMass(MassType.INFINITE);
		walls[1] = wall2;

		SimulationBody wall3 = new SimulationBody();
		wall3.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall3.setColor(arenaColor);
		wall3.translate(wallXmax, 0);
		wall3.setMass(MassType.INFINITE);
		walls[2] = wall3;

		SimulationBody wall4 = new SimulationBody();
		wall4.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall4.setColor(arenaColor);
		wall4.rotateAboutCenter(Math.PI / 4 - Math.PI / 2);
		wall4.translate(1.2, -0.5);
		wall4.setMass(MassType.INFINITE);
		walls[3] = wall4;

		SimulationBody wall5 = new SimulationBody();
		wall5.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall5.setColor(arenaColor);
		wall5.rotateAboutCenter(Math.PI / 2);
		wall5.translate(0.7, wallYmin);
		wall5.setMass(MassType.INFINITE);
		walls[4] = wall5;

		SimulationBody wall6 = new SimulationBody();
		wall6.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall6.setColor(arenaColor);
		wall6.rotateAboutCenter(Math.PI / 4);
		wall6.translate(0.2, -0.5);
		wall6.setMass(MassType.INFINITE);
		walls[5] = wall6;

		SimulationBody wall7 = new SimulationBody();
		wall7.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall7.translate(wallXmin, 0);
		wall7.setMass(MassType.INFINITE);
		wall7.setColor(arenaColor);
		walls[6] = wall7;

		SimulationBody wall8 = new SimulationBody();
		wall8.addFixture(Geometry.createRectangle(wallWide, wallLength));
		wall8.setColor(arenaColor);
		wall8.rotateAboutCenter(Math.PI / 4 - Math.PI / 2);
		wall8.translate(0.2, 0.5);
		wall8.setMass(MassType.INFINITE);
		walls[7] = wall8;

	}
	
}
