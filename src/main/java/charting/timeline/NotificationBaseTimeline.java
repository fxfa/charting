package charting.timeline;

import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An abstract {@link Timeline} base class that handles the listener notification aspect.
 */
public abstract class NotificationBaseTimeline<T> implements Timeline<T> {
    private final List<Object> listeners = new CopyOnWriteArrayList<>();

    private boolean isNotifying;

    protected void onUpdate(Instant instant, T newValue) {
        if (isNotifying) {
            throw new IllegalStateException();
        }

        isNotifying = true;

        for (Object l : listeners) {
            if (l instanceof TimelineListener<?>) {
                @SuppressWarnings("unchecked")
                TimelineListener<? super T> t = (TimelineListener<? super T>) l;
                
                t.onUpdate(instant, newValue);
            } else {
                @SuppressWarnings("unchecked")
                TimelineListener<? super T> t = ((WeakReference<? extends TimelineListener<? super T>>) l).get();

                if (t == null) {
                    listeners.remove(l);
                } else {
                    t.onUpdate(instant, newValue);
                }
            }
        }

        isNotifying = false;
    }

    @Override
    public void addListener(TimelineListener<? super T> listener) {
        listeners.add(listener);
    }

    @Override
    public void addWeakListener(TimelineListener<? super T> listener) {
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeListener(TimelineListener<? super T> listener) {
        if (listeners.remove(listener)) {
            return;
        }

        for (Object l : listeners) {
            if (l instanceof WeakReference<?> w) {
                Object r = w.get();

                if (r == listener) {
                    listeners.remove(w);
                    return;
                }
            }
        }
    }

    protected boolean hasListeners() {
        return !listeners.isEmpty();
    }
}
