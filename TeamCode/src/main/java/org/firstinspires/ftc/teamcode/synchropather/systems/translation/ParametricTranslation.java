package org.firstinspires.ftc.teamcode.synchropather.systems.translation;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;

import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.Movement;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.superclasses.RobotState;

public class ParametricTranslation extends Movement {

    @Override
    public double getMinDuration() {
        return 0;
    }

    @Override
    public RobotState getState(double elapsedTime) {
        Pose2d target = new Pose2d(
                8 * Math.cos(runtime.seconds() / 0.6),
                8 * Math.sin(runtime.seconds() / 1.2),
                new Rotation2d(0)
        );
        return null;
    }

    @Override
    public RobotState getVelocity(double elapsedTime) {
        return null;
    }

    @Override
    public RobotState getStartState() {
        return null;
    }

    @Override
    public RobotState getEndState() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "ParametricTranslation";
    }
}
