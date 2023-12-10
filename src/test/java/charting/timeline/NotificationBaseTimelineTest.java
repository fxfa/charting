package charting.timeline;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class NotificationBaseTimelineTest {
    @Test
    void hasListeners() {
        TimelineListener<Integer> l = (i, v) -> {
        };

        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();
        t.addListener(l);
        assertTrue(t.hasListeners());

        t = new OrderStatsTreeTimeline<>();
        t.addWeakListener(l);
        assertTrue(t.hasListeners());
    }

    @Test
    void listenersAreNotifiedInOrder() {
        List<Integer> order = new ArrayList<>();

        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();

        t.addListener((i, v) -> order.add(1));
        TimelineListener<Integer> l = (i, v) -> order.add(2);
        t.addWeakListener(l);
        t.addListener((i, v) -> order.add(3));

        t.put(Instant.EPOCH, 1);

        assertEquals(List.of(1, 2, 3), order);
    }

    @Test
    void nonSequentialUpdatingThrows() {
        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();

        AtomicBoolean threw = new AtomicBoolean();

        t.addListener((i, v) -> {
            try {
                t.put(Instant.EPOCH, 2);
            } catch (Exception e) {
                threw.set(true);
            }
        });

        t.addListener((i, v) -> {
        });

        t.put(Instant.EPOCH, 1);

        assertTrue(threw.get());
    }

    @Test
    void doesNotNotifyRemovedListeners() {
        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();

        AtomicBoolean notified = new AtomicBoolean();

        TimelineListener<Integer> l1 = (i, v) -> notified.set(true);
        t.addListener(l1);
        TimelineListener<Integer> l2 = (i, v) -> notified.set(true);
        t.addWeakListener(l2);

        t.removeListener(l1);
        t.removeListener(l2);

        t.put(Instant.EPOCH, 1);

        assertFalse(notified.get());
    }
}