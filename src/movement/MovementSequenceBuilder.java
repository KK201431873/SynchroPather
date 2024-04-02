package movement;

import java.util.ArrayList;

import movement.movements.CRSpline;
import movement.movements.StraightLine;
import movement.movements.Turn;
import movement.util.Movement;
import movement.util.Pose;

public class MovementSequenceBuilder {

	private ArrayList<Movement> movements;
	private Pose lastPose;
	
	public MovementSequenceBuilder(double x, double y, double heading) {
		this(new Pose(x, y, heading * Math.PI / 180));
	}
	
	public MovementSequenceBuilder(Pose startPose) {
		movements = new ArrayList<>();
		lastPose = startPose;
	}
	
	public ArrayList<Movement> getMovements() {
		return movements;
	}
	
	public MovementSequence build() {
		return new MovementSequence(this);
	}
	
	
	/////////////////////////////
	// PRIMARY PATTERN METHODS //
	/////////////////////////////
	
	public MovementSequenceBuilder addMovement(Movement movement) {
		add(movement);
		return this;
	}
	
	public MovementSequenceBuilder addCRSpline(CRSpline spline) {
		add(spline);
		return this;
	}
	
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
	
	public MovementSequenceBuilder turnLeft(double degrees) {
		add(new Turn(lastPose, (-degrees * Math.PI / 180.0)));
		return this; 
	}
	
	public MovementSequenceBuilder turnRight(double degrees) {
		add(new Turn(lastPose, (degrees * Math.PI / 180.0)));
		return this; 
	}
	
	public MovementSequenceBuilder goStraightTo(double x, double y, double heading) {
		add(new StraightLine(lastPose, new Pose(x, y, normalizeAngle(heading * Math.PI / 180.0))));
		return this; 
	}
	
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
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
