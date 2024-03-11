package charting.timeline;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimelineTest {
    @Test
    void floorIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().floorIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4);

        assertEquals(-1, timeline.floorIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(0, timeline.floorIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(0, timeline.floorIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(1, timeline.floorIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(1, timeline.floorIndexOf(Instant.ofEpochMilli(5)));
    }

    @Test
    void ceilingIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().ceilingIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4);

        assertEquals(0, timeline.ceilingIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(0, timeline.ceilingIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(1, timeline.ceilingIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(1, timeline.ceilingIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(-3, timeline.ceilingIndexOf(Instant.ofEpochMilli(5)));
    }

    @Test
    void lowerIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().lowerIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4);

        assertEquals(-1, timeline.lowerIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(-1, timeline.lowerIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(0, timeline.lowerIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(0, timeline.lowerIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(1, timeline.lowerIndexOf(Instant.ofEpochMilli(5)));
    }

    @Test
    void higherIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().higherIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4);

        assertEquals(0, timeline.higherIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(1, timeline.higherIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(1, timeline.higherIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(-3, timeline.higherIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(-3, timeline.higherIndexOf(Instant.ofEpochMilli(5)));
    }

    private OrderStatsTreeTimeline<Integer> createTimeline(int... ints) {
        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();

        for (int i : ints) {
            t.put(Instant.ofEpochMilli(i), i);
        }

        return t;
    }
}