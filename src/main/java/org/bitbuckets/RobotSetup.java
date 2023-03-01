package org.bitbuckets;

import config.*;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.arm.ArmSubsystemSetup;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.auto.AutoSubsystemSetup;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.DriveSubsystemSetup;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.holo.HoloControlSetup;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SimulatorKiller;
import org.bitbuckets.lib.ToggleableSetup;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.odometry.PidgeonOdometryControlSetup;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<Void> {




    @Override
    public Void build(IProcess self) {



        SwerveDriveKinematics KINEMATICS = DriveTurdSpecific.KINEMATICS; //TODO make this swappable

        OperatorInput operatorInput = new OperatorInput(
                new Joystick(0),
                new Joystick(1)
        );

        //if only these could be children of the drive subsystem... TODO fix this in mattlib future editions
        IDriveControl driveControl = self.childSetup(
                "drive-ctrl",
                new ToggleableSetup<>(
                        Enabled.drive,
                        IDriveControl.class,
                        new DriveControlSetup(
                                KINEMATICS,
                                DriveSetups.FRONT_LEFT,
                                DriveSetups.FRONT_RIGHT,
                                DriveSetups.BACK_LEFT,
                                DriveSetups.BACK_RIGHT
                        )
                )

        );
        IVisionControl visionControl = self.childSetup(
                "vision-ctrl",
                new ToggleableSetup<>(
                        Enabled.vision,
                        IVisionControl.class,
                        new VisionControlSetup()
                )
        );
        IOdometryControl odometryControl = self.childSetup(
                "odometry-control",
                new ToggleableSetup<>(
                        Enabled.drive,
                        IOdometryControl.class,
                        new PidgeonOdometryControlSetup( //needs to be swappable
                                driveControl,
                                visionControl,
                                KINEMATICS,
                                MotorIds.PIDGEON_IMU_ID
                        )
                )
        );

        AutoSubsystem autoSubsystem = self.childSetup(
                "auto-system",
                new ToggleableSetup<>(
                        Enabled.auto,
                        AutoSubsystem.class,
                        new AutoSubsystemSetup(
                            new AutoControlSetup(driveControl::currentPositions)
                        )
                )
        );

        self.childSetup(
                "arm-system",
                new ToggleableSetup<>(
                        Enabled.arm,
                        ArmSubsystem.class,
                        new ArmSubsystemSetup(
                                operatorInput,
                                autoSubsystem,
                                ArmSetups.ARM_CONTROL
                        )
                )

        );

        self.childSetup(
                "drive-system",
                new ToggleableSetup<>(
                        Enabled.drive,
                        DriveSubsystem.class,
                        new DriveSubsystemSetup(
                                operatorInput,
                                autoSubsystem,
                                visionControl,
                                odometryControl,
                                DriveSetups.BALANCE_SETUP,
                                new HoloControlSetup(
                                        driveControl,
                                        odometryControl
                                ),
                                driveControl
                        )
                )
        );

        /**
         * Register the crasher runnable if we're in github
         */
        if (System.getenv().containsKey("CI")) {
            self.registerLogicLoop(new SimulatorKiller());
        }


        return null;
    }


}
