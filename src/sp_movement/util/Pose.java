package sp_movement.util;

/**
 * Object containing three coordinates representing the global position of a robot on the field.
 */
public class Pose {
	
	private final double x;
	private final double y;
	private final double heading, normalizedHeading;
	
	/**
	 * Creates a new Pose object with given values (heading is not normalized).
	 * @param x inches
	 * @param y inches
	 * @param heading radians
	 */
	public Pose(double x, double y, double heading) {
	    this.x = x;
	    this.y = y;
	    this.heading = heading;
	    this.normalizedHeading = normalizeAngle(heading);
	}
	
	/**
	 * @return this Pose's x coordinate.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return this Pose's y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return this Pose's heading.
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * @return this Pose's normalized heading.
	 */
	public double getNormalizedHeading() {
		return normalizedHeading;
	}

	/**
	 * @return if this Pose's coordinates are equal to the given Pose (compares normalized headings).
	 */
	public boolean isEqualTo(Pose comparison) {
		return x==comparison.getX() &&
				y==comparison.getY() &&
				normalizedHeading==comparison.getNormalizedHeading();
	}

	/**
	 * @return the matrix addition of this Pose and the given Pose (heading is not normalized).
	 */
	public Pose plus(Pose addend) {
	    return new Pose(x + addend.x, y + addend.y, heading + addend.heading);
	}

	/**
	 * @return the matrix subtraction of this Pose and the given Pose (heading is not normalized).
	 */
	public Pose minus(Pose subtrahend) {
	    return new Pose(x - subtrahend.x, y - subtrahend.y, heading - subtrahend.heading);
	}

	/**
	 * @return a String containing an ordered tuple representation of this Pose (x,y,h).
	 */
	public String toString() {
		String res = String.format("(%s,%s,%s)", x, y, heading);
		return res;
	}
	
	/**
	 * Normalizes a given angle to [-pi,pi) radians.
	 * @param degrees the given angle in radians.
	 * @return the normalized angle in radians.
	 */
	private double normalizeAngle(double degrees) {
	    double angle = degrees;
	    while (angle <= -Math.PI) //TODO: opMode.opModeIsActive() && 
	        angle += 2*Math.PI;
	    while (angle > Math.PI)
	        angle -= 2*Math.PI;
	    return angle;
	}

}