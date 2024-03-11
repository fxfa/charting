package charting.indicators;

import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;
import charting.util.Preconditions;

import java.time.Instant;

/**
 * The bollinger bands consist of a simple moving average and both an upper and a lower band
 * which are n (typically two) standard deviations away from the simple moving average.
 */
public final class BollingerBands extends MappedTimeline<Number, BollingerBands.Values> {
    private final int deviations;

    private final Sma sma;

    public BollingerBands(Timeline<? extends Number> base) {
        this(base, 20, 2);
    }

    /**
     * @param length     The length of the simple moving average.
     * @param deviations The distance of standard deviations between the simple moving average and the upper/lower band.
     */
    public BollingerBands(Timeline<? extends Number> base, int length, int deviations) {
        super(new StandardDeviation(base, length));

        Preconditions.checkArgument(length > 0);
        Preconditions.checkArgument(deviations > 0);

        this.deviations = deviations;

        sma = new Sma(base, length);
        sma.addListener(this::onSmaUpdate);
    }

    @Override
    protected Values map(Instant instant, Number v) {
        if (v == null) {
            return null;
        }

        Timestamped<Double> sma = this.sma.get(instant);

        if (sma == null) {
            return new Values(Double.NaN, Double.NaN, Double.NaN);
        }

        return new Values(sma.value() + 2 * v.doubleValue(),
                sma.value() - 2 * v.doubleValue(), sma.value());
    }

    @Override
    protected void onBaseUpdate(Instant instant, Number newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        onUpdate(instant, map(instant, newBaseValue));
    }

    private void onSmaUpdate(Instant instant, Double newValue) {
        if (!hasListeners()) {
            return;
        }

        if (newValue == null) {
            onUpdate(instant, null);
        } else {
            onUpdate(instant, map(instant, getBase().get(instant).value()));
        }
    }

    public int getDeviations() {
        return deviations;
    }

    public int getLength() {
        return sma.getLength();
    }

    public record Values(double upper, double lower, double ma) {
    }
}
