package charting.data;

import charting.timeline.Timeline;

import java.util.Optional;
import java.util.Set;

public interface TickerDataProvider {
    Set<Ticker> getTickers();

    Optional<? extends Timeline<? extends Candle>> getCandles(Ticker ticker, Interval interval);

    Optional<String> getTickerSymbol(Ticker ticker);
}
