package synchropather.systems.translation;

import java.util.ArrayList;

import synchropather.DriveConstants;
import synchropather.systems.Movement;
import synchropather.systems.StretchedDisplacementCalculator;
import synchropather.systems.TimeSpan;

/**
 * Movement for planning a Catmull-Rom spline translation.
 */
public class CRSplineTranslation extends Movement {
	
	private double distance, duration, minDuration;
	private double[] lengths, times, partialTimes, props, partialProps;
	private ArrayList<TranslationState> anchors;
	private TimeSpan timeSpan;
	private StretchedDisplacementCalculator calculator;

	
	/**
	 * Creates a new CRSplineTranslation object with the given anchor TranslationStates alloted for the given TimeSpan.
	 * @param start
	 * @param end
	 * @param timeSpan
	 */
	public CRSplineTranslation(ArrayList<TranslationState> anchors, TimeSpan timeSpan) {
		this.MOVEMENT_TYPE = MovementType.TRANSLATION;
		this.anchors = anchors;
		this.timeSpan = timeSpan;
		init();
	}

	@Override
	public double getStartTime() {
		return timeSpan.getStartTime();
	}

	@Override
	public double getEndTime() {
		return timeSpan.getEndTime();
	}

	@Override
	public double getMinDuration() {
		return minDuration;
	}

	@Override
	public double getDuration() {
		return duration;
	}

	@Override
	public TranslationState getState(double elapsedTime) {
		int n = getLocalSegment(elapsedTime);
		double p_r = getLocalProportion(elapsedTime);
		
		return getState(n, p_r);
	}

	@Override
	public TranslationState getVelocity(double elapsedTime) {
		// get theta
		int n = getLocalSegment(elapsedTime);
		double p_r = getLocalProportion(elapsedTime);
		TranslationState derivative = getDerivative(n, p_r);

		double speed = calculator.getVelocity(elapsedTime);
		double theta = Math.atan2(derivative.getY(), derivative.getX());

		return new TranslationState(speed, theta, true);
	}

	/**
	 * @return the TranslationState of this Movement at time zero.
	 */
	@Override
	public TranslationState getStartState() {
		return getLength()>0 ? anchors.get(0) : null;
	}

	/**
	 * @return the TranslationState reached by the end of this Movement.
	 */
	@Override
	public TranslationState getEndState() {
		return getLength()>0 ? anchors.get(getLength()-1) : null;
	}

	/**
	 * @return "CRSplineTranslation"
	 */
	@Override
	public String getDisplayName() {
		return "CRSplineTranslation";
	}

	/**
	 * @return the number of anchor TranslationStates in this CRSpline.
	 */
	public int getLength() {
		return anchors.size();
	}
	
	/**
	 * @return the total distance traveled by this CRSpline, in inches.
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * Gets the TranslationState at parameter 0<=t<=1 of the given spline segment.
	 * @param segment
	 * @param t
	 * @return the indicated TranslationState.
	 */
	public TranslationState getState(int segment, double t) {
		if (segment < 0 || getLength()-2 < segment)
			throw new RuntimeException(String.format("Segment index %s outside of [%s,%s]", segment, 0, getLength()-2));

		TranslationState p0 = anchors.get(Math.max(0, segment-1));
		TranslationState p1 = anchors.get(segment);
		TranslationState p2 = anchors.get(segment + 1);
		TranslationState p3 = anchors.get(Math.min(getLength()-1, segment+2));
		
		double tt = t*t;
		double ttt = tt*t;

		double q0 = -ttt + 2*tt - t;
		double q1 = 3*ttt - 5*tt + 2;
		double q2 = -3*ttt + 4*tt + t;
		double q3 = ttt - tt;
		
		return (p0.times(q0)) .plus 
				(p1.times(q1)) .plus 
				(p2.times(q2)) .plus 
				(p3.times(q3)) 
				.times(0.5);
	}
	
	/**
	 * Gets the derivative at parameter t (between 0 and 1) of the given spline segment.
	 * @param segment
	 * @param t
	 * @return the TranslationState representation of the derivative within the segment.
	 */
	public TranslationState getDerivative(int segment, double t) {
		if (segment < 0 || getLength()-2 < segment)
			throw new RuntimeException(String.format("Segment index %s outside of [%s,%s]", segment, 0, getLength()-2));

		TranslationState p0 = anchors.get(Math.max(0, segment-1));
		TranslationState p1 = anchors.get(segment);
		TranslationState p2 = anchors.get(segment + 1);
		TranslationState p3 = anchors.get(Math.min(getLength()-1, segment+2));
		
		double tt = t*t;

		double q0 = -3*tt + 4*t - 1;
		double q1 = 9*tt - 10*t;
		double q2 = -9*tt + 8*t + 1;
		double q3 = 3*tt - 2*t;
		
		return (p0.times(q0)) .plus 
				(p1.times(q1)) .plus 
				(p2.times(q2)) .plus 
				(p3.times(q3)) 
				.times(0.5);
	}
	
	/**
	 * Gets the index of the spline segment being traveled at the given elapsed time.
	 * @param elapsedTime
	 * @return the index of the spline segment.
	 */
	public int getLocalSegment(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, duration);
		
		double dx = calculator.getDisplacement(elapsedTime);
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
		double dx = calculator.getDisplacement(elapsedTime);
		int n = getLocalSegment(elapsedTime);
		
		double delta_t = DriveConstants.delta_t;
		double p_r = 0;
		double localDisplacement = 0;
		TranslationState lastPose = getState(n,0);
		while (localDisplacement < dx - partialProps[n] * distance) {
			p_r += delta_t;
			TranslationState currentPose = getState(n, p_r);
			localDisplacement += Math.hypot(currentPose.getX()-lastPose.getX(), currentPose.getY()-lastPose.getY());
			lastPose = currentPose;
		}
		
		return p_r;
	}
	
	/**
	 * Calculates total distance and total time.
	 */
	private void init() {
		
		lengths = new double[Math.max(0, getLength()-1)];

		// calculate distance
		distance = 0;
		double delta_t = DriveConstants.delta_t;
		double x = anchors.get(0).getX();
		double y = anchors.get(0).getY();
		for (int i = 0; i < getLength()-1; i++) {
			double length = 0;
			for (double t = 0; t <= 1; t += delta_t) {
				// integrate distances over time
				TranslationState currentPose = getState(i, t);
				double deltaDistance = Math.hypot(currentPose.getX()-x, currentPose.getY()-y);
				distance += deltaDistance;
				length += deltaDistance;
				x = currentPose.getX();
				y = currentPose.getY();
			}
			lengths[i] = length;
		}

		// create calculator object
		calculator = new StretchedDisplacementCalculator(distance, timeSpan.getDuration(), DriveConstants.MAX_VELOCITY, DriveConstants.MAX_ACCELERATION);

		duration = calculator.getDuration();
		minDuration = calculator.getMinDuration();

		times = new double[Math.max(0, getLength()-1)];
		partialTimes = new double[Math.max(0, getLength()-1)];
		props = new double[Math.max(0, getLength()-1)];
		partialProps = new double[Math.max(0, getLength()-1)];
		
		// calculate props, time
		double partialLength = 0;
		double partialTime = 0;
		for (int i = 0; i < lengths.length; i++) {
			// calculate proportions
			partialProps[i] = partialLength / distance;
			props[i] = lengths[i] / distance;
			partialLength += lengths[i];
			
			// calculate times
			double currentPartialTime = calculator.getElapsedTime(partialLength);
			partialTimes[i] = partialTime;
			times[i] = currentPartialTime - partialTime;
			partialTime = currentPartialTime;
		}
		
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
