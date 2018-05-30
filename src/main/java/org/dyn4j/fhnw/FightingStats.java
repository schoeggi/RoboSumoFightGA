package org.dyn4j.fhnw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class FightingStats extends JComponent {
	private static Color m_tRed = new Color(255, 0, 0, 150);
	private static Color m_tGreen = new Color(0, 255, 0, 150);
	private static Color m_tBlue = new Color(0, 0, 255, 150);
	private static Font monoFont = new Font("Monospaced", Font.BOLD | Font.ITALIC, 36);
	private static Font sanSerifFont = new Font("SanSerif", Font.PLAIN, 12);
	private static Font serifFont = new Font("Serif", Font.BOLD, 24);
	private static ImageIcon java2sLogo = new ImageIcon("java2s.gif");
	private static boolean winnerKnown = false;
	private static String winnerIs = "";
	private long fightStart, fightStop, pauseStart, pauseStop;
	private int fightDuration;
	private int pauseDuration = 0;
	private int previousPauseDuration = 0;

	private boolean startMeasuring = false;
	private boolean pauseMeassuringStarted = false;

	private int fittestOfPreviousFight = 0;
	private int fightGeneration = -1;
	private boolean fightMeassuringStarted = false;
	private boolean fightMeasuringPaused = false;

	// Font font = new Font("Serif", Font.ITALIC, 15);
	Font titleFont = new Font("Serif", Font.PLAIN, 20);
	Font normalFont = new Font("Serif", Font.PLAIN, 14);

	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		Image offscreen;
		Graphics bufferGraphics;
		offscreen = createImage(getWidth(), getHeight());
		bufferGraphics = offscreen.getGraphics();
		// g = offscreen.getGraphics();

		// draw entire component white
		bufferGraphics.setColor(Color.white);
		bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

		bufferGraphics.setColor(Color.black);

		// FontMetrics fm = g.getFontMetrics();

		// bufferGraphics.setFont(sanSerifFont);
		// fm = bufferGraphics.getFontMetrics();

		bufferGraphics.setFont(titleFont);
		bufferGraphics.drawString("Fighting Stats", 160, 50);
		bufferGraphics.setFont(normalFont);

		int robotGeneration = RobotPopulation.getGeneration();

		if (winnerKnown) {
			robotGeneration = RobotPopulation.getGeneration() - 1;
		}

		bufferGraphics.drawString("Generation:     " + robotGeneration, 10, 90);

		bufferGraphics.drawString("Fighting time:  " + calcFightDuration() + "ms", 10, 120);

		bufferGraphics.drawString("Last round ", 10, 150);

		if (fittestOfPreviousFight == 0) {
			bufferGraphics.drawString("winner genes:   " + "-", 10, 165);
		} else {
			bufferGraphics.drawString("winner genes:   " + fittestOfPreviousFight, 10, 165);

		}

		if (winnerKnown) {
			bufferGraphics.setFont(normalFont);
			bufferGraphics.drawString("Solution:         " + winnerIs, 10, 195);
			fightMeasuringPaused = true;
		} else {
			bufferGraphics.setFont(normalFont);
			bufferGraphics.drawString("Best Solution:  Wait till fights are over", 10, 195);
		}
		
		
		
		// Gene to robot sensor mapping
		bufferGraphics.setFont(titleFont);
		bufferGraphics.drawString("Gene to robot attribut mapping", 100, 300);
		bufferGraphics.setFont(normalFont);
		
		bufferGraphics.drawString("Gene Position 0 --> attribut A (speed)", 10, 340);
		bufferGraphics.drawString("Gene Position 1 --> attribut B (search range) ", 10, 360);
		bufferGraphics.drawString("Gene Position 2 --> attribut C (wall detection range) ", 10, 380);
		bufferGraphics.drawString("Gene Position 3 --> attribut D (size) ", 10, 400);
		bufferGraphics.drawString("Gene Position 4 --> attribut E (strategy persistency) ", 10, 420);
		bufferGraphics.drawString("Gene Position 5 --> attribut F (driving accuracy) ", 10, 440);




		
		

		g.drawImage(offscreen, 0, 0, this);
	}

	public void setWinnerIsKnown() {
		winnerKnown = true;
	}

	public void setWinnerIs(String winner) {
		winnerIs = winner;
	}

	private int calcFightDuration() {
		if (startMeasuring) {

			if (fightMeasuringPaused && pauseMeassuringStarted == false) {
				pauseStart = System.currentTimeMillis();
				pauseMeassuringStarted = true;
			}

			if (fightMeasuringPaused == false && pauseMeassuringStarted) {
				pauseStop = System.currentTimeMillis();
				pauseMeassuringStarted = false;
				pauseDuration = (int) (pauseStop - pauseStart);
				previousPauseDuration = previousPauseDuration + pauseDuration;
				return fightDuration;
			}

			if (fightMeasuringPaused && pauseMeassuringStarted) {

				return fightDuration;
			}

			if (fightGeneration < RobotPopulation.getGeneration() && fightMeassuringStarted == false) {
				fightStart = System.currentTimeMillis();
				fightGeneration++;
				fightMeassuringStarted = true;
				fightDuration = 0;
				return (int) fightDuration;
			}

			if (fightGeneration == RobotPopulation.getGeneration() && fightMeassuringStarted) {
				fightStop = System.currentTimeMillis();
				fightDuration = (int) (fightStop - fightStart - previousPauseDuration);
				return (int) fightDuration;
			}

			if (fightGeneration < RobotPopulation.getGeneration() && fightMeassuringStarted) {
				fightStart = System.currentTimeMillis() ;
				fightDuration = 0;
				fightMeassuringStarted = false;

			}
			return (int) fightDuration;
		}

		return 0;

	}

	public boolean getWinnerIsKnown() {
		return winnerKnown;
	}

	public void resetWinner() {
		winnerKnown = false;
	}

	public void pauseFightMeassuring() {
		fightMeasuringPaused = true;
	}

	public void resumeFightMeassuring() {
		fightMeasuringPaused = false;
	}

	public void resetFightDuration() {
		fightDuration = 0;
		pauseDuration = 0;
		previousPauseDuration = 0;
	}

	public void resetFightStats() {
		fightDuration = 0;
		pauseDuration = 0;
		previousPauseDuration = 0;
		pauseMeassuringStarted = false;
		fightMeassuringStarted = false;
		fightGeneration = -1;
	}

	public void startMeasuring() {
		startMeasuring = true;
	}

	public void setFittestOfPreviousFight(int fittest) {
		fittestOfPreviousFight = fittest;
	}
	
	public void resetFittestOfPreviousFight() {
		fittestOfPreviousFight = 0;
	}

	public void stopMeasuring() {
		startMeasuring = false;
	}

	public Dimension getPreferredSize() {
		return new Dimension(420, 620);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

}