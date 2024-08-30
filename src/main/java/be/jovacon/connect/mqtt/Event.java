package be.jovacon.connect.mqtt;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * A data based on MQTT message, it encapsulates key, value, and header.
 *
 * @author zhuchunlai
 */
public class Event<E> {

    private final E record;
    private final long size;
    private final ContextAttributes attributes;

    public Event(final E record, final long size) {
        this.record = record;
        this.size = size;
        this.attributes = new ContextAttributes();
    }

    public E getRecord() {
        return record;
    }

    public long size() {
        return size;
    }

    public void setAttribute(String name, Object value) {
        this.attributes.addAttribute(name, value);
    }

    public Object getAttribute(String name) {
        return this.attributes.getAttribute(name);
    }

    @Override
    public String toString() {
        return "Event [size=" + size + "; record=" + record + "; attributes=]" + attributes;
    }

    private static final class ContextAttributes {

        final Map<String, Object> attributes;

        ContextAttributes() {
            this(10);
        }

        ContextAttributes(int capacity) {
            this.attributes = new HashMap<>(capacity);
        }

        void addAttribute(String name, Object value) {
            if (Strings.isNullOrEmpty(name)) {
                throw new IllegalArgumentException("Name is null or empty.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Value is null.");
            }

            this.attributes.put(name, value);
        }

        Object getAttribute(String name) {
            if (Strings.isNullOrEmpty(name)) {
                throw new IllegalArgumentException("Name is null or empty.");
            }
            return this.attributes.get(name);
        }

        @Override
        public String toString() {
            return this.attributes.isEmpty() ? "" : this.attributes.toString();
        }
    }

}
