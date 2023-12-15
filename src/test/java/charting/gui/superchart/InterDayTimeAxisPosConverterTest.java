package charting.gui.superchart;

import charting.data.Candle;
import charting.data.DoubleCandle;
import charting.timeline.OrderStatsTreeTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class InterDayTimeAxisPosConverterTest {
    private OrderStatsTreeTimeline<Candle> t;

    @BeforeEach
    void beforeEach() {
        Instant i0 = Instant.EPOCH;
        Instant i1 = Instant.EPOCH.plus(1, ChronoUnit.DAYS);
        Instant i2 = Instant.EPOCH.plus(2, ChronoUnit.DAYS);
        Instant i3 = Instant.EPOCH.plus(3, ChronoUnit.DAYS);

        t = new OrderStatsTreeTimeline<>();
        t.put(i0, new DoubleCandle(2, 3, 1, 2, 100, i0, i1));
        t.put(i1, new DoubleCandle(2, 3, 1, 2, 100, i1, i2));
        t.put(i2, new DoubleCandle(2, 3, 1, 2, 100, i2, i3));
    }

    @Test
    void toIndexWithEmptyTimeline() {
        t = new OrderStatsTreeTimeline<>();

        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(0, converter.toIndex(Instant.EPOCH));
        assertEquals(1, converter.toIndex(Instant.EPOCH.plus(1, ChronoUnit.DAYS)));
        assertEquals(-1, converter.toIndex(Instant.EPOCH.plus(-1, ChronoUnit.DAYS)));
    }

    @Test
    void toInstantWithEmptyTimeline() {
        t = new OrderStatsTreeTimeline<>();

        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(Instant.EPOCH, converter.toInstant(0));
        assertEquals(Instant.EPOCH.plus(1, ChronoUnit.DAYS), converter.toInstant(1));
        assertEquals(Instant.EPOCH.plus(-1, ChronoUnit.DAYS), converter.toInstant(-1));
    }

    @Test
    void toInstantWithNegativeIndex() {
        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(Instant.EPOCH.plus(-1, ChronoUnit.DAYS), converter.toInstant(-1));
    }

    @Test
    void toIndexWithInstantBeforeTimeline() {
        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(-1, converter.toIndex(Instant.EPOCH.plus(-1, ChronoUnit.DAYS)));
    }

    @Test
    void toInstantWithIndexOutOfBounds() {
        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(Instant.EPOCH.plus(5, ChronoUnit.DAYS), converter.toInstant(5));
    }

    @Test
    void toIndexWithInstantAfterTimeline() {
        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(5, converter.toIndex(Instant.EPOCH.plus(5, ChronoUnit.DAYS)));
    }

    @Test
    void toInstantWithIndexInsideBounds() {
        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(Instant.EPOCH, converter.toInstant(0));
    }

    @Test
    void toIndexWithInstantInsideTimeline() {
        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(0, converter.toIndex(Instant.EPOCH));
    }

    @Test
    void toIndexRoundsInstantDown() {
        InterDayTimeAxisPosConverter converter = new InterDayTimeAxisPosConverter(t);

        assertEquals(0, converter.toIndex(Instant.ofEpochSecond(1)));
        assertEquals(-5, converter.toIndex(Instant.EPOCH.plus(-5, ChronoUnit.DAYS)
                .plus(-1, ChronoUnit.SECONDS)));
        assertEquals(5, converter.toIndex(Instant.EPOCH.plus(5, ChronoUnit.DAYS)
                .plus(1, ChronoUnit.SECONDS)));
    }
}
