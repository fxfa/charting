package charting.timeline;

import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Timeline} implementation where every element of a base {@link Timeline}
 * is mapped to exactly one new element by using a mapper.
 */
public final class MapperTimeline<T, R> extends MappedTimeline<T, R> {
    private final BiFunction<Instant, T, R> mapper;

    public MapperTimeline(Timeline<T> base, BiFunction<Instant, T, R> mapper) {
        super(base);

        this.mapper = mapper;
    }

    public MapperTimeline(Timeline<T> base, Function<T, R> mapper) {
        this(base, (i, v) -> mapper.apply(v));
    }

    @Override
    protected R map(Instant instant, T t) {
        return mapper.apply(instant, t);
    }

    @Override
    protected void onBaseUpdate(Instant instant, T newBaseValue) {
        if (!hasListeners()) {
            return;
        }

        R newValue = newBaseValue == null ? null : map(instant, newBaseValue);
        onUpdate(instant, newValue);
    }
}
