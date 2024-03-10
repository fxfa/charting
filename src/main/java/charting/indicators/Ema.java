package charting.indicators;

import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;
import charting.util.Preconditions;

import java.time.Instant;
import java.util.TreeMap;

/**
 * An exponential moving average timeline. Similar to {@link Sma} but more weight is given to more recent values.
 */
public final class Ema extends MappedTimeline<Number, Double> {
    private final int emaLength;

    private final double multiplier;

    private final TreeMap<Integer, Double> emas = new TreeMap<>();

    public Ema(Timeline<? extends Number> base, int emaLength) {
        super(base);

        Preconditions.checkArgument(emaLength > 0);

        this.emaLength = emaLength;
        multiplier = 2.0 / (1 + emaLength);
    }

    @Override
    protected Double map(Instant instant, Number number) {
        if (number == null) {
            emas.tailMap(getBase().indexOf(instant), true).clear();
            return null;
        }

        return getEma(indexOf(instant));
    }

    private Double getEma(int index) {
        if (index < 0) {
            return null;
        }

        if (emas.containsKey(index)) {
            return emas.get(index);
        }

        if (index + 1 < emaLength) {
            return Double.NaN;
        }

        if (emas.isEmpty()) {
            emas.put(emaLength - 1, calculateStartEma());
        }

        for (int i = emas.size() + emaLength - 1; i <= index; i++) {
            emas.put(i, multiplier * getBase().get(i).value().doubleValue() +
                    emas.lastEntry().getValue() * (1 - multiplier));
        }

        return emas.lastEntry().getValue();
    }

    private double calculateStartEma() {
        double sum = 0;

        int i = 0;
        for (Timestamped<? extends Number> n : getBase()) {
            sum += n.value().doubleValue();

            i++;

            if (i == emaLength) {
                break;
            }
        }

        return sum / emaLength;
    }

    @Override
    protected void onBaseUpdate(Instant instant, Number newBaseValue) {
        int i = indexOf(instant);

        emas.tailMap(i).clear();

        if (!hasListeners()) {
            return;
        }

        onUpdate(instant, newBaseValue == null ? null : getEma(indexOf(instant)));

        Timestamped<? extends Number> t = getBase().higher(instant);
        while (t != null) {
            onUpdate(t.timestamp(), getEma(indexOf(t.timestamp())));
            t = getBase().higher(t.timestamp());
        }
    }

    public int getEmaLength() {
        return emaLength;
    }
}
