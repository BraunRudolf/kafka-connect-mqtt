package be.jovacon.kafka.connect;

import be.jovacon.kafka.connect.config.MQTTSourceConnectorConfig;
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
    private final Logger log = LoggerFactory.getLogger(MQTTSourceConnector.class);

    private MQTTSourceConnectorConfig config;
    private MQTTSourceConverter mqttSourceConverter;
    private SourceRecordDeque sourceRecordDeque;

    private IMqttClient mqttClient;

    public void start(Map<String, String> props) {
        config = new MQTTSourceConnectorConfig(props);
        mqttSourceConverter = new MQTTSourceConverter(config);
        this.sourceRecordDeque = SourceRecordDequeBuilder.of()
                .batchSize(config.getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_MAX_BATCH_SIZE))
                .emptyWaitMs(config.getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_EMPTY_TIMEOUT))
                .maximumCapacityTimeoutMs(config.getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_FULL_TIMEOUT))
                .maximumCapacity(config.getInt(MQTTSourceConnectorConfig.RECORDS_BUFFER_MAX_CAPACITY))
                .build();
        try {
            mqttClient = new MqttClient(config.getString(MQTTSourceConnectorConfig.BROKER), config.getString(MQTTSourceConnectorConfig.MQTT_CLIENT_ID), new MemoryPersistence());
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        log.info("Reconnected to MQTT Broker " + serverURI);
                    }

                    String subscribedTopic = config.getString(MQTTSourceConnectorConfig.MQTT_TOPICS);
                    int qos = config.getInt(MQTTSourceConnectorConfig.MQTT_QOS);

                    log.info("Subscribing to " + subscribedTopic + " with QoS " + qos);
                    try {
                        mqttClient.subscribe(subscribedTopic, qos, (topic, message) -> {
                            messageArrived0(topic, message);
                        });
                        log.info("Subscribed to " + subscribedTopic + " with QoS " + qos);

                    } catch (MqttException e) {
                        throw new ConnectException(e);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("Connection is lost", cause);
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
            options.setCleanSession(config.getBoolean(MQTTSourceConnectorConfig.MQTT_CLEAN_SESSION));
            options.setKeepAliveInterval(config.getInt(MQTTSourceConnectorConfig.MQTT_KEEPALIVE_INTERVAL));
            options.setConnectionTimeout(config.getInt(MQTTSourceConnectorConfig.MQTT_CONNECTION_TIMEOUT));
            options.setAutomaticReconnect(config.getBoolean(MQTTSourceConnectorConfig.MQTT_AUTO_RECONNECT));

            if (!config.getString(MQTTSourceConnectorConfig.MQTT_USERNAME).isEmpty()
                    && !config.getPassword(MQTTSourceConnectorConfig.MQTT_PASSWORD).equals("")) {
                options.setUserName(config.getString(MQTTSourceConnectorConfig.MQTT_USERNAME));
                options.setPassword(config.getPassword(MQTTSourceConnectorConfig.MQTT_PASSWORD).value().toCharArray());
            }
            log.info("MQTT Connection properties: " + options);

            log.info("Connecting to MQTT Broker " + config.getString(MQTTSourceConnectorConfig.BROKER));
            mqttClient.connect(options);
            log.info("Connected to MQTT Broker");

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
        List<SourceRecord> records = sourceRecordDeque.getBatch();
        log.trace("Records returning to poll(): " + records);
        return records;
    }

    public void stop() {
        // TODO need graceful shutdown
        if (mqttClient.isConnected()) {
            try {
                log.debug("Disconnecting from MQTT Broker " + config.getString(MQTTSourceConnectorConfig.BROKER));
                mqttClient.disconnect();
            } catch (MqttException mqttException) {
                log.error("Exception thrown while disconnecting client.", mqttException);
            }
        }
    }

    public String version() {
        return Version.getVersion();
    }

    private void messageArrived0(String topic, MqttMessage message) throws Exception {
        log.debug("Message arrived in connector from topic " + topic);
        SourceRecord record = mqttSourceConverter.convert(topic, message);
        log.debug("Converted record: " + record);
        sourceRecordDeque.add(record);
    }
}
