package org.bitbuckets.drive;


import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.robot.RobotConstants;

public interface DriveConstants {

    //TODO divide by 2
    double SENSOR_UNITS_PER_REVOLUTION = 2048.0;

    double TURN_REDUCTION = (15.0 / 32.0) * (10.0 / 60.0);
    double DRIVE_REDUCTION = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);
    double DRIVE_METERS_FACTOR = 1;
    double TURN_METERS_FACTOR = 1; //TODO fix

    double MAX_DRIVE_VELOCITY = 6380.0 / 60.0 * (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0) * 0.10033 * Math.PI;
    double SLOW_DRIVE_VELOCITY = MAX_DRIVE_VELOCITY * 0.75;
    double MAX_ANG_VELOCITY = MAX_DRIVE_VELOCITY / Math.hypot(RobotConstants.WIDTH, RobotConstants.BASE);

    //TODO get rid of this
    SimpleMotorFeedforward FF = new SimpleMotorFeedforward(0.65292, 2.3053, 0.37626); //converts velocity to voltage

    SwerveModuleState[] LOCK = new SwerveModuleState[]{
            new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(45))
    };

    // Drive Subsystem
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

    double drivetrainTrackWidth_meters = 0.6096; // set trackwidth

    double drivetrainWheelBase_meters = 0.7112; // set wheelbase

    double frontLeftModuleSteerOffset = -Math.toRadians(232.55); // set front left steer offset

    double frontRightModuleSteerOffset = -Math.toRadians(331.96 - 180); // set front right steer offset

    double backLeftModuleSteerOffset = -Math.toRadians(255.49); // set back left steer offset

    double backRightModuleSteerOffset = -Math.toRadians(70.66 + 180); // set back right steer offset

}