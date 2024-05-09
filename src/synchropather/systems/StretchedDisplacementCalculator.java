package synchropather.systems;

/**
 * Object that calculates position based on elapsed time from a velocity curve defined by displacement, time and adjusted max velocity, and max acceleration.
 */
public class StretchedDisplacementCalculator extends DisplacementCalculator{

	private double distance, minDuration, duration, sign;
	private double MV, MA;

	/**
	 * Creates a new StretchedDisplacementCalculator with a given target, duration, and kinematic constraints.
	 * @param targetDisplacement
	 * @param targetDuration
	 * @param MV
	 * @param MA
	 */
	public StretchedDisplacementCalculator(double targetDisplacement, double targetDuration, double MV, double MA) {
		super(targetDisplacement, MV, MA);
		this.duration = targetDuration;
		init();
	}

	/**
	 * @return the absolute value of the target displacement.
	 */
	public double getTotalDistance() {
		return distance;
	}

	/**
	 * @return the target displacement.
	 */
	public double getTotalDisplacement() {
		return distance * sign;
	}

	/**
	 * @return the minimum time needed to reach the target displacement value.
	 */
	public double getMinDuration() {
		return minDuration;
	}

	/**
	 * @return the user-set time needed to reach the target displacement value.
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Sets a new duration and finds max velocity accordingly.
	 */
	public void setDuration(double newDuration) {
		duration = newDuration;
		
		/// catch error for when time < min_time
		if (duration < minDuration) {
			throw new RuntimeException(
					String.format("TimeSpan duration %s is less than the minimum needed time %s.", 
							duration,
							minDuration
					)
			);
		}

		/// calculate MV
		// we now know that time >= min_time, so we might need to stretch the graph
		// we use quadratic formula to find MV (minus root)
		double a, b, c;
		a = 1/MA;
		b = -duration;
		c = distance;
		MV = (-b - Math.sqrt(b*b - 4*a*c))/(2*a);
		
	}

	/**
	 * Calculates the displacement at a certain elapsed time.
	 * @param elapsedTime 
	 * @return the displacement value the given elapsed time.
	 */
	public double getDisplacement(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, duration);
		
		double D = Math.abs(this.distance);
		double displacement;
		
		double t_n = duration - elapsedTime, t_a = MV/MA;
		if (duration <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= duration/2)
				displacement = 0.5*MA*elapsedTime*elapsedTime;
			else
				displacement = D - 0.5*MA*t_n*t_n;
		} 
		else {
			// trapezoid graph
			if (elapsedTime <= duration/2)
				displacement = 0.5*(elapsedTime + Math.max(0, elapsedTime - t_a))* Math.min(MV, MA*elapsedTime);
			else
				displacement = D - 0.5*(t_n + Math.max(0, t_n - t_a))* Math.min(MV, MA*t_n);
		}
		
		displacement *= sign;
		
		return displacement;
	}

	/**
	 * Calculates the velocity at a certain elapsed time.
	 * @param elapsedTime 
	 * @return the velocity value the given elapsed time.
	 */
	public double getVelocity(double elapsedTime) {
		if (distance == 0) return 0;
		elapsedTime = bound(elapsedTime, 0, duration);
		
		double velocity;
		
		double t_n = duration - elapsedTime, t_a = MV/MA;
		if (duration <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= duration/2)
				velocity = MA*elapsedTime;
			else
				velocity = MA*t_n;
		} 
		else {
			// trapezoid graph
			if (elapsedTime <= duration/2)
				velocity = Math.min(MV, MA*elapsedTime);
			else
				velocity = Math.min(MV, MA*t_n);
		}
		
		velocity *= sign;
		
		return velocity;
	}

	/**
	 * Calculates min time and max velocity.
	 */
	public void init() {
		
		/// calculate min_time
		// time is the given duration
		// min_time is the minimum needed time
		double d_a = 0.5 * MV*MV / MA;
		if (distance / 2 <= d_a) {
			// triangle graph
			minDuration = 2*Math.sqrt(distance/MA);
		} else {
			// trapezoid graph
			minDuration = distance/MV + MV/MA;
		}
		
		setDuration(duration);
		
	}

	/**
	 * Clips the input x between a given lower and upper bound.
	 * @param x
	 * @param lower
	 * @param upper
	 * @return the clipped value of x.
	 */
	private static double bound(double x, double lower, double upper) {
		return Math.max(lower, Math.min(upper, x));
	}
	
}
