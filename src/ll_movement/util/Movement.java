package ll_movement.util;

/**
 * Object containing the motion plan of a single Movement with respect to elapsed time.
 */
public abstract class Movement {
	
	/**
	 * The type of this movement.
	 */
	public MovementType MOVEMENT_TYPE;

	/**
	 * Returns the robot's desired Pose in this Movement at the given elapsed time.
	 * @param elapsedTime
	 * @return the indicated Pose.
	 */
	public abstract Pose getPose(double elapsedTime);

	/**
	 * Returns the robot's desired velocity Pose in this Movement at the given elapsed time.
	 * @param elapsedTime
	 * @return the indicated velocity Pose.
	 */
	public abstract Pose getVelocityPose(double elapsedTime);

	/**
	 * @return the total allotted runtime for this Movement.
	 */
	public abstract double getTime();
	
	/**
	 * @return the Pose of this Movement at time zero.
	 */
	public abstract Pose getStartPose();
	
	/**
	 * @return the Pose reached by the end of this Movement.
	 */
	public abstract Pose getEndPose();

}
