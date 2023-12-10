package charting.data;

import charting.util.Preconditions;

import java.time.Instant;

public final class DoubleCandle implements Candle {
    private final Double open;
    private final Double high;
    private final Double low;
    private final Double close;
    private final Double volume;
    private final Instant startInstant;
    private final Instant endInstant;
    private final Instant currentInstant;

    public DoubleCandle(Number open, Number high, Number low, Number close, Number volume,
                        Instant startInstant, Instant currentInstant, Instant endInstant) {
        this.open = open.doubleValue();
        this.high = high.doubleValue();
        this.low = low.doubleValue();
        this.close = close.doubleValue();
        this.volume = volume.doubleValue();
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.currentInstant = currentInstant;

        Preconditions.checkArgument(this.high >= this.open && this.high >= this.close);
        Preconditions.checkArgument(this.low <= this.open && this.low <= this.close);
        Preconditions.checkArgument(Double.isNaN(this.volume) || this.volume >= 0);
        Preconditions.checkArgument(endInstant.isAfter(startInstant));
        Preconditions.checkArgument(!currentInstant.isBefore(startInstant));
        Preconditions.checkArgument(!currentInstant.isAfter(endInstant));
    }

    public DoubleCandle(Number open, Number high, Number low, Number close, Number volume,
                        Instant startInstant, Instant endInstant) {
        this(open, high, low, close, volume, startInstant, endInstant, endInstant);
    }

    @Override
    public Double getOpen() {
        return open;
    }

    @Override
    public Double getHigh() {
        return high;
    }

    @Override
    public Double getLow() {
        return low;
    }

    @Override
    public Double getClose() {
        return close;
    }

    @Override
    public Double getVolume() {
        return volume;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    @Override
    public Instant getCurrentInstant() {
        return currentInstant;
    }
}
