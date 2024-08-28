# kafka-connect-mqtt

This repo contains a MQTT Source and Sink Connector for Apache Kafka. It is tested with Kafka 2+.

Using the Source connector you can subscribe to a MQTT topic and write these messages to a Kafka topic.

The Sink connector works the other way around.

Note: 
* SSL connections are not supported at the moment
* The connector works only with a single task. Settings maxTasks > 1 has no effect.

## Building the connector

To build the connector, you must have the following installed:

* Java 8 or later
* Maven
* GIT

Clone the repository with the following command:
```
git clone https://gitlab.pyou.com/opsPlatform/kafka-connect-mqtt.git
```
Change directory into the `kafka-connect-mqtt` directory
```
cd kafka-connect-mqtt
```
Build the connector using Maven
```
mvn clean install
```
## Installing the connector

### Prerequisites

You must have Kafka 2+ installed


### Installing

* Copy the folder `/target/kafka-connect-mqtt-1.0-0-package/kafka-connect-mqtt` to your Kafka Connect plugin path
* Restart Kafka Connect
* Check if the connector has been loaded successfully

```
http://<kafkaconnect>:8083/connector-plugins
```
If you see these entries, the connector has been installed successfully

```
{
    "class": "MQTTSinkConnector",
    "type": "sink",
    "version": "1.0.0"
},
{
    "class": "MQTTSourceConnector",
    "type": "source",
    "version": "1.0.0"
},
```

## Configuring the Source connector

The MQTT Source connector subscribes to a Topic on a MQTT Broker and sends the messages to a Kafka topic.

Here is a basic configuration example:
```
name=mqtt-source
connector.class=be.jovacon.connect.mqtt.MQTTSourceConnector
mqtt.broker=tcp://emqx:1883
mqtt.clientID=my_client_id
mqtt.topics=my_mqtt_topic
kafka.topic=my-kafka-topic
records.buffer.max.capacity=100
records.buffer.max.bytes=1024
records.buffer.max.batch.size=10
records.buffer.empty.timeout=2000
key.converter=org.apache.kafka.connect.storage.StringConverter
value.converter=org.apache.kafka.connect.converters.ByteArrayConverter
```
### Optional Configuration options
* `mqtt.qos` (optional)(default: 1): 0 – At most Once, 1 – At Least Once, 2 – Exactly Once
* `mqtt.automaticReconnect` (optional)(default: true): Should the client automatically reconnect in case of connection failures
* `mqtt.keepAliveInterval` (optional)(default: 60 seconds)
* `mqtt.cleanSession` (optional)(default: true): Controls the state after disconnecting the client from the broker.
* `mqtt.connectionTimeout` (optional)(default: 30 seconds)
* `mqtt.username` (optional)(default: ""): Username to connect to MQTT broker
* `mqtt.password` (optional)(default: ""): Password to connect to MQTT broker
* `key.converter` (optional)(default: "org.apache.kafka.connect.storage.StringConverter")
* `key.converter.schemas.enable` (optional)(default: false)
* `value.converter` (optional)(default: "org.apache.kafka.connect.converters.ByteArrayConverter")
* `value.converter.schemas.enable` (optional)(default: false)
* `records.buffer.max.capacity` (optional)(default: 50000): The max capacity of the buffer that stores the data before sending it to the Kafka Topic
* `records.buffer.max.bytes` (optional)(default: 100 * 1024 * 1024): The maximum byte size of the buffer used to store data before sending it to the Kafka topic.
* `records.buffer.max.batch.size` (optional)(default: 4096): The maximum size of the batch to send to Kafka topic in a single request.
* `records.buffer.empty.timeout` (optional)(default: 100): Timeout(ms) to wait on an empty buffer for extracting records.
* `records.buffer.full.timeout` (optional)(default: 60000): Timeout(ms) to wait on a full buffer for inserting new records.

## Configuring the Sink connector(Not optimized, not recommended for production use)

The MQTT Sink Connector reads messages from a Kafka topic and publishes them to a MQTT topic.

Here is a basic configuration example:
```
curl -X POST \
  http://<kafkaconnect>>:8083/connectors \
  -H 'Content-Type: application/json' \
  -d '{ "name": "mqtt-sink-connector",
    "config":
    {
      "connector.class":"be.jovacon.connect.mqtt.MQTTSinkConnector",
      "mqtt.topic":"my_mqtt_topic",
      "topics":"my_kafka_topic",
      "mqtt.clientID":"my_client_id",
      "mqtt.broker":"tcp://127.0.0.1:1883",
      "key.converter":"org.apache.kafka.connect.storage.StringConverter",
      "key.converter.schemas.enable":false,
      "value.converter":"org.apache.kafka.connect.storage.StringConverter",
      "value.converter.schemas.enable":false
    }
}'
```

### Optional Configuration options
* `mqtt.qos` (optional): 0 – At most Once, 1 – At Least Once, 2 – Exactly Once
* `mqtt.automaticReconnect` (optional)(default: true): Should the client automatically reconnect in case of connection failures
* `mqtt.keepAliveInterval` (optional)(default: 60 seconds)
* `mqtt.cleanSession` (optional)(default: true): Controls the state after disconnecting the client from the broker.
* `mqtt.connectionTimeout` (optional)(default: 30 seconds)
* `mqtt.username` (optional): Username to connect to MQTT broker
* `mqtt.password` (optional): Password to connect to MQTT broker


## Optimization Points(By wl-times.com)

### Source Connector
* Optimize the cache queue by adding a backpressure mechanism and more detailed parameter configuration.
* Support for Kafka Converter mechanism enhancement
* Optimized the project structure


## TODO
* Observability, metrics related to performance and stability, integrated with Prometheus.
* Optimization of the Sink Connector for use in production environments.


## Remark
* It's forked from https://github.com/johanvandevenne/kafka-connect-mqtt.git
* Origin Author: Johan Vandevenne 
