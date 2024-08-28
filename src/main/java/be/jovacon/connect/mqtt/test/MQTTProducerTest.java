package be.jovacon.connect.mqtt.test;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author zhuchunlai
 * @since
 */
public final class MQTTProducerTest {
    private static final Logger logger = LoggerFactory.getLogger(MQTTProducerTest.class);

    public static void main(String[] args) {
        final String broker = "tcp://127.0.0.1:1883";
        final String clientId = "my_producer_3";
        final String[] topics = new String[]{"my_mqtt_topic"};
        final int qos = 1;

        try {
            IMqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setKeepAliveInterval(60);
            options.setConnectionTimeout(30);
            options.setAutomaticReconnect(true);
            logger.info("MQTT Connection properties: " + options);

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.close();
                    } catch (MqttException e) {
                        logger.error("Close connection error.", e);
                    }
                }
            }));

            logger.info("Connecting to MQTT Broker " + broker);
            try {
                client.connect(options);
                logger.info("Connected to MQTT Broker");

                for (int i = 0; ; i++) {
                    if (i > 0) {
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException ignored) {
                        }
                    }

                    if (!client.isConnected()) {
                        logger.warn("Connecting to MQTT broker");
                        continue;
                    }
                    String message = String.valueOf(i);
                    for (String topic : topics) {
                        client.publish(topic, (topic + "_" + message).getBytes(StandardCharsets.UTF_8), qos, false);
                        logger.info("Published " + message);
                    }
                }

            } catch (MqttException e) {
                logger.error("Connect to MQTT broker error.", e);
            }

        } catch (MqttException e) {
            logger.error("Initialize MQTT client error.", e);
        }
    }
}
