package movement.movements;

import movement.util.DisplacementCalculator;
import movement.util.Movement;
import movement.util.Pose;
import teamcode_util.DriveConstants;

public class StraightLine extends Movement{
	
	private double distance, time;
	private Pose startPose, endPose;
	private DisplacementCalculator calculator;
	private Turn turn;
	
	public StraightLine(Pose startPose, Pose endPose) {
		this.startPose = startPose;
		this.endPose = endPose;
		init();
	}
	
	@Override
	public Pose getVelocityPose(double elapsedTime) {
		double theta = Math.atan2(
				endPose.getY()-startPose.getY(), 
				endPose.getX()-startPose.getX()
		);
		double velocity = calculator.getVelocity(elapsedTime);
		
		return new Pose(
				velocity * Math.cos(theta), 
				velocity * Math.sin(theta), 
				turn.getVelocityPose(elapsedTime).getHeading()
		);
	}

	@Override
	public Pose getPose(double elapsedTime) {
		double t = distance!=0 ? calculator.getDisplacement(elapsedTime) / distance : 0;

		double q0 = 1 - t;
		double q1 = t;

		double tx = startPose.getX()*q0 + endPose.getX()*q1;
		double ty = startPose.getY()*q0 + endPose.getY()*q1;
		double theading = normalizeAngle(turn.getPose(elapsedTime).getHeading());

		return new Pose(tx, ty, theading);
	}

	@Override
	public double getTime() {
		return Math.max(time, turn.getTime());
	}
	
	public Pose getStartPose() {
		return startPose;
	}

	@Override
	public Pose getEndPose() {
		return endPose;
	}
	
	private void init() {
		distance = Math.hypot(endPose.getX()-startPose.getX(), endPose.getY()-startPose.getY());

		double MV = DriveConstants.MAX_VELOCITY;
		double MA = DriveConstants.MAX_ACCELERATION;
		calculator = new DisplacementCalculator(distance, MV, MA);
		
		time = calculator.getTime();
		
		turn = new Turn(startPose, startPose.getHeading()-endPose.getHeading());
		
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
