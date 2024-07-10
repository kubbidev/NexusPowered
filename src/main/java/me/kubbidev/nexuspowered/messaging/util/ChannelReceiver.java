package me.kubbidev.nexuspowered.messaging.util;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

/**
 * Receives a message from a channel, sent at a fixed interval.
 *
 * @param <T> the message type
 */
public final class ChannelReceiver<T> {

    private T value;

    private long timestamp = 0;
    private final long expiryMillis;

    public ChannelReceiver(long expiryDuration, @NotNull TimeUnit unit) {
        this.expiryMillis = unit.toMillis(expiryDuration);
    }

    public void set(T value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Gets the last known value.
     *
     * @return the last known value
     */
    public Optional<T> getLastKnownValue() {
        return Optional.ofNullable(this.value);
    }

    /**
     * Gets the value.
     *
     * <p>Returns empty if the expiry on the last known value has been exceeded.</p>
     *
     * @return the value
     */
    public Optional<T> getValue() {
        long now = System.currentTimeMillis();
        long diff = now - this.timestamp;
        if (diff > this.expiryMillis) {
            return Optional.empty();
        }
        return getLastKnownValue();
    }

    /**
     * Gets the timestamp when the last value was received.
     *
     * @return the last received timestamp
     */
    public OptionalLong getLastReceivedTimestamp() {
        return this.timestamp == 0 ? OptionalLong.empty() : OptionalLong.of(this.timestamp);
    }

    @Override
    public String toString() {
        return "ChannelReceiver{value=" + this.value + ", timestamp=" + this.timestamp + ", expiryMillis=" + this.expiryMillis + '}';
    }
}