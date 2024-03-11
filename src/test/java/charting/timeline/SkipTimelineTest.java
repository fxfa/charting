package charting.timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SkipTimelineTest {
    private OrderStatsTreeTimeline<Integer> base;

    @BeforeEach
    void beforeEach() {
        base = new OrderStatsTreeTimeline<>();
        base.put(Instant.ofEpochMilli(0), 0);
        base.put(Instant.ofEpochMilli(2), 2);
        base.put(Instant.ofEpochMilli(4), 4);
        base.put(Instant.ofEpochMilli(6), 6);
        base.put(Instant.ofEpochMilli(8), 8);
    }

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new SkipTimeline<>(base, -1));
        assertDoesNotThrow(() -> new SkipTimeline<>(base, 0));
    }

    @Test
    void first() {
        assertNull(new SkipTimeline<>(base, 5).first());
        assertEquals(8, new SkipTimeline<>(base, 4).first().value());
    }

    @Test
    void last() {
        assertNull(new SkipTimeline<>(base, 5).last());
        assertEquals(8, new SkipTimeline<>(base, 4).last().value());
    }

    @Test
    void get() {
        assertNull(new SkipTimeline<>(base, 1).get(Instant.ofEpochMilli(0)));
        assertEquals(2, new SkipTimeline<>(base, 1).get(Instant.ofEpochMilli(2)).value());
    }

    @Test
    void floor() {
        assertNull(new SkipTimeline<>(base, 3).floor(Instant.ofEpochMilli(0)));
        assertNull(new SkipTimeline<>(base, 3).floor(Instant.ofEpochMilli(1)));
        assertEquals(6, new SkipTimeline<>(base, 3).floor(Instant.ofEpochMilli(6)).value());
        assertEquals(6, new SkipTimeline<>(base, 3).floor(Instant.ofEpochMilli(7)).value());

    }

    @Test
    void ceiling() {
        assertEquals(6, new SkipTimeline<>(base, 3).ceiling(Instant.ofEpochMilli(1)).value());
        assertEquals(6, new SkipTimeline<>(base, 3).ceiling(Instant.ofEpochMilli(6)).value());
        assertEquals(8, new SkipTimeline<>(base, 3).ceiling(Instant.ofEpochMilli(7)).value());
        assertNull(new SkipTimeline<>(base, 3).ceiling(Instant.ofEpochMilli(9)));
    }

    @Test
    void lower() {
        assertNull(new SkipTimeline<>(base, 3).lower(Instant.ofEpochMilli(1)));
        assertNull(new SkipTimeline<>(base, 3).lower(Instant.ofEpochMilli(6)));
        assertEquals(6, new SkipTimeline<>(base, 3).lower(Instant.ofEpochMilli(7)).value());
        assertEquals(6, new SkipTimeline<>(base, 3).lower(Instant.ofEpochMilli(8)).value());
    }

    @Test
    void higher() {
        assertEquals(6, new SkipTimeline<>(base, 3).higher(Instant.ofEpochMilli(1)).value());
        assertEquals(8, new SkipTimeline<>(base, 3).higher(Instant.ofEpochMilli(6)).value());
        assertEquals(8, new SkipTimeline<>(base, 3).higher(Instant.ofEpochMilli(7)).value());
        assertNull(new SkipTimeline<>(base, 3).higher(Instant.ofEpochMilli(9)));
    }

    @Test
    void getWithIndex() {
        assertNull(new SkipTimeline<>(base, 5).get(0));
        assertEquals(2, new SkipTimeline<>(base, 1).get(0).value());
        assertEquals(8, new SkipTimeline<>(base, 4).get(0).value());
        assertThrows(IllegalArgumentException.class, () -> new SkipTimeline<>(base, 0).get(-1));
    }

    @Test
    void indexOf() {
        assertEquals(-1, new SkipTimeline<>(base, 3).indexOf(Instant.ofEpochMilli(0)));
        assertEquals(-1, new SkipTimeline<>(base, 3).indexOf(Instant.ofEpochMilli(3)));
        assertEquals(0, new SkipTimeline<>(base, 3).indexOf(Instant.ofEpochMilli(6)));
        assertEquals(-2, new SkipTimeline<>(base, 3).indexOf(Instant.ofEpochMilli(7)));
    }

    @Test
    void floorIndexOf() {
        assertEquals(-1, new SkipTimeline<>(base, 3).floorIndexOf(Instant.ofEpochMilli(0)));
        assertEquals(-1, new SkipTimeline<>(base, 3).floorIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(0, new SkipTimeline<>(base, 3).floorIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(0, new SkipTimeline<>(base, 3).floorIndexOf(Instant.ofEpochMilli(7)));
    }

    @Test
    void ceilingIndexOf() {
        assertEquals(0, new SkipTimeline<>(base, 3).ceilingIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(0, new SkipTimeline<>(base, 3).ceilingIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(1, new SkipTimeline<>(base, 3).ceilingIndexOf(Instant.ofEpochMilli(7)));
        assertEquals(-3, new SkipTimeline<>(base, 3).ceilingIndexOf(Instant.ofEpochMilli(9)));
    }

    @Test
    void lowerIndexOf() {
        assertEquals(-1, new SkipTimeline<>(base, 3).lowerIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(-1, new SkipTimeline<>(base, 3).lowerIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(0, new SkipTimeline<>(base, 3).lowerIndexOf(Instant.ofEpochMilli(7)));
        assertEquals(0, new SkipTimeline<>(base, 3).lowerIndexOf(Instant.ofEpochMilli(8)));
    }

    @Test
    void higherIndexOf() {
        assertEquals(0, new SkipTimeline<>(base, 3).higherIndexOf(Instant.ofEpochMilli(1)));
        assertEquals(1, new SkipTimeline<>(base, 3).higherIndexOf(Instant.ofEpochMilli(6)));
        assertEquals(1, new SkipTimeline<>(base, 3).higherIndexOf(Instant.ofEpochMilli(7)));
        assertEquals(-3, new SkipTimeline<>(base, 3).higherIndexOf(Instant.ofEpochMilli(9)));
    }

    @Test
    void size() {
        assertEquals(5, new SkipTimeline<>(base, 0).size());
        assertEquals(4, new SkipTimeline<>(base, 1).size());
        assertEquals(0, new SkipTimeline<>(base, 5).size());
    }
}
