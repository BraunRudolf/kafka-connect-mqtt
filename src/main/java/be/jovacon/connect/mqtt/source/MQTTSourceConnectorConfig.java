package be.jovacon.connect.mqtt.source;

import be.jovacon.connect.mqtt.Configuration;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public final class MQTTSourceConnectorConfig extends Configuration {
    public static final String MQTT_TOPICS = "mqtt.topics";
    public static final String MQTT_TOPICS_DOC = "List of topic names to subscribe to";

    public static final String KAFKA_TOPIC = "kafka.topic";
    public static final String KAFKA_TOPIC_DOC = "kafka topic to publish to";

    public static final String RECORDS_BUFFER_MAX_CAPACITY = "records.buffer.max.capacity";
    public static final String RECORDS_BUFFER_MAX_CAPACITY_DOC = "The max capacity of the buffer that stores the data before sending it to the Kafka Topic.";
    public static final int DEFAULT_RECORDS_BUFFER_MAX_CAPACITY = 50000;

    public static final String RECORDS_BUFFER_MAX_CAPACITY_IN_BYTES = "records.buffer.max.bytes";
    public static final String RECORDS_BUFFER_MAX_CAPACITY_IN_BYTES_DOC = "The maximum byte size of the buffer used to store data before sending it to the Kafka topic.";
    public static final int DEFAULT_RECORDS_BUFFER_MAX_CAPACITY_IN_BYTES = 100 * 1024 * 1024;

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
        super(definition(), originals);
    }

    public static final ConfigDef CONFIG_DEFINITION = definition();

    private static ConfigDef definition() {
        return new ConfigDef()
                .define(BROKER,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        BROKER_DOC
                )
                .define(MQTT_CLIENT_ID,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        MQTT_CLIENT_ID_DOC
                )
                .define(MQTT_TOPICS,
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        MQTT_TOPICS_DOC
                )
                .define(KAFKA_TOPIC,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        KAFKA_TOPIC_DOC
                )
                .define(MQTT_QOS,
                        ConfigDef.Type.INT,
                        DEFAULT_MQTT_QOS,
                        ConfigDef.Range.between(1, 3),
                        ConfigDef.Importance.MEDIUM,
                        MQTT_QOS_DOC
                )
                .define(MQTT_AUTO_RECONNECT,
                        ConfigDef.Type.BOOLEAN,
                        DEFAULT_MQTT_AUTO_RECONNECT,
                        ConfigDef.Importance.MEDIUM,
                        MQTT_AUTO_RECONNECT_DOC
                )
                .define(MQTT_KEEPALIVE_INTERVAL,
                        ConfigDef.Type.INT,
                        DEFAULT_MQTT_KEEPALIVE_INTERVAL,
                        ConfigDef.Importance.LOW,
                        MQTT_KEEPALIVE_INTERVAL_DOC
                )
                .define(MQTT_CLEAN_SESSION,
                        ConfigDef.Type.BOOLEAN,
                        DEFAULT_MQTT_CLEAN_SESSION,
                        ConfigDef.Importance.LOW,
                        MQTT_CLEAN_SESSION_DOC
                )
                .define(MQTT_CONNECTION_TIMEOUT,
                        ConfigDef.Type.INT,
                        DEFAULT_MQTT_CONNECTION_TIMEOUT,
                        ConfigDef.Importance.LOW,
                        MQTT_CONNECTION_TIMEOUT_DOC
                )
                .define(MQTT_USERNAME,
                        ConfigDef.Type.STRING,
                        DEFAULT_MQTT_USERNAME,
                        ConfigDef.Importance.LOW,
                        MQTT_USERNAME_DOC
                )
                .define(MQTT_PASSWORD,
                        ConfigDef.Type.PASSWORD,
                        DEFAULT_MQTT_PASSWORD,
                        ConfigDef.Importance.LOW,
                        MQTT_PASSWORD_DOC
                )
                .define(RECORDS_BUFFER_MAX_CAPACITY,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_MAX_CAPACITY,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_MAX_CAPACITY_DOC
                )
                .define(RECORDS_BUFFER_MAX_CAPACITY_IN_BYTES,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_MAX_CAPACITY_IN_BYTES,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_MAX_CAPACITY_IN_BYTES_DOC
                )
                .define(RECORDS_BUFFER_MAX_BATCH_SIZE,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_MAX_BATCH_SIZE,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_MAX_BATCH_SIZE_DOC
                )
                .define(RECORDS_BUFFER_EMPTY_TIMEOUT,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_EMPTY_TIMEOUT,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_EMPTY_TIMEOUT_DOC
                )
                .define(RECORDS_BUFFER_FULL_TIMEOUT,
                        ConfigDef.Type.INT,
                        DEFAULT_RECORDS_BUFFER_FULL_TIMEOUT,
                        ConfigDef.Importance.LOW,
                        RECORDS_BUFFER_FULL_TIMEOUT_DOC
                )
                .define(KEY_CONVERTER,
                        ConfigDef.Type.STRING,
                        DEFAULT_KEY_CONVERTER,
                        ConfigDef.Importance.LOW,
                        KEY_CONVERTER_DOC
                )
                .define(KEY_CONVERTER_SCHEMAS_ENABLED,
                        ConfigDef.Type.BOOLEAN,
                        DEFAULT_KEY_CONVERTER_SCHEMAS_ENABLED,
                        ConfigDef.Importance.LOW,
                        KEY_CONVERTER_SCHEMAS_ENABLED_DOC
                )
                .define(VALUE_CONVERTER,
                        ConfigDef.Type.STRING,
                        DEFAULT_VALUE_CONVERTER,
                        ConfigDef.Importance.LOW,
                        VALUE_CONVERTER_DOC
                )
                .define(VALUE_CONVERTER_SCHEMAS_ENABLED,
                        ConfigDef.Type.BOOLEAN,
                        DEFAULT_VALUE_CONVERTER_SCHEMAS_ENABLED,
                        ConfigDef.Importance.LOW,
                        VALUE_CONVERTER_SCHEMAS_ENABLED_DOC
                );
    }

}
