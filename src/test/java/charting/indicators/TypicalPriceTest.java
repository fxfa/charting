package charting.indicators;

import charting.data.Candle;
import charting.data.DoubleCandle;
import charting.timeline.OrderStatsTreeTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypicalPriceTest {
    private OrderStatsTreeTimeline<Candle> base;
    private TypicalPrice typicalPrice;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
        typicalPrice = new TypicalPrice(base);
    }

    @Test
    void calculatesCorrectly() {
        base.put(Instant.EPOCH, new DoubleCandle(2, 3, 1, 2, 100,
                Instant.EPOCH, Instant.ofEpochSecond(1)));

        assertEquals(2, typicalPrice.get(Instant.EPOCH).value());
    }

    @Test
    void notifiesListener() {
        base.put(Instant.EPOCH, new DoubleCandle(2, 3, 1, 2, 100,
                Instant.EPOCH, Instant.ofEpochSecond(1)));

        Map<Instant, Double> values = new HashMap<>();
        typicalPrice.addListener(values::put);

        base.put(Instant.EPOCH, new DoubleCandle(2, 6, 1, 2, 100,
                Instant.EPOCH, Instant.ofEpochSecond(1)));

        assertEquals(3, values.get(Instant.EPOCH));
    }
}
