package graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import movement.MovementSequence;
import movement.util.Pose;

public class MovementVisualizer {

	private MovementSequence sequence;
	private boolean running, paused, requestedStop;
	private JFrame frame;
	private double elapsedTime, lastTime, deltaTime;
	private RobotImage robotImage;
	private MovementSequenceImage splineImage;
	
	
	public MovementVisualizer(MovementSequence sequence) {
		this.sequence = sequence;
		this.running = false;
		this.paused = false;
		this.requestedStop = false;
	}
	
	public void setMovementSequence(MovementSequence sequence) {
		if (running) throw new RuntimeException("Cannot set new MovementSequence while simulation is running!");
		this.sequence = sequence;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public void start() {
		if (running) throw new RuntimeException("Simulation is already running!");
		running = true;
		
        frame = new JFrame("Spline Sim");
        frame.setSize(782, 805);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("./src/DRIVE.png").getImage());

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
			elapsedTime += deltaTime;
			if (elapsedTime > sequence.getTime())
				elapsedTime = 0;
			
			Pose currentPose = sequence.getPose(elapsedTime);
			robotImage.setPose(currentPose, elapsedTime);

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
	
}
