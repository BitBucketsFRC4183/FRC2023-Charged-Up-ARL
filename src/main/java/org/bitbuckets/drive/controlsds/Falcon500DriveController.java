package org.bitbuckets.drive.controlsds;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class Falcon500DriveController implements DriveController {
    private final TalonFX motor;
    private final double sensorVelocityCoefficient;
    private final double nominalVoltage;

    Falcon500DriveController(TalonFX motor, double sensorVelocityCoefficient, double nominalVoltage) {
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