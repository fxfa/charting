package charting.indicators;

import charting.timeline.MappedTimeline;
import charting.timeline.SkipTimeline;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;

import java.time.Instant;

public final class Macd extends MappedTimeline<Double, Macd.Values> {
    private final Ema signalEma;

    private final int shortPeriod;
    private final int longPeriod;
    private final int signalPeriod;

    public Macd(Timeline<? extends Number> base) {
        this(base, 12, 26, 9);
    }

    public Macd(Timeline<? extends Number> base, int shortPeriod, int longPeriod, int signalPeriod) {
        super(new Subtraction(new Ema(base, shortPeriod), new Ema(base, longPeriod)));

        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;

        signalEma = new Ema(new SkipTimeline<>(getBase(), longPeriod - 1), signalPeriod);
        signalEma.addListener(this::onSignalEmaUpdate);
    }

    @Override
    protected Values map(Instant instant, Double number) {
        if (number == null) {
            return null;
        }

        Timestamped<Double> s = signalEma.get(instant);
        return new Values(number, s == null ? Double.NaN : s.value());
    }

    @Override
    protected void onBaseUpdate(Instant instant, Double newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        onUpdate(instant, map(instant, newBaseValue));
    }

    private void onSignalEmaUpdate(Instant instant, Double newValue) {
        if (!hasListeners()) {
            return;
        }

        if (newValue == null) {
            onUpdate(instant, null);
        } else {
            onUpdate(instant, map(instant, getBase().get(instant).value()));
        }
    }

    public int getShortPeriod() {
        return shortPeriod;
    }

    public int getLongPeriod() {
        return longPeriod;
    }

    public int getSignalPeriod() {
        return signalPeriod;
    }

    public record Values(double macd, double signal) {
        public double histogram() {
            return macd - signal;
        }

        @Override
        public String toString() {
            return "Values[macd = %f,signal = %f,histogram = %f]".formatted(macd, signal, histogram());
        }
    }
}
