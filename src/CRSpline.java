import java.util.ArrayList;

public class CRSpline {

	public MovementType MOVEMENT_TYPE;
	private double distance, time;
	private double[] l, p, pi;
	private ArrayList<Pose> poses = new ArrayList<>();

	public CRSpline(ArrayList<Pose> poses) {
		this.MOVEMENT_TYPE = MovementType.DRIVE;
		this.poses = poses;
		updateDistances();
	}
	
	// primary methods
	
	public double getDistance() {
		return distance;
	}
	
	public double getTime() {
		return time;
	}
	
	public ArrayList<Pose> getPoses() {
		return poses;
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
	
	public Pose getPose(double elapsedTime) {
		elapsedTime = bound(elapsedTime, 0, time);
		
		double dx = getDisplacement(elapsedTime);
		double p_x = dx / distance;
		
		int n = 0;
		while (n+1 < pi.length && p_x >= pi[n+1]) n++;

		double delta_t = 0.01;
		double p_r = 0;
		double localDisplacement = 0;
		Pose lastPose = getPose(n,0);
		while (localDisplacement < dx - pi[n] * distance) {
			p_r += delta_t;
			Pose currentPose = getPose(n, p_r);
			localDisplacement += Math.hypot(currentPose.getX()-lastPose.getX(), currentPose.getY()-lastPose.getY());
			lastPose = currentPose;
		}
		
		return lastPose;
	}
	
	public double getDisplacement(double elapsedTime) {
		if (elapsedTime < 0 || time < elapsedTime)
			throw new RuntimeException(String.format("Elapsed time %s outside of [%s,%s]", elapsedTime, 0, time));
		
		double MV = DriveCoefficients.MAX_VELOCITY, MA = DriveCoefficients.MAX_ACCELERATION;
		double t_n = time - elapsedTime, t_a = MV/MA;
		if (time <= 2*t_a) {
			// triangle graph
			if (elapsedTime <= time/2)
				return 0.5*MA*elapsedTime*elapsedTime;
			else
				return distance - 0.5*MA*t_n*t_n;
		} else {
			// trapezoid graph
			if (elapsedTime <= time/2)
				return 0.5*(elapsedTime + Math.max(0, elapsedTime - t_a))* Math.min(MV, MA*elapsedTime);
			else
				return distance - 0.5*(t_n + Math.max(0, t_n - t_a))* Math.min(MV, MA*t_n);
		}
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
	
	private void updateDistances() {
		l = new double[Math.max(0, poses.size()-1)];
		p = new double[Math.max(0, poses.size()-1)];
		pi = new double[Math.max(0, poses.size()-1)];

		distance = 0;
		double delta_t = 0.01;
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
		
		double MV = DriveCoefficients.MAX_VELOCITY, MA = DriveCoefficients.MAX_ACCELERATION;
		double d_a = 0.5 * MV*MV / MA;
		if (distance / 2 <= d_a) {
			// triangle graph
			time = 2*Math.sqrt(distance/MA);
		} else {
			// trapezoid graph
			time = distance/MV + MV/MA;
		}
		
	}

}
