package sp_movement.movements;

import sp_constants.DriveConstants;
import sp_movement.util.DisplacementCalculator;
import sp_movement.util.Movement;
import sp_movement.util.MovementType;
import sp_movement.util.Pose;

/**
 * Movement containing the motion plan for a turn trajectory with respect to elapsed time.
 */
public class Turn extends Movement {

	private double displacement, time;
	private Pose startPose, endPose;
	private DisplacementCalculator calculator;

	/**
	 * Creates a new Turn object with a given starting Pose and turn angle.
	 * @param startPose
	 * @param radians (positive = turn right)
	 */
	public Turn(Pose startPose, double radians) {
		this.MOVEMENT_TYPE = MovementType.DRIVE;
		this.displacement = -radians;
		this.startPose = startPose;
		this.endPose = new Pose(
				startPose.getX(), 
				startPose.getY(), 
				normalizeAngle(startPose.getHeading() + displacement)
		);
		init();
	}

	public Pose getPose(double elapsedTime) {
		double t = displacement!=0 ? calculator.getDisplacement(elapsedTime) / displacement : 0;
		t = Math.abs(t);

		double theading = normalizeAngle(startPose.getHeading() + displacement*t);

		return new Pose(endPose.getX(), endPose.getY(), theading);
	}
	
	public Pose getVelocityPose(double elapsedTime) {
		return new Pose(0, 0, -calculator.getVelocity(elapsedTime));
	}

	public double getTime() {
		return time;
	}
	
	public Pose getStartPose() {
		return startPose;
	}

	public Pose getEndPose() {
		return endPose;
	}
	
	public String getName() {
		return "Turn";
	}
	
	/**
	 * Calculates total time.
	 */
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
