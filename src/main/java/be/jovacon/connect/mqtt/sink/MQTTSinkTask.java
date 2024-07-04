package be.jovacon.connect.mqtt.sink;

import be.jovacon.connect.mqtt.util.Version;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of the Kafka Connect Sink task
 */
public class MQTTSinkTask extends SinkTask {
    private final static Logger logger = LoggerFactory.getLogger(MQTTSinkTask.class);
    private MQTTSinkConnectorConfig config;
    private MQTTSinkConverter mqttSinkConverter;

    private IMqttClient mqttClient;

    @Override
    public String version() {
        return Version.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        config = new MQTTSinkConnectorConfig(map);
        mqttSinkConverter = new MQTTSinkConverter(config);
        try {
            mqttClient = new MqttClient(config.getBroker(), config.getClientId(), new MemoryPersistence());

            logger.info("Connecting to MQTT Broker " + config.getBroker());
            connect(mqttClient);
            logger.info("Connected to MQTT Broker. This connector publishes to the " + this.config.getTopic() + " topic");

        } catch (MqttException e) {
            throw new ConnectException(e);
        }
    }

    private void connect(IMqttClient mqttClient) throws MqttException {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(config.isCleanSession());
        connOpts.setKeepAliveInterval(config.getKeepaliveInterval());
        connOpts.setConnectionTimeout(config.getConnectionTimeout());
        connOpts.setAutomaticReconnect(config.isAutoReconnect());

        if (!config.getUserName().equals("") && !config.getPassword().equals("")) {
            connOpts.setUserName(config.getUserName());
            connOpts.setPassword(config.getPassword().value().toCharArray());
        }

        logger.debug("MQTT Connection properties: " + connOpts);

        mqttClient.connect(connOpts);
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        try {
            for (Iterator<SinkRecord> iterator = collection.iterator(); iterator.hasNext(); ) {
                SinkRecord sinkRecord = iterator.next();
                logger.debug("Received message with offset " + sinkRecord.kafkaOffset());
                MqttMessage mqttMessage = mqttSinkConverter.convert(sinkRecord);
                if (!mqttClient.isConnected()) mqttClient.connect();
                logger.debug("Publishing message to topic " + this.config.getTopic() + " with payload " + new String(mqttMessage.getPayload()));
                mqttClient.publish(this.config.getTopic(), mqttMessage);
            }
        } catch (MqttException e) {
            throw new ConnectException(e);
        }

    }

    @Override
    public void stop() {
        if (mqttClient.isConnected()) {
            try {
                logger.debug("Disconnecting from MQTT Broker " + config.getBroker());
                mqttClient.disconnect();
            } catch (MqttException mqttException) {
                logger.error("Exception thrown while disconnecting client.", mqttException);
            }
        }
    }
}
