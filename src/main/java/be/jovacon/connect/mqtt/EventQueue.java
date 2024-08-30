package be.jovacon.connect.mqtt;

import be.jovacon.connect.mqtt.annotation.ThreadSafe;
import be.jovacon.connect.mqtt.util.Clock;
import be.jovacon.connect.mqtt.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A queue which serves as handover point between producer threads and the Kafka Connect polling loop.
 * <p>
 * The queue is configurable in different aspects, e.g. its maximum size and the
 * time to sleep (block) between two subsequent poll calls. See the
 * {@link Builder} for the different options. The queue applies back-pressure
 * semantics, i.e. if it holds the maximum number of elements, subsequent calls
 * to {@link #enqueue(Event)} will block until elements have been removed from
 * the queue.
 * <p>
 * If an exception occurs on the producer side, the producer should make that
 * exception known by calling {@link #producerException(RuntimeException)} before stopping its
 * operation. Upon the next call to {@link #poll()}, that exception will be
 * raised, causing Kafka Connect to stop the connector and mark it as
 * {@code FAILED}.
 *
 */
@ThreadSafe
public class EventQueue<E> implements EventQueueMetrics {

    private static final Logger logger = LoggerFactory.getLogger(EventQueue.class);

    private final Duration pollInterval;
    private final int maxBatchSize;
    private final int maxQueueSize;
    private final long maxQueueSizeInBytes;

    private final Lock lock;
    private final Condition isFull;
    private final Condition isNotFull;

    private final Queue<Event<E>> queue;
    private final Queue<Long> sizeInBytesQueue;
    private long currentQueueSizeInBytes = 0;

    private volatile RuntimeException producerException;

    private EventQueue(Duration pollInterval, int maxQueueSize, int maxBatchSize, long maxQueueSizeInBytes) {
        this.pollInterval = pollInterval;
        this.maxBatchSize = maxBatchSize;
        this.maxQueueSize = maxQueueSize;

        this.lock = new ReentrantLock();
        this.isFull = lock.newCondition();
        this.isNotFull = lock.newCondition();

        this.queue = new ArrayDeque<>(maxQueueSize);
        this.sizeInBytesQueue = new ArrayDeque<>(maxQueueSize);
        this.maxQueueSizeInBytes = maxQueueSizeInBytes;
    }

    public static class Builder<E> {

        private Duration pollInterval;
        private int maxQueueSize;
        private int maxBatchSize;
        private long maxQueueSizeInBytes;

        public Builder<E> pollInterval(Duration pollInterval) {
            this.pollInterval = pollInterval;
            return this;
        }

        public Builder<E> maxQueueSize(int maxQueueSize) {
            this.maxQueueSize = maxQueueSize;
            return this;
        }

        public Builder<E> maxBatchSize(int maxBatchSize) {
            this.maxBatchSize = maxBatchSize;
            return this;
        }

        public Builder<E> maxQueueSizeInBytes(long maxQueueSizeInBytes) {
            this.maxQueueSizeInBytes = maxQueueSizeInBytes;
            return this;
        }

        public EventQueue<E> build() {
            return new EventQueue<>(pollInterval, maxQueueSize, maxBatchSize, maxQueueSizeInBytes);
        }
    }

    /**
     * Enqueues a record so that it can be obtained via {@link #poll()}. This method
     * will block if the queue is full.
     *
     * @param record the record to be enqueued
     * @throws InterruptedException if this thread has been interrupted
     */
    public void enqueue(Event<E> record) throws InterruptedException {
        if (record == null) {
            return;
        }

        // The calling thread has been interrupted, let's abort
        if (Thread.interrupted()) {
            logger.debug("Thread is interrupted.");
            throw new InterruptedException();
        }

        doEnqueue(record);
    }

    protected void doEnqueue(Event<E> record) throws InterruptedException {
        if (logger.isTraceEnabled()) {
            logger.trace("Enqueuing source record '{}'", record);
        }

        try {
            this.lock.lock();

            while (queue.size() >= maxQueueSize || (maxQueueSizeInBytes > 0 && currentQueueSizeInBytes >= maxQueueSizeInBytes)) {
                // signal poll() to drain queue
                this.isFull.signalAll();

                logger.debug("The buffer is full, sleeping a bit...");
                // queue size or queue sizeInBytes threshold reached, so wait a bit
                this.isNotFull.await(pollInterval.toMillis(), TimeUnit.MILLISECONDS);
            }

            logger.debug("The buffer has space, preparing to add record: " + record);
            queue.add(record);
            logger.debug("Record has been added to buffer, record: " + record);

            // If we pass a positiveLong max.queue.size.in.bytes to enable handling queue size in bytes feature
            if (maxQueueSizeInBytes > 0) {
                long messageSize = record.size();
                sizeInBytesQueue.add(messageSize);
                currentQueueSizeInBytes += messageSize;
            }

            // batch size or queue sizeInBytes threshold reached
            if (queue.size() >= maxBatchSize || (maxQueueSizeInBytes > 0 && currentQueueSizeInBytes >= maxQueueSizeInBytes)) {
                // signal poll() to start draining queue and do not wait
                this.isFull.signalAll();
            }
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Returns the next batch of elements from this queue. May be empty in case no
     * elements have arrived in the maximum waiting time.
     *
     * @throws InterruptedException if this thread has been interrupted while waiting for more
     *                              elements to arrive
     */
    public List<Event<E>> poll() throws InterruptedException {
        logger.debug("polling records...");
        final Threads.Timer timeout = Threads.timer(Clock.SYSTEM, pollInterval);
        try {
            this.lock.lock();
            List<Event<E>> records = new ArrayList<>(Math.min(maxBatchSize, queue.size()));
            throwProducerExceptionIfPresent();
            while (drainRecords(records, maxBatchSize - records.size()) < maxBatchSize
                    && (maxQueueSizeInBytes == 0 || currentQueueSizeInBytes < maxQueueSizeInBytes)
                    && !timeout.expired()) {
                throwProducerExceptionIfPresent();

                logger.debug("no records available or batch size not reached yet, sleeping a bit...");
                long remainingTimeoutMills = timeout.remaining().toMillis();
                if (remainingTimeoutMills > 0) {
                    // signal doEnqueue() to add more records
                    this.isNotFull.signalAll();
                    // no records available or batch size not reached yet, so wait a bit
                    this.isFull.await(remainingTimeoutMills, TimeUnit.MILLISECONDS);
                }
                logger.debug("checking for more records...");
            }
            // signal doEnqueue() to add more records
            this.isNotFull.signalAll();

            logger.debug("Polled records: " + records);
            return records;
        } finally {
            this.lock.unlock();
        }
    }

    private long drainRecords(List<Event<E>> records, int maxElements) {
        int queueSize = queue.size();
        if (queueSize == 0) {
            return records.size();
        }
        int recordsToDrain = Math.min(queueSize, maxElements);
        List<Event<E>> drainedRecords = new ArrayList<>(recordsToDrain);
        for (int i = 0; i < recordsToDrain; i++) {
            Event<E> record = queue.poll();
            drainedRecords.add(record);
        }
        if (maxQueueSizeInBytes > 0) {
            for (int i = 0; i < recordsToDrain; i++) {
                Long objectSize = sizeInBytesQueue.poll();
                currentQueueSizeInBytes -= (objectSize == null ? 0L : objectSize);
            }
        }
        records.addAll(drainedRecords);
        return records.size();
    }

    public void producerException(final RuntimeException producerException) {
        this.producerException = producerException;
    }

    private void throwProducerExceptionIfPresent() {
        if (producerException != null) {
            throw producerException;
        }
    }

    @Override
    public int totalCapacity() {
        return maxQueueSize;
    }

    @Override
    public int remainingCapacity() {
        return maxQueueSize - queue.size();
    }

    @Override
    public long maxQueueSizeInBytes() {
        return maxQueueSizeInBytes;
    }

    @Override
    public long currentQueueSizeInBytes() {
        return currentQueueSizeInBytes;
    }

}
