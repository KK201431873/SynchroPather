
import graphics.MovementVisualizer;
import movement.MovementSequence;
import movement.MovementSequenceBuilder;
import movement.movements.CRSpline;
import movement.movements.CRSplineBuilder;

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
				.build();
		
		System.out.println(seq1.getTime());
		
		MovementSequence seq2 = new MovementSequenceBuilder(-40.75,63.5,-90)
				.forward(50)
				.turnRight(45)
				.forward(20)
				.goStraightTo(-40,0,-180)
				.backward(50)
				.build();
		
		MovementVisualizer visualizer1 = new MovementVisualizer(seq1);
		visualizer1.start();

		MovementVisualizer visualizer2 = new MovementVisualizer(seq2);
		visualizer2.start();

	}
	
}
