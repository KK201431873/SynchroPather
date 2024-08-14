package org.firstinspires.ftc.teamcode.synchropather.systems.rotation;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.synchropather.systems.MovementType;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.Movement;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.Plan;
import org.firstinspires.ftc.teamcode.synchropather.systems.translation.TranslationState;

import java.util.ArrayList;

/**
 * Object containing a sequence of Movements for rotational drive.
 */
public class RotationPlan extends Plan<RotationState> {

	/**
	 * Creates a new RotationPlan object with the given Movements.
	 * @param movements
	 */
	public RotationPlan(Movement... movements) {
		super(MovementType.ROTATION, movements);
	}

	/**
	 * Controls the rotation output of the robot to the RotationState at the elapsedTime.
	 */

	public static double kP = 1; // to be tuned
	public static double kD = 1; // to be tuned
	public static double lastErrorTh = 0;

	public static ElapsedTime tt;
	public ArrayList<Double> times;
	@Override
	public void loop() {
		// TODO Auto-generated method stub
		// brute force PID to target

		// TODO Auto-generated method stub
		RotationState rotState = getCurrentState();

		Pose2d currentPos = OdometrySubsystem.getPose();
		double theta = currentPos.getHeading();
		double errorTh = theta - rotState.getHeading();


		while (times.size() < 5) {
			times.add((double) 0);
		}
		times.add(tt.seconds());
		while (times.size() > 5) {
			times.remove(0);
		}
		//double dx = (errorX-lastErrorX)/(tt.seconds()-lastTime);
		//double dy = (errorY-lastErrorY)/(tt.seconds()-lastTime);

		// five point stencil
		double dErrorTh = (0 - errorTh * times.get(times.size()-1)+ 8*errorTh*(times.get(times.size()-2)) - 8*errorTh*(times.get(times.size()-4)) + errorTh * times.get(times.size() - 5) )
				/ (12 * (times.get(times.size()-1) - times.get(times.size()-2)) );



		DriveSubsystem.driveFieldCentric(0,0, kP * errorTh + kD * dErrorTh);
		lastErrorTh = errorTh;
	}

}
