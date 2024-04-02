package movement.movements;

import movement.util.DisplacementCalculator;
import movement.util.Movement;
import movement.util.Pose;
import teamcode_util.DriveConstants;

public class Turn extends Movement {

	private double displacement, time;
	private Pose startPose, endPose;
	private DisplacementCalculator calculator;

	public Turn(Pose startPose, double radians) {
		this.displacement = -radians;
		this.startPose = startPose;
		this.endPose = new Pose(
				startPose.getX(), 
				startPose.getY(), 
				normalizeAngle(startPose.getHeading() + displacement)
		);
		init();
	}

	@Override
	public Pose getPose(double elapsedTime) {
		double t = displacement!=0 ? calculator.getDisplacement(elapsedTime) / displacement : 0;
		t = Math.abs(t);

		double theading = normalizeAngle(startPose.getHeading() + displacement*t);

		return new Pose(endPose.getX(), endPose.getY(), theading);
	}
	
	@Override
	public Pose getVelocityPose(double elapsedTime) {
		return new Pose(0, 0, -calculator.getVelocity(elapsedTime));
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

		double MAV = DriveConstants.MAX_ANGULAR_VELOCITY;
		double MAA = DriveConstants.MAX_ANGULAR_ACCELERATION;
		calculator = new DisplacementCalculator(displacement, MAV, MAA);
		
		time = calculator.getTime();
		
	}

    /**
     * Normalizes a given angle to [-180,180) degrees.
     * @param degrees the given angle in degrees.
     * @return the normalized angle in degrees.
     */
    private double normalizeAngle(double degrees) {
        double angle = degrees;
        while (angle <= -Math.PI) //TODO: opMode.opModeIsActive() && 
            angle += 2*Math.PI;
        while (angle > Math.PI) //TODO: opMode.opModeIsActive() && 
            angle -= 2*Math.PI;
        return angle;
    }

}
