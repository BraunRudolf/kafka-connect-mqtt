package be.jovacon.connect.mqtt;

public interface EventQueueMetrics {

    int totalCapacity();

    int remainingCapacity();

    long maxQueueSizeInBytes();

    long currentQueueSizeInBytes();
}
