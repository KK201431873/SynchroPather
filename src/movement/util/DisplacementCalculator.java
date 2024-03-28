package movement.util;

public class DisplacementCalculator {

	private double distance, time, sign;
	private final double MV, MA;
	
	public DisplacementCalculator(double targetDisplacement, double MV, double MA) {
		this.sign = Math.signum(targetDisplacement);
		this.distance = Math.abs(targetDisplacement);
		this.MV = MV;
		this.MA = MA;
		init();
	}
	
	public double getTotalDistance() {
		return distance;
	}
	
	public double getTotalDisplacement() {
		return distance * sign;
	}
	
	public double getTime() {
		return time;
	}
	
	public double getElapsedTime(double displacement) {
		displacement = bound(displacement * sign, 0, distance); // positive in [0, distance]
		
		double time;
		double d_n = distance - displacement, d_a = 0.5 * MV*MV/MA, t_a = MV/MA;
		if (this.time <= 2*t_a) {
			// triangle graph
			if (displacement <= distance/2)
				time = Math.sqrt(2*displacement/MA);
			else
				time = this.time - Math.sqrt(2*d_n/MA);
		} 
		else {
			// trapezoid graph
			if (displacement <= distance/2)
				if (displacement <= d_a)
					// in first slope
					time = Math.sqrt(2*displacement/MA);
				else
					// in first plateau
					time = displacement/MV + t_a/2d;
			else
				if (d_n <= d_a)
					// in last slope
					time = this.time - Math.sqrt(2*d_n/MA);
				else
					// in second plateau
					time = this.time - (d_n/MV + t_a/2d);
		}
		
		return time;
	}
	
	public double getDisplacement(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, time);
		
		double displacement;
		double t_n = time - elapsedTime, t_a = MV/MA;
		if (time <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= time/2)
				displacement = 0.5*MA*elapsedTime*elapsedTime;
			else
				displacement = distance - 0.5*MA*t_n*t_n;
		} else {
			// trapezoid graph
			if (elapsedTime <= time/2)
				displacement = 0.5*(elapsedTime + Math.max(0, elapsedTime - t_a))* Math.min(MV, MA*elapsedTime);
			else
				displacement = distance - 0.5*(t_n + Math.max(0, t_n - t_a))* Math.min(MV, MA*t_n);
		}
		
		displacement *= sign;
		
		return displacement;
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
		
		velocity *= sign;
		
		return velocity;
	}
	
	private void init() {
		
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