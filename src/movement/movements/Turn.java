package movement.movements;

import movement.util.DisplacementCalculator;
import movement.util.Movement;
import movement.util.Pose;
import teamcode_util.DriveConstants;

public class Turn extends Movement {

	private double distance, time;
	private Pose startPose, endPose;
	private DisplacementCalculator calculator;

	public Turn(Pose startPose, double radians) {
		this.startPose = startPose;
		this.endPose = new Pose(
				startPose.getX(), 
				startPose.getY(), 
				startPose.getHeading()-radians
		);
		init();
	}

	@Override
	public Pose getPose(double elapsedTime) {
		double t = distance!=0 ? calculator.getDisplacement(elapsedTime) / distance : 0;

		double q0 = 1 - t;
		double q1 = t;

		double theading = startPose.getHeading()*q0 + endPose.getHeading()*q1;

		return new Pose(endPose.getX(), endPose.getY(), theading);
	}

	@Override
	public double getTime() {
		return time;
	}
	
	@Override
	public Pose getStartPose() {
		return startPose;
	}

	@Override
	public Pose getEndPose() {
		return endPose;
	}
	
	private void init() {
		distance = Math.abs(endPose.getHeading() - startPose.getHeading());

		calculator = new DisplacementCalculator(distance, DriveConstants.MAX_ANGULAR_VELOCITY, DriveConstants.MAX_ANGULAR_ACCELERATION);
		
		time = calculator.getTime();
		
	}

}
