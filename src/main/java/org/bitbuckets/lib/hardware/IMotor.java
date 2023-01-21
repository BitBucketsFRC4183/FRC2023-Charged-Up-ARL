package org.bitbuckets.lib.hardware;

/**
 * Represents setpoint control over a device, make sure you know what kind of units you
 * configured it to use, because a velocity pid setpoint is much different than controlling velocity via voltage
 *
 * sorry if this is confusing it's basically just motor.set() but with checks to make it safer and
 * also let you use many  types of motors and not cry
 *
 * Common ways of obtaining: TalonPositionSetup, TalonVelocitySetup, TalonPercentSetup
 */
public interface IMotor extends IRaw {


    /**
     * Moves to percent output
     * @param percent -1.0 to 1.0 representing a percentage of speed
     */
    void moveAtPercent(double percent);

    /**
     * commands to move with pid control to a position
     * should throw if not pid
     * @param position_encoderRotations
     */
    void moveToPosition(double position_encoderRotations);

    /**
     * commands to move with pid control at a velocity
     * should throw if not pid
     * @param velocity_encoderMetersPerSecond
     */
    void moveAtVelocity(double velocity_encoderMetersPerSecond);

    /**
     * TODO migrate this to a sane unit type
     * @return
     */
    double getSetpoint_rawUnits();


}
