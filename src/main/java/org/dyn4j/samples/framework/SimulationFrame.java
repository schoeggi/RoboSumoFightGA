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
package org.dyn4j.samples.framework;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.dynamics.World;
import org.dyn4j.fhnw.FightingStats;
import org.dyn4j.fhnw.Diagram;
import org.dyn4j.fhnw.RobotPopulation;

import com.jogamp.newt.event.KeyEvent;
import org.dyn4j.fhnw.Algorithm;

/**
 * A very VERY simple framework for building samples.
 * 
 * @since 3.2.0
 * @version 3.2.0
 */
public abstract class SimulationFrame extends JFrame {
	/** The serial version id */
	private static final long serialVersionUID = 7659608187025022915L;

	/** The conversion factor from nano to base */
	public static final double NANO_TO_BASE = 1.0e9;

	/** The canvas to draw the Arena to */
	protected final Canvas canvasArena;

	/** The canvas to draw the Statistics to */
	protected final FightingStats canvasStats;
	protected final Diagram canvasDiagram;

	/** The canvas to draw the Configuration to */
	// protected final CanvasConfiguration canvasConfiguration;

	/** The dynamics engine */
	protected final World world;

	/** The pixels per meter scale factor */
	protected final double scale;

	/** True if the simulation is exited */
	private boolean stopped;

	/** True if the simulation is paused */
	private boolean paused;

	private boolean initialStart;

	/** True if the simulation is reseted */

	/** True if the simulation is paused */
	private boolean started = false;

	/** The time stamp for the last iteration */
	private long last;

	private RobotPopulation myPop;
	private int numberOfGenerationsToSimulate;
	private int guiSpeed;
	private int gaMutationRate;

	int fightEndedDelay = 0;
	long generationTimer = System.currentTimeMillis() / 1000;

	final JButton startButton = new JButton("Start");
	final JButton resetButton = new JButton("Reset");
	final JButton pauseButton = new JButton("Pause");
	final JButton resumeButton = new JButton("Resume");
	final JCheckBox elitismCheckbox;

	final Color buttonColorInactive = Color.lightGray;
	final Color buttonColorActive = startButton.getBackground();

	String generationText = "";
	String fittestText = "";
	String fitnessText = "";
	String genesText = "";
	String solution = "";

	/**
	 * Constructor.
	 * <p>
	 * By default creates a 800x600 canvas.
	 * 
	 * @param name
	 *            the frame name
	 * @param scale
	 *            the pixels per meter scale factor
	 */
	public SimulationFrame(String name, double scale) {
		super(name);

		// set the scale
		this.scale = scale;

		// create the world
		this.world = new World();

		// Set the Layout Manager, added by Joel
		setLayout(new FlowLayout());

		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add a window listener
		this.addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				// before we stop the JVM stop the simulation
				stop();
				super.windowClosing(e);
			}
		});

		// create the size of the window where all canvas are projected on
		Dimension sizeAranaPanel = new Dimension(480, 620);
		Dimension sizeDiagramPanel = new Dimension(420, 620);
		Dimension sizeConfigurationPanel = new Dimension(320, 620);

		// create a canvas to paint to and add it to the JFrame
		this.canvasArena = new Canvas();
		this.canvasArena.setPreferredSize(sizeAranaPanel);
		this.canvasArena.setMinimumSize(sizeAranaPanel);
		this.canvasArena.setMaximumSize(sizeAranaPanel);
		this.add(this.canvasArena);

		// create a canvas to paint to and add it to the JFrame
		/*
		 * // create a canvas to paint to and add it to the JFrame
		 * this.canvasConfiguration = new CanvasExample();
		 * this.canvasConfiguration.setPreferredSize(size);
		 * this.canvasConfiguration.setMinimumSize(size);
		 * this.canvasConfiguration.setMaximumSize(size);
		 * this.canvasConfiguration.setBackground(Color.MAGENTA);
		 * this.add(this.canvasConfiguration);
		 */

		// create a canvas to paint to and add it to the JFrame
		/*
		 * this.canvasConfiguration = new CanvasConfiguration();
		 * this.canvasConfiguration.setPreferredSize(size);
		 * this.canvasConfiguration.setMinimumSize(size);
		 * this.canvasConfiguration.setMaximumSize(size);
		 * this.canvasConfiguration.setBackground(Color.MAGENTA);
		 * this.canvasConfiguration.add(okButton); this.add(this.canvasConfiguration);
		 */

		// CanvasConfiguration canvasConfiguration = new CanvasConfiguration();

		final int sliderMaxGenerations = 200;
		final int sliderMinGenerations = 2;
		final int sliderInitGenerations = 80;
		numberOfGenerationsToSimulate = sliderInitGenerations;

		JSlider sliderGenerations = new JSlider(JSlider.HORIZONTAL, sliderMinGenerations, sliderMaxGenerations,
				sliderInitGenerations);
		sliderGenerations.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					// System.out.println("Not Adjusting: " +(int)source.getValue());
				} else {
					// System.out.println("Adjusting: " +(int)source.getValue());
					numberOfGenerationsToSimulate = (int) source.getValue();
				}
			}
		});
		sliderGenerations.setMinimum(10);
		sliderGenerations.setMaximum(200);

		sliderGenerations.setMajorTickSpacing(50);
		sliderGenerations.setMinorTickSpacing(10);
		sliderGenerations.setPaintTicks(true);
		sliderGenerations.setPaintLabels(true);
		JLabel sliderLabel = new JLabel("Number of Generations", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.TOP_ALIGNMENT);

		final int sliderMaxMutation = 50;
		final int sliderMinMutation = 1;
		final int sliderInitMutation = 2;
		gaMutationRate = sliderInitMutation;
		Algorithm.setMutationRate(gaMutationRate);

		JSlider sliderMutation = new JSlider(JSlider.HORIZONTAL, sliderMinMutation, sliderMaxMutation,
				sliderInitMutation);
		sliderMutation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					// System.out.println("Not Adjusting: " +(int)source.getValue());
				} else {
					//System.out.println("Mutation Rate changed: " + (int) source.getValue());
					gaMutationRate = (int) source.getValue();
					Algorithm.setMutationRate(gaMutationRate);
				}
			}
		});
		sliderMutation.setMinimum(1);
		sliderMutation.setMaximum(50);

		sliderMutation.setMajorTickSpacing(10);
		sliderMutation.setMinorTickSpacing(5);
		sliderMutation.setPaintTicks(true);
		sliderMutation.setPaintLabels(true);
		JLabel sliderLabelMutation = new JLabel("Mutation Rate in %", JLabel.CENTER);
		sliderLabelMutation.setAlignmentX(Component.TOP_ALIGNMENT);

		final int sliderMaxGuiSpeed = 10;
		final int sliderMinGuiSpeed = 1;
		final int sliderInitGuiSpeed = 5;
		guiSpeed = sliderInitGuiSpeed;

		JSlider sliderGuiSpeed = new JSlider(JSlider.HORIZONTAL, sliderMinGuiSpeed, sliderMaxGuiSpeed,
				sliderInitGuiSpeed);
		sliderGuiSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					// System.out.println("Not Adjusting: " +(int)source.getValue());
				} else {
					// System.out.println("Adjusting: " +(int)source.getValue());
					guiSpeed = (int) source.getValue();
					speedAccelrationForGUI(guiSpeed);
				}
			}
		});
		sliderGuiSpeed.setMajorTickSpacing(2);
		sliderGuiSpeed.setMinorTickSpacing(1);
		sliderGuiSpeed.setPaintTicks(true);
		sliderGuiSpeed.setPaintLabels(true);
		JLabel sliderLabelGuiSpeed = new JLabel("Simulation Speed", JLabel.CENTER);
		sliderLabelGuiSpeed.setAlignmentX(Component.TOP_ALIGNMENT);

		pauseButton.setBackground(buttonColorInactive);
		resumeButton.setBackground(buttonColorInactive);

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!started && !initialStart) {
					System.out.println("Started");
					started = true;
					paused = false;
					initialStart = true;
					canvasStats.startMeasuring();
					startButton.setBackground(buttonColorInactive);
					startButton.setFocusPainted(false);
					pauseButton.setBackground(buttonColorActive);
					resumeButton.setBackground(buttonColorInactive);
					elitismCheckbox.setBackground(buttonColorInactive);
					elitismCheckbox.setEnabled(false);

					// display/center the jdialog when the button is pressed
					// JDialog d = new JDialog(frame, "Hello", true);
					// d.setLocationRelativeTo(frame);
					// d.setVisible(true);

				} else {
					System.out.println("Already started");

				}
			}
		});

		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// resetRobots();
				started = false;
				RobotPopulation.resetGenerationCounter();

				resetSimulation();
				canvasStats.resetWinner();
				canvasStats.stopMeasuring();
				canvasStats.resetFightStats();
				canvasStats.resetFittestOfPreviousFight();
				canvasDiagram.resetAllSensorCounters();

				initialStart = false;
				myPop = null;
				System.out.println("Reseted");
				startButton.setBackground(buttonColorActive);
				pauseButton.setBackground(buttonColorInactive);
				elitismCheckbox.setBackground(buttonColorActive);
				elitismCheckbox.setEnabled(true);

				startButton.setEnabled(true);
				paused = true;
			}
		});

		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!paused) {
					// resetRobots();
					started = false;
					System.out.println("Paused");
					canvasStats.pauseFightMeassuring();

					// startButton.setBackground(buttonBGcolor);
					pauseButton.setBackground(buttonColorInactive);
					startButton.setFocusPainted(false);
					startButton.setBackground(buttonColorInactive);
					resumeButton.setBackground(buttonColorActive);

					// pauseButton.setEnabled(false);
					paused = true;
				}
			}
		});

		resumeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (paused) {
					// resetRobots();
					resume();
					started = true;
					paused = false;
					System.out.println("Resume");
					canvasStats.resumeFightMeassuring();
					startButton.setBackground(buttonColorInactive);
					pauseButton.setBackground(buttonColorActive);
					resumeButton.setBackground(buttonColorInactive);

					startButton.setEnabled(true);
				}
			}
		});

		elitismCheckbox = new JCheckBox("Use elitism");
		elitismCheckbox.setMnemonic(KeyEvent.VK_C);
		elitismCheckbox.setSelected(true);
		elitismCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Algorithm.setElitism(true);
					System.out.println("elitism pressed");
				} else {
					Algorithm.setElitism(false);

				}
			}
		});

		if (elitismCheckbox.isSelected()) {
			System.out.println("elitism pressed initially");
			Algorithm.setElitism(true);
		} else {
			Algorithm.setElitism(false);
		}

		JPanel container = new JPanel();
		container.setLayout(new FlowLayout());
		container.setBackground(Color.WHITE);
		container.setPreferredSize(sizeConfigurationPanel);

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		// JPanel panel3 = new JPanel();
		// JPanel panel4 = new JPanel();
		JPanel panel5 = new JPanel();

		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		panel1.setBackground(Color.WHITE);
		panel1.add(startButton);
		panel1.add(pauseButton);

		panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
		panel2.setBackground(Color.WHITE);
		panel2.add(resetButton);
		panel2.add(resumeButton);

		panel5.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
		// panel5.setLayout(new BoxLayout(panel5, BoxLayout.PAGE_AXIS));
		panel5.setLayout(new BoxLayout(panel5, BoxLayout.PAGE_AXIS));
		panel5.setBackground(Color.WHITE);
		panel5.add(sliderLabel);
		panel5.add(sliderGenerations);
		// panel5.add
		panel5.add(sliderLabelGuiSpeed);
		panel5.add(sliderGuiSpeed);
		panel5.add(sliderLabelMutation);
		panel5.add(sliderMutation);
		panel5.add(elitismCheckbox);

		// panel1.set[Preferred/Maximum/Minimum]Size()

		container.add(panel1);
		container.add(panel2);
		// container.add(panel3);
		// container.add(panel4);
		container.add(panel5);

		this.add(container);

		// create a canvas to paint to and add it to the JFrame
		this.canvasStats = new FightingStats();
		this.canvasStats.setPreferredSize(sizeAranaPanel);
		this.canvasStats.setMinimumSize(sizeAranaPanel);
		this.canvasStats.setMaximumSize(sizeAranaPanel);
		// this.canvasStats.setBackground(Color.BLUE);
		this.add(this.canvasStats);

		this.canvasDiagram = new Diagram();
		this.canvasDiagram.setPreferredSize(sizeDiagramPanel);
		this.canvasDiagram.setMinimumSize(sizeDiagramPanel);
		this.canvasDiagram.setMaximumSize(sizeDiagramPanel);
		// this.canvasStats.setBackground(Color.BLUE);
		this.add(this.canvasDiagram);

		// make the JFrame not resizable
		// (this way I dont have to worry about resize events)
		this.setResizable(false);

		// size everything
		this.pack();

		// setup the world
		this.initializeWorld();
	}

	/**
	 * Creates game objects and adds them to the world.
	 */
	protected abstract void initializeWorld();

	protected abstract void createRobots(RobotPopulation myPop);

	protected abstract void moveRobots();

	protected abstract boolean fightEnded();

	protected abstract int getFittest();

	protected abstract RobotPopulation getPopulation();

	protected abstract void setPopulation(RobotPopulation myPop);

	protected abstract int getSecondFittest();

	protected abstract int getThirdFittest();

	protected abstract void resetSimulation();

	protected abstract void increaseGeneration();

	protected abstract void printInactiveRobots();

	protected abstract void changeMaxSpeedRobots();

	protected abstract void changeDirectionOfRobots();

	protected abstract void clearOldPopulationRobots(RobotPopulation myPop);

	protected abstract void speedAccelrationForGUI(int guisSeed);

	protected abstract void setGenerationCounter(int generationCount);

	/**
	 * Start active rendering the simulation.
	 * <p>
	 * This should be called after the JFrame has been shown.
	 */
	private void start() {
		// initialize the last update time
		this.last = System.nanoTime();
		// don't allow AWT to paint the canvas since we are
		this.canvasArena.setIgnoreRepaint(true);
		// enable double buffering (the JFrame has to be
		// visible before this can be done)
		this.canvasArena.createBufferStrategy(2);
		// run a separate thread to do active rendering
		// because we don't want to do it on the EDT
		Thread thread = new Thread() {
			public void run() {
				// perform an infinite loop stopped
				// render as fast as possible
				while (!isStopped()) {
					gameLoop();
					// you could add a Thread.yield(); or
					// Thread.sleep(long) here to give the
					// CPU some breathing room
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		// set the game loop thread to a daemon thread so that
		// it cannot stop the JVM from exiting
		thread.setDaemon(true);
		// start the game loop
		thread.start();
	}

	/**
	 * The method calling the necessary methods to update the game, graphics, and
	 * poll for input.
	 */
	private void gameLoop() {
		// get the graphics object to render to
		Graphics2D g = (Graphics2D) this.canvasArena.getBufferStrategy().getDrawGraphics();

		// by default, set (0, 0) to be the center of the screen with the positive x
		// axis
		// pointing right and the positive y axis pointing up
		this.transform(g);

		// reset the view
		this.clear(g);

		// get the current time
		long time = System.nanoTime();
		// get the elapsed time from the last iteration
		long diff = time - this.last;
		// set the last time
		this.last = time;
		// convert from nanoseconds to seconds
		double elapsedTime = (double) diff / NANO_TO_BASE;

		if (started && numberOfGenerationsToSimulate > RobotPopulation.getGeneration()) {

			// while (numberOfGenerationsToSimulate < getGenerationCounter()) {

			// if (getGenerationCounter() == 0 && myPop == null) {
			if (RobotPopulation.getGeneration() == 0 && myPop == null) {
				System.out.println("Gather initial population from RoboSumoFight");
				myPop = getPopulation();
				System.out.println("numberOfGenerationsToSimulate: " + numberOfGenerationsToSimulate);

			}

			if (fightEnded() && fightEndedDelay == 1) {
				fightEndedDelay = 0;
				// System.out.println("NumberOfActiveRobots: " +
				// myPop.getNumberOfActiveRobots());

				// System.out.println("Fight ended Time: " + System.currentTimeMillis());

				// setGenerationCounter(getGenerationCounter() + 1);

				System.out.print("Generation: " + RobotPopulation.getGeneration() + " / ");
				System.out.print(" Fittest:       " + myPop.getFittest().getRobotNr() + ",");
				System.out.print(+myPop.getFittest().getRobotGenerationNr() + " / ");
				System.out.print("Genes: " + myPop.getFittest().toString());
				System.out.print(" Fitness: " + myPop.getFittest().getFitness() + "\n");

				System.out.print("Generation: " + RobotPopulation.getGeneration() + " / ");
				System.out.print(" SecondFittest: " + myPop.getSecondFittest().getRobotNr() + ",");
				System.out.print(+myPop.getSecondFittest().getRobotGenerationNr() + " / ");
				System.out.print("Genes: " + myPop.getSecondFittest().toString());
				System.out.print(" Fitness: " + myPop.getSecondFittest().getFitness() + "\n");

				System.out.print("Generation: " + RobotPopulation.getGeneration() + " / ");
				System.out.print(" ThirdFittest:  " + myPop.getThirdFittest().getRobotNr() + ",");
				System.out.print(+myPop.getThirdFittest().getRobotGenerationNr() + " / ");
				System.out.print("Genes: " + myPop.getThirdFittest().toString());
				System.out.print(" Fitness: " + myPop.getThirdFittest().getFitness() + "\n");

				generationText = "Generation: " + RobotPopulation.getGeneration() + " / ";
				fittestText = "Fittest: " + myPop.getFittest().getRobotNr() + " / ";
				fitnessText = "Fitness: " + myPop.getFittest().getFitness() + " / ";
				genesText = "Genes: " + myPop.getFittest().toString();
				solution = generationText + fittestText + fitnessText + genesText;

				canvasStats.setFittestOfPreviousFight(Integer.parseInt(myPop.getFittest().toString()));

				int highestGeneValue = 9;
				int hightestGeneValueIDs[] = { 99, 99, 99, 99, 99, 99 };
				int hightestGeneValueID = 98;
				int numberOfHighRankedGenes = 0;
				boolean highRankedGeneFound = false;

				for (int hightesGeneValue = 9; hightesGeneValue >= 0; hightesGeneValue--) {
					for (int i = 0; i < myPop.getFittest().getAllGenes().length; i++) {
						if (myPop.getFittest().getGene(i) == hightesGeneValue) {
							// highestGeneValue = myPop.getFittest().getGene(i);
							hightestGeneValueIDs[i] = i;
							numberOfHighRankedGenes++;
							highRankedGeneFound = true;
						}
					}
					if (highRankedGeneFound) {
						//System.out.println("stopped at highest value:" +hightesGeneValue);
						break;
					}

				}

				if (numberOfHighRankedGenes == 1) {
					for (int i = 0; i < hightestGeneValueIDs.length; i++) {
						if (hightestGeneValueIDs[i] != 99) {
							hightestGeneValueID = hightestGeneValueIDs[i];
						}
					}
				}

				if (numberOfHighRankedGenes > 1) {
					int lowestCounterValue = 99;
					int counterValue = 9999;

					for (int i = 0; i < hightestGeneValueIDs.length; i++) {
						if (hightestGeneValueIDs[i] != 99) {

							switch (i) {
							case 0: // Sensor A
								counterValue = canvasDiagram.getSensorCounterA();
								if (counterValue < lowestCounterValue) {
									lowestCounterValue = counterValue;
									hightestGeneValueID = i;
								}
								break;
							case 1: // Sensor B
								counterValue = canvasDiagram.getSensorCounterB();
								if (counterValue < lowestCounterValue) {
									lowestCounterValue = counterValue;
									hightestGeneValueID = i;
								}
								break;
							case 2: // Sensor C
								counterValue = canvasDiagram.getSensorCounterC();
								if (counterValue < lowestCounterValue) {
									lowestCounterValue = counterValue;
									hightestGeneValueID = i;
								}
								break;
							case 3: // Sensor D
								counterValue = canvasDiagram.getSensorCounterD();
								if (counterValue < lowestCounterValue) {
									lowestCounterValue = counterValue;
									hightestGeneValueID = i;
								}
								break;
							case 4: // Sensor E
								counterValue = canvasDiagram.getSensorCounterE();
								if (counterValue < lowestCounterValue) {
									lowestCounterValue = counterValue;
									hightestGeneValueID = i;
								}
								break;
							case 5: // Sensor F
								counterValue = canvasDiagram.getSensorCounterF();
								if (counterValue < lowestCounterValue) {
									lowestCounterValue = counterValue;
									hightestGeneValueID = i;
								}
								break;

							}
						}
					}
				}

				switch (hightestGeneValueID) {
				case 0: // Sensor A
					canvasDiagram.increaseSensorCounterA();
					break;
				case 1: // Sensor B
					canvasDiagram.increaseSensorCounterB();
					break;
				case 2: // Sensor C
					canvasDiagram.increaseSensorCounterC();
					break;
				case 3: // Sensor D
					canvasDiagram.increaseSensorCounterD();
					break;
				case 4: // Sensor E
					canvasDiagram.increaseSensorCounterE();
					break;
				case 5: // Sensor F
					canvasDiagram.increaseSensorCounterF();
					break;
				case 99: // Error
					System.out.print("Error, dominant gene could not be recognized");
					break;
				}

				// RobotPopulation.increaseGeneration();
				increaseGeneration();
				RobotPopulation newPopulation = Algorithm.evolvePopulation(myPop);
				// RobotPopulation newPopulation = new RobotPopulation(10, true);
				clearOldPopulationRobots(myPop);
				myPop = newPopulation;
				createRobots(newPopulation);
				setPopulation(myPop);
				generationTimer = System.currentTimeMillis() / 1000;
				canvasStats.resetFightDuration();

			} else {
				moveRobots();
				// printInactiveRobots();

				if (fightEnded() && fightEndedDelay == 0) { // fightEndedDelay++;
					fightEndedDelay++;
				}

				if ((System.currentTimeMillis() / 1000) - generationTimer > 5) {
					// System.out
					// .println("Time over: Change vMax for that many robots: " +
					// myPop.getNumberOfActiveRobots());
					changeMaxSpeedRobots();
					changeDirectionOfRobots();
					generationTimer = System.currentTimeMillis() / 1000;

				}
				// System.out.println("Time : " +System.currentTimeMillis() / 1000);
				// System.out.println("Time Gen: " +generationTimer);
			}

		}
		if (numberOfGenerationsToSimulate <= RobotPopulation.getGeneration()
				&& canvasStats.getWinnerIsKnown() == false) {
			canvasStats.setWinnerIsKnown();

			// Hack because Overall Solution is always 1 higher
			generationText = "Generation: " + (RobotPopulation.getGeneration() - 1) + " / ";
			solution = generationText + fittestText + fitnessText + genesText;

			canvasStats.setWinnerIs(solution);
			started = false;
			clearOldPopulationRobots(myPop);
			pauseButton.setBackground(buttonColorInactive);

		}
		// Hack because Overall Solution is always 1 higher

		this.canvasStats.update(canvasStats.getGraphics());
		this.canvasDiagram.update(canvasDiagram.getGraphics());
		;

		// render anything about the simulation (will render the World objects)
		this.render(g, elapsedTime);

		if (!paused) {
			// update the World
			this.update(g, elapsedTime);
		}

		// dispose of the graphics object
		g.dispose();

		// blit/flip the buffer
		BufferStrategy strategy = this.canvasArena.getBufferStrategy();
		if (!strategy.contentsLost()) {
			strategy.show();
		}

		// Sync the display on some systems.
		// (on Linux, this fixes event queue problems)
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Performs any transformations to the graphics.
	 * <p>
	 * By default, this method puts the origin (0,0) in the center of the window and
	 * points the positive y-axis pointing up.
	 * 
	 * @param g
	 *            the graphics object to render to
	 */
	protected void transform(Graphics2D g) {
		final int w = this.canvasArena.getWidth();
		final int h = this.canvasArena.getHeight();

		// before we render everything im going to flip the y axis and move the
		// origin to the center (instead of it being in the top left corner)
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
		AffineTransform move = AffineTransform.getTranslateInstance(w / 2, -h / 2);
		g.transform(yFlip);
		g.transform(move);
	}

	/**
	 * Clears the previous frame.
	 * 
	 * @param g
	 *            the graphics object to render to
	 */
	protected void clear(Graphics2D g) {
		final int w = this.canvasArena.getWidth();
		final int h = this.canvasArena.getHeight();

		// lets draw over everything with a white background
		g.setColor(Color.WHITE);
		g.fillRect(-w / 2, -h / 2, w, h);
	}

	/**
	 * Renders the example.
	 * 
	 * @param g
	 *            the graphics object to render to
	 * @param elapsedTime
	 *            the elapsed time from the last update
	 */
	protected void render(Graphics2D g, double elapsedTime) {
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) {
			// get the object
			SimulationBody body = (SimulationBody) this.world.getBody(i);
			this.render(g, elapsedTime, body);
		}
	}

	/**
	 * Renders the body.
	 * 
	 * @param g
	 *            the graphics object to render to
	 * @param elapsedTime
	 *            the elapsed time from the last update
	 * @param body
	 *            the body to render
	 */
	protected void render(Graphics2D g, double elapsedTime, SimulationBody body) {
		// draw the object
		body.render(g, this.scale);
	}

	/**
	 * Updates the world.
	 * 
	 * @param g
	 *            the graphics object to render to
	 * @param elapsedTime
	 *            the elapsed time from the last update
	 */
	protected void update(Graphics2D g, double elapsedTime) {
		// update the world with the elapsed time
		this.world.update(elapsedTime);
	}

	/**
	 * Stops the simulation.
	 */
	public synchronized void stop() {
		this.stopped = true;
	}

	/**
	 * Returns true if the simulation is stopped.
	 * 
	 * @return boolean true if stopped
	 */
	public boolean isStopped() {
		return this.stopped;
	}

	/**
	 * Pauses the simulation.
	 */
	public synchronized void pause() {
		this.paused = true;
	}

	/**
	 * Pauses the simulation.
	 */
	public synchronized void resume() {
		this.paused = false;
	}

	/**
	 * Returns true if the simulation is paused.
	 * 
	 * @return boolean true if paused
	 */
	public boolean isPaused() {
		return this.paused;
	}

	/**
	 * Starts the simulation.
	 */
	public void run() {
		// Run function only call once when app was started
		// set the look and feel to the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// show it
		this.setVisible(true);

		// start it
		this.start();
		this.paused = true;
		this.initialStart = false;
		// System.out.println("dsjkflösdajfsdalkfsjdöklafjksdajfklösdakljöfkljsödafjklö");
	}
}
