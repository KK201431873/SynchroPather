package movement.movements;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.MovementType;
import util.Pose;

public class CRSplineBuilder {

	public MovementType MOVEMENT_TYPE;
	private ArrayList<Pose> poses = new ArrayList<>();

	// instantiation overloads
	
	public CRSplineBuilder() {
		this(new ArrayList<>());
	}

	public CRSplineBuilder(double x, double y, double degreesHeading) {
		this(new Pose(x, y, degreesHeading * Math.PI / 180.0));
	}
	
	public CRSplineBuilder(Pose startPose) {
		this(new ArrayList<>(Stream.of(startPose).collect(Collectors.toList())));
	}
	
	public CRSplineBuilder(CRSpline spline) {
		this(spline.getPoses());
	}
		
	public CRSplineBuilder(ArrayList<Pose> poses) {
		this.MOVEMENT_TYPE = MovementType.DRIVE;
		this.poses = poses;
	}
	
	// primary methods
	
	public CRSpline build() {
		return new CRSpline(this.poses);
	}
	
	/**
	 * Adds a pose to the end of this CRSpline.
	 * 
	 * @param x  The pose's x position
	 * @param y  The pose's y position
	 * @param degreesHeading  The pose's heading in degrees
	 */
	public CRSplineBuilder addPose(double x, double y, double degreesHeading) {
		addPose(new Pose(x, y, degreesHeading * Math.PI / 180.0));
		return this;
	}
	
	public CRSplineBuilder addPose(Pose pose) {
		ArrayList<Pose> poses = new ArrayList<>();
		poses.add(pose);
		addPoses(poses);
		return this;
	}

	public CRSplineBuilder addSpline(CRSpline spline) {
		addPoses(spline.getPoses());
		return this;
	}

	public CRSplineBuilder addPoses(ArrayList<Pose> concatPoses) {
		poses.addAll(concatPoses);
		
		return this;
	}
}
