package org.bitbuckets.drive;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.balance.ClosedLoopsControl;
import org.bitbuckets.drive.balance.ClosedLoopsSetup;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.controlsds.sds.DriveControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SteerControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleSetup;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.drive.holo.HoloControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.ctre.CANCoderAbsoluteEncoderSetup;
import org.bitbuckets.lib.vendor.ctre.TalonDriveMotorSetup;
import org.bitbuckets.lib.vendor.ctre.TalonSteerMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.odometry.OdometryControlSetup;
import org.bitbuckets.robot.RobotStateControl;
import org.bitbuckets.vision.VisionControl;

import java.util.Optional;

public class DriveSubsystemSetup implements ISetup<DriveSubsystem> {

    final boolean driveEnabled;

    final RobotStateControl robotStateControl;
    final VisionControl visionControl;



    public DriveSubsystemSetup(boolean driveEnabled, RobotStateControl robotStateControl, VisionControl visionControl) {
        this.driveEnabled = driveEnabled;
        this.robotStateControl = robotStateControl;
        this.visionControl = visionControl;
    }

    @Override
    public DriveSubsystem build(ProcessPath path) {
        if (!driveEnabled) {
            return MockingUtil.buddy(DriveSubsystem.class);
        }

        DriveInput input = new DriveInput(new Joystick(0));
        ClosedLoopsControl closedLoopsControl = new ClosedLoopsSetup()
                .build(path.addChild("axis-control"));

        DriveControl driveControl = buildNeoDriveControl(path); //or use talons, when they work

        IOdometryControl odometryControl = new OdometryControlSetup(5, driveControl)
                .build(path.addChild("odo-control"));
        HoloControl holoControl = new HoloControlSetup(driveControl, odometryControl)
                .build(path.addChild("holo-control"));
        AutoControl autoControl = new AutoControlSetup()
                .build(path.addChild("auto-control"));

        IValueTuner<AutoPath> pathTuneable = path.generateEnumTuner("path", AutoPath.class, AutoPath.AUTO_TEST_PATH_ONE);
        ILoggable<double[]> configs = path.generateDoubleLoggers(
                "x",
                "y",
                "rot"
        );

        return new DriveSubsystem(
                input,
                robotStateControl,
                odometryControl,
                closedLoopsControl,
                driveControl,
                autoControl,
                holoControl,
                visionControl,
                pathTuneable,
                path.generateEnumLogger("State"),
                configs,
                path.generateEnumTuner("Orientation", DriveSubsystem.OrientationChooser.class, DriveSubsystem.OrientationChooser.FIELD_ORIENTED));
    }



    DriveControl buildNeoDriveControl(ProcessPath path) {
        // used to configure the spark motor in SparkSetup
        MotorConfig driveMotorConfig = new MotorConfig(
                DriveConstants.MK4I_L2.getDriveReduction(),
                1,
                DriveConstants.MK4I_L2.getWheelDiameter(),
                true,
                true,
                20,
                false,
                false,
                Optional.empty()
        );

        MotorConfig steerMotorConfig = new MotorConfig(
                DriveConstants.MK4I_L2.getSteerReduction(),
                1,
                DriveConstants.MK4I_L2.getWheelDiameter(),
                true,
                true,
                20,
                false,
                false,
                Optional.empty()
        );

        PIDConfig steerPidConfig = new PIDConfig(1, 0, 0.1, 0);

        DriveControl driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.FRONT_LEFT_DRIVE_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.FRONT_LEFT_STEER_ID, steerMotorConfig, steerPidConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_LEFT_ENCODER_CHANNEL, DriveConstants.FRONT_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.FRONT_RIGHT_DRIVE_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.FRONT_RIGHT_STEER_ID, steerMotorConfig, steerPidConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_RIGHT_ENCODER_CHANNEL, DriveConstants.FRONT_RIGHT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.BACK_LEFT_DRIVE_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.BACK_LEFT_STEER_ID, steerMotorConfig, steerPidConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.BACK_LEFT_ENCODER_CHANNEL, DriveConstants.BACK_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.BACK_RIGHT_DRIVE_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.BACK_RIGHT_STEER_ID, steerMotorConfig, steerPidConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.BACK_RIGHT_ENCODER_CHANNEL, DriveConstants.BACK_RIGHT_OFFSET))
                )
        ).build(path.addChild("drive-control"));

        return driveControl;
    }

    DriveControl buildTalonDriveControl(ProcessPath path) {

        int frontLeftModuleDriveMotor_ID = 1;
        int frontLeftModuleSteerMotor_ID = 2;
        int frontLeftModuleSteerEncoder_ID = 9;

        int frontRightModuleDriveMotor_ID = 7;
        int frontRightModuleSteerMotor_ID = 8;
        int frontRightModuleSteerEncoder_ID = 12;

        int backLeftModuleDriveMotor_ID = 5;
        int backLeftModuleSteerMotor_ID = 6;
        int backLeftModuleSteerEncoder_ID = 11;

        int backRightModuleDriveMotor_ID = 3;
        int backRightModuleSteerMotor_ID = 4;
        int backRightModuleSteerEncoder_ID = 10;

        double frontLeftModuleSteerOffset = -Math.toRadians(232.55); // set front left steer offset

        double frontRightModuleSteerOffset = -Math.toRadians(331.96 - 180); // set front right steer offset

        double backLeftModuleSteerOffset = -Math.toRadians(255.49); // set back left steer offset

        double backRightModuleSteerOffset = -Math.toRadians(70.66 + 180); // set back right steer offset

        double sensorPositionCoefficient = 2.0 * Math.PI / 2048 * DriveConstants.MK4_L2.getSteerReduction();

        return new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontLeftModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontLeftModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontLeftModuleSteerEncoder_ID, frontRightModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontRightModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontRightModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontRightModuleSteerEncoder_ID, frontLeftModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backLeftModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backLeftModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backLeftModuleSteerEncoder_ID, backLeftModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backRightModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backRightModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backRightModuleSteerEncoder_ID, backRightModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                )
        ).build(path.addChild("drive-control"));
    }
}
