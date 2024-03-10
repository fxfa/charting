package charting.indicators;

import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;
import charting.util.Preconditions;

import java.time.Instant;

/**
 * A simple moving average timeline (the average value over a specified period).
 */
public final class Sma extends MappedTimeline<Double, Double> {
    private final int length;
    private final boolean eager;

    public Sma(Timeline<? extends Number> base, int length) {
        this(base, length, false);
    }

    /**
     * @param eager If false, any values with index lower than the length will be NaN.
     *              If true, those values will be calculated but their index will be used
     *              for averaging instead of the length.
     */
    public Sma(Timeline<? extends Number> base, int length, boolean eager) {
        super(new Sum(base, length, eager));

        Preconditions.checkArgument(length > 0);

        this.length = length;
        this.eager = eager;
    }

    @Override
    protected Double map(Instant instant, Double number) {
        if (number == null) {
            return null;
        }

        if (eager) {
            return number / Math.min(length, indexOf(instant) + 1);
        }

        return number / length;
    }

    @Override
    protected void onBaseUpdate(Instant instant, Double newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        onUpdate(instant, map(instant, newBaseValue));
    }

    public int getLength() {
        return length;
    }

    public boolean isEager() {
        return eager;
    }
}
