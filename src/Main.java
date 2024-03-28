
import graphics.MovementVisualizer;
import movement.MovementSequence;
import movement.MovementSequenceBuilder;
import movement.movements.CRSpline;
import movement.movements.CRSplineBuilder;
import movement.util.Pose;

public class Main {

	public static void main(String[] args) throws InterruptedException {

        CRSpline spline1 = new CRSplineBuilder(-40.75,63.5,-90)
				.addPose(-40.75,38,0)
				.addPose(43,36,0)
				.build();

        CRSpline spline2 = new CRSplineBuilder(43,36,0)
				.addPose(0,12,-180+1e-6)
				.addPose(-56,12,-180+1e-6)
				.build();

        CRSpline spline3 = new CRSplineBuilder(-56,12,-180+1e-6)
				.addPose(12,12,0)
				.addPose(43,36,0)
				.build();

        CRSpline spline4 = new CRSplineBuilder(43,36,0)
				.addPose(0,12,-180+1e-6)
				.addPose(-36,12,-180+1e-6)
				.addPose(-56,24,-180+1e-6)
				.build();

        CRSpline spline5 = new CRSplineBuilder(-56,24,-180+1e-6)
				.addPose(-36,12,-180+1e-6)
				.addPose(0,12,-180+1e-6)
				.addPose(43,36,0)
				.build();

		MovementSequence seq1 = new MovementSequenceBuilder(-40.75,63.5,-90)
				.addCRSpline(spline1)
				.addCRSpline(spline2)
				.addCRSpline(spline3)
				.addCRSpline(spline4)
				.addCRSpline(spline5)
				.rightAndTurn(20, 180)
				.backward(10)
				.goStraightTo(0, 0, 0)
				.build();
		
		System.out.println(seq1.getTime());
		
		MovementSequence seq2 = new MovementSequenceBuilder(-40.75,63.5,-90)
//				.forward(50)
//				.turnRight(45)
//				.forward(20)
//				.goStraightTo(-40,0,-180)
//				.backward(50)
//				.goStraightTo(0,0,0)
				.build();
		
		MovementSequence seq3 = new MovementSequenceBuilder(0,0,0)
				.forward(10)
				.turnRight(270)
				.turnLeft(540)
				.turnRight(270)
				.backward(10)
				.build();
		
		

		// testing bounded displacement calculator
//		double MV = DriveConstants.MAX_VELOCITY, MA = DriveConstants.MAX_ACCELERATION;
//		BoundedDisplacementCalculator boundedCalculator = new BoundedDisplacementCalculator(-33,9, MV, MA);
//
//		System.out.println(boundedCalculator.getDistance());
//		System.out.println(boundedCalculator.getTime());
//		System.out.println(boundedCalculator.getMV());
//		System.out.println(boundedCalculator.getMA());
//		
//		double startTime = System.currentTimeMillis() / 1000.0;
//		double elapsedTime = 0;
//		
//		while (elapsedTime <= boundedCalculator.getTime()) {
//			elapsedTime = System.currentTimeMillis() / 1000.0 - startTime;
//			
//			double distance = boundedCalculator.getDisplacement(elapsedTime);
//
//			System.out.println(String.format("t: [%s], d: [%s]", Math.round(100*elapsedTime)/100.0, Math.round(100*distance)/100.0));
//			
//			Thread.sleep(10);
//			
//		}
		
		double timeFactor = 1;
		MovementVisualizer visualizer1 = new MovementVisualizer(seq1, timeFactor);
		
		// starting visualizers
		visualizer1.start();
		double x = -40.75, y = 63.5, h = -90;
//		x = 0; y = 0; h = 0;
		while (visualizer1.loop()) {
			double dt = visualizer1.getDeltaTime();
			Pose velocity = visualizer1.getCurrentVelocity();
			
			double xv = velocity.getX() * timeFactor;
			double yv = velocity.getY() * timeFactor;
			double hv = velocity.getHeading() * 180d / Math.PI * timeFactor;
			x += dt * xv;
			y += dt * yv;
			h += dt * hv;
//			System.out.println(String.format("\n\n\n\n\n\n\n\n\n\n\n\n\nVelocity \nX:%s \nY:%s \nH:%s", xv, yv, hv));
			System.out.println(String.format("\n\n\n\n\n\n\n\n\n\n\n\n\nIntegral \nX:%s \nY:%s \nH:%s", x, y, h));
			
			Thread.sleep(16);
		}

		MovementVisualizer visualizer2 = new MovementVisualizer(seq2);
		visualizer2.start();
		while (visualizer2.loop()) {
			visualizer2.stop();
		}

	}
	
}
