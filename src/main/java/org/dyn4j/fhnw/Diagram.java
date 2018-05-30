package org.dyn4j.fhnw;

import java.awt.*;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;


public class Diagram extends JComponent {
	private static int schriftgroesse = 12;
	public float tempgroesse = 50;
	private static Font f = new Font("SansSerif", Font.PLAIN, schriftgroesse);

	private static final int BREITE = 380;
	private static final int BREITE_RELATIV = 360;
	private static final int HOEHE = 200;
	private static final String beschriftungXAchse1 = "Dominant";
	private static final String beschriftungXAchse2 = "roboter";
	private static final String beschriftungXAchse3 = "attributs";

	private static final String beschriftungYAchse = "  Wins";
	private static final String beschriftungXAchseMittelwert = "";
	private static final String beschriftungXAchseAnfangswert = "";
	private static final String beschriftungXAchseEndwert = "";

	private static Graphics g;
	private boolean isDisplayed = true;
	private int startX = 30;
	private int endX = 410;
	private int startY = 150;
	private int endY = 150 + 150 + 50;
	private int pfeilLaenge = 6;
	private static int[] data = new int[BREITE];
	private Color farbeBalken = Color.red;

	private int sensorCounterA = 0;
	private int sensorCounterB = 0;
	private int sensorCounterC = 0;
	private int sensorCounterD = 0;
	private int sensorCounterE = 0;
	private int sensorCounterF = 0;
	
	private int columWidth = 20;
	private int gapBetweenRows = 30;
	private int multiplicator = 2;
	
	// Font font = new Font("Serif", Font.ITALIC, 15);
	Font titleFont = new Font("Serif", Font.PLAIN, 20);
	Font normalFont = new Font("Serif", Font.PLAIN, 14);

	public void paintComponent(Graphics g) {

		Image offscreen;
		Graphics bufferGraphics;
		offscreen = createImage(getWidth(), getHeight());
		bufferGraphics = offscreen.getGraphics();

		// draw entire component white
		bufferGraphics.setColor(Color.white);
		bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
		bufferGraphics.setColor(Color.black);
		bufferGraphics.setFont(titleFont);
		bufferGraphics.drawString("Diagram", 160, 50);
		bufferGraphics.setFont(normalFont);

		//System.out.println("SensorCounters: " + sensorCounterA + "  " + sensorCounterB + "  " + sensorCounterC + "  " + sensorCounterD
		//		+ "  " + sensorCounterE + "  " + sensorCounterF);

		drawDiagram(bufferGraphics);
		g.drawImage(offscreen, 0, 0, this);
	}

	private void drawDiagram(Graphics bufferGraphics) {

		// draw entire component white
		bufferGraphics.setColor(Color.white);
		bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

		bufferGraphics.setColor(Color.black);

		bufferGraphics.setFont(titleFont);
		bufferGraphics.drawString("Diagram", 160, 50);
		bufferGraphics.setFont(normalFont);

		bufferGraphics.setFont(f);

		// Zeichne X-Achse
		bufferGraphics.setColor(Color.black);
		bufferGraphics.drawLine(startX, startY + HOEHE, startX + BREITE_RELATIV, startY + HOEHE);

		// Zeichne Y-Achse
		bufferGraphics.setColor(Color.black);
		bufferGraphics.drawLine(startX, startY + HOEHE, startX, startY);

		// Paint Y Scale with ticks
		bufferGraphics.setColor(Color.black);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 0 * (multiplicator * 10), startX, startY + HOEHE - 0 * (multiplicator * 10));
		bufferGraphics.drawString(" 0", startX -25, startY + HOEHE - 0 * (multiplicator * 10) +5);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 1 * (multiplicator * 10), startX, startY + HOEHE - 1 * (multiplicator * 10));
		bufferGraphics.drawString("10", startX -25, startY + HOEHE - 1 * (multiplicator * 10) +5);

		bufferGraphics.drawLine(startX -5, startY + HOEHE - 2 * (multiplicator * 10), startX, startY + HOEHE - 2 * (multiplicator * 10));
		bufferGraphics.drawString("20", startX -25, startY + HOEHE - 2 * (multiplicator * 10) +5);

		bufferGraphics.drawLine(startX -5, startY + HOEHE - 3 * (multiplicator * 10), startX, startY + HOEHE - 3 * (multiplicator * 10));
		bufferGraphics.drawString("30", startX -25, startY + HOEHE - 3 * (multiplicator * 10) +5);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 4 * (multiplicator * 10), startX, startY + HOEHE - 4 * (multiplicator * 10));
		bufferGraphics.drawString("40", startX -25, startY + HOEHE - 4 * (multiplicator * 10) +5);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 5 * (multiplicator * 10), startX, startY + HOEHE - 5 * (multiplicator * 10));
		bufferGraphics.drawString("50", startX -25, startY + HOEHE - 5 * (multiplicator * 10) +5);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 6 * (multiplicator * 10), startX, startY + HOEHE - 6 * (multiplicator * 10));
		bufferGraphics.drawString("60", startX -25, startY + HOEHE - 6 * (multiplicator * 10) +5);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 7 * (multiplicator * 10), startX, startY + HOEHE - 7 * (multiplicator * 10));
		bufferGraphics.drawString("70", startX -25, startY + HOEHE - 7 * (multiplicator * 10) +5);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 8 * (multiplicator * 10), startX, startY + HOEHE - 8 * (multiplicator * 10));
		bufferGraphics.drawString("80", startX -25, startY + HOEHE - 8 * (multiplicator * 10) +5);
		
		bufferGraphics.drawLine(startX -5, startY + HOEHE - 9 * (multiplicator * 10), startX, startY + HOEHE - 9 * (multiplicator * 10));
		bufferGraphics.drawString("90", startX -25, startY + HOEHE - 9 * (multiplicator * 10) +5);
		
		// Paint arrow head X
		bufferGraphics.setColor(Color.black);
		bufferGraphics.drawLine(startX + BREITE_RELATIV, startY + HOEHE, startX + BREITE_RELATIV - pfeilLaenge, startY + HOEHE + pfeilLaenge);
		bufferGraphics.drawLine(startX + BREITE_RELATIV, startY + HOEHE, startX + BREITE_RELATIV - pfeilLaenge, startY + HOEHE - pfeilLaenge);

		// Paint arrow head Y
		bufferGraphics.setColor(Color.black);
		bufferGraphics.drawLine(startX, startY, startX - pfeilLaenge, startY + pfeilLaenge);
		bufferGraphics.drawLine(startX, startY, startX + pfeilLaenge, startY + pfeilLaenge);

		// Beschriftung Y-Achse
		bufferGraphics.setColor(Color.black);
		bufferGraphics.setFont(f);
		bufferGraphics.drawString(beschriftungYAchse, startX - 19, startY - 6);

		// Beschriftung X-Achse
		bufferGraphics.setColor(Color.black);
		bufferGraphics.setFont(f);
		bufferGraphics.drawString(beschriftungXAchse1, endX - 8, endY - 10);
		bufferGraphics.drawString(beschriftungXAchse2, endX - 8, endY + 5);
		bufferGraphics.drawString(beschriftungXAchse3, endX - 8, endY + 20);


		// Mittelwert X-Achse
		// bufferGraphics.drawLine(startX + BREITE_RELATIV/2, endY, startX +
		// BREITE_RELATIV/2, endY + 5);
		bufferGraphics.drawString(beschriftungXAchseMittelwert, startX + BREITE_RELATIV / 2 - 3, endY + 20);

		// Anfangswert X-Achse
		//bufferGraphics.drawLine(startX, endY, startX, endY + 5);
		//bufferGraphics.drawString(beschriftungXAchseAnfangswert, startX - 15, endY + 20);

		// Endwert X-Achse
		//bufferGraphics.drawLine(startX + BREITE_RELATIV, endY, startX + BREITE_RELATIV, endY + 5);
		//bufferGraphics.drawString(beschriftungXAchseEndwert, startX + BREITE_RELATIV - 15, endY + 20);
		// bufferGraphics.clearRect(0, 0, 400, 400);

		
		bufferGraphics.setColor(farbeBalken);
		bufferGraphics.fillRect(startX + gapBetweenRows * 1, startY  + HOEHE + (sensorCounterA * multiplicator) * -1 , columWidth, sensorCounterA * multiplicator);
		bufferGraphics.fillRect(startX + gapBetweenRows * 2 + 1 * columWidth, startY  + HOEHE + (sensorCounterB * multiplicator) * -1 , columWidth, sensorCounterB * multiplicator);
		bufferGraphics.fillRect(startX + gapBetweenRows * 3 + 2 * columWidth, startY  + HOEHE + (sensorCounterC * multiplicator) * -1 , columWidth, sensorCounterC * multiplicator);
		bufferGraphics.fillRect(startX + gapBetweenRows * 4 + 3 * columWidth, startY  + HOEHE + (sensorCounterD * multiplicator) * -1 , columWidth, sensorCounterD * multiplicator);
		bufferGraphics.fillRect(startX + gapBetweenRows * 5 + 4 * columWidth, startY  + HOEHE + (sensorCounterE * multiplicator) * -1 , columWidth, sensorCounterE * multiplicator);
		bufferGraphics.fillRect(startX + gapBetweenRows * 6 + 5 * columWidth, startY  + HOEHE + (sensorCounterF * multiplicator) * -1 , columWidth, sensorCounterF * multiplicator);

		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawString("A", startX + gapBetweenRows * 1 + (columWidth / 2 -3), endY+20);
		//bufferGraphics.drawLine(startX + gapBetweenRows * 1 + (columWidth / 2), endY, startX + gapBetweenRows * 1 + (columWidth / 2), endY + 5);

		bufferGraphics.drawString("B", startX + gapBetweenRows * 2 + 1 * columWidth + (columWidth / 2 -3), endY+20);
		bufferGraphics.drawString("C", startX + gapBetweenRows * 3 + 2 * columWidth + (columWidth / 2 -3), endY+20);
		bufferGraphics.drawString("D", startX + gapBetweenRows * 4 + 3 * columWidth + (columWidth / 2 -3), endY+20);
		bufferGraphics.drawString("E", startX + gapBetweenRows * 5 + 4 * columWidth + (columWidth / 2 -3), endY+20);
		bufferGraphics.drawString("F", startX + gapBetweenRows * 6 + 5 * columWidth + (columWidth / 2 -3), endY+20);

		
	
		
	}

	public boolean getIsDisplayed() {
		return isDisplayed;
	}

	public void setIsDisplayed(boolean newValue) {
		isDisplayed = newValue;
	}

	

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getEndY() {
		return endY;
	}

	public static int getHOEHE() {
		return HOEHE;
	}

	public static int getBREITE() {
		return BREITE;
	}

	public Dimension getPreferredSize() {
		return new Dimension(470, 620);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public int getSensorCounterA() {
		return sensorCounterA;
	}

	public void resetSensorCounterA() {
		this.sensorCounterA = 0;
	}

	public int getSensorCounterB() {
		return sensorCounterB;
	}

	public void resetSensorCounterB() {
		this.sensorCounterB = 0;
	}

	public int getSensorCounterC() {
		return sensorCounterC;
	}

	public void resetSensorCounterC() {
		this.sensorCounterC = 0;
	}

	public int getSensorCounterD() {
		return sensorCounterD;
	}

	public void resetSensorCounterD() {
		this.sensorCounterD = 0;
	}

	public int getSensorCounterE() {
		return sensorCounterE;
	}

	public void resetSensorCounterE() {
		this.sensorCounterE = 0;
	}

	public int getSensorCounterF() {
		return sensorCounterF;
	}

	public void resetSensorCounterF() {
		this.sensorCounterF = 0;
	}

	public void resetAllSensorCounters() {
		this.sensorCounterA = 0;
		this.sensorCounterB = 0;
		this.sensorCounterC = 0;
		this.sensorCounterD = 0;
		this.sensorCounterE = 0;
		this.sensorCounterF = 0;
	}

	public void increaseSensorCounterA() {
		this.sensorCounterA++;
	}

	public void increaseSensorCounterB() {
		this.sensorCounterB++;
	}

	public void increaseSensorCounterC() {
		this.sensorCounterC++;
	}

	public void increaseSensorCounterD() {
		this.sensorCounterD++;
	}

	public void increaseSensorCounterE() {
		this.sensorCounterE++;
	}

	public void increaseSensorCounterF() {
		this.sensorCounterF++;
	}

}
