package charting.indicators;

import charting.timeline.OrderStatsTreeTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmaTest {
    private OrderStatsTreeTimeline<Number> base;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
    }

    @Test
    void valueBeforeLengthWhenNotEager() {
        Sma sma = new Sma(base, 3, false);

        base.put(Instant.EPOCH, 1);

        assertEquals(Double.NaN, sma.get(Instant.EPOCH).value());
    }

    @Test
    void valueBeforeLengthWhenEager() {
        Sma sma = new Sma(base, 3, true);

        base.put(Instant.EPOCH, 1);

        assertEquals(1, sma.get(Instant.EPOCH).value());
    }

    @Test
    void calculatesCorrectly() {
        Sma sma = new Sma(base, 3, false);

        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 1);
        base.put(Instant.ofEpochSecond(2), 2);
        base.put(Instant.ofEpochSecond(3), 3);

        assertEquals(1, sma.get(Instant.ofEpochSecond(2)).value());
        assertEquals(2, sma.get(Instant.ofEpochSecond(3)).value());
    }

    @Test
    void notifiesListener() {
        Sma sma = new Sma(base, 3, false);

        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 0);
        base.put(Instant.ofEpochSecond(2), 2);

        Map<Instant, Double> values = new HashMap<>();
        sma.addListener(values::put);

        base.put(Instant.ofEpochSecond(1), 1);

        assertEquals(1, values.get(Instant.ofEpochSecond(2)));
    }
}