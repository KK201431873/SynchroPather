package org.firstinspires.ftc.teamcode.synchropather.systems.translation;


import com.arcrobotics.ftclib.geometry.Pose2d;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.synchropather.systems.MovementType;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.Movement;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.Plan;

import java.util.ArrayList;

/**
 * Object containing a sequence of Movements for translational drive.
 */
public class TranslationPlan extends Plan<TranslationState> {

	/**
	 * Creates a new TranslationPlan object with the given Movements.
	 * @param movements
	 */


	public TranslationPlan(Movement... movements) {
		super(MovementType.TRANSLATION, movements);
	}
	public static double kP = 1; // to be tuned
	public static double kD = 1; // to be tuned
	public static double lastErrorX = 0;
	public static double lastErrorY = 0;

	public static ElapsedTime tt;
	public ArrayList<Double> times;

	/**
	 * Controls the translation output of the robot to the TranslationState at the elapsedTime.
	 */
	@Override
	public void loop() {

		// brute force PID to target

		// TODO Auto-generated method stub
		TranslationState poseState = getCurrentState();

    	Pose2d currentPos = OdometrySubsystem.getPose();
		double x = currentPos.getX();
		double y = currentPos.getY();
		double errorX = x - poseState.getX();
		double errorY = y - poseState.getY();





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
		double dErrorX = (0 - errorX * times.get(times.size()-1)+ 8*errorX*(times.get(times.size()-2)) - 8*errorX*(times.get(times.size()-4)) + errorX * times.get(times.size() - 5) )
				/ (12 * (times.get(times.size()-1) - times.get(times.size()-2)) );
		double dErrorY = (0 - errorY * times.get(times.size()-1)+ 8*errorY*(times.get(times.size()-2)) - 8*errorY*(times.get(times.size()-4)) + errorY * times.get(times.size() - 5) )
				/ (12 * (times.get(times.size()-1) - times.get(times.size()-2)) );


		DriveSubsystem.driveFieldCentric(errorX * kP + kD * dErrorX, errorY * kP + kD * dErrorY, 0);
		lastErrorX = errorX;
		lastErrorY = errorY;

	}

}
