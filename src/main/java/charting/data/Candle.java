package charting.data;

import java.time.Duration;
import java.time.Instant;

public interface Candle {
    Number getOpen();

    Number getHigh();

    Number getLow();

    Number getClose();

    Number getVolume();

    Instant getStartInstant();

    Instant getEndInstant();

    Instant getCurrentInstant();

    default boolean isTerminated() {
        return getCurrentInstant().equals(getEndInstant());
    }

    default Duration getCurrentDuration() {
        return Duration.between(getStartInstant(), getCurrentInstant());
    }

    default Duration getTotalDuration() {
        return Duration.between(getStartInstant(), getEndInstant());
    }
}
