package ll_movement.movements;

import ll_movement.util.Movement;
import ll_movement.util.Pose;

/**
 * Movement representing a delay command.
 */
public class Delay extends Movement {
	
	private double seconds;
	private Pose pose;
	
	/**
	 * Creates a new Delay object with a given pose and delay.
	 * @param pose
	 * @param seconds 
	 */
	public Delay(Pose pose, double seconds) {
		this.pose = pose;
		this.seconds = seconds;
	}
	

	public Pose getPose(double elapsedTime) {
		return pose;
	}

	public Pose getVelocityPose(double elapsedTime) {
		return new Pose(0,0,0);
	}

	public double getTime() {
		return seconds;
	}

	public Pose getStartPose() {
		return pose;
	}

	public Pose getEndPose() {
		return pose;
	}
	
}
