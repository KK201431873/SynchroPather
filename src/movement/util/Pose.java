package movement.util;

public class Pose {
	
	private final double x;
	private final double y;
	private final double heading;
	
	public Pose(double x, double y, double heading) {
	    this.x = x;
	    this.y = y;
	    this.heading = normalizeAngle(heading);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getHeading() {
		return heading;
	}
	
	public Pose plus(Pose addend) {
	    return new Pose(x + addend.x, y + addend.y, heading + addend.heading);
	}
	
	public Pose minus(Pose subtrahend) {
	    return new Pose(x - subtrahend.x, y - subtrahend.y, heading - subtrahend.heading);
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

	public String toString() {
		String res = String.format("(%s,%s,%s)", x, y, heading);
		return res;
	}

}