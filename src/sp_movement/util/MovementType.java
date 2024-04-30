package sp_movement.util;

/**
 * The type of Movement.
 */
public enum MovementType {
		/**
		 * A Movement involving solely the drivetrain.
		 */
		DRIVE(),
		
		/**
		 * A Movement that pauses the robot.
		 */
		DELAY();

		MovementType() {};
}