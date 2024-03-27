package movement.util;

public class BoundedDisplacementCalculator {

	private double distance, time;
	private double MV, MA;
	
	public BoundedDisplacementCalculator(double targetDistance, double targetTime, double MV, double MA) {
		this.distance = targetDistance;
		this.time = targetTime;
		this.MV = MV;
		this.MA = MA;
		
		updateValue();
	}
	
	public double getDistance() {
		return distance;
	}
	
	public double getTime() {
		return time;
	}
	
	public double getMV() {
		return MV;
	}
	
	public double getMA() {
		return MA;
	}
	
	public double getDisplacement(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, time);
		
		double D = Math.abs(this.distance);
		double distance;
		
		double t_n = time - elapsedTime, t_a = MV/MA;
		if (time <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= time/2)
				distance = 0.5*MA*elapsedTime*elapsedTime;
			else
				distance = D - 0.5*MA*t_n*t_n;
		} else {
			// trapezoid graph
			if (elapsedTime <= time/2)
				distance = 0.5*(elapsedTime + Math.max(0, elapsedTime - t_a))* Math.min(MV, MA*elapsedTime);
			else
				distance = D - 0.5*(t_n + Math.max(0, t_n - t_a))* Math.min(MV, MA*t_n);
		}
		
		return distance * Math.signum(this.distance);
	}
	
	public double getVelocity(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, time);
		
		double velocity;
		
		double t_n = time - elapsedTime, t_a = MV/MA;
		if (time <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= time/2)
				velocity = MA*elapsedTime;
			else
				velocity = MA*t_n;
		} else {
			// trapezoid graph
			if (elapsedTime <= time/2)
				velocity = Math.min(MV, MA*elapsedTime);
			else
				velocity = Math.min(MV, MA*t_n);
		}
		
		return velocity * Math.signum(distance);
	}
	
	public void updateValue() {
		
		double T = time, D = Math.abs(distance);
		double t_a = MV/MA;
		double D_max;
		
		if (T < 2*t_a) {
			// triangle graph
			D_max = MA * T * T / 4.0;
		} 
		else {
			// trapezoid graph
			D_max = (T - MV/MA) * MV;
		}
		
		if (D >= D_max) {
			// no need to compensate MV
			D = D_max;
		} 
		else {
			// compensate MV
			MV = (T - Math.sqrt(T*T - 4*D/MA))/(2.0/MA);
		}
		
		distance = D * Math.signum(distance);
		time = T;
		
	}
	
	private static double bound(double x, double lower, double upper) {
		return Math.max(lower, Math.min(upper, x));
	}
	
}
