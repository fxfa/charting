package charting.timeline;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimelineTest {
    @Test
    void floorIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().floorIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4, 5, 7);

        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(1)), timeline.floorIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(2)), timeline.floorIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(3)), timeline.floorIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(4)), timeline.floorIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(5)), timeline.floorIndexOf(Instant.ofEpochMilli(5)));
        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(6)), timeline.floorIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(7)), timeline.floorIndexOf(Instant.ofEpochMilli(7)));
        assertEquals(floorIndexOf(timeline, Instant.ofEpochMilli(8)), timeline.floorIndexOf(Instant.ofEpochMilli(8)));
    }

    @Test
    void ceilingIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().ceilingIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4, 5, 7);

        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(1)), timeline.ceilingIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(2)), timeline.ceilingIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(3)), timeline.ceilingIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(4)), timeline.ceilingIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(5)), timeline.ceilingIndexOf(Instant.ofEpochMilli(5)));
        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(6)), timeline.ceilingIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(7)), timeline.ceilingIndexOf(Instant.ofEpochMilli(7)));
        assertEquals(ceilingIndexOf(timeline, Instant.ofEpochMilli(8)), timeline.ceilingIndexOf(Instant.ofEpochMilli(8)));
    }

    @Test
    void lowerIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().lowerIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4, 5, 7);

        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(1)), timeline.lowerIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(2)), timeline.lowerIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(3)), timeline.lowerIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(4)), timeline.lowerIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(5)), timeline.lowerIndexOf(Instant.ofEpochMilli(5)));
        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(6)), timeline.lowerIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(7)), timeline.lowerIndexOf(Instant.ofEpochMilli(7)));
        assertEquals(lowerIndexOf(timeline, Instant.ofEpochMilli(8)), timeline.lowerIndexOf(Instant.ofEpochMilli(8)));
    }

    @Test
    void higherIndexOf() {
        assertEquals(-1, new OrderStatsTreeTimeline<>().higherIndexOf(Instant.EPOCH));

        OrderStatsTreeTimeline<Integer> timeline = createTimeline(2, 4, 5, 7);

        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(1)), timeline.higherIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(2)), timeline.higherIndexOf(Instant.ofEpochMilli(2)));
        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(3)), timeline.higherIndexOf(Instant.ofEpochMilli(3)));
        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(4)), timeline.higherIndexOf(Instant.ofEpochMilli(4)));
        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(5)), timeline.higherIndexOf(Instant.ofEpochMilli(5)));
        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(6)), timeline.higherIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(7)), timeline.higherIndexOf(Instant.ofEpochMilli(7)));
        assertEquals(higherIndexOf(timeline, Instant.ofEpochMilli(8)), timeline.higherIndexOf(Instant.ofEpochMilli(8)));
    }

    private int floorIndexOf(Timeline<Integer> timeline, Instant i) {
        Timestamped<Integer> t = timeline.floor(i);
        return t == null ? -1 : timeline.indexOf(t.timestamp());
    }

    private int ceilingIndexOf(Timeline<Integer> timeline, Instant i) {
        Timestamped<Integer> t = timeline.ceiling(i);
        return t == null ? -1 : timeline.indexOf(t.timestamp());
    }

    private int lowerIndexOf(Timeline<Integer> timeline, Instant i) {
        Timestamped<Integer> t = timeline.lower(i);
        return t == null ? -1 : timeline.indexOf(t.timestamp());
    }

    private int higherIndexOf(Timeline<Integer> timeline, Instant i) {
        Timestamped<Integer> t = timeline.higher(i);
        return t == null ? -1 : timeline.indexOf(t.timestamp());
    }

    private OrderStatsTreeTimeline<Integer> createTimeline(int... ints) {
        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();

        for (int i : ints) {
            t.put(Instant.ofEpochMilli(i), i);
        }

        return t;
    }
}