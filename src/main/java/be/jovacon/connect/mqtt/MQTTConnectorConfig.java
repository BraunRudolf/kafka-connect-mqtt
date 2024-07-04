package be.jovacon.connect.mqtt;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.types.Password;

import java.util.Map;

/**
 * Base config for MQTT connector
 *
 * @author zhuchunlai
 * @since 1.1.1
 */
public abstract class MQTTConnectorConfig extends AbstractConfig {
    protected static final String BROKER = "mqtt.broker";
    protected static final String BROKER_DOC = "Host and port of the MQTT broker, eg: tcp://192.168.1.1:1883";

    protected static final String MQTT_CLIENT_ID = "mqtt.clientID";
    protected static final String MQTT_CLIENT_ID_DOC = "clientID";

    protected static final String MQTT_AUTO_RECONNECT = "mqtt.automaticReconnect";
    protected static final String MQTT_AUTO_RECONNECT_DOC = "set Automatic reconnect, default true";
    protected static final boolean DEFAULT_MQTT_AUTO_RECONNECT = true;

    protected static final String MQTT_KEEPALIVE_INTERVAL = "mqtt.keepAliveInterval";
    protected static final String MQTT_KEEPALIVE_INTERVAL_DOC = "set the keepalive interval, default is 60 seconds";
    protected static final int DEFAULT_MQTT_KEEPALIVE_INTERVAL = 60;

    protected static final String MQTT_CLEAN_SESSION = "mqtt.cleanSession";
    protected static final String MQTT_CLEAN_SESSION_DOC = "Sets whether the client and server should remember state across restarts and reconnects, default is true";
    protected static final boolean DEFAULT_MQTT_CLEAN_SESSION = true;

    protected static final String MQTT_CONNECTION_TIMEOUT = "mqtt.connectionTimeout";
    protected static final String MQTT_CONNECTION_TIMEOUT_DOC = "Sets the connection timeout, default is 30";
    protected static final int DEFAULT_MQTT_CONNECTION_TIMEOUT = 30;

    protected static final String MQTT_QOS = "mqtt.qos";
    protected static final String MQTT_QOS_DOC = "Quality of service MQTT messaging, default is 1 (at least once)";
    protected static final int DEFAULT_MQTT_QOS = 1;

    protected static final String MQTT_USERNAME = "mqtt.userName";
    protected static final String MQTT_USERNAME_DOC = "Sets the username for the MQTT connection timeout, default is \"\"";
    protected static final String DEFAULT_MQTT_USERNAME = "";

    protected static final String MQTT_PASSWORD = "mqtt.password";
    protected static final String MQTT_PASSWORD_DOC = "Sets the password for the MQTT connection timeout, default is \"\"";
    protected static final String DEFAULT_MQTT_PASSWORD = "";

    protected MQTTConnectorConfig(ConfigDef definition, Map<?, ?> originals) {
        super(definition, originals);
    }

    public String getBroker() {
        return getString(BROKER);
    }

    public String getClientId() {
        return getString(MQTT_CLIENT_ID);
    }

    public String getClientId(int index) {
        return String.format("%s-%d", getClientId(), index);
    }

    public boolean isAutoReconnect() {
        return getBoolean(MQTT_AUTO_RECONNECT);
    }

    public int getKeepaliveInterval() {
        return getInt(MQTT_KEEPALIVE_INTERVAL);
    }

    public boolean isCleanSession() {
        return getBoolean(MQTT_CLEAN_SESSION);
    }

    public int getConnectionTimeout() {
        return getInt(MQTT_CONNECTION_TIMEOUT);
    }

    public int getQoS() {
        return getInt(MQTT_QOS);
    }

    public String getUserName() {
        return getString(MQTT_USERNAME);
    }

    public Password getPassword() {
        return getPassword(MQTT_PASSWORD);
    }

}
