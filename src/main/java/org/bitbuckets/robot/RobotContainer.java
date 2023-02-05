package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final ElevatorSubsystem elevatorSubsystem;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, ElevatorSubsystem elevatorSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
    }

    public void autoPeriodic() {

    }

    public void robotPeriodic() {
        driveSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        elevatorSubsystem.teleopPeriodic();
        armSubsystem.teleopPeriodic();
    }




}
