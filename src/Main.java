
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import graphics.MovementSequenceImage;
import graphics.RobotImage;
import movement.MovementSequence;
import movement.MovementSequenceBuilder;
import movement.movements.CRSpline;
import movement.movements.CRSplineBuilder;
import movement.util.Pose;

public class Main {

	public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Spline Sim");
        frame.setSize(782, 805);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("./src/DRIVE.png").getImage());

        CRSpline spline1 = new CRSplineBuilder(-40.75,63.5,-90)
				.addPose(-40.75,38,0)
				.addPose(43,36,0)
				.build();

        CRSpline spline2 = new CRSplineBuilder(43,36,0)
				.addPose(0,12,-180)
				.addPose(-56,12,-180)
				.build();

        CRSpline spline3 = new CRSplineBuilder(-56,12,-180)
				.addPose(12,12,0)
				.addPose(43,36,0)
				.build();

        CRSpline spline4 = new CRSplineBuilder(43,36,0)
				.addPose(0,12,-180)
				.addPose(-36,12,-180)
				.addPose(-56,24,-180)
				.build();

        CRSpline spline5 = new CRSplineBuilder(-56,24,-180)
				.addPose(-36,12,-180)
				.addPose(0,12,-180)
				.addPose(43,36,0)
				.build();

		MovementSequence seq = new MovementSequenceBuilder(-40.75,63.5,-90)
				.addCRSpline(spline1)
				.addCRSpline(spline2)
				.addCRSpline(spline3)
				.addCRSpline(spline4)
				.addCRSpline(spline5)
				.right(20)
				.turnRight(135)
				.forward(10)
				.build();
		
		System.out.println(seq.getTime()); 
		
		
        RobotImage robotImage = new RobotImage();
        MovementSequenceImage splineImage = new MovementSequenceImage(seq);
        robotImage.setSplineImage(splineImage);
        frame.add(robotImage);
        
        frame.setVisible(true);
		
		double startTime = System.currentTimeMillis() / 1000.0;
		double elapsedTime;
		while (true) {
			double currentTime = System.currentTimeMillis() / 1000.0;
			elapsedTime = currentTime - startTime;
			
			if (elapsedTime > seq.getTime())
				startTime = currentTime;
			
//			System.out.println(elapsedTime);
			
			Pose currentPose = seq.getPose(elapsedTime);
			
			robotImage.setPose(currentPose, elapsedTime);

	        frame.repaint();
		}

	}
	
}
