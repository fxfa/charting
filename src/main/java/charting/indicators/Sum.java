package charting.indicators;

import charting.timeline.MappedTimeline;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;
import charting.util.Preconditions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sums up the last n values.
 */
public final class Sum extends MappedTimeline<Number, Double> {
    private final int length;
    private final boolean eager;

    private final Map<Instant, Double> cache = new HashMap<>();

    public Sum(Timeline<? extends Number> base, int length) {
        this(base, length, false);
    }

    public Sum(Timeline<? extends Number> base, int length, boolean eager) {
        super(base);

        Preconditions.checkArgument(length > 0);

        this.length = length;
        this.eager = eager;
    }

    @Override
    protected Double map(Instant instant, Number number) {
        if (number == null) {
            cache.remove(instant);

            for (int i = 0; i < length; i++) {
                Timestamped<? extends Number> t = getBase().higher(instant);

                if (t == null) {
                    break;
                } else {
                    instant = t.timestamp();
                    cache.remove(instant);
                }
            }

            return null;
        }

        return getSum(instant);
    }

    private Double getSum(Instant instant) {
        return cache.computeIfAbsent(instant, this::calculateSum);
    }

    private Double calculateSum(Instant instant) {
        Timestamped<? extends Number> n = getBase().get(instant);

        if (n == null) {
            return null;
        }

        int i = getBase().indexOf(instant);

        if (!eager && i + 1 < length) {
            return Double.NaN;
        }

        if (!eager && i + 1 == length) {
            return calculateSumByIteration(i);
        }

        if (length > 5) {
            if (i > 0) {
                Timestamped<? extends Number> l = getBase().lower(instant);
                if (cache.containsKey(l.timestamp())) {
                    return calculateSumBasedOnLower(i, cache.get(l.timestamp()));
                }
            }

            if (i != getBase().size() - 1) {
                Timestamped<? extends Number> h = getBase().higher(instant);
                if (cache.containsKey(h.timestamp())) {
                    return calculateSumBasedOnHigher(i, cache.get(h.timestamp()));
                }
            }
        }

        return calculateSumByIteration(i);
    }

    private double calculateSumBasedOnLower(int index, double lowerSum) {
        double n = getBase().get(index).value().doubleValue();

        if (index - length >= 0) {
            return lowerSum - getBase().get(index - length).value().doubleValue() + n;
        }

        return lowerSum + n;
    }

    private double calculateSumBasedOnHigher(int index, double higherSum) {
        double n = getBase().get(index + 1).value().doubleValue();

        if (index - length + 1 >= 0) {
            return higherSum - n + getBase().get(index - length + 1).value().doubleValue();
        }

        return higherSum - n;
    }

    private double calculateSumByIteration(int index) {
        double sum = 0;

        var it = getBase().listIterator(index + 1);
        for (int i = 0; it.hasPrevious(); ) {
            sum += it.previous().value().doubleValue();

            i++;

            if (i == length) {
                break;
            }
        }

        return sum;
    }

    @Override
    protected void onBaseUpdate(Instant instant, Number newBaseValue) {
        List<Instant> instantsToUpdate = new ArrayList<>();

        instantsToUpdate.add(instant);

        int index = higherIndexOf(instant);
        if (index >= 0) {
            var it = getBase().listIterator(index);
            for (int i = 0; i < length && it.hasNext(); i++) {
                instantsToUpdate.add(it.next().timestamp());
            }
        }

        instantsToUpdate.forEach(cache::remove);

        if (!hasListeners()) {
            return;
        }

        for (Instant i : instantsToUpdate) {
            onUpdate(i, getSum(i));
        }
    }

    public int getLength() {
        return length;
    }

    public boolean isEager() {
        return eager;
    }
}
