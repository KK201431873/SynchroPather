package movement.util;

public class Pose {
	
	private final double x;
	private final double y;
	private final double heading;
	
	public Pose(double x, double y, double heading) {
	    this.x = x;
	    this.y = y;
	    this.heading = heading;
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
	
	public boolean isEqualTo(Pose comparison) {
		return x==comparison.getX() &&
				y==comparison.getY() &&
				heading==comparison.getHeading();
	}
	
	public Pose plus(Pose addend) {
	    return new Pose(x + addend.x, y + addend.y, heading + addend.heading);
	}
	
	public Pose minus(Pose subtrahend) {
	    return new Pose(x - subtrahend.x, y - subtrahend.y, heading - subtrahend.heading);
	}

	public String toString() {
		String res = String.format("(%s,%s,%s)", x, y, heading);
		return res;
	}

}