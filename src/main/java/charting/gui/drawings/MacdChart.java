package charting.gui.drawings;

import charting.indicators.Macd;
import charting.timeline.MapperTimeline;
import charting.timeline.Timeline;
import charting.util.MathUtil;
import charting.util.Range;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class MacdChart extends DelegateLegendDrawing implements TimelineDrawing {
    private final BarChart histogram = new BarChart();
    private final LineChart macdLine = new LineChart();
    private final LineChart signalLine = new LineChart();

    private final ObjectProperty<Timeline<? extends Number>> base = new SimpleObjectProperty<>();
    private Macd macd;

    public MacdChart(Timeline<? extends Number> base) {
        this();
        setBase(base);
    }

    public MacdChart() {
        histogram.setDescription("MACD: ");
        macdLine.setColor(Color.ROYALBLUE);
        macdLine.setDescription("  ");
        signalLine.setColor(Color.ORANGE);
        signalLine.setDescription("  ");

        setDelegates(histogram, macdLine, signalLine);

        base.subscribe(this::updateMacd);
    }

    private void updateMacd() {
        if (getBase() == null) {
            macd = null;
            histogram.setValues(null);
            macdLine.setValues(null);
            signalLine.setValues(null);
        } else {
            macd = new Macd(getBase());
            histogram.setValues(new MapperTimeline<>(macd, Macd.Values::histogram));
            macdLine.setValues(new MapperTimeline<>(macd, Macd.Values::macd));
            signalLine.setValues(new MapperTimeline<>(macd, Macd.Values::signal));
        }
    }

    public Timeline<? extends Number> getBase() {
        return base.get();
    }

    public ObjectProperty<Timeline<? extends Number>> baseProperty() {
        return base;
    }

    public void setBase(Timeline<? extends Number> base) {
        this.base.set(base);
    }

    public Color getMacdLineColor() {
        return macdLine.getColor();
    }

    public ObjectProperty<Color> macdLineColorProperty() {
        return macdLine.colorProperty();
    }

    public void setMacdLineColor(Color macdLineColor) {
        macdLine.setColor(macdLineColor);
    }

    public Color getSignalLineColor() {
        return signalLine.getColor();
    }

    public ObjectProperty<Color> signalLineColorProperty() {
        return signalLine.colorProperty();
    }

    public void setSignalLineColor(Color signalLineColor) {
        signalLine.setColor(signalLineColor);
    }

    public Color getAscendingBullishHistogramColor() {
        return histogram.getAscendingPositiveColor();
    }

    public ObjectProperty<Color> ascendingBullishHistogramProperty() {
        return histogram.ascendingPositiveColorProperty();
    }

    public void setAscendingBullishHistogramColor(Color ascendingBullishHistogramColor) {
        histogram.setAscendingPositiveColor(ascendingBullishHistogramColor);
    }

    public Color getAscendingBearishHistogramColor() {
        return histogram.getAscendingNegativeColor();
    }

    public ObjectProperty<Color> ascendingBearishHistogramProperty() {
        return histogram.ascendingNegativeColorProperty();
    }

    public void setAscendingBearishHistogramColor(Color ascendingBearishHistogramColor) {
        histogram.setAscendingNegativeColor(ascendingBearishHistogramColor);
    }

    public Color getDescendingBullishHistogramColor() {
        return histogram.getDescendingPositiveColor();
    }

    public ObjectProperty<Color> descendingBullishHistogramProperty() {
        return histogram.descendingPositiveColorProperty();
    }

    public void setDescendingBullishHistogramColor(Color descendingBullishHistogramColor) {
        histogram.setDescendingPositiveColor(descendingBullishHistogramColor);
    }

    public Color getDescendingBearishHistogramColor() {
        return histogram.getDescendingNegativeColor();
    }

    public ObjectProperty<Color> descendingBearishHistogramProperty() {
        return histogram.descendingNegativeColorProperty();
    }

    public void setDescendingBearishHistogramColor(Color descendingBearishHistogramColor) {
        histogram.setDescendingNegativeColor(descendingBearishHistogramColor);
    }

    @Override
    public Range getYDrawingRange(double startX, double endX) {
        return TimelineDrawing.calculateYDrawingRange(startX, endX, macd,
                (m, v) -> MathUtil.getMinIgnoreNan(m, v.macd(), v.signal(), v.histogram()),
                (m, v) -> MathUtil.getMaxIgnoreNan(m, v.macd(), v.signal(), v.histogram()));
    }
}
