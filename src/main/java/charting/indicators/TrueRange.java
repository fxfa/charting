package charting.indicators;

import charting.data.Candle;
import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;
import charting.util.MathUtil;

import java.time.Instant;

/**
 * The true range is defined as max(high - low, abs(close - previous high), abs(close - previous low)).
 */
public final class TrueRange extends MappedTimeline<Candle, Double> {
    public TrueRange(Timeline<? extends Candle> base) {
        super(base);
    }

    @Override
    protected Double map(Instant instant, Candle candle) {
        return calculate(getValue(getBase().lower(instant)), candle);
    }

    private Double calculate(Candle p, Candle c) {
        double h = c.getHigh().doubleValue();
        double l = c.getLow().doubleValue();

        if (p == null) {
            return h - l;
        }

        double cp = p.getClose().doubleValue();

        return MathUtil.getMax(h - l, Math.abs(h - cp), Math.abs(l - cp));
    }

    private Candle getValue(Timestamped<? extends Candle> t) {
        return t == null ? null : t.value();
    }

    @Override
    protected void onBaseUpdate(Instant instant, Candle newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        Timestamped<? extends Candle> predecessor = getBase().lower(instant);

        if (newBaseValue == null) {
            onUpdate(instant, null);
        } else {
            onUpdate(instant, calculate(getValue(predecessor), newBaseValue));
        }

        if (!instant.equals(getBase().last().timestamp())) {
            Timestamped<? extends Candle> successor = getBase().higher(instant);

            onUpdate(successor.timestamp(),
                    calculate(newBaseValue == null ? getValue(predecessor) : newBaseValue, successor.value()));
        }
    }
}
