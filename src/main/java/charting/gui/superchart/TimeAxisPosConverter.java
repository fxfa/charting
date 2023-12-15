package charting.gui.superchart;

import java.time.Instant;
import java.util.function.Function;
import java.util.function.LongFunction;

/**
 * Defines which {@link Instant} corresponds to a given point on a time axis and vice versa.
 */
public interface TimeAxisPosConverter {
    long toIndex(Instant instant);

    Instant toInstant(long index);

    static TimeAxisPosConverter of(Function<Instant, Long> instantToIndex, LongFunction<Instant> indexToInstant) {
        return new TimeAxisPosConverter() {
            @Override
            public long toIndex(Instant instant) {
                return instantToIndex.apply(instant);
            }

            @Override
            public Instant toInstant(long index) {
                return indexToInstant.apply(index);
            }
        };
    }
}
