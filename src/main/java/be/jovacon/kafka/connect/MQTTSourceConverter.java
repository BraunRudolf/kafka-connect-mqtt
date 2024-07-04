package be.jovacon.kafka.connect;

import be.jovacon.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.Converter;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Converts a MQTT message to a Kafka message
 */
public class MQTTSourceConverter {
    private static final Logger log = LoggerFactory.getLogger(MQTTSourceConverter.class);

    private static final HashMap<String, ?> EMPTY = new HashMap<>();
    private final MQTTSourceConnectorConfig config;

    public MQTTSourceConverter(MQTTSourceConnectorConfig config) {
        this.config = config;
    }

    protected SourceRecord convert(String topic, MqttMessage mqttMessage) {
        log.debug("Converting MQTT message: " + mqttMessage);
        // Kafka 2.3
        ConnectHeaders headers = new ConnectHeaders();
        headers.addInt("mqtt.message.id", mqttMessage.getId());
        headers.addInt("mqtt.message.qos", mqttMessage.getQos());
        headers.addBoolean("mqtt.message.duplicate", mqttMessage.isDuplicate());
        headers.addString("mqtt.topic", topic);

        // Kafka 2.3
        SourceRecord sourceRecord = new SourceRecord(
                EMPTY,
                EMPTY,
                this.config.getString(MQTTSourceConnectorConfig.KAFKA_TOPIC),
                null,
                Schema.STRING_SCHEMA,
                topic,
                // TODO Integration with Kafka Converter
                Schema.BYTES_SCHEMA,
                mqttMessage.getPayload(),
                System.currentTimeMillis(),
                headers);
        log.debug("Converted MQTT Message: " + sourceRecord);
        return sourceRecord;
    }
}
