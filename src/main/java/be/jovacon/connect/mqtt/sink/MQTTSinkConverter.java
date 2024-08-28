package be.jovacon.connect.mqtt.sink;

import be.jovacon.connect.mqtt.Configuration;
import org.apache.kafka.connect.sink.SinkRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts a Kafka message to a MQTT Message
 */
public class MQTTSinkConverter {
    private static final Logger logger = LoggerFactory.getLogger(MQTTSinkConverter.class);
    private final MQTTSinkConnectorConfig config;

    public MQTTSinkConverter(MQTTSinkConnectorConfig config) {
        this.config = config;
    }

    protected MqttMessage convert(SinkRecord sinkRecord) {
        logger.trace("Converting Kafka message");

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(((String) sinkRecord.value()).getBytes());
        mqttMessage.setQos(this.config.getInt(Configuration.MQTT_QOS));
        logger.trace("Result MQTTMessage: " + mqttMessage);
        return mqttMessage;
    }
}
