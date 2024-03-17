package movement.util;

public class DisplacementCalculator {

	private double distance, time;
	private final double MV, MA;
	
	public DisplacementCalculator(double distance, double MV, double MA) {
		this.distance = distance;
		this.MV = MV;
		this.MA = MA;
		updateValues();
	}
	
	public double getDistance() {
		return distance;
	}
	
	public double getTime() {
		return time;
	}
	
	public double getDisplacement(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, time);
		
		double t_n = time - elapsedTime, t_a = MV/MA;
		if (time <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= time/2)
				return 0.5*MA*elapsedTime*elapsedTime;
			else
				return distance - 0.5*MA*t_n*t_n;
		} else {
			// trapezoid graph
			if (elapsedTime <= time/2)
				return 0.5*(elapsedTime + Math.max(0, elapsedTime - t_a))* Math.min(MV, MA*elapsedTime);
			else
				return distance - 0.5*(t_n + Math.max(0, t_n - t_a))* Math.min(MV, MA*t_n);
		}
	}
	
	private void updateValues() {
		double d_a = 0.5 * MV*MV / MA;
		if (distance / 2 <= d_a) {
			// triangle graph
			time = 2*Math.sqrt(distance/MA);
		} else {
			// trapezoid graph
			time = distance/MV + MV/MA;
		}
	}
	
	private static double bound(double x, double lower, double upper) {
		return Math.max(lower, Math.min(upper, x));
	}
	
}
