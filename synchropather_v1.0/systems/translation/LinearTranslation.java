package synchropather.systems.translation;

import synchropather.DriveConstants;
import synchropather.systems.MovementType;
import synchropather.systems.__util__.TimeSpan;
import synchropather.systems.__util__.calculators.StretchedDisplacementCalculator;
import synchropather.systems.__util__.superclasses.Movement;

/**
 * Movement for planning a linear translation.
 */
public class LinearTranslation extends Movement {
	
	private double distance, minDuration;
	private TranslationState start, end;
	private StretchedDisplacementCalculator calculator;
	
	
	/**
	 * Creates a new LinearTranslation object with a given start and end TranslationState allotted for the given TimeSpan.
	 * @param start
	 * @param end
	 * @param timeSpan
	 */
	public LinearTranslation(TimeSpan timeSpan, TranslationState start, TranslationState end) {
		super(timeSpan, MovementType.TRANSLATION);
		this.start = start;
		this.end = end;
		init();
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
	
	@Override
	public double getMinDuration() {
		return minDuration;
	}

	/**
	 * @return the TranslationState of this Movement at the start time.
	 */
	@Override
	public TranslationState getStartState() {
		return start;
	}

	/**
	 * @return the TranslationState of this Movement at the end time.
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
		calculator = new StretchedDisplacementCalculator(distance, timeSpan, MV, MA);
		
		minDuration = calculator.getMinDuration();
	}

}
