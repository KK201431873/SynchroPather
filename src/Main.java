
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

	public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Spline Sim");
        frame.setSize(782, 805);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("./src/DRIVE.png").getImage());
        
        JLabel bg = new JLabel(new ImageIcon("./src/centerstage_field.png"));
        frame.getContentPane().add(bg);
        
        RobotImage robot = new RobotImage();
        frame.getContentPane().add(robot);
        
        frame.setVisible(true);

		CRSpline spline = new CRSplineBuilder(-40.75,63.5,-90)
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
		
		
		double startTime = System.currentTimeMillis() / 1000.0;
		double elapsedTime;
		while (true) {
			double currentTime = System.currentTimeMillis() / 1000.0;
			elapsedTime = currentTime - startTime;
			
			if (elapsedTime > spline.getTime())
				startTime = currentTime;
			
//			System.out.println(elapsedTime);
			
			Pose currentPose = spline.getPose(elapsedTime);
			
			robot.setPose(currentPose);

	        frame.repaint();
		}

	}
	
}
