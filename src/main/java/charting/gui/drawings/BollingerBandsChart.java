package charting.gui.drawings;

import charting.gui.chart.ChartLegendString;
import charting.indicators.BollingerBands;
import charting.timeline.MapperTimeline;
import charting.timeline.Timeline;
import charting.util.MathUtil;
import charting.util.Range;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BollingerBandsChart extends DelegateLegendDrawing implements TimelineDrawing {
    private final LineChart maLine = new LineChart();
    private final LineChart lowerLine = new LineChart();
    private final LineChart upperLine = new LineChart();
    private final BandChart band = new BandChart();

    private final IntegerProperty length = new SimpleIntegerProperty(20);
    private final ObjectProperty<Timeline<? extends Number>> base = new SimpleObjectProperty<>();
    private BollingerBands bollingerBands;

    public BollingerBandsChart(Timeline<? extends Number> base, int length) {
        this();
        setBase(base);
        setLength(length);
    }


    public BollingerBandsChart(Timeline<? extends Number> base) {
        this();
        setBase(base);
    }

    public BollingerBandsChart() {
        maLine.setDescription("BB: ");
        maLine.setColor(Color.ORANGE);
        lowerLine.setDescription("  ");
        lowerLine.setColor(Color.DODGERBLUE);
        upperLine.setDescription("  ");
        upperLine.colorProperty().bind(lowerLine.colorProperty());
        band.colorProperty().bind(lowerLine.colorProperty().map(c ->
                c.deriveColor(0, 1, 1, 0.05)));

        setDelegates(band, maLine, lowerLine, upperLine);

        base.subscribe(this::updateBollinger);
        length.subscribe(this::updateBollinger);
    }

    private void updateBollinger() {
        if (getBase() == null) {
            bollingerBands = null;
            maLine.setValues(null);
            upperLine.setValues(null);
            lowerLine.setValues(null);
            band.setValues(null);
        } else {
            bollingerBands = new BollingerBands(getBase(), getLength(), 2);
            maLine.setValues(new MapperTimeline<>(bollingerBands, BollingerBands.Values::ma));
            upperLine.setValues(new MapperTimeline<>(bollingerBands, BollingerBands.Values::upper));
            lowerLine.setValues(new MapperTimeline<>(bollingerBands, BollingerBands.Values::lower));
            band.setValues(new MapperTimeline<>(bollingerBands, b -> new Range(b.lower(), b.upper())));
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

    public int getLength() {
        return length.get();
    }

    public IntegerProperty lengthProperty() {
        return length;
    }

    public void setLength(int length) {
        this.length.set(length);
    }

    public Color getMovingAverageColor() {
        return maLine.getColor();
    }

    public ObjectProperty<Color> movingAverageColorProperty() {
        return maLine.colorProperty();
    }

    public void setMovingAverageColor(Color movingAverageColor) {
        maLine.setColor(movingAverageColor);
    }

    public Color getBandColor() {
        return lowerLine.getColor();
    }

    public ObjectProperty<Color> bandColorProperty() {
        return lowerLine.colorProperty();
    }

    public void setBandColor(Color bandColor) {
        lowerLine.setColor(bandColor);
    }

    @Override
    public List<ChartLegendString> getLegend(double x) {
        if (getDelegates() == null) {
            return Collections.emptyList();
        }

        List<ChartLegendString> strings = new LinkedList<>();
        strings.addAll(maLine.getLegend(x));
        strings.addAll(lowerLine.getLegend(x));
        strings.addAll(upperLine.getLegend(x));

        return strings;
    }

    @Override
    public Range getYDrawingRange(double startX, double endX) {
        return TimelineDrawing.calculateYDrawingRange(startX, endX, bollingerBands,
                (m, v) -> MathUtil.getMinIgnoreNan(m, v.ma(), v.upper(), v.lower()),
                (m, v) -> MathUtil.getMaxIgnoreNan(m, v.ma(), v.upper(), v.lower()));
    }
}
