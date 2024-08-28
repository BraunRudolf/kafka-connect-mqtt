package be.jovacon.connect.mqtt;

import be.jovacon.connect.mqtt.util.Instantiator;
import org.apache.kafka.connect.storage.Converter;

import java.util.Map;

/**
 * A builder which creates converter functions for requested format.
 */
public class ConverterBuilder {

    private static final String KEY_CONVERTER_PREFIX = "key.converter";
    private static final String VALUE_CONVERTER_PREFIX = "value.converter";
    private final Configuration config;

    public ConverterBuilder(Configuration config) {
        this.config = config;
    }

    public Converter build(boolean isKey) {
        String clazz = config.getString(isKey ? KEY_CONVERTER_PREFIX : VALUE_CONVERTER_PREFIX);
        Converter converter = Instantiator.getInstance(clazz);

        Map<String, ?> converterConfig = config.subset(isKey ? KEY_CONVERTER_PREFIX : VALUE_CONVERTER_PREFIX, true);
        converter.configure(converterConfig, isKey);

        return converter;
    }

}
