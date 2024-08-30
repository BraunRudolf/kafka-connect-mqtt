package be.jovacon.connect.mqtt.source;

import be.jovacon.connect.mqtt.ConverterBuilder;
import be.jovacon.connect.mqtt.Event;
import be.jovacon.connect.mqtt.EventQueue;
import be.jovacon.connect.mqtt.util.Version;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.Converter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.jovacon.connect.mqtt.Configuration.*;
import static be.jovacon.connect.mqtt.source.MQTTSourceConnectorConfig.*;

/**
 * Actual implementation of the Kafka Connect MQTT Source Task
 */
public class MQTTSourceTask extends SourceTask {
    private static final Logger logger = LoggerFactory.getLogger(MQTTSourceTask.class);

    private static final String ATTR_MESSAGE_ID = "event.message.id";
    private static final String ATTR_MESSAGE_QOS = "event.message.qos";

    private MQTTSourceConnectorConfig config;
    private Converter keyConverter;
    private Converter valueConverter;
    private EventQueue<SourceRecord> buffer;
    private IMqttClient client;

    public void start(Map<String, String> props) {
        config = new MQTTSourceConnectorConfig(props);
        buffer = new EventQueue.Builder<SourceRecord>()
                .maxQueueSize(config.getInt(RECORDS_BUFFER_MAX_CAPACITY))
                .maxQueueSizeInBytes(config.getInt(RECORDS_BUFFER_MAX_CAPACITY_IN_BYTES))
                .maxBatchSize(config.getInt(RECORDS_BUFFER_MAX_BATCH_SIZE))
                .pollInterval(Duration.of(config.getInt(RECORDS_BUFFER_EMPTY_TIMEOUT), ChronoUnit.MILLIS))
                .build();

        ConverterBuilder converterBuilder = new ConverterBuilder(config);
        try {
            keyConverter = converterBuilder.build(true);
            valueConverter = converterBuilder.build(false);
        } catch (Exception e) {
            throw new ConnectException("Initialize converter failed.", e);
        }

        final String[] topics = config.getList(MQTTSourceConnectorConfig.MQTT_TOPICS).toArray(new String[0]);
        final int[] qos = new int[topics.length];
        for (int i = 0, j = qos.length; i < j; i++) {
            qos[i] = config.getInt(MQTT_QOS);
        }

        try {
            client = new MqttClient(config.getString(BROKER), config.getString(MQTT_CLIENT_ID), new MemoryPersistence());
            if (config.getInt(MQTT_QOS) > 0) {
                // Set manual ACK to prevent message loss due to queue buildup.
                client.setManualAcks(true);
            }
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        logger.info("Reconnected to MQTT Broker " + serverURI);

                        logger.info("Resubscribing to " + Arrays.toString(topics) + " with QoS " + config.getInt(MQTT_QOS));
                        try {
                            client.subscribe(topics, qos);
                            logger.info("Resubscribed to " + Arrays.toString(topics));

                        } catch (MqttException e) {
                            String message = "Resubscribe to " + Arrays.toString(topics) + " error.";
                            logger.error(message, e);

                            throw new ConnectException(message, e);
                        }
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
            options.setCleanSession(config.getBoolean(MQTT_CLEAN_SESSION));
            options.setKeepAliveInterval(config.getInt(MQTT_KEEPALIVE_INTERVAL));
            options.setConnectionTimeout(config.getInt(MQTT_CONNECTION_TIMEOUT));
            options.setAutomaticReconnect(config.getBoolean(MQTT_AUTO_RECONNECT));

            if (!config.getString(MQTT_USERNAME).isEmpty() && !config.getPassword(MQTT_PASSWORD).equals("")) {
                options.setUserName(config.getString(MQTT_USERNAME));
                options.setPassword(config.getPassword(MQTT_PASSWORD).value().toCharArray());
            }
            logger.info("MQTT Connection properties: " + options);

            logger.info("Connecting to MQTT Broker " + config.getString(BROKER));
            client.connect(options);
            logger.info("Connected to MQTT Broker");

            logger.info("Subscribing to " + Arrays.toString(topics) + " with QoS " + config.getInt(MQTT_QOS));
            try {
                client.subscribe(topics, qos);
                logger.info("Subscribed to " + Arrays.toString(topics));

            } catch (MqttException e) {
                String message = "Subscribe to " + Arrays.toString(topics) + " error.";
                logger.error(message, e);
                throw new ConnectException(message, e);
            }

        } catch (MqttException e) {
            logger.error("Connect to MQTT Broker error.", e);
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
        List<Event<SourceRecord>> events = buffer.poll();

        if (events == null || events.isEmpty()) {
            return null;
        }

        if (config.getInt(MQTT_QOS) > 0) {
            // ack
            for (Event<SourceRecord> event : events) {
                final int messageId = (int) event.getAttribute(ATTR_MESSAGE_ID);
                final int qos = (int) event.getAttribute(ATTR_MESSAGE_QOS);

                try {
                    client.messageArrivedComplete(messageId, qos);
                } catch (MqttException e) {
                    // Theoretically, such an error should not occur;
                    // even if it does, it would only result in duplicate message consumption.
                    // Therefore, if a retry still fails, it should be ignored.
                    logger.warn("Ack message[id: {}, qos: {}] error.", messageId, qos, e);

                    try {
                        client.messageArrivedComplete(messageId, qos);
                    } catch (MqttException ignored) {
                    }
                }
            }
        }
        List<SourceRecord> records = events.stream().map(Event::getRecord).collect(Collectors.toList());
        logger.trace("Records returning to poll(): " + records);

        return records;
    }

    public void stop() {
        try {
            logger.info("Disconnecting from MQTT Broker " + config.getString(BROKER));
            if (client != null) {
                client.disconnect();
            }
            logger.info("Disconnected from MQTT Broker and the task is stopped.");
        } catch (MqttException e) {
            logger.error("Exception thrown while disconnecting client.", e);
        }
    }

    public String version() {
        return Version.getVersion();
    }

    private void messageArrived0(String topic, MqttMessage message) throws Exception {
        logger.debug("Message arrived in connector from topic " + topic);
        Event<SourceRecord> event = convert(topic, message);
        logger.debug("Message converted: " + event);

        buffer.enqueue(event);
    }

    private Event<SourceRecord> convert(String topic, MqttMessage message) {
        logger.debug("Converting MQTT message: " + message);
        // Kafka 2.3
        ConnectHeaders headers = new ConnectHeaders();
        headers.addInt("mqtt.message.id", message.getId());
        headers.addInt("mqtt.message.qos", message.getQos());
        headers.addBoolean("mqtt.message.duplicate", message.isDuplicate());
        headers.addString("mqtt.topic", topic);

        byte[] payload = message.getPayload();
        long size = payload.length;

        SchemaAndValue convertedKey = keyConverter.toConnectData(this.config.getString(KAFKA_TOPIC), topic.getBytes());
        SchemaAndValue convertedValue = valueConverter.toConnectData(this.config.getString(KAFKA_TOPIC), payload);

        // Kafka 2.3
        @SuppressWarnings("unchecked")
        SourceRecord record = new SourceRecord(
                Collections.EMPTY_MAP,
                Collections.EMPTY_MAP,
                this.config.getString(KAFKA_TOPIC),
                null,
                convertedKey.schema(),
                convertedKey.value(),
                convertedValue.schema(),
                convertedValue.value(),
                System.currentTimeMillis(),
                headers
        );
        logger.debug("Converted MQTT Message: " + record);

        Event<SourceRecord> event = new Event<>(record, size);
        event.setAttribute(ATTR_MESSAGE_ID, message.getId());
        event.setAttribute(ATTR_MESSAGE_QOS, message.getQos());

        return event;
    }

}
