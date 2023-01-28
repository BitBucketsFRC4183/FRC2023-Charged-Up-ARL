package org.bitbuckets.lib.core;

import edu.wpi.first.networktables.*;
import org.bitbuckets.robot.RobotConstants;
import org.littletonrobotics.junction.LogDataReceiver;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.networktables.NT4Publisher;

import java.util.HashMap;
import java.util.Map;

public class NetworkPublisher implements LogDataReceiver {


    private final NetworkTable akitTable;
    private final IntegerPublisher timestampPublisher;
    private final Map<String, GenericPublisher> publishers = new HashMap<>();

    public NetworkPublisher() {
        akitTable = NetworkTableInstance.getDefault().getTable("/");
        timestampPublisher = akitTable.getIntegerTopic(timestampKey.substring(1)).publish(PubSubOption.sendAll(true));
    }

    int counter = 0;

    //PutTable but it doesn't do dumb stuff
    public void putTable(LogTable table) {

        counter++;

        if (counter > RobotConstants.NETWORK_SEND_PERIOD) {
            counter = 0;
            timestampPublisher.set(table.getTimestamp(), table.getTimestamp());


            Map<String, LogTable.LogValue> newMap = table.getAll(false);

            for (Map.Entry<String, LogTable.LogValue> field : newMap.entrySet()) {

                // Create publisher if necessary
                String key = field.getKey().substring(1);
                GenericPublisher publisher = publishers.get(key);
                if (publisher == null) {
                    publisher = akitTable.getTopic(key).genericPublish(field.getValue().type.getNT4Type(),
                            PubSubOption.sendAll(true));
                    publishers.put(key, publisher);
                }

                // Write new data
                switch (field.getValue().type) {
                    case Raw:
                        publisher.setRaw(field.getValue().getRaw(), table.getTimestamp());
                        break;
                    case Boolean:
                        publisher.setBoolean(field.getValue().getBoolean(), table.getTimestamp());
                        break;
                    case BooleanArray:
                        publisher.setBooleanArray(field.getValue().getBooleanArray(), table.getTimestamp());
                        break;
                    case Integer:
                        publisher.setInteger(field.getValue().getInteger(), table.getTimestamp());
                        break;
                    case IntegerArray:
                        publisher.setIntegerArray(field.getValue().getIntegerArray(), table.getTimestamp());
                        break;
                    case Float:
                        publisher.setFloat(field.getValue().getFloat(), table.getTimestamp());
                        break;
                    case FloatArray:
                        publisher.setFloatArray(field.getValue().getFloatArray(), table.getTimestamp());
                        break;
                    case Double:
                        publisher.setDouble(field.getValue().getDouble(), table.getTimestamp());
                        break;
                    case DoubleArray:
                        publisher.setDoubleArray(field.getValue().getDoubleArray(), table.getTimestamp());
                        break;
                    case String:
                        publisher.setString(field.getValue().getString(), table.getTimestamp());
                        break;
                    case StringArray:
                        publisher.setStringArray(field.getValue().getStringArray(), table.getTimestamp());
                        break;
                }
            }

        }



    }
}
