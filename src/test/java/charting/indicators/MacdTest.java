package charting.indicators;

import charting.timeline.OrderStatsTreeTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MacdTest {
    private OrderStatsTreeTimeline<Number> base;
    private Macd macd;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
        macd = new Macd(base, 2, 3, 2);
    }

    @Test
    void valueBeforeLengthIsNan() {
        base.put(Instant.EPOCH, 1);

        assertEquals(new Macd.Values(Double.NaN, Double.NaN), macd.get(Instant.EPOCH).value());
    }
}
