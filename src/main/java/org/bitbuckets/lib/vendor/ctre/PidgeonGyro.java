package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.WPI_Pigeon2;
import edu.wpi.first.math.geometry.Rotation2d;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.lib.log.ILoggable;

public class PidgeonGyro implements IGyro {

    final WPI_Pigeon2 pigeon2;

    public PidgeonGyro(WPI_Pigeon2 pigeon2) {
        this.pigeon2 = pigeon2;
    }

    @Override
    public Rotation2d getRotation2d() {
        return pigeon2.getRotation2d();
    }

    @Override
    public double getYaw_deg() {
        return pigeon2.getYaw();
    }

    @Override
    public double getPitch_deg() {
        return pigeon2.getPitch();
    }

    @Override
    public double getRoll_deg() {
        double[] data = new double[4];
        pigeon2.getAccumGyro(data); //fill data

        return data[0];
    }

    @Override
    public void zero() {
        pigeon2.setYaw(0);
        //TODO set everything to 0
    }


}
