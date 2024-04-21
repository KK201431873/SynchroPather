package graphics;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import movement.MovementSequence;
import movement.util.Pose;
import teamcode_util.DriveConstants;

/**
 * Object that utilizes Java AWT to visualize a MovementSequence in a pop-up window.
 */
public class MovementVisualizer {

	private MovementSequence sequence;
	private boolean running, paused, requestedStop;
	private JFrame frame;
	private JLabel x, y, h;
	private double time, elapsedTime, lastTime, deltaTime, lookahead;
	private Pose currentVelocity, currentPose;
	private RobotImage robotImage;
	private MovementSequenceImage splineImage;
	private final double timeFactor;
	private final int fontSize;

	/**
	 * Creates a new MovementVisualizer object with the given sequence.
	 * @param sequence
	 */
	public MovementVisualizer(MovementSequence sequence) {
		this(sequence, 1, DriveConstants.LOOKAHEAD, 25);
	}
	
	/**
	 * Creates a new MovementVisualizer object with the given sequence.
	 * @param sequence
	 * @param timeFactor between 0 and 1
	 */
	public MovementVisualizer(MovementSequence sequence, double timeFactor) {
		this(sequence, timeFactor, DriveConstants.LOOKAHEAD, 25);
	}
	
	/**
	 * Creates a new MovementVisualizer object with the given sequence.
	 * @param sequence
	 * @param timeFactor between 0 and 1
	 * @param lookaheadDistance seconds
	 */
	public MovementVisualizer(MovementSequence sequence, double timeFactor, double lookaheadDistance) {
		this(sequence, timeFactor, lookaheadDistance, 25);
	}
	
	/**
	 * Creates a new MovementVisualizer object with the given sequence.
	 * @param sequence
	 * @param timeFactor between 0 and 1
	 * @param lookaheadDistance seconds
	 * @param fontSize
	 */
	public MovementVisualizer(MovementSequence sequence, double timeFactor, double lookaheadDistance, int fontSize) {
		this.timeFactor = bound(timeFactor, 0, 1);
		this.sequence = sequence;
		this.running = false;
		this.paused = false;
		this.requestedStop = false;
		this.time = sequence.getTime();
		this.lookahead = lookaheadDistance;
		this.fontSize = fontSize;
	}
	
	/**
	 * @return the total runtime per cycle.
	 */
	public double getTime() {
		return time;
	}
	
	/**
	 * @return the JFrame object of this MovementVisualizer.
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Launches the window and begins this MovementVisualizer.
	 */
	public void start() {
		if (running) throw new RuntimeException("Simulation is already running!");
		running = true;
		
		// init frame
        frame = new JFrame("Spline Sim");
        frame.setSize(782, 805);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("src/graphics/DRIVE.png").getImage());
        
        // creating coordinate box
        JPanel textArea = new JPanel();   
        textArea.setLayout(null);
        textArea.setBackground(new Color(224, 224, 224, 96));  
        textArea.setBounds(0, 0, 11*fontSize, (int)(3.75*fontSize));
        frame.add(textArea);
        
        x = new JLabel("X: -");
        x.setVerticalAlignment(JLabel.TOP);
        x.setHorizontalAlignment(JLabel.LEFT);
        x.setSize(500, 50);
        x.setLocation(10, 0);
        x.setForeground(Color.gray);
        x.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        textArea.add(x);
        
        y = new JLabel("Y: -");
        y.setVerticalAlignment(JLabel.TOP);
        y.setHorizontalAlignment(JLabel.LEFT);
        y.setSize(500, 50);
        y.setLocation(10, fontSize);
        y.setForeground(Color.gray);
        y.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        textArea.add(y);
        
        h = new JLabel("H: -");
        h.setVerticalAlignment(JLabel.TOP);
        h.setHorizontalAlignment(JLabel.LEFT);
        h.setSize(500, 50);
        h.setLocation(10, 2*fontSize);
        h.setForeground(Color.gray);
        h.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        textArea.add(h);

        robotImage = new RobotImage();
        splineImage = new MovementSequenceImage(sequence);
        robotImage.setSplineImage(splineImage);
        frame.add(robotImage);
        
        frame.setVisible(true);
        System.out.println("Simulation Started");
		
		lastTime = System.currentTimeMillis() / 1000.0;
		deltaTime = 0;
		
	}
	
	/**
	 * Handles the next frame of this MovementVisualizer if it is running. Use as the condition for a while loop.
	 * @return whether or not stop has been requested.
	 */
	public boolean loop() {
		if (requestedStop) {
			running = false;
			requestedStop = false;
			
			frame.setVisible(false);
			frame.dispose();
			
	        System.out.println("Simulation Ended");
	        
			return false;
		}

		if (running) {
			double currentTime = System.currentTimeMillis() / 1000.0;
			deltaTime = currentTime - lastTime;
			elapsedTime += deltaTime * timeFactor;
			if (elapsedTime > time)
				elapsedTime = 0;

			currentVelocity = sequence.getVelocityPose(elapsedTime);
			currentVelocity = new Pose(
					currentVelocity.getX(),
					currentVelocity.getY(),
					-currentVelocity.getHeading()
			);

			currentPose = sequence.getPose(elapsedTime);
			double lookaheadTime = (elapsedTime + Math.min(time, elapsedTime + lookahead)) / 2d;
			Pose lookaheadPose = sequence.getPose(lookaheadTime);

			DecimalFormat df = new DecimalFormat("0.0");
			DecimalFormat la = new DecimalFormat("+0.0;-0.0");
			x.setText(String.format("X:%5s  %s", df.format(currentPose.getX()), la.format(lookaheadPose.getX()-currentPose.getX())));
			y.setText(String.format("Y:%5s  %s", df.format(currentPose.getY()), la.format(lookaheadPose.getY()-currentPose.getY())));
			h.setText(String.format("H:%6s %s", df.format( currentPose.getHeading()*180/Math.PI), la.format(normalizeAngle(lookaheadPose.getHeading()-currentPose.getHeading())*180/Math.PI)));
			
			robotImage.setPose(currentPose, lookaheadPose, elapsedTime);

	        frame.repaint();
	        lastTime = currentTime;
	        
	        while (paused) lastTime = System.currentTimeMillis() / 1000.0;
		} 
		else throw new RuntimeException("loop() called but simulation is not running!");
		
		return true;
	}
	
	/**
	 * Pauses this MovementVisualizer if it is running.
	 */
	public void pause() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		if (!paused)
	        System.out.println("Simulation Paused");
		paused = true;
	}

	/**
	 * Un-pauses this MovementVisualizer if it is running.
	 */
	public void unpause() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		if (paused)
	        System.out.println("Simulation Unpaused");
		paused = false;
	}
	
	/**
	 * Requests this MovementVisualizer to stop and close the JFrame window.
	 */
	public void stop() {
		requestedStop = true;
	}
	
	/**
	 * @return the current elapsed time of this MovementVisualizer if it is running.
	 */
	public double getElapsedTime() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return elapsedTime;
	}
	
	/**
	 * @return the time since the last time loop() was called if this MovementVisualizer is running.
	 */
	public double getDeltaTime() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return deltaTime;
	}

	/**
	 * @return the current pose of this MovementVisualizer if it is running.
	 */
	public Pose getCurrentPose() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return currentPose;
	}

	/**
	 * @return the current velocity pose of this MovementVisualizer if it is running.
	 */
	public Pose getCurrentVelocity() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return currentVelocity;
	}

	/**
	 * Clips the input x between a given lower and upper bound.
	 * @param x
	 * @param lower
	 * @param upper
	 * @return the clipped value of x.
	 */
	private static double bound(double x, double lower, double upper) {
		return Math.max(lower, Math.min(upper, x));
	}

	/**
	 * Normalizes a given angle to [-pi,pi) radians.
	 * @param degrees the given angle in radians.
	 * @return the normalized angle in radians.
	 */
	private double normalizeAngle(double degrees) {
	    double angle = degrees;
	    while (angle <= -Math.PI) //TODO: opMode.opModeIsActive() && 
	        angle += 2*Math.PI;
	    while (angle > Math.PI)
	        angle -= 2*Math.PI;
	    return angle;
	}
	
}
