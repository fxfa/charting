package charting.indicators;

import charting.data.Candle;
import charting.timeline.MappedTimeline;
import charting.timeline.MapperTimeline;
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

    public BollingerBands(Timeline<? extends Candle> base) {
        this(base, 20, 2);
    }

    /**
     * @param length     The length of the simple moving average.
     * @param deviations The distance of standard deviations between the simple moving average and the upper/lower band.
     */
    public BollingerBands(Timeline<? extends Candle> base, int length, int deviations) {
        super(new StandardDeviation(new MapperTimeline<>(base, Candle::getClose), length));

        Preconditions.checkArgument(length > 0);
        Preconditions.checkArgument(deviations > 0);

        this.deviations = deviations;

        sma = new Sma(new MapperTimeline<>(base, Candle::getClose), length);
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

    public int getDeviations() {
        return deviations;
    }

    public int getLength() {
        return sma.getLength();
    }

    public record Values(double upper, double lower, double ma) {
    }
}
