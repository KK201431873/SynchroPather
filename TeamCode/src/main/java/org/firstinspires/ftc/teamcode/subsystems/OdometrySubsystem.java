package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.Motor.Encoder;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class OdometrySubsystem extends SubsystemBase {

    protected static HolonomicOdometry odometry;

    // The lateral distance between the left and right odometers
    // is called the trackwidth. This is very important for
    // determining angle for turning approximations
    public static final double TRACK_WIDTH = 12.4; //TODO: tune

    // Center wheel offset is the distance between the
    // center of rotation of the robot and the center odometer.
    // This is to correct for the error that might occur when turning.
    // A negative offset means the odometer is closer to the back,
    // while a positive offset means it is closer to the front.
    public static final double CENTER_WHEEL_OFFSET = 0; //TODO: tune

    public static final double WHEEL_DIAMETER = 1.37795;
    // if needed, one can add a gearing term here
    public static final double TICKS_PER_REV = 4096;
    public static final double DISTANCE_PER_PULSE = Math.PI * WHEEL_DIAMETER / TICKS_PER_REV;

    private static Encoder leftOdometer, rightOdometer, centerOdometer;

    private static Pose2d lastPose;
    private static Pose2d currentPose;
    private static Pose2d currentVelocity;
    private static double lastTime;
    private static double currentTime;
    private static ElapsedTime runtime;

    private static LinearOpMode opMode;


    /**
     * Constructs an OdometrySubsystem using components derived from a HardwareMap.
     *
     * @param hardwareMap the HardwareMap for the robot.
     */
    public OdometrySubsystem(HardwareMap hardwareMap, LinearOpMode opMode) {

        MotorEx leftFront, rightFront, centerOdometerSlot;

        leftFront = new MotorEx(hardwareMap, "leftFront");
        rightFront = new MotorEx(hardwareMap, "rightFront");
        centerOdometerSlot = new MotorEx(hardwareMap, "centerOdometer");

        leftOdometer = leftFront.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        rightOdometer = rightFront.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        centerOdometer = centerOdometerSlot.encoder.setDistancePerPulse(DISTANCE_PER_PULSE);

        leftOdometer.reset();
        rightOdometer.reset();
        centerOdometer.reset();

        rightOdometer.setDirection(Motor.Direction.REVERSE);

        odometry = new HolonomicOdometry(
                leftOdometer::getDistance,
                rightOdometer::getDistance,
                centerOdometer::getDistance,
                TRACK_WIDTH, CENTER_WHEEL_OFFSET
        );

        OdometrySubsystem.opMode = opMode;

        // init pose and velocity tracking
        currentPose = odometry.getPose();
        lastPose = currentPose;
        currentVelocity = new Pose2d(0,0,new Rotation2d(0));

        // init time tracking
        runtime = new ElapsedTime();
        runtime.reset();
        lastTime = runtime.seconds();
        currentTime = lastTime;

    }

    /**
     * @return the left odometer as an Encoder.
     */
    public static Encoder getLeftOdometer() {
        return leftOdometer;
    }

    /**
     * @return the left odometer as an Encoder.
     */
    public static Encoder getRightOdometer() {
        return rightOdometer;
    }

    /**
     * @return the left odometer as an Encoder.
     */
    public static Encoder getCenterOdometer() {
        return centerOdometer;
    }

    /**
     * Returns the current pose of the robot.
     *
     * @return The current pose as a Pose2d object.
     */
    public static Pose2d getPose() {
        return new Pose2d(currentPose.getY(), currentPose.getX(), new Rotation2d(currentPose.getHeading()));
    }

    /**
     * Returns the current velocity of the robot.
     *
     * @return The current velocity as a Pose2d object.
     */
    public static Pose2d getVelocity() {
        return new Pose2d(currentVelocity.getY(), currentVelocity.getX(), new Rotation2d(currentPose.getHeading()));
    }

    /**
     * Updates the robot's pose using the odometry system.
     * This should be called at the end of every loop to keep the pose estimate up to date.
     */
    public static void update() {

        // get current and delta time
        currentTime = runtime.seconds();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        // get current and delta pose
        odometry.updatePose();
        currentPose = odometry.getPose();
        Pose2d deltaPose = new Pose2d(
                currentPose.getX() - lastPose.getX(),
                currentPose.getY() - lastPose.getY(),
                new Rotation2d(normalizeAngle(currentPose.getHeading() - lastPose.getHeading()))
        );
        lastPose = currentPose;

        if (Math.abs(deltaTime) > 1e-6) {
            // calculate velocities
            currentVelocity = new Pose2d(
                    deltaPose.getX()/ deltaTime,
                    deltaPose.getY()/ deltaTime,
                    new Rotation2d(deltaPose.getHeading()/ deltaTime)
            );
        }

    }

    /**
     * Automatically updates the pose every cycle.
     */
    @Override
    public void periodic() {
        // Keep the odometry system's pose estimate up to date each cycle.
        update();
    }

    /**
     * Normalizes a given angle to [-pi,pi) degrees.
     * @param degrees the given angle in degrees.
     * @return the normalized angle in degrees.
     */
    private static double normalizeAngle(double degrees) {
        double angle = degrees;
        while (opMode.opModeIsActive() && angle <= -Math.PI)
            angle += 2*Math.PI;
        while (opMode.opModeIsActive() && angle > Math.PI)
            angle -= 2*Math.PI;
        return angle;
    }

}