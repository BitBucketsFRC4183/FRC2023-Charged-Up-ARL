package org.bitbuckets.lib.log.type;

import edu.wpi.first.math.geometry.Pose3d;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.log.ILoggable;

public class Pose3dLoggable implements ILoggable<Pose3d> {

    final LogDriver driver;
    final int id;
    final String keyName;

    public Pose3dLoggable(LogDriver driver, int id, String keyName) {
        this.driver = driver;
        this.id = id;
        this.keyName = keyName;
    }

    @Override
    public void log(Pose3d data) {
        driver.report(id, keyName, data);
    }
}
