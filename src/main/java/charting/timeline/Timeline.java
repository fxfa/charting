package charting.timeline;

import java.time.Instant;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * A data structure where elements are sorted in chronological order.
 */
public interface Timeline<T> extends Iterable<Timestamped<T>> {
    Timestamped<T> first();

    Timestamped<T> last();

    /**
     * @return The element at the given instant.
     */
    Timestamped<T> get(Instant instant);

    /**
     * @return The element that is lower than or equal to the given instant or null if there is no such element.
     */
    Timestamped<T> floor(Instant instant);

    /**
     * @return The element that is higher than or equal to the given instant or null if there is no such element.
     */
    Timestamped<T> ceiling(Instant instant);

    /**
     * @return The element that is lower than the given instant or null if there is no such element.
     */
    Timestamped<T> lower(Instant instant);

    /**
     * @return The element that is higher than the given instant or null if there is no such element.
     */
    Timestamped<T> higher(Instant instant);

    /**
     * @return The element at the given index.
     */
    Timestamped<T> get(int i);

    /**
     * @return A {@link ListIterator} starting from the given index.
     */
    ListIterator<Timestamped<T>> listIterator(int i);

    /**
     * @return A {@link ListIterator} starting from the given instant.
     */
    ListIterator<Timestamped<T>> listIterator(Instant instant);

    /**
     * @return A {@link ListIterator} starting from the first element.
     */
    default ListIterator<Timestamped<T>> listIterator() {
        return listIterator(0);
    }

    @Override
    default Iterator<Timestamped<T>> iterator() {
        return listIterator();
    }

    /**
     * @return The index of the given instant or -1 if the instant is not contained.
     */
    int indexOf(Instant instant);

    /**
     * @return The index of the element that is lower than or equal to the given instant.
     * -1 if no such element exists.
     */
    default int floorIndexOf(Instant instant) {
        int i = indexOf(instant);

        if (i < -1) {
            return -i - 2;
        }

        return i;
    }

    /**
     * @return The index of the element that is greater than or equal to the given instant.
     * -1 if no such element exists.
     */
    default int ceilingIndexOf(Instant instant) {
        int i = indexOf(instant);

        if (i == -size() - 1) {
            return -1;
        }

        if (i < 0) {
            return -i - 1;
        }

        return i;
    }

    /**
     * @return The index of the element that is lower than the given instant.
     * -1 if no such element exists.
     */
    default int lowerIndexOf(Instant instant) {
        int i = indexOf(instant);

        if (i > 0) {
            return i - 1;
        }

        if (i < -1) {
            return -i - 2;
        }

        return -1;
    }

    /**
     * @return The index of the element that is greater than the given instant.
     * -1 if no such element exists.
     */
    default int higherIndexOf(Instant instant) {
        int i = indexOf(instant);

        int l = size();
        if (i == -l - 1 || i == l - 1) {
            return -1;
        }

        if (i < 0) {
            return -i - 1;
        }

        return i + 1;
    }

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Adds a listener to this timeline which will be called at least once for each modification.
     * Redundant notifications are allowed.
     */
    void addListener(TimelineListener<? super T> listener);

    /**
     * Adds a listener to this timeline but only keeps a weak reference.
     */
    void addWeakListener(TimelineListener<? super T> listener);

    void removeListener(TimelineListener<? super T> listener);
}
