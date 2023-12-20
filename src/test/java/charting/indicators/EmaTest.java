package charting.indicators;

import charting.timeline.OrderStatsTreeTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmaTest {
    private OrderStatsTreeTimeline<Number> base;
    private Ema ema;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
        ema = new Ema(base, 3);
    }

    @Test
    void valueBeforeLengthIsNan() {
        base.put(Instant.EPOCH, 1);

        assertEquals(Double.NaN, ema.get(Instant.EPOCH).value());
    }

    @Test
    void calculatesCorrectly() {
        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 1);
        base.put(Instant.ofEpochSecond(2), 2);
        base.put(Instant.ofEpochSecond(3), 3);
        base.put(Instant.ofEpochSecond(4), 5);

        assertEquals(1, ema.get(Instant.ofEpochSecond(2)).value());
        assertEquals(2, ema.get(Instant.ofEpochSecond(3)).value());
        double s = 2.0 / (1 + ema.getEmaLength());
        assertEquals(5 * s + 2 * (1 - s), ema.get(Instant.ofEpochSecond(4)).value());
    }

    @Test
    void notifiesListener() {
        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 0);
        base.put(Instant.ofEpochSecond(2), 2);

        Map<Instant, Double> values = new HashMap<>();
        ema.addListener(values::put);

        base.put(Instant.ofEpochSecond(1), 1);

        assertEquals(1, values.get(Instant.ofEpochSecond(2)));
    }
}