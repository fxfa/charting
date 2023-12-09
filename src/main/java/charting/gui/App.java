package charting.gui;

import charting.data.*;
import charting.data.demo.DemoAssetDataProvider;
import charting.data.demo.DemoTickerDataProvider;
import charting.gui.chart.Drawing;
import charting.gui.drawings.*;
import charting.gui.superchart.InterDayTimeAxisPosConverter;
import charting.gui.superchart.ManualMeasureArea;
import charting.gui.superchart.SuperChart;
import charting.gui.superchart.indicatorspane.Indicator;
import charting.timeline.MapperTimeline;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class App extends Application {
    private final Color bullishColor = Color.valueOf("#0ea59d");
    private final Color bearishColor = Color.valueOf("#ff2e54");

    public static void launch(String[] args) {
        Application.launch(App.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        AssetDataProvider assetDataProvider = new DemoAssetDataProvider();
        TickerDataProvider tickerDataProvider = new DemoTickerDataProvider();

        Ticker ticker = DemoTickerDataProvider.MSFT;

        Timeline<? extends Candle> candles = tickerDataProvider.getCandles(ticker,
                new PeriodInterval(Period.ofDays(1))).orElseThrow();

        SuperChart superChart = new SuperChart();
        superChart.setViewport(calculateChartHeadViewport(candles));
        superChart.setLongName(assetDataProvider.getAssetName(ticker.sourceAssetId()).orElse(""));
        superChart.setShortName(tickerDataProvider.getTickerSymbol(ticker).orElse(""));
        superChart.getIndicators().setAll(getIndicators().keySet());
        superChart.setIndicatorSelectionHandler(i -> superChart.getDrawings().add(getIndicators().get(i).apply(candles)));
        superChart.setTimeAxisPosConverter(new InterDayTimeAxisPosConverter(candles));
        superChart.activeManualDrawingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal instanceof ManualMeasureArea m) {
                m.getUnderlying().setBullishColor(bullishColor);
                m.getUnderlying().setBearishColor(bearishColor);
            }
        });

        CandleChart candleChart = new CandleChart(candles);
        candleChart.setBullishColor(bullishColor);
        candleChart.setBearishColor(bearishColor);

        VolumeChart volumeChart = new VolumeChart(candles);
        volumeChart.setBullishColor(bullishColor);
        volumeChart.setBearishColor(bearishColor);

        superChart.getDrawings().add(candleChart);
        superChart.getDrawings().add(volumeChart);

        primaryStage.setScene(new Scene(superChart));

        URL styleSheetUrl = Objects.requireNonNull(getClass().getResource("App.css"));
        primaryStage.getScene().getStylesheets().add(styleSheetUrl.toExternalForm());

        primaryStage.show();
    }

    private Map<Indicator, Function<Timeline<? extends Candle>, Drawing>> getIndicators() {
        Map<Indicator, Function<Timeline<? extends Candle>, Drawing>> indicators = new LinkedHashMap<>();
        indicators.put(new Indicator("Simple Moving Average"), c -> new SmaChart(atClose(c), 200));
        indicators.put(new Indicator("Exponential Moving Average"), c -> new EmaChart(atClose(c), 30));
        indicators.put(new Indicator("Bollinger Bands"), BollingerBandsChart::new);

        return indicators;
    }

    private Timeline<Number> atClose(Timeline<? extends Candle> t) {
        return new MapperTimeline<>(t, Candle::getClose);
    }

    private Bounds calculateChartHeadViewport(Timeline<? extends Candle> candles) {
        int s = candles.size();

        double x1 = Math.max(0, s - 400);
        double x2 = s + 20;

        double maxY = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;

        ListIterator<? extends Timestamped<? extends Candle>> it = candles.listIterator((int) x1);
        while (it.hasNext()) {
            Candle c = it.next().value();
            maxY = Math.max(maxY, c.getHigh().doubleValue());
            minY = Math.min(minY, c.getLow().doubleValue());
        }

        double padding = minY - minY * 0.95;
        double y1 = minY - padding;
        double y2 = maxY + padding;

        return new BoundingBox(x1, y1, x2 - x1, y2 - y1);
    }
}
