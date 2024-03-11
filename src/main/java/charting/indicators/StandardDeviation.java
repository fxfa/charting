package charting.indicators;

import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;

import java.time.Instant;

public final class StandardDeviation extends MappedTimeline<Number, Double> {
    private final int length;

    private final Sma sma;

    public <N extends Number> StandardDeviation(Timeline<N> base, int length) {
        super(base);

        this.length = length;

        sma = new Sma(base, length);
        sma.addListener(this::onSmaUpdate);
    }

    @Override
    protected Double map(Instant instant, Number v) {
        if (v == null) {
            return null;
        }

        int index = getBase().indexOf(instant);

        if (index + 1 < length) {
            return Double.NaN;
        }

        double sma = this.sma.get(instant).value();

        double sum = 0;

        var it = getBase().listIterator(index + 1);
        for (int i = 0; it.hasPrevious(); ) {
            sum += Math.pow(it.previous().value().doubleValue() - sma, 2);

            i++;

            if (i == length) {
                break;
            }
        }

        return Math.sqrt(sum / getLength());
    }

    @Override
    protected void onBaseUpdate(Instant instant, Number newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        var it = getBase().listIterator(instant);
        for (int i = 0; it.hasNext() && i < length; i++) {
            Timestamped<? extends Number> t = it.next();
            onUpdate(t.timestamp(), map(t.timestamp(), t.value()));
        }
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

    public int getLength() {
        return length;
    }
}
