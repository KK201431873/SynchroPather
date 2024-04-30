package sp_movement;
import java.util.ArrayList;

import sp_movement.util.Movement;
import sp_movement.util.Pose;

/**
 * Object containing the motion plan for a sequence of Movements with respect to elapsed time.
 */
public class MovementSequence extends Movement {
	
	private Movement[] movements;
	private double time;
	private double[] times, partialTimes;
	
	/**
	 * Creates a new MovementSequence object with the give
	 * @param builder
	 */
	public MovementSequence(ArrayList<Movement> movements) {
		this.movements = movements.stream().toArray(Movement[]::new);
		init();
	}
	
	/**
	 * @return the number of Movements in this MovementSequence.
	 */
	public int getLength() {
		return movements.length;
	}
	
	public double getTime() {
		return time;
	}
	
	public Pose getPose(double elapsedTime) {
		int n = getLocalMovementIndex(elapsedTime);
		return movements[n].getPose(getLocalElapsedTime(elapsedTime));
	}
	
	public Pose getVelocityPose(double elapsedTime) {
		int n = getLocalMovementIndex(elapsedTime);
		return movements[n].getVelocityPose(getLocalElapsedTime(elapsedTime));
	}
	
	public Pose getStartPose() {
		return getLength()>0 ? movements[0].getStartPose() : null;
	}

	public Pose getEndPose() {
		return getLength()>0 ? movements[getLength()-1].getEndPose() : null;
	}
	
	public String getName() {
		return "MovementSequence";
	}
	
	/**
	 * Gets the Movement at the given index.
	 * @param index
	 * @return the indicated Movement.
	 */
	public Movement getMovement(int index) {
		return movements[index];
	}
	
	/**
	 * Gets the Movement running at the given elapsed time.
	 * @param elapsedTime
	 * @return the indicated Movement.
	 */
	public Movement getMovement(double elapsedTime) {
		return movements[getLocalMovementIndex(elapsedTime)];
	}
	
	/**
	 * Gets the elapsed time since the beginning of the currently running Movement.
	 * @param elapsedTime
	 * @return the relative elapsed time.
	 */
	public double getLocalElapsedTime(double elapsedTime) {
		int n = getLocalMovementIndex(elapsedTime);
		return elapsedTime - partialTimes[n];
	}
	
	/**
	 * Gets the index of the Movement running at the given elapsed time.
	 * @param elapsedTime
	 * @return the index of the indicated Movement.
	 */
	public int getLocalMovementIndex(double elapsedTime) {
		int n = 0;
		while (n+1 < partialTimes.length && elapsedTime >= partialTimes[n+1]) n++;
		return n;
	}
	
	/**
	 * Calculates total time.
	 */
	private void init() {
		times = new double[getLength()];
		partialTimes = new double[getLength()];
		time = 0;
		for (int i = 0; i < getLength(); i++) {
			double movementTime = movements[i].getTime();
			times[i] = movementTime;
			if (i < getLength())
				partialTimes[i] = time;
			time += movementTime;
		}
	}
	
}
