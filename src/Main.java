
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import graphics.RobotImage;
import graphics.SplineImage;
import movement.movements.CRSpline;
import movement.movements.CRSplineBuilder;
import util.Pose;

public class Main {
	
	public static CRSpline spline;
	public static double elapsedTime;

	public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Spline Sim");
        frame.setSize(782, 805);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("./src/DRIVE.png").getImage());

		spline = new CRSplineBuilder(-40.75,63.5,-90)
				.addPose(-40.75,38,0)
				.addPose(43,36,0)
				.addPose(0,12,-180)
				.addPose(-56,12,-180)
				.addPose(12,12,0)
				.addPose(43,36,0)
				.addPose(0,12,-180)
				.addPose(-36,12,-180)
				.addPose(-56,24,-180)
				.addPose(-36,12,-180)
				.addPose(0,12,-180)
				.addPose(43,36,0)
				.build();

		System.out.println(spline.getTime()); 
		
		
        RobotImage robotImage = new RobotImage();
        SplineImage splineImage = new SplineImage(spline);
        robotImage.setSplineImage(splineImage);
        frame.add(robotImage);
        
        frame.setVisible(true);
		
		double startTime = System.currentTimeMillis() / 1000.0;
		while (true) {
			double currentTime = System.currentTimeMillis() / 1000.0;
			elapsedTime = currentTime - startTime;
			
			if (elapsedTime > spline.getTime())
				startTime = currentTime;
			
//			System.out.println(elapsedTime);
			
			Pose currentPose = spline.getPose(elapsedTime);
			
			robotImage.setPose(currentPose, elapsedTime);

	        frame.repaint();
		}

	}
	
}
