package charting.timeline;

import charting.util.Preconditions;

import java.time.Instant;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * {@link Timeline} view implementation that ignores the first n elements of its base.
 */
public final class SkipTimeline<T> extends NotificationBaseTimeline<T> {
    private final Timeline<T> base;

    private final int skips;

    private final TimelineListener<T> listener = this::onBaseUpdate;

    public SkipTimeline(Timeline<T> base, int skips) {
        Preconditions.checkArgument(skips >= 0);

        this.base = base;
        this.skips = skips;

        base.addWeakListener(listener);
    }

    private void onBaseUpdate(Instant instant, T value) {
        int i = base.indexOf(instant);

        if (i + 1 > skips) {
            onUpdate(instant, value);
        } else if (skips > 0 && base.size() >= skips) {
            Timestamped<T> t = base.get(skips - 1);
            onUpdate(t.timestamp(), t.value());
        }
    }

    public int getSkips() {
        return skips;
    }

    @Override
    public Timestamped<T> first() {
        return get(0);
    }

    @Override
    public Timestamped<T> last() {
        return get(Math.max(size() - 1, 0));
    }

    @Override
    public Timestamped<T> get(Instant instant) {
        return indexOf(instant) < 0 ? null : base.get(instant);
    }

    @Override
    public Timestamped<T> floor(Instant instant) {
        int i = floorIndexOf(instant);
        return i < 0 ? null : get(i);
    }

    @Override
    public Timestamped<T> ceiling(Instant instant) {
        int i = ceilingIndexOf(instant);
        return i < 0 ? null : get(i);
    }

    @Override
    public Timestamped<T> lower(Instant instant) {
        int i = lowerIndexOf(instant);
        return i < 0 ? null : get(i);
    }

    @Override
    public Timestamped<T> higher(Instant instant) {
        int i = higherIndexOf(instant);
        return i < 0 ? null : get(i);
    }

    @Override
    public Timestamped<T> get(int i) {
        Preconditions.checkArgument(i >= 0);
        return base.get(i + skips);
    }

    @Override
    public ListIterator<Timestamped<T>> listIterator(int i) {
        Preconditions.checkIndex(i, size() + 1);

        return new ListIterator<>() {
            final ListIterator<Timestamped<T>> it = base.listIterator(i + skips);

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Timestamped<T> next() {
                return it.next();
            }

            @Override
            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            @Override
            public Timestamped<T> previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }

                return it.previous();
            }

            @Override
            public int nextIndex() {
                return it.nextIndex();
            }

            @Override
            public int previousIndex() {
                return it.previousIndex() - skips;
            }

            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public void set(Timestamped<T> t) {
                it.set(t);
            }

            @Override
            public void add(Timestamped<T> t) {
                it.add(t);
            }
        };
    }

    @Override
    public ListIterator<Timestamped<T>> listIterator(Instant instant) {
        return listIterator(indexOf(instant));
    }

    @Override
    public int indexOf(Instant instant) {
        int i = base.indexOf(instant);

        if (i < 0) {
            return -Math.max(-i - 1 - skips, 0) - 1;
        }

        return Math.max(i - skips, -1);
    }

    @Override
    public int size() {
        return Math.max(base.size() - skips, 0);
    }
}
