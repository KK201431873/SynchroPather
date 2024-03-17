package graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import movement.MovementSequence;
import movement.util.Pose;

public class MovementVisualizer {

	private MovementSequence sequence;
	private boolean running, paused;
	
	public MovementVisualizer(MovementSequence sequence) {
		this.sequence = sequence;
		this.running = false;
		this.paused = false;
	}
	
	public void setMovementSequence(MovementSequence sequence) {
		if (running) throw new RuntimeException("Cannot set new MovementSequence while simulation is running!");
		this.sequence = sequence;
	}
	
	public void start() {
		if (running) throw new RuntimeException("Simulation is already running!");
		running = true;
		
        JFrame frame = new JFrame("Spline Sim");
        frame.setSize(782, 805);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("./src/DRIVE.png").getImage());

        RobotImage robotImage = new RobotImage();
        MovementSequenceImage splineImage = new MovementSequenceImage(sequence);
        robotImage.setSplineImage(splineImage);
        frame.add(robotImage);
        
        frame.setVisible(true);
        System.out.println("Simulation Started");
		
		double startTime = System.currentTimeMillis() / 1000.0;
		double elapsedTime;
		while (running) {
			double currentTime = System.currentTimeMillis() / 1000.0;
			elapsedTime = currentTime - startTime;
			if (elapsedTime > sequence.getTime())
				startTime = currentTime;
			
			Pose currentPose = sequence.getPose(elapsedTime);
			robotImage.setPose(currentPose, elapsedTime);

	        frame.repaint();
	        while (paused);
		}
		
		frame.setVisible(false);
		frame.dispose();
		
        System.out.println("Simulation Ended");
		
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
		running = false;
	}
	
}
