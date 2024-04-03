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

	public MovementVisualizer(MovementSequence sequence) {
		this(sequence, 1, DriveConstants.LOOKAHEAD, 25);
	}
	
	public MovementVisualizer(MovementSequence sequence, double timeFactor) {
		this(sequence, timeFactor, DriveConstants.LOOKAHEAD, 25);
	}
	
	public MovementVisualizer(MovementSequence sequence, double timeFactor, double lookaheadDistance) {
		this(sequence, timeFactor, lookaheadDistance, 25);
	}
	
	public MovementVisualizer(MovementSequence sequence, double timeFactor, double lookaheadDistance, int fontSize) {
		this.timeFactor = timeFactor;
		this.sequence = sequence;
		this.running = false;
		this.paused = false;
		this.requestedStop = false;
		this.time = sequence.getTime();
		this.lookahead = lookaheadDistance;
		this.fontSize = fontSize;
	}
	
	public double getTime() {
		return time;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
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
//			System.out.println(String.format("\n\n\n\n\n\n\n\n\n\n\n\n\nVelocity \nX:%s \nY:%s \nH:%s", velocity.getX(), velocity.getY(), velocity.getHeading()));
			
			currentPose = sequence.getPose(elapsedTime);
			double lookaheadTime = (elapsedTime + Math.min(time, elapsedTime + lookahead)) / 2d;
			Pose lookaheadPose = sequence.getPose(lookaheadTime);

			DecimalFormat df = new DecimalFormat("0.0");
			x.setText(String.format("X:%5s (%5s)", df.format(currentPose.getX()), df.format(lookaheadPose.getX())));
			y.setText(String.format("Y:%5s (%5s)", df.format(currentPose.getY()), df.format(lookaheadPose.getY())));
			h.setText(String.format("H:%6s (%6s)", df.format(currentPose.getHeading()*180/Math.PI), df.format(lookaheadPose.getHeading()*180/Math.PI)));
			
			robotImage.setPose(currentPose, lookaheadPose, elapsedTime);

	        frame.repaint();
	        lastTime = currentTime;
	        
	        while (paused) lastTime = System.currentTimeMillis() / 1000.0;
		} 
		else throw new RuntimeException("loop() called but simulation is not running!");
		
		return true;
	}
	
	public void pause() {
		if (!paused)
	        System.out.println("Simulation Paused");
		paused = true;
	}
	
	public void unpause() {
		if (paused)
	        System.out.println("Simulation Unpaused");
		paused = false;
	}
	
	public void stop() {
		requestedStop = true;
	}
	
	public double getElapsedTime() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return elapsedTime;
	}
	
	public double getDeltaTime() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return deltaTime;
	}

	public Pose getCurrentPose() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return currentPose;
	}
	public Pose getCurrentVelocity() {
		if (!running) throw new RuntimeException("Simulation is not running!");
		return currentVelocity;
	}
	
}
