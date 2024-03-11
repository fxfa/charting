package charting.indicators;

import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;
import charting.timeline.TimelineListener;
import charting.timeline.Timestamped;

import java.time.Instant;

/**
 * Subtracts from every value in the base timeline the value at the same instant of another timeline
 * if such a value is present.
 */
public final class Subtraction extends MappedTimeline<Number, Double> {
    private final Timeline<? extends Number> subtrahend;

    private final TimelineListener<Number> subtrahendListener = this::onSubtrahendUpdate;

    public Subtraction(Timeline<? extends Number> base, Timeline<? extends Number> subtrahend) {
        super(base);

        this.subtrahend = subtrahend;

        subtrahend.addWeakListener(subtrahendListener);
    }

    @Override
    protected Double map(Instant instant, Number number) {
        if (number == null) {
            return null;
        }

        Timestamped<? extends Number> s = subtrahend.get(instant);

        if (s == null) {
            return number.doubleValue();
        }

        return number.doubleValue() - s.value().doubleValue();
    }

    @Override
    protected void onBaseUpdate(Instant instant, Number newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        onUpdate(instant, map(instant, newBaseValue));
    }

    private void onSubtrahendUpdate(Instant instant, Number newValue) {
        if (!hasListeners()) {
            return;
        }

        Timestamped<? extends Number> s = getBase().get(instant);

        if (s == null) {
            return;
        }

        double n = newValue == null ? 0 : newValue.doubleValue();
        onUpdate(instant, s.value().doubleValue() - n);
    }
}
