package charting.timeline;

import java.time.Instant;

public interface TimelineListener<T> {
    void onUpdate(Instant instant, T newValue);
}
