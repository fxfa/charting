package charting.gui.drawings;

import charting.data.Candle;
import charting.gui.chart.Line;
import charting.gui.chart.*;
import charting.indicators.BollingerBands;
import charting.timeline.MapperTimeline;
import charting.timeline.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BollingerBandsChart implements LegendDrawing {
    private final IntegerProperty length = new SimpleIntegerProperty(20);

    private final ObjectProperty<Color> movingAverageColor = new SimpleObjectProperty<>(Color.ORANGE);
    private final ObjectProperty<Color> bandColor = new SimpleObjectProperty<>(Color.DODGERBLUE);

    private final LineChart maLine = new LineChart();
    private final LineChart upperLine = new LineChart();
    private final LineChart lowerLine = new LineChart();

    private final ObjectProperty<Timeline<? extends Candle>> base = new SimpleObjectProperty<>();
    private BollingerBands bollingerBands;

    public BollingerBandsChart(Timeline<? extends Candle> base) {
        this();
        setBase(base);
    }

    public BollingerBandsChart() {
        maLine.colorProperty().bind(movingAverageColor);
        upperLine.colorProperty().bind(bandColor);
        lowerLine.colorProperty().bind(bandColor);

        baseProperty().addListener((obs, oldVal, newVal) -> updateBollinger());
        length.addListener((obs, oldVal, newVal) -> updateBollinger());
    }

    private void updateBollinger() {
        if (getBase() == null) {
            bollingerBands = null;
            maLine.setValues(null);
            upperLine.setValues(null);
            lowerLine.setValues(null);
        } else {
            bollingerBands = new BollingerBands(getBase(), getLength(), 2);
            maLine.setValues(new MapperTimeline<>(bollingerBands, BollingerBands.Values::ma));
            upperLine.setValues(new MapperTimeline<>(bollingerBands, BollingerBands.Values::upper));
            lowerLine.setValues(new MapperTimeline<>(bollingerBands, BollingerBands.Values::lower));
        }
    }

    public Timeline<? extends Candle> getBase() {
        return base.get();
    }

    public ObjectProperty<Timeline<? extends Candle>> baseProperty() {
        return base;
    }

    public void setBase(Timeline<? extends Candle> base) {
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
        return movingAverageColor.get();
    }

    public ObjectProperty<Color> movingAverageColorProperty() {
        return movingAverageColor;
    }

    public void setMovingAverageColor(Color movingAverageColor) {
        this.movingAverageColor.set(movingAverageColor);
    }

    public Color getBandColor() {
        return bandColor.get();
    }

    public ObjectProperty<Color> bandColorProperty() {
        return bandColor;
    }

    public void setBandColor(Color bandColor) {
        this.bandColor.set(bandColor);
    }

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (bollingerBands == null) {
            return Collections.emptyList();
        }

        List<Line> upperLines = upperLine.getDrawables(context);
        List<Line> lowerLines = lowerLine.getDrawables(context);

        double[] xs = new double[upperLines.size() * 2 + 2];
        double[] ys = new double[upperLines.size() * 2 + 2];

        for (int i = 0; i < upperLines.size(); i++) {
            xs[i] = upperLines.get(i).getX1();
            ys[i] = upperLines.get(i).getY1();
        }
        xs[upperLines.size()] = upperLines.get(upperLines.size() - 1).getX2();
        ys[upperLines.size()] = upperLines.get(upperLines.size() - 1).getY2();

        for (int i = 0; i < lowerLines.size(); i++) {
            xs[lowerLines.size() + 1 + i] = lowerLines.get(lowerLines.size() - 1 - i).getX2();
            ys[lowerLines.size() + 1 + i] = lowerLines.get(lowerLines.size() - 1 - i).getY2();
        }
        xs[upperLines.size() * 2 + 1] = lowerLines.get(0).getX1();
        ys[upperLines.size() * 2 + 1] = lowerLines.get(0).getY1();

        List<Drawable> drawables = new ArrayList<>();
        drawables.add(new Polygon(xs, ys, getBandColor().deriveColor(
                0, 1, 1, 0.05)));
        drawables.addAll(maLine.getDrawables(context));
        drawables.addAll(upperLines);
        drawables.addAll(lowerLines);

        return drawables;
    }

    @Override
    public List<ChartLegendString> getLegend(double x) {
        int s = bollingerBands == null ? 0 : bollingerBands.size();

        if (s == 0) {
            return Collections.emptyList();
        }

        int i = (int) (Double.isNaN(x) ? s - 1 : Math.min(Math.max(0, Math.round(x)), s - 1));
        BollingerBands.Values v = bollingerBands.get(i).value();
        double ma = Math.round(v.ma() * 100) / 100d;
        double u = Math.round(v.upper() * 100) / 100d;
        double l = Math.round(v.lower() * 100) / 100d;

        return List.of(new ChartLegendString("BB:", "", Color.TRANSPARENT),
                new ChartLegendString(" ", String.valueOf(ma), getMovingAverageColor()),
                new ChartLegendString(" ", String.valueOf(u), getBandColor()),
                new ChartLegendString(" ", String.valueOf(l), getBandColor()));
    }
}
