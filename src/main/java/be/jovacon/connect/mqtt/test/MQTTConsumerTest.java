package be.jovacon.connect.mqtt.test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhuchunlai
 * @since
 */
public final class MQTTConsumerTest {
    private static final Logger logger = LoggerFactory.getLogger(MQTTConsumerTest.class);

    public static void main(String[] args) {
        final String broker = "tcp://127.0.0.1:1883";
        final String clientId = "my_consumer_3";
        final String[] topics = new String[]{"test_3"};
        final int qos = 1;
        final long cost = 1000L;
        final LinkedBlockingQueue<MqttMessage> queue = new LinkedBlockingQueue<>(10);

        try {
            IMqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
            client.setManualAcks(true);

            final Thread worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            MqttMessage message = queue.take();
                            logger.info("Process message: " + message.toString());
                            Thread.sleep(cost);

                            try {
                                client.messageArrivedComplete(message.getId(), message.getQos());
                            } catch (MqttException e) {
                                logger.error("Ack message error", e);
                                try {
                                    client.messageArrivedComplete(message.getId(), message.getQos());
                                } catch (MqttException ignored) {
                                }
                            }
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }, "my-worker");
            worker.setDaemon(true);
            worker.start();

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        logger.info("Reconnected to MQTT Broker " + serverURI);

                        logger.info("Resubscribing to " + Arrays.toString(topics) + " with QoS " + qos);
                        try {
                            client.subscribe(topics, new int[]{qos});
                            logger.info("Resubscribed to " + Arrays.toString(topics) + " with QoS " + qos);
                        } catch (Exception e) {
                            logger.error("Resubscribe to " + Arrays.toString(topics) + " with QoS " + qos + "error", e);
                        }
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    logger.warn("Connection is lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    logger.info("[Callback] Receive message: " + message.toString());
                    queue.put(message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // nothing to do.
                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
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

                logger.info("Subscribing to " + Arrays.toString(topics) + " with QoS " + qos);
                client.subscribe(topics, new int[]{qos});
                logger.info("Subscribed to " + Arrays.toString(topics) + " with QoS " + qos);

                CountDownLatch latch = new CountDownLatch(1);
                try {
                    latch.await();
                } catch (InterruptedException ignored) {
                }

            } catch (MqttException e) {
                logger.error("Connect to MQTT broker error.", e);
            }

        } catch (MqttException e) {
            logger.error("Initialize MQTT client error.", e);
        }

    }

}
