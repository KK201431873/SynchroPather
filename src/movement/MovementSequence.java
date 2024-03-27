package movement;
import java.util.ArrayList;

import movement.util.Movement;
import movement.util.Pose;

public class MovementSequence {
	
	private ArrayList<Movement> movements;
	private int length;
	private double time;
	private double[] times, partialTimes;
	
	public MovementSequence(MovementSequenceBuilder builder) {
		this.movements = builder.getMovements();
		init();
	}
	
	public int getLength() {
		return length;
	}
	
	public double getTime() {
		return time;
	}
	
	public Pose getPose(double elapsedTime) {
		int n = getLocalMovementIndex(elapsedTime);
		return movements.get(n).getPose(getLocalElapsedTime(elapsedTime));
	}
	
	public Pose getVelocityPose(double elapsedTime) {
		int n = getLocalMovementIndex(elapsedTime);
		return movements.get(n).getVelocityPose(getLocalElapsedTime(elapsedTime));
	}
	
	public Movement getMovement(int index) {
		return movements.get(index);
	}
	
	public Movement getMovement(double elapsedTime) {
		return movements.get(getLocalMovementIndex(elapsedTime));
	}
	
	public double getLocalElapsedTime(double elapsedTime) {
		int n = getLocalMovementIndex(elapsedTime);
		return elapsedTime - partialTimes[n];
	}
	
	public int getLocalMovementIndex(double elapsedTime) {
		int n = 0;
		while (n+1 < partialTimes.length && elapsedTime >= partialTimes[n+1]) n++;
		return n;
	}
	
	private void init() {
		length = movements.size();
		
		times = new double[length];
		partialTimes = new double[length];
		time = 0;
		for (int i = 0; i < length; i++) {
			double movementTime = movements.get(i).getTime();
			times[i] = movementTime;
			if (i < length)
				partialTimes[i] = time;
			time += movementTime;
		}
	}
	
}
