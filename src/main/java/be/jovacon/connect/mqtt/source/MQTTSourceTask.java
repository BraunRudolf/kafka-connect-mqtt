package be.jovacon.connect.mqtt.source;

import be.jovacon.connect.mqtt.util.Version;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDeque;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDequeBuilder;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Actual implementation of the Kafka Connect MQTT Source Task
 */
public class MQTTSourceTask extends SourceTask {
    private static final Logger logger = LoggerFactory.getLogger(MQTTSourceTask.class);

    private MQTTSourceConnectorConfig config;
    private MQTTSourceConverter converter;
    private SourceRecordDeque buffer;
    private IMqttClient client;

    public void start(Map<String, String> props) {
        config = new MQTTSourceConnectorConfig(props);
        converter = new MQTTSourceConverter(config);
        buffer = SourceRecordDequeBuilder.of().batchSize(config.getRecordsBufferMaxBatchSize()).emptyWaitMs(config.getRecordsBufferEmptyTimeout()).maximumCapacityTimeoutMs(config.getRecordsBufferFullTimeout()).maximumCapacity(config.getRecordsBufferMaxCapacity()).build();
        try {
            client = new MqttClient(config.getBroker(), config.getClientId(), new MemoryPersistence());
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        logger.info("Reconnected to MQTT Broker " + serverURI);
                    }

                    final String subscribedTopic = config.getTopics();
                    final int qos = config.getQoS();

                    logger.info("Subscribing to " + subscribedTopic + " with QoS " + qos);
                    try {
                        client.subscribe(subscribedTopic, qos, (topic, message) -> {
                            messageArrived0(topic, message);
                        });
                        logger.info("Subscribed to " + subscribedTopic + " with QoS " + qos);

                    } catch (MqttException e) {
                        throw new ConnectException(e);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    logger.warn("Connection is lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    messageArrived0(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // nothing to do.
                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(config.isCleanSession());
            options.setKeepAliveInterval(config.getKeepaliveInterval());
            options.setConnectionTimeout(config.getConnectionTimeout());
            options.setAutomaticReconnect(config.isAutoReconnect());

            if (!config.getUserName().isEmpty() && !config.getPassword().equals("")) {
                options.setUserName(config.getUserName());
                options.setPassword(config.getPassword().value().toCharArray());
            }
            logger.info("MQTT Connection properties: " + options);

            logger.info("Connecting to MQTT Broker " + config.getBroker());
            client.connect(options);
            logger.info("Connected to MQTT Broker");

        } catch (MqttException e) {
            throw new ConnectException(e);
        }
    }

    /**
     * method is called periodically by the Connect framework
     *
     * @return
     * @throws InterruptedException
     */
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = buffer.getBatch();
        logger.trace("Records returning to poll(): " + records);
        return records;
    }

    public void stop() {
        // TODO need graceful shutdown
        if (client.isConnected()) {
            try {
                logger.debug("Disconnecting from MQTT Broker " + config.getBroker());
                client.disconnect();
            } catch (MqttException mqttException) {
                logger.error("Exception thrown while disconnecting client.", mqttException);
            }
        }
    }

    public String version() {
        return Version.getVersion();
    }

    private void messageArrived0(String topic, MqttMessage message) throws Exception {
        logger.debug("Message arrived in connector from topic " + topic);
        SourceRecord record = converter.convert(topic, message);
        logger.debug("Converted record: " + record);
        buffer.add(record);
    }
}
