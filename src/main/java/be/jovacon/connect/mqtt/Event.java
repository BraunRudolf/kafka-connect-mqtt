package be.jovacon.connect.mqtt;

import org.apache.kafka.connect.connector.ConnectRecord;

/**
 * A data based on MQTT message, it encapsulates key, value, and header.
 *
 * @author zhuchunlai
 */
public class Event<E extends ConnectRecord<E>> {

    private final E record;
    private final long size;
    private final int messageId;
    private final int qos;

    public Event(E record, long size, int messageId, int qos) {
        this.record = record;
        this.size = size;
        this.messageId = messageId;
        this.qos = qos;
    }

    public E getRecord() {
        return record;
    }

    public long size() {
        return size;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getQos() {
        return qos;
    }

    @Override
    public String toString() {
        return "Event [record=" + record + "]";
    }
}
