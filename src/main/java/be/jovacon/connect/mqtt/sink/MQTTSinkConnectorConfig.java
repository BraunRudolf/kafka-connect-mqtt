package be.jovacon.connect.mqtt.sink;

import be.jovacon.connect.mqtt.Configuration;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class MQTTSinkConnectorConfig extends Configuration {

    public static final String MQTT_TOPIC = "mqtt.topic";
    public static final String MQTT_TOPIC_DOC = "List of topic names to subscribe to";

    public static final String KAFKA_TOPICS = "topics";
    public static final String KAFKA_TOPICS_DOC = "List of kafka topics to consume from";

    public MQTTSinkConnectorConfig(Map<?, ?> originals) {
        super(definition(), originals);
    }

    public static final ConfigDef CONFIG_DEFINITION = definition();

    private static ConfigDef definition() {
        return new ConfigDef()
                .define(BROKER,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        BROKER_DOC)
                .define(MQTT_CLIENT_ID,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        MQTT_CLIENT_ID_DOC)
                .define(MQTT_TOPIC,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        MQTT_TOPIC_DOC)
                .define(KAFKA_TOPICS,
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        KAFKA_TOPICS_DOC)
                .define(MQTT_QOS,
                        ConfigDef.Type.INT,
                        DEFAULT_MQTT_QOS,
                        ConfigDef.Range.between(1, 3),
                        ConfigDef.Importance.MEDIUM,
                        MQTT_QOS_DOC)
                .define(MQTT_AUTO_RECONNECT,
                        ConfigDef.Type.BOOLEAN,
                        DEFAULT_MQTT_AUTO_RECONNECT,
                        ConfigDef.Importance.MEDIUM,
                        MQTT_AUTO_RECONNECT_DOC)
                .define(MQTT_KEEPALIVE_INTERVAL,
                        ConfigDef.Type.INT,
                        DEFAULT_MQTT_KEEPALIVE_INTERVAL,
                        ConfigDef.Importance.LOW,
                        MQTT_KEEPALIVE_INTERVAL_DOC)
                .define(MQTT_CLEAN_SESSION,
                        ConfigDef.Type.BOOLEAN,
                        DEFAULT_MQTT_CLEAN_SESSION,
                        ConfigDef.Importance.LOW,
                        MQTT_CLEAN_SESSION_DOC)
                .define(MQTT_CONNECTION_TIMEOUT,
                        ConfigDef.Type.INT,
                        DEFAULT_MQTT_CONNECTION_TIMEOUT,
                        ConfigDef.Importance.LOW,
                        MQTT_CONNECTION_TIMEOUT_DOC)
                .define(MQTT_USERNAME,
                        ConfigDef.Type.STRING,
                        DEFAULT_MQTT_USERNAME,
                        ConfigDef.Importance.LOW,
                        MQTT_USERNAME_DOC)
                .define(MQTT_PASSWORD,
                        ConfigDef.Type.PASSWORD,
                        DEFAULT_MQTT_PASSWORD,
                        ConfigDef.Importance.LOW,
                        MQTT_PASSWORD_DOC)
                ;
    }
}
