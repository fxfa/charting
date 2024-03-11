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
    private BollingerBands bollingerBands;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
        bollingerBands = new BollingerBands(base, 3, 2);
    }

    @Test
    void valueBeforeLengthIsNan() {
        base.put(Instant.EPOCH, 1);

        assertEquals(new BollingerBands.Values(Double.NaN, Double.NaN, Double.NaN),
                bollingerBands.get(Instant.EPOCH).value());
    }

    @Test
    void calculatesCorrectly() {
        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 1);
        base.put(Instant.ofEpochSecond(2), 4);
        base.put(Instant.ofEpochSecond(3), 6);

        double ma1 = 5 / 3d;
        double ma2 = 11 / 3d;
        double sd1 = 1.7;
        double sd2 = 2.055;
        BollingerBands.Values v1 = bollingerBands.get(Instant.ofEpochSecond(2)).value();
        BollingerBands.Values v2 = bollingerBands.get(Instant.ofEpochSecond(3)).value();

        assertTrue(Math.abs(ma1 - v1.ma()) < 0.01);
        assertTrue(Math.abs(ma1 + sd1 * 2 - v1.upper()) < 0.01);
        assertTrue(Math.abs(ma1 - sd1 * 2 - v1.lower()) < 0.01);
        assertTrue(Math.abs(ma2 - v2.ma()) < 0.01);
        assertTrue(Math.abs(ma2 + sd2 * 2 - v2.upper()) < 0.01);
        assertTrue(Math.abs(ma2 - sd2 * 2 - v2.lower()) < 0.01);
    }

    @Test
    void notifiesListener() {
        base.put(Instant.EPOCH, 0);
        base.put(Instant.ofEpochSecond(1), 0);
        base.put(Instant.ofEpochSecond(2), 2);

        Map<Instant, BollingerBands.Values> values = new HashMap<>();
        bollingerBands.addListener(values::put);

        base.put(Instant.ofEpochSecond(1), 1);

        double ma = 1;
        double sd = 0.816;

        BollingerBands.Values v = values.get(Instant.ofEpochSecond(2));
        assertTrue(Math.abs(ma - v.ma()) < 0.01);
        assertTrue(Math.abs(ma + sd * 2 - v.upper()) < 0.01);
        assertTrue(Math.abs(ma - sd * 2 - v.lower()) < 0.01);
    }
}
