package be.jovacon.connect.mqtt;

import be.jovacon.connect.mqtt.sink.MQTTSinkConnectorConfig;
import be.jovacon.connect.mqtt.sink.MQTTSinkTask;
import be.jovacon.connect.mqtt.util.Version;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of the Kafka Connect Sink connector
 */
public class MQTTSinkConnector extends SinkConnector {
    private static final Logger logger = LoggerFactory.getLogger(MQTTSinkConnector.class);
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> map) {
        this.configProps = Collections.unmodifiableMap(map);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MQTTSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        logger.debug("Enter taskconfigs");
        if (maxTasks > 1) {
            logger.info("maxTasks is " + maxTasks + ". MaxTasks > 1 is not supported in this connector.");
        }
        List<Map<String, String>> taskConfigs = new ArrayList<>(1);
        taskConfigs.add(new HashMap<>(configProps));

        logger.debug("Taskconfigs: " + taskConfigs);
        return taskConfigs;
    }

    @Override
    public void stop() {
        // nothing to do.
    }

    @Override
    public ConfigDef config() {
        return MQTTSinkConnectorConfig.CONFIG_DEFINITION;
    }

    @Override
    public String version() {
        return Version.getVersion();
    }
}
