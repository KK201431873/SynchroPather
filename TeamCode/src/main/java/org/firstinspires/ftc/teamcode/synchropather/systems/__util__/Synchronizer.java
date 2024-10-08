package org.firstinspires.ftc.teamcode.synchropather.systems.__util__;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.synchropather.systems.MovementType;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.Plan;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.RobotState;

/**
 * An object that contains other Plans for synchronizing robot subsystems.
 */
public class Synchronizer {

	private ElapsedTime runtime;
	private Plan[] plans;
	
	/**
	 * Creates a new Synchronizer object with the given Plans.
	 * @param plans
	 */
	public Synchronizer(Plan... plans) {
		this.plans = plans;
		this.runtime = new ElapsedTime();
		this.runtime.reset();
	}

	/**
	 * Sets the current runtime to zero seconds.
	 */
	public void resetRuntime() {
		runtime.reset();
	}

	/**
	 * Advances the current target elapsedTime and sends control commands to all plans.
	 */
	public void loop() {
		setTarget(runtime.seconds());
		for (Plan plan : plans) {
			plan.loop();
		}
	}

	/**
	 * Sets the target of all plans contained within this Synchronizer to the given elapsedTime.
	 */
	public void setTarget(double elapsedTime) {
		for (Plan plan : plans) {
			plan.setTarget(elapsedTime);
		}
	}

	/**
	 * Gets the RobotState at the given elapsedTime within the Plan of the given movementType.
	 * @param movementType
	 * @param elapsedTime
	 * @return the indicated RobotState, or null if the Plan does not exist.
	 */
	@SuppressWarnings("unchecked")
	public RobotState getState(MovementType movementType, double elapsedTime) {
		for (Plan plan : plans) {
			if (plan.movementType == movementType) {
				return plan.getState(elapsedTime);
			}
		}
		return null;
	}

	/**
	 * Gets the velocity RobotState at the given elapsedTime within the Plan of the given movementType.
	 * @param movementType
	 * @param elapsedTime
	 * @return the indicated velocity RobotState, or null if the Plan does not exist.
	 */
	@SuppressWarnings("unchecked")
	public RobotState getVelocity(MovementType movementType, double elapsedTime) {
		for (Plan plan : plans) {
			if (plan.movementType == movementType) {
				return plan.getVelocity(elapsedTime);
			}
		}
		return null;
	}

	/**
	 * @return the minimum duration needed to execute all Plans contained within this Synchronizer.
	 */
	public double getDuration() {
		double max = -1;
		for (Plan plan : plans) {
			max = Math.max(max, plan.getDuration());
		}
		return max;
	}

}
