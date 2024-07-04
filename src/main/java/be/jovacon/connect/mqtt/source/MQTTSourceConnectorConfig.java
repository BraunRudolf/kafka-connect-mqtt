package be.jovacon.connect.mqtt.source;

import be.jovacon.connect.mqtt.MQTTConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.List;
import java.util.Map;

public class MQTTSourceConnectorConfig extends MQTTConnectorConfig {
    private static final String MQTT_TOPICS = "mqtt.topics";
    private static final String MQTT_TOPICS_DOC = "List of topic names to subscribe to";

    private static final String KAFKA_TOPIC = "kafka.topic";
    private static final String KAFKA_TOPIC_DOC = "kafka topic to publish to";

    private static final String RECORDS_BUFFER_MAX_CAPACITY = "records.buffer.max.capacity";
    private static final String RECORDS_BUFFER_MAX_CAPACITY_DOC = "The max capacity of the buffer that stores the data before sending it to the Kafka Topic.";
    private static final int DEFAULT_RECORDS_BUFFER_MAX_CAPACITY = 50000;

    private static final String RECORDS_BUFFER_MAX_BATCH_SIZE = "records.buffer.max.batch.size";
    private static final String RECORDS_BUFFER_MAX_BATCH_SIZE_DOC = "The maximum size of the batch to send to Kafka topic in a single request.";
    private static final int DEFAULT_RECORDS_BUFFER_MAX_BATCH_SIZE = 4096;

    private static final String RECORDS_BUFFER_EMPTY_TIMEOUT = "records.buffer.empty.timeout";
    private static final String RECORDS_BUFFER_EMPTY_TIMEOUT_DOC = "Timeout(ms) to wait on an empty buffer for extracting records.";
    private static final int DEFAULT_RECORDS_BUFFER_EMPTY_TIMEOUT = 100;

    private static final String RECORDS_BUFFER_FULL_TIMEOUT = "records.buffer.full.timeout";
    private static final String RECORDS_BUFFER_FULL_TIMEOUT_DOC = "Timeout(ms) to wait on a full buffer for inserting new records.";
    private static final int DEFAULT_RECORDS_BUFFER_FULL_TIMEOUT = 60000;

    public MQTTSourceConnectorConfig(Map<?, ?> originals) {
        super(definition(), originals);
    }

    public String getTopics() {
        return getString(MQTTSourceConnectorConfig.MQTT_TOPICS);
    }

    public String getKafkaTopic() {
        return getString(KAFKA_TOPIC);
    }

    public int getRecordsBufferMaxCapacity() {
        return getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_MAX_CAPACITY);
    }

    public int getRecordsBufferFullTimeout() {
        return getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_FULL_TIMEOUT);
    }

    public int getRecordsBufferEmptyTimeout() {
        return getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_EMPTY_TIMEOUT);
    }

    public int getRecordsBufferMaxBatchSize() {
        return getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_MAX_BATCH_SIZE);
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
                .define(MQTT_TOPICS,
                        ConfigDef.Type.STRING,
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
