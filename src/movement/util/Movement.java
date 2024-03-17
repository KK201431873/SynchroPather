package movement.util;

public abstract class Movement {
	
	public MovementType MOVEMENT_TYPE;

	public abstract Pose getPose(double elapsedTime);

	public abstract double getTime();
	
	public abstract Pose getStartPose();
	
	public abstract Pose getEndPose();

}
