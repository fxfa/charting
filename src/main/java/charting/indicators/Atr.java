package charting.indicators;

import charting.data.Candle;
import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;

import java.time.Instant;

/**
 * The ATR (Average true range) is a simple moving average of the candles true ranges.
 */
public final class Atr extends MappedTimeline<Double, Double> {
    private final int length;

    public Atr(Timeline<? extends Candle> base) {
        this(base, 14);
    }

    public Atr(Timeline<? extends Candle> base, int length) {
        super(new Sma(new TrueRange(base), length));

        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    protected Double map(Instant instant, Double o) {
        return o;
    }

    @Override
    protected void onBaseUpdate(Instant instant, Double newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        onUpdate(instant, newBaseValue);
    }
}
