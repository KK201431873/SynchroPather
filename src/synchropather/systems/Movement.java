package synchropather.systems;

/**
 * Object containing the motion plan of a single Movement with respect to elapsed time.
 */
public abstract class Movement {
	
	/**
	 * The type of Movement.
	 */
	public enum MovementType {
			/**
			 * A Movement involving the global x and y coordinates.
			 */
			TRANSLATION(),
			
			/**
			 * A Movement involving the global heading.
			 */
			ROTATION();

			MovementType() {};
	}
	
	/**
	 * The type of this movement.
	 */
	public MovementType MOVEMENT_TYPE;


	/**
	 * @return the timestamp for when this Movement starts.
	 */
	public abstract double getStartTime();


	/**
	 * @return the timestamp for when this Movement ends.
	 */
	public abstract double getEndTime();

	/**
	 * @return the minimum runtime needed for this Movement.
	 */
	public abstract double getMinDuration();

	/**
	 * @return the total allotted runtime for this Movement.
	 */
	public abstract double getDuration();

	/**
	 * Returns the robot's desired RobotState in this Movement at the given elapsed time.
	 * @param elapsedTime
	 * @return the indicated RobotState.
	 */
	public abstract RobotState getState(double elapsedTime);

	/**
	 * Returns the robot's desired velocity RobotState in this Movement at the given elapsed time.
	 * @param elapsedTime
	 * @return the indicated velocity RobotState.
	 */
	public abstract RobotState getVelocity(double elapsedTime);
	
	/**
	 * @return the RobotState of this Movement at time zero.
	 */
	public abstract RobotState getStartState();
	
	/**
	 * @return the RobotState reached by the end of this Movement.
	 */
	public abstract RobotState getEndState();
	
	/**
	 * @return a String representing the class name of this Movement.
	 */
	public abstract String getDisplayName();

}
