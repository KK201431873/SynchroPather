package movement.movements;

import java.util.ArrayList;

import movement.util.DisplacementCalculator;
import movement.util.Movement;
import movement.util.MovementType;
import movement.util.Pose;
import teamcode_util.DriveConstants;

public class CRSpline extends Movement {

	private double distance, time;
	private double[] l, p, pi;
	private ArrayList<Pose> poses = new ArrayList<>();
	private DisplacementCalculator calculator;

	public CRSpline(ArrayList<Pose> poses) {
		this.MOVEMENT_TYPE = MovementType.DRIVE;
		this.poses = poses;
		init();
	}
	
	// primary methods

	public double getLength() {
		return poses.size();
	}
	
	public double getDistance() {
		return distance;
	}
	
	public ArrayList<Pose> getPoses() {
		return poses;
	}

	@Override
	public Pose getPose(double elapsedTime) {
		int n = getLocalSegment(elapsedTime);
		double p_r = getLocalProportion(elapsedTime);
		
		return getPose(n, p_r);
	}

	@Override
	public Pose getVelocityPose(double elapsedTime) {
		// get theta
		int n = getLocalSegment(elapsedTime);
		double p_r = getLocalProportion(elapsedTime);
		Pose derivative = getDerivative(n, p_r);
		
		double theta = Math.atan2(derivative.getY(), derivative.getX());
		double velocity = calculator.getVelocity(elapsedTime);
		
		return new Pose(
				velocity * Math.cos(theta),
				velocity * Math.sin(theta),
				derivative.getHeading()
		);
	}
	
	@Override
	public double getTime() {
		return time;
	}
	
	@Override
	public Pose getStartPose() {
		return poses.size()>0 ? poses.get(0) : null;
	}

	@Override
	public Pose getEndPose() {
		return poses.size()>0 ? poses.get(poses.size()-1) : null;
	}
	
	public Pose getPose(int index) {
		if (index < 0 || poses.size()-1 < index)
			throw new RuntimeException(String.format("Index %s outside of [%s,%s]", index, 0, poses.size()-1));
		return poses.get(index);
	}
	
	public Pose getPose(int segment, double t) {
		if (segment < 0 || poses.size()-2 < segment)
			throw new RuntimeException(String.format("Segment index %s outside of [%s,%s]", segment, 0, poses.size()-2));

		Pose p0 = poses.get(Math.max(0, segment-1));
		Pose p1 = poses.get(segment);
		Pose p2 = poses.get(segment + 1);
		Pose p3 = poses.get(Math.min(poses.size()-1, segment+2));
		
		double tt = t*t;
		double ttt = tt*t;

		double q0 = -ttt + 2*tt - t;
		double q1 = 3*ttt - 5*tt + 2;
		double q2 = -3*ttt + 4*tt + t;
		double q3 = ttt - tt;

		double tx = 0.5 * (p0.getX()*q0 + p1.getX()*q1 + p2.getX()*q2 + p3.getX()*q3);
		double ty = 0.5 * (p0.getY()*q0 + p1.getY()*q1 + p2.getY()*q2 + p3.getY()*q3);

		double qh1 = bound(1.2 / (1 + 148*Math.exp(5*(t-1.5))) - 0.1, 0, 1);
		double qh2 = bound(1.2 / (1 + 148*Math.exp(-5*(t+0.5))) - 0.1, 0, 1);
		double theading = p1.getHeading()*qh1 + p2.getHeading()*qh2;
		
		return new Pose(tx, ty, theading);
	}
	
	public Pose getDerivative(int segment, double t) {
		if (segment < 0 || poses.size()-2 < segment)
			throw new RuntimeException(String.format("Segment index %s outside of [%s,%s]", segment, 0, poses.size()-2));

		Pose p0 = poses.get(Math.max(0, segment-1));
		Pose p1 = poses.get(segment);
		Pose p2 = poses.get(segment + 1);
		Pose p3 = poses.get(Math.min(poses.size()-1, segment+2));
		
		double tt = t*t;

		double q0 = -3*tt + 4*t - 1;
		double q1 = 9*tt - 10*t;
		double q2 = -9*tt + 8*t + 1;
		double q3 = 3*tt - 2*t;

		double tx = 0.5 * (p0.getX()*q0 + p1.getX()*q1 + p2.getX()*q2 + p3.getX()*q3);
		double ty = 0.5 * (p0.getY()*q0 + p1.getY()*q1 + p2.getY()*q2 + p3.getY()*q3);

		double qh1 = -73.229*Math.exp(5*t)/Math.pow(12.2165+Math.exp(5*t),2);
		double qh2 = 72.8915*Math.exp(5*t)/Math.pow(12.1486+Math.exp(5*t),2);
		double theading = p1.getHeading()*qh1 + p2.getHeading()*qh2;
		
		return new Pose(tx, ty, theading);
	}
	
	public int getLocalSegment(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, time);
		
		double dx = calculator.getDisplacement(elapsedTime);
		double p_x = distance!=0 ? dx / distance : 0;
		
		int n = 0;
		while (n+1 < pi.length && p_x >= pi[n+1]) n++;
		
		return n;
	}
	
	public double getLocalProportion(double elapsedTime) {
		double dx = calculator.getDisplacement(elapsedTime);
		int n = getLocalSegment(elapsedTime);
		
		double delta_t = DriveConstants.delta_t;
		double p_r = 0;
		double localDisplacement = 0;
		Pose lastPose = getPose(n,0);
		while (localDisplacement < dx - pi[n] * distance) {
			p_r += delta_t;
			Pose currentPose = getPose(n, p_r);
			localDisplacement += Math.hypot(currentPose.getX()-lastPose.getX(), currentPose.getY()-lastPose.getY());
			lastPose = currentPose;
		}
		
		return p_r;
	}
	
	public String toString() {
		String res = "[";
		for (int i = 0; i < poses.size(); i++)
			res += String.format("%s%s", poses.get(i), (i==poses.size()-1 ? "" : ", "));
		return res + "]";
	}
	
	private static double bound(double x, double lower, double upper) {
		return Math.max(lower, Math.min(upper, x));
	}
	
	private void init() {
		l = new double[Math.max(0, poses.size()-1)];
		p = new double[Math.max(0, poses.size()-1)];
		pi = new double[Math.max(0, poses.size()-1)];

		distance = 0;
		double delta_t = DriveConstants.delta_t;
		double x = poses.get(0).getX();
		double y = poses.get(0).getY();
		for (int i = 0; i < poses.size()-1; i++) {
			double length = 0;
			for (double t = 0; t <= 1; t += delta_t) {
				Pose currentPose = getPose(i, t);
				double deltaDistance = Math.hypot(currentPose.getX()-x, currentPose.getY()-y);
				distance += deltaDistance;
				length += deltaDistance;
				x = currentPose.getX();
				y = currentPose.getY();
			}
			l[i] = length;
		}
		
		double partialSum = 0;
		for (int i = 0; i < l.length; i++) {
			pi[i] = partialSum / distance;
			p[i] = l[i] / distance;
			partialSum += l[i];
		}

		calculator = new DisplacementCalculator(distance, DriveConstants.MAX_VELOCITY, DriveConstants.MAX_ACCELERATION);
		
		time = calculator.getTime();
		
	}

}
