package ll_movement;

import java.util.ArrayList;

import ll_movement.movements.CRSpline;
import ll_movement.movements.Delay;
import ll_movement.movements.StraightLine;
import ll_movement.movements.Turn;
import ll_movement.util.Movement;
import ll_movement.util.Pose;

/**
 * Builder pattern object that can create and store the Movements of a MovementSequence.
 */
public class MovementSequenceBuilder {

	private ArrayList<Movement> movements;
	private Pose lastPose;

	
	/////////////////////////////
	// INSTANTIATION OVERLOADS //
	/////////////////////////////
	
	/**
	 * Creates a new MovementSequenceBuilder object with the given start Pose.
	 * @param x inches
	 * @param y inches
	 * @param heading degrees
	 */
	public MovementSequenceBuilder(double x, double y, double heading) {
		this(new Pose(x, y, heading * Math.PI / 180));
	}
	
	/**
	 * Creates a new MovementSequenceBuilder object with the given start Pose.
	 * @param startPose
	 */
	public MovementSequenceBuilder(Pose startPose) {
		movements = new ArrayList<>();
		lastPose = startPose;
	}
	

	/////////////////////
	// PRIMARY METHODS //
	/////////////////////
	
	/**
	 * Builds this MovementSequenceBuilder object into a MovementSequence object.
	 * @return the built MovementSequence object.
	 */
	public MovementSequence build() {
		return new MovementSequence(movements);
	}
	
	/**
	 * @return the ArrayList of Movements for this MovementSequenceBuilder.
	 */
	public ArrayList<Movement> getMovements() {
		return movements;
	}
	
	/**
	 * Appends the given Movement to the end of this MovementSequenceBuilder.
	 * @param movement
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder addMovement(Movement movement) {
		add(movement);
		return this;
	}
	
	/**
	 * Appends a Delay with the given delay value to the end of this MovementSequenceBuilder.
	 * @param delay seconds
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder waitSeconds(double delay) {
		add(new Delay(lastPose, delay));
		return this;
	}

	/**
	 * Appends the given CRSpline to the end of this MovementSequenceBuilder.
	 * @param spline
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder addCRSpline(CRSpline spline) {
		add(spline);
		return this;
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches forward
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder forward(double inches) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + inches*Math.cos(p0.getHeading()), 
				p0.getY() + inches*Math.sin(p0.getHeading()),
				p0.getHeading()
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches backward
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder backward(double inches) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + -inches*Math.cos(p0.getHeading()), 
				p0.getY() + -inches*Math.sin(p0.getHeading()),
				p0.getHeading()
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches left
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder left(double inches) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + -inches*Math.cos(p0.getHeading()-Math.PI/2), 
				p0.getY() + -inches*Math.sin(p0.getHeading()-Math.PI/2),
				p0.getHeading()
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches right
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder right(double inches) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + inches*Math.cos(p0.getHeading()-Math.PI/2), 
				p0.getY() + inches*Math.sin(p0.getHeading()-Math.PI/2),
				p0.getHeading()
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}
	
	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches forward
	 * @param degrees turn (positive = clockwise)
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder forwardAndTurn(double inches, double degrees) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + inches*Math.cos(p0.getHeading()), 
				p0.getY() + inches*Math.sin(p0.getHeading()),
				p0.getHeading()-(degrees * Math.PI / 180.0)
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches backward
	 * @param degrees turn (positive = clockwise)
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder backwardAndTurn(double inches, double degrees) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + -inches*Math.cos(p0.getHeading()), 
				p0.getY() + -inches*Math.sin(p0.getHeading()),
				p0.getHeading()-(degrees * Math.PI / 180.0)
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches left
	 * @param degrees turn (positive = clockwise)
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder leftAndTurn(double inches, double degrees) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + -inches*Math.cos(p0.getHeading()-Math.PI/2), 
				p0.getY() + -inches*Math.sin(p0.getHeading()-Math.PI/2),
				p0.getHeading()-(degrees * Math.PI / 180.0)
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param inches right
	 * @param degrees turn (positive = clockwise)
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder rightAndTurn(double inches, double degrees) {
		Pose p0 = lastPose;
		Pose p1 = new Pose(
				p0.getX() + inches*Math.cos(p0.getHeading()-Math.PI/2), 
				p0.getY() + inches*Math.sin(p0.getHeading()-Math.PI/2),
				p0.getHeading()-(degrees * Math.PI / 180.0)
		);
		
		add(new StraightLine(p0, p1));
		return this; 
	}
	
	/**
	 * Appends a Turn to the end of this MovementSequenceBuilder.
	 * @param degrees left 
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder turnLeft(double degrees) {
		add(new Turn(lastPose, (-degrees * Math.PI / 180.0)));
		return this; 
	}

	/**
	 * Appends a Turn to the end of this MovementSequenceBuilder.
	 * @param degrees right 
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder turnRight(double degrees) {
		add(new Turn(lastPose, (degrees * Math.PI / 180.0)));
		return this; 
	}

	/**
	 * Appends a StraightLine to the end of this MovementSequenceBuilder.
	 * @param x inches
	 * @param y inches
	 * @param heading degrees
	 * @return this MovementSequenceBuilder.
	 */
	public MovementSequenceBuilder strafeTo(double x, double y, double heading) {
		add(new StraightLine(lastPose, new Pose(x, y, normalizeAngle(heading * Math.PI / 180.0))));
		return this; 
	}
	
	/**
	 * Updates and appends the given Movement to the end of this MovementSequenceBuilder.
	 * @param movement
	 */
	private void add(Movement movement) {
		if (!movement.getStartPose().isEqualTo(lastPose)) throw new RuntimeException(String.format("Movement %s (%s) does not start at the same Pose as the previous Movement (Movement $s)", movements.size(), movement.getClass(), movements.size()-1));
		movements.add(movement);
		lastPose = movement.getEndPose();
		lastPose = new Pose(
				lastPose.getX(), 
				lastPose.getY(), 
				normalizeAngle(lastPose.getHeading())
		);
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
