package charting.indicators;

import charting.data.Candle;
import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;

import java.time.Instant;

/**
 * The typical price of a candle is (high + low + close) / 3.
 */
public final class TypicalPrice extends MappedTimeline<Candle, Double> {
    public TypicalPrice(Timeline<? extends Candle> base) {
        super(base);
    }

    @Override
    protected Double map(Instant instant, Candle candle) {
        if (candle == null) {
            return null;
        }

        return (candle.getHigh().doubleValue() + candle.getLow().doubleValue() + candle.getClose().doubleValue()) / 3;
    }

    @Override
    protected void onBaseUpdate(Instant instant, Candle newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        onUpdate(instant, map(instant, newBaseValue));
    }
}
