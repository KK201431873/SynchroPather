package movement.util;

import java.util.ArrayList;
import java.util.Collections;

public class DisplacementCalculator {

	private double totalDistance, totalTime;
	private final double MV, MA;
	private ArrayList<Double> distanceMarkers, temporalMarkers;
	private boolean isTrapezoidal;
	
	public DisplacementCalculator(double distance, double MV, double MA) {
		this.distanceMarkers = new ArrayList<>();
		this.temporalMarkers = new ArrayList<>();
		isTrapezoidal = false;

		this.distanceMarkers.add(distance);

		this.totalDistance = 0;
		this.totalTime = 0;
		this.MV = MV;
		this.MA = MA;
		
		updateValues();
	}
	
	public double getDistance() {
		return totalDistance;
	}
	
	public double getTime() {
		return totalTime;
	}
	
	public double getDisplacement(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, totalTime);
		
		double t_n = totalTime - elapsedTime, t_a = MV/MA;
		if (totalTime <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= totalTime/2)
				return 0.5*MA*elapsedTime*elapsedTime;
			else
				return totalDistance - 0.5*MA*t_n*t_n;
		} else {
			// trapezoid graph
			if (elapsedTime <= totalTime/2)
				return 0.5*(elapsedTime + Math.max(0, elapsedTime - t_a))* Math.min(MV, MA*elapsedTime);
			else
				return totalDistance - 0.5*(t_n + Math.max(0, t_n - t_a))* Math.min(MV, MA*t_n);
		}
	}
	
	public double getVelocity(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, totalTime);
		
		double t_n = totalTime - elapsedTime, t_a = MV/MA;
		if (totalTime <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= totalTime/2)
				return MA*elapsedTime;
			else
				return MA*t_n;
		} else {
			// trapezoid graph
			if (elapsedTime <= totalTime/2)
				return Math.min(MV, MA*elapsedTime);
			else
				return Math.min(MV, MA*t_n);
		}
	}
	
	private void updateValues() {
		double d_a = 0.5 * MV*MV / MA;
		
		// get total distance
		Collections.sort(distanceMarkers);
		totalDistance = distanceMarkers.get(distanceMarkers.size()-1);
		
		// get total time
		isTrapezoidal = totalDistance / 2 > d_a;
		double theoretical_MV;
		if (isTrapezoidal) {
			// trapezoid graph
			totalTime = totalDistance/MV + MV/MA;
			theoretical_MV = MV;
		} else {
			// triangle graph
			totalTime = 2*Math.sqrt(totalDistance/MA);
			theoretical_MV = 0.5 * totalTime * MA;
		}
		
		// get time markers
		temporalMarkers.clear();
		for (int i = 0; i < distanceMarkers.size(); i++) {
			// get positive and negative distances
			double d = distanceMarkers.get(i);
			double d_n = totalDistance - d;
			double t;
			
			if (isTrapezoidal) {
				// trapezoid graph
				if (d < d_a) {
					// in the first acceleration triangle
					t = Math.sqrt(2*d / MA);
				}
				else if (d_n < d_a) {
					// in the second acceleration triangle
					t = Math.sqrt(2*d_n / MA);
				}
				else {
					// in the rectangle
					t = (d - d_n) / MV;
				}
			} else {
				// triangular graph
				if (d < 0.5 * totalDistance) {
					// in the first acceleration triangle
					t = Math.sqrt(2*d / MA);
				}
				else {
					// in the second acceleration triangle
					t = Math.sqrt(2*d_n / MA);
				}
			}
			
			temporalMarkers.add(t);
		}
		
		
	}
	
	private static double bound(double x, double lower, double upper) {
		return Math.max(lower, Math.min(upper, x));
	}
	
}
