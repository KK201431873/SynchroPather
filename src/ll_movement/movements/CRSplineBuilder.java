package ll_movement.movements;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ll_movement.util.Pose;

/**
 * Builder pattern object that can create and store the anchor Poses of a CRSpline.
 */
public class CRSplineBuilder {

	private ArrayList<Pose> anchors = new ArrayList<>();

	
	/////////////////////////////
	// INSTANTIATION OVERLOADS //
	/////////////////////////////
	
	/**
	 * Creates a new CRSplineBuilder object.
	 */
	public CRSplineBuilder() {
		this(new ArrayList<>());
	}

	/**
	 * Creates a new CRSplineBuilder object with the given start Pose.
	 * @param x inches
	 * @param y inches
	 * @param heading degrees
	 */
	public CRSplineBuilder(double x, double y, double heading) {
		this(new Pose(x, y, heading * Math.PI / 180.0));
	}

	/**
	 * Creates a new CRSplineBuilder object with the given start Pose.
	 * @param startPose
	 */
	public CRSplineBuilder(Pose startPose) {
		this(new ArrayList<>(Stream.of(startPose).collect(Collectors.toList())));
	}

	/**
	 * Creates a new CRSplineBuilder object with the anchor Poses of the given CRSpline.
	 * @param spline
	 */
	public CRSplineBuilder(CRSpline spline) {
		this(spline.getAnchors());
	}
	
	/**
	 * Creates a new CRSplineBuilder object with the given anchor Poses.
	 * @param anchors
	 */
	public CRSplineBuilder(ArrayList<Pose> anchors) {
		this.anchors = anchors;
	}
	
	
	/////////////////////
	// PRIMARY METHODS //
	/////////////////////
	
	/**
	 * Builds this CRSplineBuilder object into a CRSpline object.
	 * @return the built CRSpline object.
	 */
	public CRSpline build() {
		return new CRSpline(this.anchors);
	}
	
	/**
	 * Appends the given anchor Pose to the end of this CRSpline.
	 * @param x inches
	 * @param y inches
	 * @param heading degrees
	 * @return this CRSplineBuilder.
	 */
	public CRSplineBuilder addPose(double x, double y, double heading) {
		return addPose(new Pose(x, y, heading * Math.PI / 180.0));
	}
	
	/**
	 * Appends the given anchor Pose to the end of this CRSpline.
	 * @param pose
	 * @return this CRSplineBuilder.
	 */
	public CRSplineBuilder addPose(Pose pose) {
		ArrayList<Pose> poses = new ArrayList<>();
		poses.add(pose);
		return addPoses(poses);
	}

	/**
	 * Appends the given anchor Poses to the end of this CRSpline.
	 * @param spline
	 * @return this CRSplineBuilder.
	 */
	public CRSplineBuilder addSpline(CRSpline spline) {
		return addPoses(spline.getAnchors());
	}

	/**
	 * Appends the given anchor Poses to the end of this CRSpline.
	 * @param anchors
	 * @return this CRSplineBuilder.
	 */
	public CRSplineBuilder addPoses(ArrayList<Pose> anchors) {
		this.anchors.addAll(anchors);
		return this;
	}
}
