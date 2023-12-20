package charting.indicators;

import charting.timeline.OrderStatsTreeTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BollingerBandsTest {
    private OrderStatsTreeTimeline<Number> base;
    private StandardDeviation standardDeviation;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
        standardDeviation = new StandardDeviation(base, 3);
    }

    @Test
    void valueBeforeLengthIsNan() {
        base.put(Instant.EPOCH, 1);

        assertEquals(Double.NaN, standardDeviation.get(Instant.EPOCH).value());
    }

    @Test
    void calculatesCorrectly() {
        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 1);
        base.put(Instant.ofEpochSecond(2), 4);
        base.put(Instant.ofEpochSecond(3), 5);

        assertTrue(0.8165 - standardDeviation.get(Instant.ofEpochSecond(2)).value() <= 0.01);
        assertTrue(1.247 - standardDeviation.get(Instant.ofEpochSecond(3)).value() <= 0.01);
    }

    @Test
    void notifiesListener() {
        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 0);
        base.put(Instant.ofEpochSecond(2), 2);

        Map<Instant, Double> values = new HashMap<>();
        standardDeviation.addListener(values::put);

        base.put(Instant.ofEpochSecond(1), 1);

        assertTrue(0.8165 - values.get(Instant.ofEpochSecond(2)) <= 0.01);
    }
}
