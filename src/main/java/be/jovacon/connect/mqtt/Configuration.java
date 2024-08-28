package be.jovacon.connect.mqtt;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * Base config for MQTT connector
 *
 * @author zhuchunlai
 * @since 1.1.1
 */
public abstract class Configuration extends AbstractConfig {
    public static final String BROKER = "mqtt.broker";
    public static final String BROKER_DOC = "Host and port of the MQTT broker, eg: tcp://192.168.1.1:1883";

    public static final String MQTT_CLIENT_ID = "mqtt.clientID";
    public static final String MQTT_CLIENT_ID_DOC = "clientID";

    public static final String MQTT_AUTO_RECONNECT = "mqtt.automaticReconnect";
    public static final String MQTT_AUTO_RECONNECT_DOC = "set Automatic reconnect, default true";
    public static final boolean DEFAULT_MQTT_AUTO_RECONNECT = true;

    public static final String MQTT_KEEPALIVE_INTERVAL = "mqtt.keepAliveInterval";
    public static final String MQTT_KEEPALIVE_INTERVAL_DOC = "set the keepalive interval, default is 60 seconds";
    public static final int DEFAULT_MQTT_KEEPALIVE_INTERVAL = 60;

    public static final String MQTT_CLEAN_SESSION = "mqtt.cleanSession";
    public static final String MQTT_CLEAN_SESSION_DOC = "Sets whether the client and server should remember state across restarts and reconnects, default is true";
    public static final boolean DEFAULT_MQTT_CLEAN_SESSION = true;

    public static final String MQTT_CONNECTION_TIMEOUT = "mqtt.connectionTimeout";
    public static final String MQTT_CONNECTION_TIMEOUT_DOC = "Sets the connection timeout, default is 30";
    public static final int DEFAULT_MQTT_CONNECTION_TIMEOUT = 30;

    public static final String MQTT_QOS = "mqtt.qos";
    public static final String MQTT_QOS_DOC = "Quality of service MQTT messaging, default is 1 (at least once)";
    public static final int DEFAULT_MQTT_QOS = 1;

    public static final String MQTT_USERNAME = "mqtt.userName";
    public static final String MQTT_USERNAME_DOC = "Sets the username for the MQTT connection timeout, default is \"\"";
    public static final String DEFAULT_MQTT_USERNAME = "";

    public static final String MQTT_PASSWORD = "mqtt.password";
    public static final String MQTT_PASSWORD_DOC = "Sets the password for the MQTT connection timeout, default is \"\"";
    public static final String DEFAULT_MQTT_PASSWORD = "";

    public static final String KEY_CONVERTER = "key.converter";
    public static final String KEY_CONVERTER_DOC = "";
    public static final String DEFAULT_KEY_CONVERTER = "org.apache.kafka.connect.storage.StringConverter";

    public static final String VALUE_CONVERTER = "value.converter";
    public static final String VALUE_CONVERTER_DOC = "value.converter";
    public static final String DEFAULT_VALUE_CONVERTER = "org.apache.kafka.connect.converters.ByteArrayConverter";

    public static final String KEY_CONVERTER_SCHEMAS_ENABLED = "key.converter.schemas.enable";
    public static final String KEY_CONVERTER_SCHEMAS_ENABLED_DOC = "key.converter.schemas.enable";
    public static final boolean DEFAULT_KEY_CONVERTER_SCHEMAS_ENABLED = false;
    public static final String VALUE_CONVERTER_SCHEMAS_ENABLED = "value.converter.schemas.enable";
    public static final String VALUE_CONVERTER_SCHEMAS_ENABLED_DOC = "value.converter.schemas.enable";
    public static final boolean DEFAULT_VALUE_CONVERTER_SCHEMAS_ENABLED = false;

    public Configuration(ConfigDef definition, Map<?, ?> originals) {
        super(definition, originals);
    }

    public Map<String, ?> subset(String prefix, boolean removePrefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return this.originals();
        }

        return this.originalsWithPrefix(prefix.trim(), removePrefix);
    }

}
