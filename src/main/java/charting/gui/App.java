package charting.gui;

import charting.data.*;
import charting.data.demo.DemoAssetDataProvider;
import charting.data.demo.DemoTickerDataProvider;
import charting.gui.chart.Drawing;
import charting.gui.drawings.*;
import charting.gui.superchart.ChartContent;
import charting.gui.superchart.InterDayTimeAxisPosConverter;
import charting.gui.superchart.ManualMeasureArea;
import charting.gui.superchart.SuperChart;
import charting.gui.superchart.indicatorspane.Indicator;
import charting.timeline.MapperTimeline;
import charting.timeline.Timeline;
import charting.util.Range;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class App extends Application {
    private final Color bullishColor = Color.valueOf("#0ea59d");
    private final Color bearishColor = Color.valueOf("#ff2e54");
    private final Color lightBullishColor = Color.valueOf("#b2f5f0");
    private final Color lightBearishColor = Color.valueOf("#ffccd5");

    private final SuperChart superChart;
    private final ChartContent mainContent;

    private final Timeline<? extends Candle> candles;

    private Map<Indicator, Runnable> indicators;

    public App() {
        AssetDataProvider assetDataProvider = new DemoAssetDataProvider();
        TickerDataProvider tickerDataProvider = new DemoTickerDataProvider();

        Ticker ticker = DemoTickerDataProvider.MSFT;

        candles = tickerDataProvider.getCandles(ticker,
                new PeriodInterval(Period.ofDays(1))).orElseThrow();

        superChart = new SuperChart();
        superChart.setTimeAxis(new Range(Math.max(0, candles.size() - 400), candles.size() + 20));
        superChart.getIndicators().setAll(getIndicators().keySet());
        superChart.setIndicatorSelectionHandler(this::handleIndicatorSelection);
        superChart.setTimeAxisPosConverter(new InterDayTimeAxisPosConverter(candles));
        superChart.userDrawingProperty().subscribe(this::onUserDrawingChange);

        CandleChart candleChart = new CandleChart(candles);
        candleChart.setBullishColor(bullishColor);
        candleChart.setBearishColor(bearishColor);

        VolumeChart volumeChart = new VolumeChart(candles);
        volumeChart.setBullishColor(bullishColor);
        volumeChart.setBearishColor(bearishColor);

        mainContent = new ChartContent();
        mainContent.setLongName(assetDataProvider.getAssetName(ticker.sourceAssetId()).orElse(""));
        mainContent.setShortName(tickerDataProvider.getTickerSymbol(ticker).orElse(""));
        mainContent.setValueAxis(getYDrawingRange(candleChart));
        mainContent.getDrawings().addAll(candleChart, volumeChart);
        superChart.getChartContents().add(mainContent);
    }

    public static void launch(String[] args) {
        Application.launch(App.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(superChart));

        URL styleSheetUrl = Objects.requireNonNull(getClass().getResource("App.css"));
        primaryStage.getScene().getStylesheets().add(styleSheetUrl.toExternalForm());

        primaryStage.show();
    }

    private Map<Indicator, Runnable> getIndicators() {
        if (indicators == null) {
            indicators = new LinkedHashMap<>();

            indicators.put(new Indicator("Simple Moving Average"),
                    () -> mainContent.getDrawings().add(new SmaChart(atClose(candles), 200)));
            indicators.put(new Indicator("Exponential Moving Average"),
                    () -> mainContent.getDrawings().add(new EmaChart(atClose(candles), 30)));
            indicators.put(new Indicator("Bollinger Bands"),
                    () -> mainContent.getDrawings().add(new BollingerBandsChart(atClose(candles))));
            indicators.put(new Indicator("Moving Average Convergence Divergence"),
                    () -> addAsNewChartContent(configureMacdChart(new MacdChart(atClose(candles)))));
            indicators.put(new Indicator("Average True Range"),
                    () -> addAsNewChartContent(new AtrChart(candles)));
        }

        return indicators;
    }

    private MacdChart configureMacdChart(MacdChart macdChart) {
        macdChart.setAscendingBullishHistogramColor(bullishColor);
        macdChart.setDescendingBearishHistogramColor(bearishColor);
        macdChart.setDescendingBullishHistogramColor(lightBullishColor);
        macdChart.setAscendingBearishHistogramColor(lightBearishColor);
        return macdChart;
    }

    private void addAsNewChartContent(TimelineDrawing t) {
        ChartContent chartContent = new ChartContent();
        chartContent.setValueAxis(getYDrawingRange(t));
        chartContent.getDrawings().add(t);
        superChart.getChartContents().add(chartContent);
    }

    private void handleIndicatorSelection(Indicator i) {
        getIndicators().get(i).run();
    }

    private Range getYDrawingRange(TimelineDrawing t) {
        Range r = t.getYDrawingRange(superChart.getTimeAxis());
        r = r.scaled(1.2);

        if (!Double.isFinite(r.start())) {
            r = new Range(-1, r.end());
        }

        if (!Double.isFinite(r.end())) {
            r = new Range(r.start(), 1);
        }

        return r;
    }

    private Timeline<Number> atClose(Timeline<? extends Candle> t) {
        return new MapperTimeline<>(t, Candle::getClose);
    }

    private void onUserDrawingChange(Drawing newVal) {
        if (newVal instanceof ManualMeasureArea m) {
            m.getUnderlying().setBullishColor(bullishColor);
            m.getUnderlying().setBearishColor(bearishColor);
        }
    }
}
