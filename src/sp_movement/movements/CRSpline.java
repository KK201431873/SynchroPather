package sp_movement.movements;

import java.util.ArrayList;
import java.util.Arrays;

import sp_constants.DriveConstants;
import sp_movement.util.BoundedDisplacementCalculator;
import sp_movement.util.DisplacementCalculator;
import sp_movement.util.Movement;
import sp_movement.util.MovementType;
import sp_movement.util.Pose;

/**
 * Movement containing the motion plan for a (Catmull-Rom "CR") spline trajectory with respect to elapsed time.
 */
public class CRSpline extends Movement {

	private double distance, time;
	private double[] lengths, times, partialTimes, props, partialProps;
	private Pose[] anchors;
	private DisplacementCalculator dispCalculator;
	private BoundedDisplacementCalculator[] turnCalculators;
	private double[] correctedHeadings;

	/**
	 * Creates a new CRSpline object with the given anchor Poses.
	 * @param anchors
	 */
	public CRSpline(ArrayList<Pose> anchors) {
		this.MOVEMENT_TYPE = MovementType.DRIVE;
		this.anchors = anchors.stream().toArray(Pose[]::new);
		init();
	}

	/**
	 * @return the number of anchor Poses in this CRSpline.
	 */
	public int getLength() {
		return anchors.length;
	}
	
	/**
	 * @return the total distance traveled by this CRSpline, in inches.
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * @return the ArrayList of anchor Poses in this CRSpline.
	 */
	public ArrayList<Pose> getAnchors() {
		return new ArrayList<>(Arrays.asList(anchors));
	}

	public Pose getPose(double elapsedTime) {
		int n = getLocalSegment(elapsedTime);
		double p_r = getLocalProportion(elapsedTime);
		
		Pose pose = getPose(n, p_r);

		int turnIndex = n;
		while (turnCalculators[turnIndex] == null) turnIndex--;
		double heading = correctedHeadings[turnIndex] + turnCalculators[turnIndex].getDisplacement(elapsedTime - partialTimes[turnIndex]);
		heading = normalizeAngle(heading);
		
		return new Pose(pose.getX(), pose.getY(), heading);
	}

	public Pose getVelocityPose(double elapsedTime) {
		// get theta
		int n = getLocalSegment(elapsedTime);
		double p_r = getLocalProportion(elapsedTime);
		Pose derivative = getDerivative(n, p_r);
		
		double theta = Math.atan2(derivative.getY(), derivative.getX());
		double speed = dispCalculator.getVelocity(elapsedTime);

		int turnIndex = n;
		while (turnCalculators[turnIndex] == null) turnIndex--;
		double angularVelocity = -turnCalculators[turnIndex].getVelocity(elapsedTime - partialTimes[turnIndex]);
		
		return new Pose(
				speed * Math.cos(theta),
				speed * Math.sin(theta),
				angularVelocity
		);
	}
	
	public double getTime() {
		return time;
	}
	
	public Pose getStartPose() {
		return getLength()>0 ? anchors[0] : null;
	}

	public Pose getEndPose() {
		return getLength()>0 ? anchors[getLength()-1] : null;
	}
	
	public String getName() {
		return "CRSpline";
	}
	
	/**
	 * Gets the Pose at parameter 0<=t<=1 of the given spline segment.
	 * @param segment
	 * @param t
	 * @return the Pose within the segment.
	 */
	public Pose getPose(int segment, double t) {
		if (segment < 0 || getLength()-2 < segment)
			throw new RuntimeException(String.format("Segment index %s outside of [%s,%s]", segment, 0, getLength()-2));

		Pose p0 = anchors[Math.max(0, segment-1)];
		Pose p1 = anchors[segment];
		Pose p2 = anchors[segment + 1];
		Pose p3 = anchors[Math.min(getLength()-1, segment+2)];
		
		double tt = t*t;
		double ttt = tt*t;

		double q0 = -ttt + 2*tt - t;
		double q1 = 3*ttt - 5*tt + 2;
		double q2 = -3*ttt + 4*tt + t;
		double q3 = ttt - tt;

		double tx = 0.5 * (p0.getX()*q0 + p1.getX()*q1 + p2.getX()*q2 + p3.getX()*q3);
		double ty = 0.5 * (p0.getY()*q0 + p1.getY()*q1 + p2.getY()*q2 + p3.getY()*q3);
		
		return new Pose(tx, ty, 0);
	}
	
	/**
	 * Gets the derivative at parameter t (between 0 and 1) of the given spline segment.
	 * @param segment
	 * @param t
	 * @return the Pose representation of the derivative within the segment.
	 */
	public Pose getDerivative(int segment, double t) {
		if (segment < 0 || getLength()-2 < segment)
			throw new RuntimeException(String.format("Segment index %s outside of [%s,%s]", segment, 0, getLength()-2));

		Pose p0 = anchors[Math.max(0, segment-1)];
		Pose p1 = anchors[segment];
		Pose p2 = anchors[segment + 1];
		Pose p3 = anchors[Math.min(getLength()-1, segment+2)];
		
		double tt = t*t;

		double q0 = -3*tt + 4*t - 1;
		double q1 = 9*tt - 10*t;
		double q2 = -9*tt + 8*t + 1;
		double q3 = 3*tt - 2*t;

		double tx = 0.5 * (p0.getX()*q0 + p1.getX()*q1 + p2.getX()*q2 + p3.getX()*q3);
		double ty = 0.5 * (p0.getY()*q0 + p1.getY()*q1 + p2.getY()*q2 + p3.getY()*q3);
		
		return new Pose(tx, ty, 0);
	}
	
	/**
	 * Gets the index of the spline segment being traveled at the given elapsed time.
	 * @param elapsedTime
	 * @return the index of the spline segment.
	 */
	public int getLocalSegment(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, time);
		
		double dx = dispCalculator.getDisplacement(elapsedTime);
		double p_x = distance!=0 ? dx / distance : 0;
		
		int n = 0;
		while (n+1 < partialProps.length && p_x >= partialProps[n+1]) n++;
		
		return n;
	}
	
	/**
	 * Gets the proportion (between 0 and 1) of distance traveled within the local spline segment at the given elapsed time.
	 * @param elapsedTime
	 * @return the proportion of distance traveled.
	 */
	public double getLocalProportion(double elapsedTime) {
		double dx = dispCalculator.getDisplacement(elapsedTime);
		int n = getLocalSegment(elapsedTime);
		
		double delta_t = DriveConstants.delta_t;
		double p_r = 0;
		double localDisplacement = 0;
		Pose lastPose = getPose(n,0);
		while (localDisplacement < dx - partialProps[n] * distance) {
			p_r += delta_t;
			Pose currentPose = getPose(n, p_r);
			localDisplacement += Math.hypot(currentPose.getX()-lastPose.getX(), currentPose.getY()-lastPose.getY());
			lastPose = currentPose;
		}
		
		return p_r;
	}
	
	/**
	 * Calculates total time, total distance, and corrected headings.
	 */
	private void init() {
		
		lengths = new double[Math.max(0, anchors.length-1)];

		// calculate distance
		distance = 0;
		double delta_t = DriveConstants.delta_t;
		double x = anchors[0].getX();
		double y = anchors[0].getY();
		for (int i = 0; i < anchors.length-1; i++) {
			double length = 0;
			for (double t = 0; t <= 1; t += delta_t) {
				// integrate distances over time
				Pose currentPose = getPose(i, t);
				double deltaDistance = Math.hypot(currentPose.getX()-x, currentPose.getY()-y);
				distance += deltaDistance;
				length += deltaDistance;
				x = currentPose.getX();
				y = currentPose.getY();
			}
			lengths[i] = length;
		}

		dispCalculator = new DisplacementCalculator(distance, DriveConstants.MAX_VELOCITY, DriveConstants.MAX_ACCELERATION);
		time = dispCalculator.getTime();

		turnCalculators = new BoundedDisplacementCalculator[Math.max(0, anchors.length-1)];
		
		times = new double[Math.max(0, anchors.length-1)];
		partialTimes = new double[Math.max(0, anchors.length-1)];
		props = new double[Math.max(0, anchors.length-1)];
		partialProps = new double[Math.max(0, anchors.length-1)];
		
		correctedHeadings = new double[anchors.length];
		correctedHeadings[0] = anchors[0].getHeading();
		
		// calculate props, time
		double partialLength = 0;
		double partialTime = 0;
		for (int i = 0; i < lengths.length; i++) {
			// calculate proportions
			partialProps[i] = partialLength / distance;
			props[i] = lengths[i] / distance;
			partialLength += lengths[i];
			
			// calculate times
			double currentPartialTime = dispCalculator.getElapsedTime(partialLength);
			partialTimes[i] = partialTime;
			times[i] = currentPartialTime - partialTime;
			partialTime = currentPartialTime;
		}
		
		// create turn calculators
		double h = anchors[0].getHeading();
		double corrected_h = anchors[0].getHeading();
		double MAV = DriveConstants.MAX_ANGULAR_VELOCITY;
		double MAA = DriveConstants.MAX_ANGULAR_ACCELERATION;
		for (int i = 0; i < lengths.length; i++) {
			// create turn calculator
			int index = i;
			double corrected_delta_h = normalizeAngle(anchors[i+1].getHeading() - corrected_h);
			
			// skip if no change in heading
			if (corrected_delta_h == 0) {
				turnCalculators[index] = new BoundedDisplacementCalculator(0, 1, 1, 1);
				correctedHeadings[index+1] = corrected_h;
				continue;
			}
			
			// get max time available for completing turn
			double maxTime = 0;
			double delta_h = normalizeAngle(anchors[i+1].getHeading() - h);
			do {
				// get change in heading since last
				h += delta_h;
				maxTime += times[i];
				correctedHeadings[i] = correctedHeadings[index];
				
				// set non-turning segments to null
				turnCalculators[i] = null;
				
				i++;
				if (i == lengths.length) break;
				delta_h = normalizeAngle(anchors[i+1].getHeading() - h);
			}
			while (i < lengths.length && delta_h == 0);
			i--;
			
			// create turn calculator for the original segment, bounded by the max time
			if (i == lengths.length-1) {
				// it is the last segment, make sure robot reaches final pose
				DisplacementCalculator turnTimer = new DisplacementCalculator(corrected_delta_h, MAV, MAA);
				times[times.length-1] += Math.max(0, turnTimer.getTime() - maxTime);
				time += Math.max(0, turnTimer.getTime() - maxTime);
				maxTime = turnTimer.getTime();
			}
			turnCalculators[index] = new BoundedDisplacementCalculator(corrected_delta_h, maxTime, MAV, MAA);
			
			// update heading values for next iteration
			corrected_h += turnCalculators[index].getTotalDisplacement();
			correctedHeadings[i+1] = corrected_h;
		}
		
	}
	
	public String toString() {
		String res = "[";
		for (int i = 0; i < getLength(); i++)
			res += String.format("%s%s", anchors[i], (i==getLength()-1 ? "" : ", "));
		return res + "]";
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

    /**
     * Normalizes a given angle to [-180,180) degrees.
     * @param degrees the given angle in degrees.
     * @return the normalized angle in degrees.
     */
    private double normalizeAngle(double degrees) {
        double angle = degrees;
        while (angle <= -Math.PI) //TODO: opMode.opModeIsActive() && 
            angle += 2*Math.PI;
        while (angle > Math.PI) //TODO: opMode.opModeIsActive() && 
            angle -= 2*Math.PI;
        return angle;
    }

}
