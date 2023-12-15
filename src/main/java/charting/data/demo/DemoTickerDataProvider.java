package charting.data.demo;

import charting.data.*;
import charting.timeline.OrderStatsTreeTimeline;
import charting.timeline.Timeline;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.Period;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class DemoTickerDataProvider implements TickerDataProvider {
    private static final TickerSourceId NASDAQ = new TickerSourceId("MIC", "XNAS");

    public static final Ticker AAPL = new Ticker(DemoAssetDataProvider.AAPL, DemoAssetDataProvider.USD, NASDAQ);
    public static final Ticker MSFT = new Ticker(DemoAssetDataProvider.MSFT, DemoAssetDataProvider.USD, NASDAQ);
    public static final Ticker TSLA = new Ticker(DemoAssetDataProvider.TSLA, DemoAssetDataProvider.USD, NASDAQ);

    private static final Set<Ticker> TICKERS = Set.of(AAPL, TSLA);

    private static final Map<Ticker, String> SYMBOLS = Map.of(AAPL, "AAPL", MSFT, "MSFT", TSLA, "TSLA");

    private static final Map<Ticker, Timeline<? extends Candle>> DAILY_AGGREGATES;

    static {
        InputStream aapl = DemoTickerDataProvider.class.getResourceAsStream("AAPL_1D.csv");
        InputStream msft = DemoTickerDataProvider.class.getResourceAsStream("MSFT_1D.csv");
        InputStream tsla = DemoTickerDataProvider.class.getResourceAsStream("TSLA_1D.csv");
        DAILY_AGGREGATES = Map.of(AAPL, parseCandles(aapl), MSFT, parseCandles(msft), TSLA, parseCandles(tsla));
    }

    private static Timeline<DoubleCandle> parseCandles(InputStream inputStream) {
        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(inputStream)))
                .withSkipLines(1).build()) {
            OrderStatsTreeTimeline<DoubleCandle> candles = new OrderStatsTreeTimeline<>();

            reader.forEach(line -> {
                Instant start = Instant.ofEpochSecond(Long.parseLong(line[0]));
                Instant end = Instant.ofEpochSecond(Long.parseLong(line[1]));
                double open = Double.parseDouble(line[2]);
                double high = Double.parseDouble(line[3]);
                double low = Double.parseDouble(line[4]);
                double close = Double.parseDouble(line[5]);
                double volume = Double.parseDouble(line[6]);

                candles.put(start, new DoubleCandle(open, high, low, close, volume, start, end));
            });

            return candles;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Ticker> getTickers() {
        return TICKERS;
    }

    @Override
    public Optional<? extends Timeline<? extends Candle>> getCandles(Ticker ticker, Interval interval) {
        if (!(interval instanceof PeriodInterval p) || !p.getPeriod().equals(Period.ofDays(1))) {
            return Optional.empty();
        }

        return Optional.ofNullable(DAILY_AGGREGATES.get(ticker));
    }

    @Override
    public Optional<String> getTickerSymbol(Ticker ticker) {
        return Optional.ofNullable(SYMBOLS.get(ticker));
    }
}
