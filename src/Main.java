
import graphics.MovementVisualizer;
import movement.MovementSequence;
import movement.MovementSequenceBuilder;
import movement.movements.CRSpline;
import movement.movements.CRSplineBuilder;
import movement.util.Pose;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		// define all the splines using waypoints
        CRSpline spline1 = new CRSplineBuilder(-40.75,63.5,-90)
				.addPose(-40.75,38,90)
				.addPose(43,36,0)
				.build();

        CRSpline spline2 = new CRSplineBuilder(43,36,0)
				.addPose(0,12,-180+1e-6)
				.addPose(-56,12,-180+1e-6)
				.build();

        CRSpline spline3 = new CRSplineBuilder(-56,12,-180+1e-6)
				.addPose(12,12,0)
				.addPose(43,36,0)
				.build();

        CRSpline spline4 = new CRSplineBuilder(43,36,0)
				.addPose(0,12,-180+1e-6)
				.addPose(-36,12,-180+1e-6)
				.addPose(-56,24,-180+1e-6)
				.build();

        CRSpline spline5 = new CRSplineBuilder(-56,24,-180+1e-6)
				.addPose(-36,12,-180+1e-6)
				.addPose(0,12,-180+1e-6)
				.addPose(43,36,0)
				.build();
        
        CRSpline returnToStart = new CRSplineBuilder(0,0,0)
        		.addPose(-36,12, -90)
        		.addPose(-40.75,63.5,-90)
        		.build();

        // put all the splines into a MovementSequence plus some extra movements
		MovementSequence seq1 = new MovementSequenceBuilder(-40.75,63.5,-90)
				.addCRSpline(spline1)
				.addCRSpline(spline2)
				.addCRSpline(spline3)
				.addCRSpline(spline4)
				.addCRSpline(spline5)
				.rightAndTurn(24, 180)
				.backward(10)
				.goStraightTo(0, 0, 0)
				.goStraightTo(48, 60, 90)
				.goStraightTo(-48, 60, 180)
				.goStraightTo(-48, -60, 270)
				.goStraightTo(48, -60, 360)
				.goStraightTo(0,0,0)
				.addCRSpline(returnToStart)
				.build();
		
		// put the MovementSequence into a visualizer object, with timeFactor between 0 and 1 representing the speed of the visualizer
		double timeFactor = 0.3;
		MovementVisualizer visualizer1 = new MovementVisualizer(seq1, timeFactor);
		
		// start visualizer
		visualizer1.start();
		
		// main visualizer loop with an example telemetry function
		while (visualizer1.loop()) {
			generateTelemetry(visualizer1, timeFactor);
			Thread.sleep(16);
		}

	}
	
	
	
	//////////////////
	// random stuff //
	//////////////////
	
	

	static double x = -40.75, y = 63.5, h = -90;
	static double[] maxAccel = {0,0,0}, maxAccelPos = {0,0}, prevVelocity = {0,0,0};
	
	private static void generateTelemetry(MovementVisualizer visualizer, double timeFactor) {
		double dt = visualizer.getDeltaTime();
		Pose velocity = visualizer.getCurrentVelocity();
		Pose pose = visualizer.getCurrentPose();
		double[] accel = {(velocity.getX()-prevVelocity[0])/dt, (velocity.getY()-prevVelocity[1])/dt, (velocity.getHeading()-prevVelocity[2])/dt};
		prevVelocity = new double[]{velocity.getX(), velocity.getY(), velocity.getHeading()};
		double m = Math.hypot(accel[0], accel[1]), theta = Math.atan2(accel[1], accel[0])*180d/Math.PI;
		accel[0] = m;
		accel[1] = theta;
		if (m > maxAccel[0]) {
			maxAccel[0] = m;
			maxAccel[1] = theta;
			maxAccelPos = new double[] {pose.getX(), pose.getY()};
		}
		if (Math.abs(accel[2]) > Math.abs(maxAccel[2]))
			maxAccel[2] = accel[2];
		
		double xv = velocity.getX() * timeFactor;
		double yv = velocity.getY() * timeFactor;
		double hv = velocity.getHeading() * 180d / Math.PI * timeFactor;
		x += dt * xv;
		y += dt * yv;
		h = normalizeAngle(h + dt * hv);
		System.out.println(String.format("\n\n\n\n\n\n\n\n\n\n\n\n\nRUNTIME [%ss]/[%ss] \n[position = getPose()] \n  X %s𝘪𝘯 \n  Y %s𝘪𝘯 \n  H %s° \n[position = ∫ν𝒹𝓉] \n  X %s𝘪𝘯 \n  Y %s𝘪𝘯 \n  H %s° \n[velocity] \n  X %s𝘪𝘯/𝘴 \n  Y %s𝘪𝘯/𝘴 \n  H %s°/𝘴 \n[accel] \n  m %s𝘪𝘯/𝘴/𝘴 \n  θ %s° \n  h %s𝘳𝘢𝘥/𝘴/𝘴 \n[maxAccel] \n  m %s𝘪𝘯/𝘴/𝘴 \n  θ %s° \n  @ (%s𝘪𝘯, %s𝘪𝘯)", 
				Math.round(visualizer.getElapsedTime()*10000)/10000.0, Math.round(visualizer.getTime()*10000)/10000.0,
				Math.round(pose.getX()*100)/100.0, Math.round(pose.getY()*100)/100.0, Math.round(pose.getHeading()*180/Math.PI*100)/100.0, 
				Math.round(x*100)/100.0, Math.round(y*100)/100.0, Math.round(h*100)/100.0, 
				Math.round(xv*100)/100.0, Math.round(yv*100)/100.0, Math.round(hv*100)/100.0, 
				Math.round(accel[0]*100)/100.0, Math.round(accel[1]*100)/100.0, Math.round(accel[2]*100)/100.0,
				Math.round(maxAccel[0]*100)/100.0, Math.round(maxAccel[1]*100)/100.0,
				Math.round(maxAccelPos[0]*100)/100.0, Math.round(maxAccelPos[1]*100)/100.0
		));
		
	}
	
	/**
	 * Normalizes a given angle to [-pi,pi) radians.
	 * @param degrees the given angle in radians.
	 * @return the normalized angle in radians.
	 */
	private static double normalizeAngle(double degrees) {
	    double angle = degrees;
	    while (angle <= -180) //TODO: opMode.opModeIsActive() && 
	        angle += 360;
	    while (angle > 180)
	        angle -= 360;
	    return angle;
	}
	
}
