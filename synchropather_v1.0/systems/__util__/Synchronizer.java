package synchropather.systems.__util__;

import synchropather.systems.__util__.superclasses.Plan;

/**
 * An object that contains other Plans for synchronizing robot subsystems.
 */
public class Synchronizer {

	private Plan[] plans;
	
	/**
	 * Creates a new Synchronizer object with the given Plans.
	 * @param plans
	 */
	public Synchronizer(Plan... plans) {
		this.plans = plans;
	}

	/**
	 * Calls the loop() method of all plans contained within this Synchronizer.
	 */
	public void loop() {
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

}
