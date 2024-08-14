package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.synchropather.systems.__util__.Synchronizer;
import org.firstinspires.ftc.teamcode.synchropather.systems.rotation.LinearRotation;
import org.firstinspires.ftc.teamcode.synchropather.systems.rotation.RotationPlan;
import org.firstinspires.ftc.teamcode.synchropather.systems.rotation.RotationState;
import org.firstinspires.ftc.teamcode.synchropather.systems.translation.CRSplineTranslation;
import org.firstinspires.ftc.teamcode.synchropather.systems.translation.LinearTranslation;
import org.firstinspires.ftc.teamcode.synchropather.systems.translation.TranslationPlan;
import org.firstinspires.ftc.teamcode.synchropather.systems.translation.TranslationState;

@Autonomous(name="SynchroPather Test")
public class SynchropatherTest extends LinearOpMode {
    private DriveSubsystem drive;
    private OdometrySubsystem odometry;
    private final double drive_speed = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        HardwareRobot hardwareRobot = new HardwareRobot(hardwareMap);
        odometry = new OdometrySubsystem(hardwareMap, this);
        drive = new DriveSubsystem(
                hardwareRobot.leftFront,
                hardwareRobot.rightFront,
                hardwareRobot.leftBack,
                hardwareRobot.rightBack,
                hardwareRobot.imu,
                this,
                telemetry
        );

        waitForStart();

        // translation Plan
        CRSplineTranslation spline1 = new CRSplineTranslation(0,
                new TranslationState(-40.75,63.5),
                new TranslationState(-40.75,38),
                new TranslationState(43,36)
        );

        CRSplineTranslation spline2 = new CRSplineTranslation(spline1.getEndTime(),
                new TranslationState(43,36),
                new TranslationState(0,12),
                new TranslationState(-56,12)
        );

        CRSplineTranslation spline3 = new CRSplineTranslation(spline2.getEndTime(),
                new TranslationState(-56,12),
                new TranslationState(12,12),
                new TranslationState(43,36)
        );

        CRSplineTranslation spline4 = new CRSplineTranslation(spline3.getEndTime(),
                new TranslationState(43,36),
                new TranslationState(0,12),
                new TranslationState(-36,12),
                new TranslationState(-56,24)
        );

        CRSplineTranslation spline5 = new CRSplineTranslation(spline4.getEndTime(),
                new TranslationState(-56,24),
                new TranslationState(-36,12),
                new TranslationState(0,12),
                new TranslationState(43,36)
        );

        LinearTranslation line1 = new LinearTranslation(spline5.getEndTime(),
                new TranslationState(43,36),
                new TranslationState(43,12)
        );

        LinearTranslation line2 = new LinearTranslation(line1.getEndTime(),
                new TranslationState(43,12),
                new TranslationState(53,12)
        );

        LinearTranslation line3 = new LinearTranslation(line2.getEndTime(),
                new TranslationState(53,12),
                new TranslationState(0,0)
        );

        LinearTranslation line4 = new LinearTranslation(line3.getEndTime()+3,
                new TranslationState(0, 0),
                new TranslationState(48, 60)
        );

        LinearTranslation line5 = new LinearTranslation(line4.getEndTime(),
                new TranslationState(48, 60),
                new TranslationState(-48, 60)
        );

        LinearTranslation line6 = new LinearTranslation(line5.getEndTime(),
                new TranslationState(-48, 60),
                new TranslationState(-48, -60)
        );

        LinearTranslation line7 = new LinearTranslation(line6.getEndTime(),
                new TranslationState(-48, -60),
                new TranslationState(48, -60)
        );

        LinearTranslation line8 = new LinearTranslation(line7.getEndTime(),
                new TranslationState(48, -60),
                new TranslationState(0,0)
        );

        CRSplineTranslation returnToStart = new CRSplineTranslation(line8.getEndTime(),
                new TranslationState(0,0),
                new TranslationState(-36,12),
                new TranslationState(-40.75,63.5)
        );

        TranslationPlan translationPlan = new TranslationPlan(
                spline1,
                spline2,
                spline3,
                spline4,
                spline5,
                line1,
                line2,
                line3,
                line4,
                line5,
                line6,
                line7,
                line8,
                returnToStart
        );


        // rotation Plan
        LinearRotation rot1 = new LinearRotation(0,
                new RotationState(0),
                new RotationState(Math.toRadians(360))
        );

        RotationPlan rotationPlan = new RotationPlan(
                rot1
        );


        // put all the Plans into a Synchronizer
        Synchronizer synchronizer = new Synchronizer(
                translationPlan,
                rotationPlan
        );

        while (opModeIsActive()) {
            synchronizer.loop();
        }

    }
}
