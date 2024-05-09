package synchropather.systems.translation;

import synchropather.DriveConstants;
import synchropather.systems.Movement;
import synchropather.systems.StretchedDisplacementCalculator;
import synchropather.systems.TimeSpan;

/**
 * Movement for planning a linear translation.
 */
public class LinearTranslation extends Movement {
	
	private double distance, duration, minDuration;
	private TranslationState start, end;
	private TimeSpan timeSpan;
	private StretchedDisplacementCalculator calculator;
	
	
	/**
	 * Creates a new LinearTranslation object with a given start and end TranslationState alloted for the given TimeSpan.
	 * @param start
	 * @param end
	 * @param timeSpan
	 */
	public LinearTranslation(TranslationState start, TranslationState end, TimeSpan timeSpan) {
		this.MOVEMENT_TYPE = MovementType.TRANSLATION;
		this.start = start;
		this.end = end;
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

	/**
	 * @return the indicated TranslationState.
	 */
	@Override
	public TranslationState getState(double elapsedTime) {
		double t = distance!=0 ? calculator.getDisplacement(elapsedTime) / distance : 0;

		double q0 = 1 - t;
		double q1 = t;

		// linear interpolation
		return start.times(q0).plus(end.times(q1));
	}

	/**
	 * @return the indicated velocity TranslationState.
	 */
	@Override
	public TranslationState getVelocity(double elapsedTime) {
		double theta = end.minus(start).theta();
		double speed = calculator.getVelocity(elapsedTime);
		
		// scaled velocity vector
		return new TranslationState(speed, theta, true);
	}

	/**
	 * @return the TranslationState of this Movement at time zero.
	 */
	@Override
	public TranslationState getStartState() {
		return start;
	}

	/**
	 * @return the TranslationState reached by the end of this Movement.
	 */
	@Override
	public TranslationState getEndState() {
		return end;
	}

	/**
	 * @return "LinearTranslation"
	 */
	@Override
	public String getDisplayName() {
		return "LinearTranslation";
	}
	
	/**
	 * Calculates total time.
	 */
	private void init() {
		distance = end.minus(start).hypot();

		double MV = DriveConstants.MAX_VELOCITY;
		double MA = DriveConstants.MAX_ACCELERATION;
		
		// create calculator object
		calculator = new StretchedDisplacementCalculator(distance, timeSpan.getDuration(), MV, MA);
		
		duration = calculator.getDuration();
		minDuration = calculator.getMinDuration();
		
	}

}
