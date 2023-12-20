package charting.indicators;

import charting.timeline.OrderStatsTreeTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumTest {
    private OrderStatsTreeTimeline<Number> base;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
    }

    @Test
    void valueBeforeLengthWhenNotEager() {
        Sum sum = new Sum(base, 3, false);

        base.put(Instant.EPOCH, 1);

        assertEquals(Double.NaN, sum.get(Instant.EPOCH).value());
    }

    @Test
    void valueBeforeLengthWhenEager() {
        Sum sum = new Sum(base, 3, true);

        base.put(Instant.EPOCH, 1);

        assertEquals(1, sum.get(Instant.EPOCH).value());
    }

    @Test
    void calculatesCorrectly() {
        Sum sum = new Sum(base, 3, false);

        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 1);
        base.put(Instant.ofEpochSecond(2), 2);
        base.put(Instant.ofEpochSecond(3), 3);

        assertEquals(3, sum.get(Instant.ofEpochSecond(2)).value());
        assertEquals(6, sum.get(Instant.ofEpochSecond(3)).value());
    }

    @Test
    void notifiesListener() {
        Sum sum = new Sum(base, 3, false);

        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 0);
        base.put(Instant.ofEpochSecond(2), 2);

        Map<Instant, Double> values = new HashMap<>();
        sum.addListener(values::put);

        base.put(Instant.ofEpochSecond(1), 1);

        assertEquals(3, values.get(Instant.ofEpochSecond(2)));
    }
}
