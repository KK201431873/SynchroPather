package movement;
import java.util.ArrayList;

import util.Movement;
import util.Pose;

public class MovementSequence {
	
	private ArrayList<Movement> movements;
	private int length;
	private double time;
	private double[] times, partialTimes;
	
	public MovementSequence(ArrayList<Movement> movements) {
		this.movements = movements;
		updateValues();
	}
	
	public double getTime() {
		return time;
	}
	
	public Pose getPose(double elapsedTime) {
		int n = getIndex(elapsedTime);
		
		return movements.get(n).getPose(elapsedTime - partialTimes[n]);
	}
	
	public Movement getMovement(double elapsedTime) {
		return movements.get(getIndex(elapsedTime));
	}
	
	private int getIndex(double elapsedTime) {
		int n = 0;
		while (n+1 < partialTimes.length && elapsedTime >= partialTimes[n+1]) n++;
		
		return n;
	}
	
	private void updateValues() {
		length = movements.size();
		
		times = new double[length];
		partialTimes = new double[Math.max(0, length-1)];
		time = 0;
		for (int i = 0; i < length; i++) {
			double movementTime = movements.get(i).getTime();
			times[i] = movementTime;
			if (i < length-1)
				partialTimes[i] = time;
			time += movementTime;
		}
	}
	
}
