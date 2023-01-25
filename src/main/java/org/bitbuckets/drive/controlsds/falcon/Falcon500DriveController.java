package org.bitbuckets.drive.controlsds.falcon;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.drive.controlsds.sds.DriveController;

public class Falcon500DriveController implements DriveController {
    private final WPI_TalonFX motor;
    private final double sensorVelocityCoefficient;
    private final double nominalVoltage;

    public Falcon500DriveController(WPI_TalonFX motor, double sensorVelocityCoefficient, double nominalVoltage) {
        this.motor = motor;
        this.sensorVelocityCoefficient = sensorVelocityCoefficient;
        this.nominalVoltage = nominalVoltage;
    }

    @Override
    public void setReferenceVoltage(double voltage) {
        motor.set(TalonFXControlMode.PercentOutput, voltage / nominalVoltage);
    }

    @Override
    public double getStateVelocity() {
        return motor.getSelectedSensorVelocity() * sensorVelocityCoefficient;
    }
}