package charting.data;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DoubleCandleTest {
    @Test
    void throwsIfHighIsNotHighest() {
        assertThrows(IllegalArgumentException.class,
                () -> new DoubleCandle(2, 1.5, 1, 1, 1, Instant.EPOCH, Instant.MAX));
        assertThrows(IllegalArgumentException.class,
                () -> new DoubleCandle(1, 1.5, 1, 2, 1, Instant.EPOCH, Instant.MAX));
        assertThrows(IllegalArgumentException.class,
                () -> new DoubleCandle(1, 1.5, 2, 1, 1, Instant.EPOCH, Instant.MAX));
    }

    @Test
    void throwsIfLowIsNotLowest() {
        assertThrows(IllegalArgumentException.class,
                () -> new DoubleCandle(1, 2, 1.5, 2, 1, Instant.EPOCH, Instant.MAX));
        assertThrows(IllegalArgumentException.class,
                () -> new DoubleCandle(2, 2, 1.5, 1, 1, Instant.EPOCH, Instant.MAX));
        assertThrows(IllegalArgumentException.class,
                () -> new DoubleCandle(2, 1, 1.5, 2, 1, Instant.EPOCH, Instant.MAX));
    }

    @Test
    void throwsIfVolumeIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new DoubleCandle(2, 3, 1, 2, -1, Instant.EPOCH, Instant.MAX));
        assertThrows(IllegalArgumentException.class, () -> new DoubleCandle(2, 3, 1, 2,
                Double.NEGATIVE_INFINITY, Instant.EPOCH, Instant.MAX));

        assertDoesNotThrow(() -> new DoubleCandle(2, 3, 1, 2, 0,
                Instant.EPOCH, Instant.MAX));
        assertDoesNotThrow(() -> new DoubleCandle(2, 3, 1, 2, 1,
                Instant.EPOCH, Instant.MAX));
        assertDoesNotThrow(() -> new DoubleCandle(2, 3, 1, 2, Double.POSITIVE_INFINITY,
                Instant.EPOCH, Instant.MAX));
        assertDoesNotThrow(() -> new DoubleCandle(2, 3, 1, 2, Double.NaN,
                Instant.EPOCH, Instant.MAX));
    }

    @Test
    void throwsIfEndIsNotBeforeStart() {
        assertThrows(IllegalArgumentException.class, () -> new DoubleCandle(2, 3, 1, 2,
                0, Instant.MAX, Instant.MIN));
        assertThrows(IllegalArgumentException.class, () -> new DoubleCandle(2, 3, 1, 2,
                0, Instant.EPOCH, Instant.EPOCH));

        assertDoesNotThrow(() -> new DoubleCandle(2, 3, 1, 2, 0,
                Instant.EPOCH, Instant.MAX));
    }

    @Test
    void throwsIfCurrentIsNotBetweenStartAndEnd() {
        assertThrows(IllegalArgumentException.class, () -> new DoubleCandle(2, 3, 1, 2,
                0, Instant.EPOCH, Instant.MIN, Instant.MAX));
        assertThrows(IllegalArgumentException.class, () -> new DoubleCandle(2, 3, 1, 2,
                0, Instant.MIN, Instant.MAX, Instant.EPOCH));

        assertDoesNotThrow(() -> new DoubleCandle(2, 3, 1, 2, 0,
                Instant.EPOCH, Instant.EPOCH, Instant.MAX));
        assertDoesNotThrow(() -> new DoubleCandle(2, 3, 1, 2, 0,
                Instant.EPOCH, Instant.MAX, Instant.MAX));
    }
}