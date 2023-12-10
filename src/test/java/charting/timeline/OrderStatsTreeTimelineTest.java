package charting.timeline;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatsTreeTimelineTest {
    @Test
    void put() {
        List<Integer> l = new ArrayList<>(IntStream.range(0, 100).boxed().toList());
        Collections.shuffle(l);

        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();
        for (int i : l) {
            t.put(Instant.ofEpochSecond(i), i);
        }

        l.sort(Comparator.naturalOrder());

        assertEquals(l, StreamSupport.stream(t.spliterator(), false)
                .map(Timestamped::value)
                .toList());
    }

    @Test
    void replacingWithPut() {
        OrderStatsTreeTimeline<Integer> t = createTimeline(2, 2, 3);
        t.put(Instant.EPOCH, 1);
        assertEquals(1, t.get(Instant.EPOCH).value());
    }

    @Test
    void first() {
        assertNull(createTimeline().first());

        Timestamped<Integer> t = createTimeline(1, 1, 1).first();
        assertNotNull(t);
        assertEquals(0, t.timestamp().getEpochSecond());
    }

    @Test
    void last() {
        assertNull(createTimeline().last());

        Timestamped<Integer> t = createTimeline(1, 1, 1).last();
        assertNotNull(t);
        assertEquals(2, t.timestamp().getEpochSecond());
    }

    @Test
    void get() {
        assertNull(createTimeline().get(Instant.EPOCH));

        Timestamped<Integer> t = createTimeline(1, 2, 3).get(Instant.EPOCH);
        assertNotNull(t);
        assertEquals(1, t.value());
    }

    @Test
    void floor() {
        assertNull(createTimeline().floor(Instant.EPOCH));

        Timestamped<Integer> t = createTimeline(1, 2, 3).floor(Instant.EPOCH);
        assertNotNull(t);
        assertEquals(1, t.value());

        t = createTimeline(1, 2, 3).floor(Instant.MAX);
        assertNotNull(t);
        assertEquals(3, t.value());

        assertNull(createTimeline(1, 2, 3).floor(Instant.MIN));
    }

    @Test
    void ceiling() {
        assertNull(createTimeline().ceiling(Instant.EPOCH));

        Timestamped<Integer> t = createTimeline(1, 2, 3).ceiling(Instant.EPOCH);
        assertNotNull(t);
        assertEquals(1, t.value());

        t = createTimeline(1, 2, 3).ceiling(Instant.MIN);
        assertNotNull(t);
        assertEquals(1, t.value());

        assertNull(createTimeline(1, 2, 3).ceiling(Instant.MAX));
    }

    @Test
    void lower() {
        assertNull(createTimeline().lower(Instant.EPOCH));

        Timestamped<Integer> t = createTimeline(1, 2, 3).lower(Instant.ofEpochSecond(1));
        assertNotNull(t);
        assertEquals(1, t.value());

        t = createTimeline(1, 2, 3).lower(Instant.MAX);
        assertNotNull(t);
        assertEquals(3, t.value());

        assertNull(createTimeline(1, 2, 3).lower(Instant.EPOCH));
    }

    @Test
    void higher() {
        assertNull(createTimeline().higher(Instant.EPOCH));

        Timestamped<Integer> t = createTimeline(1, 2, 3).higher(Instant.ofEpochSecond(1));
        assertNotNull(t);
        assertEquals(3, t.value());

        t = createTimeline(1, 2, 3).higher(Instant.MIN);
        assertNotNull(t);
        assertEquals(1, t.value());

        assertNull(createTimeline(1, 2, 3).higher(Instant.ofEpochSecond(2)));
    }

    @Test
    void getWithIndex() {
        assertNull(createTimeline().get(0));

        Timestamped<Integer> t = createTimeline(1, 2, 3).get(0);
        assertNotNull(t);
        assertEquals(1, t.value());
    }

    @Test
    void indexOf() {
        assertEquals(-1, createTimeline().indexOf(Instant.EPOCH));

        List<Integer> l = new ArrayList<>(IntStream.range(0, 100).boxed().toList());
        Collections.shuffle(l);
        int[] a = l.stream().limit(50).mapToInt(i -> i).sorted().toArray();

        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();
        for (int i : a) {
            t.put(Instant.ofEpochSecond(i), i);
        }

        for (int i = -1; i < 101; i++) {
            assertEquals(Arrays.binarySearch(a, i), t.indexOf(Instant.ofEpochSecond(i)));
        }
    }

    @Test
    void listIteratorWithIndex() {
        OrderStatsTreeTimeline<Integer> t = createTimeline(1, 2, 3, 4, 5);
        List<Integer> l = List.of(1, 2, 3, 4, 5);

        ListIterator<Timestamped<Integer>> tIt = t.listIterator(2);
        ListIterator<Integer> lIt = l.listIterator(2);

        Random r = new Random(0);
        for (int i = 0; i < 1000; i++) {
            boolean b = r.nextBoolean();

            if (b) {
                assertEquals(lIt.hasNext(), tIt.hasNext());
                assertEquals(lIt.nextIndex(), tIt.nextIndex());

                if (lIt.hasNext()) {
                    assertEquals(lIt.next(), tIt.next().value());
                } else {
                    assertThrows(NoSuchElementException.class, tIt::next);
                }
            } else {
                assertEquals(lIt.hasPrevious(), tIt.hasPrevious());
                assertEquals(lIt.previousIndex(), tIt.previousIndex());

                if (lIt.hasPrevious()) {
                    assertEquals(lIt.previous(), tIt.previous().value());
                } else {
                    assertThrows(NoSuchElementException.class, tIt::previous);
                }
            }
        }

        tIt = t.listIterator(0);
        lIt = l.listIterator(0);
        assertEquals(lIt.hasPrevious(), tIt.hasPrevious());
        assertEquals(lIt.previousIndex(), tIt.previousIndex());
        assertThrows(NoSuchElementException.class, tIt::previous);

        tIt = t.listIterator(5);
        lIt = l.listIterator(5);
        assertEquals(lIt.hasNext(), tIt.hasNext());
        assertEquals(lIt.nextIndex(), tIt.nextIndex());
        assertThrows(NoSuchElementException.class, tIt::next);

        assertThrows(IllegalArgumentException.class, () -> t.listIterator(-1));
        assertThrows(IllegalArgumentException.class, () -> t.listIterator(6));
    }

    @Test
    void listIteratorWithInstant() {
        OrderStatsTreeTimeline<Integer> t = createTimeline(1, 2, 3, 4, 5);

        ListIterator<Timestamped<Integer>> it = t.listIterator(Instant.ofEpochSecond(2));
        assertEquals(2, it.nextIndex());
        assertEquals(1, it.previousIndex());
    }

    @Test
    void modifyingWhileIteratingThrowsConcurrentModificationException() {
        OrderStatsTreeTimeline<Integer> t = createTimeline(1, 2, 3, 4, 5);

        ListIterator<Timestamped<Integer>> it = t.listIterator(Instant.ofEpochSecond(2));
        t.put(Instant.EPOCH, 0);
        assertThrows(ConcurrentModificationException.class, it::next);
        assertThrows(ConcurrentModificationException.class, it::previous);
    }

    @Test
    void size() {
        assertEquals(0, createTimeline().size());
        assertEquals(3, createTimeline(1, 2, 3).size());
    }

    private OrderStatsTreeTimeline<Integer> createTimeline(int... ints) {
        OrderStatsTreeTimeline<Integer> t = new OrderStatsTreeTimeline<>();

        int s = 0;
        for (int i : ints) {
            t.put(Instant.ofEpochSecond(s++), i);
        }

        return t;
    }
}