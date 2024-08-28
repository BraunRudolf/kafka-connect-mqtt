package be.jovacon.connect.mqtt;

import be.jovacon.connect.mqtt.source.MQTTSourceConnectorConfig;
import be.jovacon.connect.mqtt.source.MQTTSourceTask;
import be.jovacon.connect.mqtt.util.Version;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of the Kafka Connect Source connector
 */
public class MQTTSourceConnector extends SourceConnector {
    private static final Logger logger = LoggerFactory.getLogger(MQTTSourceConnector.class);
    private Map<String, String> configProperties;
    private MQTTSourceConnectorConfig config;

    public void start(Map<String, String> properties) {
        logger.info("Starting MQTT Source Connector");

        this.configProperties = Collections.unmodifiableMap(properties);
        this.config = new MQTTSourceConnectorConfig(configProperties);
    }

    public Class<? extends Task> taskClass() {
        return MQTTSourceTask.class;
    }

    public List<Map<String, String>> taskConfigs(int maxTasks) {
        if (maxTasks > 1) {
            logger.info("maxTasks is " + maxTasks + ". MaxTasks > 1 is not supported in this connector.");
        }

        List<Map<String, String>> taskConfigs = new ArrayList<>(1);
        taskConfigs.add(new HashMap<>(configProperties));

        logger.debug("Task configs: " + taskConfigs);
        return taskConfigs;
    }

    public void stop() {
        // nothing to do.
    }

    public ConfigDef config() {
        return MQTTSourceConnectorConfig.CONFIG_DEFINITION;
    }

    public String version() {
        return Version.getVersion();
    }
}
