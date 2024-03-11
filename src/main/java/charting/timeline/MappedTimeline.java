package charting.timeline;

import charting.util.Preconditions;

import java.time.Instant;
import java.util.ListIterator;

/**
 * This class is meant to simplify the implementation of {@link Timeline}s that are based on another {@link Timeline}
 * with a 1 to 1 mapping.
 */
public abstract class MappedTimeline<T, R> extends NotificationBaseTimeline<R> {
    private final Timeline<? extends T> base;

    private final TimelineListener<T> listener = this::onBaseUpdate;

    protected MappedTimeline(Timeline<? extends T> base) {
        this.base = base;

        base.addWeakListener(listener);
    }

    protected final Timeline<? extends T> getBase() {
        return base;
    }

    private Timestamped<R> export(Timestamped<? extends T> t) {
        if (t == null) {
            return null;
        }

        return new Timestamped<>(t.timestamp(), map(t.timestamp(), t.value()));
    }

    /**
     * Called whenever an element is accessed. Should map the base element to the corresponding derived element.
     *
     * @param instant The instant of the accessed element.
     * @param t       The accessed element.
     */
    protected abstract R map(Instant instant, T t);

    /**
     * Called whenever a base element is updated. Should notify subscribed {@link TimelineListener}s of all
     * caused changes for the derived elements.
     */
    protected abstract void onBaseUpdate(Instant instant, T newBaseValue);

    @Override
    public Timestamped<R> first() {
        return export(base.first());
    }

    @Override
    public Timestamped<R> last() {
        return export(base.last());
    }

    @Override
    public Timestamped<R> get(Instant instant) {
        return export(base.get(instant));
    }

    @Override
    public Timestamped<R> floor(Instant instant) {
        return export(base.floor(instant));
    }

    @Override
    public Timestamped<R> ceiling(Instant instant) {
        return export(base.ceiling(instant));
    }

    @Override
    public Timestamped<R> lower(Instant instant) {
        return export(base.lower(instant));
    }

    @Override
    public Timestamped<R> higher(Instant instant) {
        return export(base.higher(instant));
    }

    @Override
    public Timestamped<R> get(int i) {
        return export(base.get(i));
    }

    @Override
    public ListIterator<Timestamped<R>> listIterator(int i) {
        Preconditions.checkIndex(i, size() + 1);

        return new ListIterator<>() {
            private final ListIterator<? extends Timestamped<? extends T>> d = base.listIterator(i);

            @Override
            public boolean hasNext() {
                return d.hasNext();
            }

            @Override
            public Timestamped<R> next() {
                return export(d.next());
            }

            @Override
            public boolean hasPrevious() {
                return d.hasPrevious();
            }

            @Override
            public Timestamped<R> previous() {
                return export(d.previous());
            }

            @Override
            public int nextIndex() {
                return d.nextIndex();
            }

            @Override
            public int previousIndex() {
                return d.previousIndex();
            }

            @Override
            public void remove() {
                d.remove();
            }

            @Override
            public void set(Timestamped<R> r) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Timestamped<R> r) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public ListIterator<Timestamped<R>> listIterator(Instant instant) {
        return listIterator(indexOf(instant));
    }

    @Override
    public int indexOf(Instant instant) {
        return base.indexOf(instant);
    }

    @Override
    public int size() {
        return base.size();
    }
}
