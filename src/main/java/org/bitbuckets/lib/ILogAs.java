package org.bitbuckets.lib;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.log.ILoggable;

import java.util.Map;

public interface ILogAs<T> {

    ILoggable<T> generate(String key, ShuffleboardContainer con);

    ILogAs<Boolean> BOOLEAN = (k, c) -> {
        var e = c
                .add(k, false)
                .withWidget(BuiltInWidgets.kBooleanBox)
                .getEntry();

        return e::setBoolean;
    };

    ILogAs<Double> DOUBLE = (key,con) -> {
        var e = con.add(key, 0.0).withWidget(BuiltInWidgets.kTextView).getEntry();

        return e::setDouble;
    };

    ILogAs<Double> DOUBLE_GRAPH = (key,con) -> {
        var e = con
                .add(key, 0.0)
                .withWidget(BuiltInWidgets.kGraph)
                .getEntry();

        return e::setDouble;
    };

    ILogAs<Double> DOUBLE_ANGLE = (key, con) -> {
        var e = con
                .add(key, 0.0)
                .withWidget(BuiltInWidgets.kGyro)
                .getEntry();

        return e::setDouble;
    };

    ILogAs<double[]> PID_OUT_GRAPH = (key,con) -> {
        var e = con
                .add(key, new double[] {0})
                .withWidget(BuiltInWidgets.kGraph)
                .getEntry();

        return e::setDoubleArray;
    };

    static ILogAs<Double> DOUBLE_BAR(double min, double max) {
        return (key,con) -> {
            var e = con
                    .add(key, 0.0)
                    .withWidget(BuiltInWidgets.kNumberBar)
                    .withProperties(Map.of("min", min, "max", max))
                    .getEntry();

            return e::setDouble;
        };
    }

    //this is a silly type hack, pls fix
    static <T extends Enum<T>> ILogAs<T> ENUM(Class<T> clazz) {
        return (k,c) -> {
            var e = c
                    .add(k, "default data")
                    .getEntry();

            return t -> e.setString(t.name());
        };
    }


}
