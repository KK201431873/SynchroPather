package synchropather.systems;

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