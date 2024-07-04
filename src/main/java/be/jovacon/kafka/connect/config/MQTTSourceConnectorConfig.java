package be.jovacon.kafka.connect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class MQTTSourceConnectorConfig extends AbstractConfig {

    public static final String BROKER = "mqtt.broker";
    public static final String BROKER_DOC = "Host and port of the MQTT broker, eg: tcp://192.168.1.1:1883";

    public static final String MQTT_CLIENT_ID = "mqtt.clientID";
    public static final String MQTT_CLIENT_ID_DOC = "clientID";

    public static final String MQTT_TOPICS = "mqtt.topics";
    public static final String MQTT_TOPICS_DOC = "List of topic names to subscribe to";

    public static final String MQTT_QOS = "mqtt.qos";
    public static final String MQTT_QOS_DOC = "Quality of service MQTT messaging, default is 1 (at least once)";
    public static final int DEFAULT_MQTT_QOS = 1;

    public static final String MQTT_AUTO_RECONNECT = "mqtt.automaticReconnect";
    public static final String MQTT_AUTO_RECONNECT_DOC = "set Automatic reconnect, default true";
    public static final boolean DEFAULT_MQTT_AUTO_RECONNECT = true;

    public static final String MQTT_KEEPALIVE_INTERVAL = "mqtt.keepAliveInterval";
    public static final String MQTT_KEEPALIVE_INTERVAL_DOC = "set the keepalive interval, default is 60 seconds";
    public static final int DEFAULT_MQTT_KEEPALIVE_INTERVAL = 60;

    public static final String MQTT_CLEAN_SESSION = "mqtt.cleanSession";
    public static final String MQTT_CLEAN_SESSION_DOC = "Sets whether the client and server should remember state across restarts and reconnects, default is true";
    public static final boolean DEFAULT_MQTT_CLEAN_SESSION = true;

    public static final String MQTT_CONNECTION_TIMEOUT = "mqtt.connectionTimeout";
    public static final String MQTT_CONNECTION_TIMEOUT_DOC = "Sets the connection timeout, default is 30";
    public static final int DEFAULT_MQTT_CONNECTION_TIMEOUT = 30;

    public static final String MQTT_USERNAME = "mqtt.userName";
    public static final String MQTT_USERNAME_DOC = "Sets the username for the MQTT connection timeout, default is \"\"";
    public static final String DEFAULT_MQTT_USERNAME = "";

    public static final String MQTT_PASSWORD = "mqtt.password";
    public static final String MQTT_PASSWORD_DOC = "Sets the password for the MQTT connection timeout, default is \"\"";
    public static final String DEFAULT_MQTT_PASSWORD = "";


    public static final String KAFKA_TOPIC = "kafka.topic";
    public static final String KAFKA_TOPIC_DOC = "List of kafka topics to publish to";

    public static final String RECORDS_BUFFER_MAX_CAPACITY = "records.buffer.max.capacity";
    public static final String RECORDS_BUFFER_MAX_CAPACITY_DOC = "The max capacity of the buffer that stores the data before sending it to the Kafka Topic.";
    public static final int DEFAULT_RECORDS_BUFFER_MAX_CAPACITY = 50000;

    public static final String RECORDS_BUFFER_MAX_BATCH_SIZE = "records.buffer.max.batch.size";
    public static final String RECORDS_BUFFER_MAX_BATCH_SIZE_DOC = "The maximum size of the batch to send to Kafka topic in a single request.";
    public static final int DEFAULT_RECORDS_BUFFER_MAX_BATCH_SIZE = 4096;

    public static final String RECORDS_BUFFER_EMPTY_TIMEOUT = "records.buffer.empty.timeout";
    public static final String RECORDS_BUFFER_EMPTY_TIMEOUT_DOC = "Timeout(ms) to wait on an empty buffer for extracting records.";
    public static final int DEFAULT_RECORDS_BUFFER_EMPTY_TIMEOUT = 100;

    public static final String RECORDS_BUFFER_FULL_TIMEOUT = "records.buffer.full.timeout";
    public static final String RECORDS_BUFFER_FULL_TIMEOUT_DOC = "Timeout(ms) to wait on a full buffer for inserting new records.";
    public static final int DEFAULT_RECORDS_BUFFER_FULL_TIMEOUT = 60000;

    public MQTTSourceConnectorConfig(Map<?, ?> originals) {
        super(configDef(), originals);
    }

    private static final ConfigDef CONFIG_DEFINITION;

    public static ConfigDef configDef() {
        return CONFIG_DEFINITION;
    }

    static {
        CONFIG_DEFINITION = new ConfigDef();

        CONFIG_DEFINITION
                .define(BROKER,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        BROKER_DOC)
                .define(MQTT_CLIENT_ID,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        MQTT_CLIENT_ID_DOC)
                .define(MQTT_TOPICS,
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        MQTT_TOPICS_DOC)
                .define(KAFKA_TOPIC,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        KAFKA_TOPIC_DOC)
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
                .define(RECORDS_BUFFER_MAX_CAPACITY,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_MAX_CAPACITY,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_MAX_CAPACITY_DOC)
                .define(RECORDS_BUFFER_MAX_BATCH_SIZE,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_MAX_BATCH_SIZE,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_MAX_BATCH_SIZE_DOC)
                .define(RECORDS_BUFFER_EMPTY_TIMEOUT,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_EMPTY_TIMEOUT,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_EMPTY_TIMEOUT_DOC)
                .define(RECORDS_BUFFER_FULL_TIMEOUT,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_FULL_TIMEOUT,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_FULL_TIMEOUT_DOC)
        ;
    }
}
