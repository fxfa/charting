package charting.gui.drawings;

import charting.gui.chart.ChartLegendString;
import charting.gui.chart.Drawable;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.Rect;
import charting.timeline.Timeline;
import charting.util.MathUtil;
import charting.util.Preconditions;
import charting.util.Range;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BarChart implements TimelineDrawing {
    private final ObjectProperty<Color> ascendingPositiveColor =
            new SimpleObjectProperty<>(Color.rgb(0, 178, 0));
    private final ObjectProperty<Color> descendingNegativeColor =
            new SimpleObjectProperty<>(Color.rgb(255, 26, 26));
    private final ObjectProperty<Color> descendingPositiveColor =
            new SimpleObjectProperty<>(Color.rgb(153, 255, 153));
    private final ObjectProperty<Color> ascendingNegativeColor =
            new SimpleObjectProperty<>(Color.rgb(255, 153, 153));

    private final ObjectProperty<Timeline<? extends Number>> values = new SimpleObjectProperty<>();

    private final StringProperty description = new SimpleStringProperty("");

    public BarChart() {
    }

    public BarChart(Timeline<? extends Number> values) {
        setValues(values);
    }

    public BarChart(Timeline<? extends Number> values, String description) {
        setValues(values);
        setDescription(description);
    }

    public Timeline<? extends Number> getValues() {
        return values.get();
    }

    public ObjectProperty<Timeline<? extends Number>> valuesProperty() {
        return values;
    }

    public void setValues(Timeline<? extends Number> values) {
        this.values.set(values);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Color getAscendingPositiveColor() {
        return ascendingPositiveColor.get();
    }

    public ObjectProperty<Color> ascendingPositiveColorProperty() {
        return ascendingPositiveColor;
    }

    public void setAscendingPositiveColor(Color ascendingPositiveColor) {
        this.ascendingPositiveColor.set(ascendingPositiveColor);
    }

    public Color getDescendingNegativeColor() {
        return descendingNegativeColor.get();
    }

    public ObjectProperty<Color> descendingNegativeColorProperty() {
        return descendingNegativeColor;
    }

    public void setDescendingNegativeColor(Color descendingNegativeColor) {
        this.descendingNegativeColor.set(descendingNegativeColor);
    }

    public Color getDescendingPositiveColor() {
        return descendingPositiveColor.get();
    }

    public ObjectProperty<Color> descendingPositiveColorProperty() {
        return descendingPositiveColor;
    }

    public void setDescendingPositiveColor(Color descendingPositiveColor) {
        this.descendingPositiveColor.set(descendingPositiveColor);
    }

    public Color getAscendingNegativeColor() {
        return ascendingNegativeColor.get();
    }

    public ObjectProperty<Color> ascendingNegativeColorProperty() {
        return ascendingNegativeColor;
    }

    public void setAscendingNegativeColor(Color ascendingNegativeColor) {
        this.ascendingNegativeColor.set(ascendingNegativeColor);
    }

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (getValues() == null) {
            return Collections.emptyList();
        }

        List<Drawable> bars = new LinkedList<>();

        double last = Double.NaN;

        var it = getValues().listIterator(getStartIndex(context));
        while (it.nextIndex() < getEndIndex(context)) {
            double current = it.next().value().doubleValue();

            if (!Double.isNaN(current)) {
                double barX = it.previousIndex() - 0.5 + (1 - 0.8) / 2;

                bars.add(new Rect(barX, 0, 0.8, current, getBarColor(last, current)));
            }

            last = current;
        }

        return bars;
    }

    private int getStartIndex(DrawingContext c) {
        return (int) Math.min(Math.max(c.getViewport().startX() - 1, 0), getValues().size());
    }

    private int getEndIndex(DrawingContext c) {
        return (int) Math.min(c.getViewport().endX() + 2, getValues().size());
    }

    private Color getBarColor(double last, double current) {
        boolean positive = current >= 0;

        if (!Double.isNaN(last) && positive && last > current) {
            return getDescendingPositiveColor();
        }

        if (!Double.isNaN(last) && !positive && last < current) {
            return getAscendingNegativeColor();
        }

        return positive ? getAscendingPositiveColor() : getDescendingNegativeColor();
    }

    @Override
    public List<ChartLegendString> getLegend(double x) {
        int s = getValues() == null ? 0 : getValues().size();

        if (s == 0) {
            return Collections.emptyList();
        }

        int i = (int) (Double.isNaN(x) ? s - 1 : Math.min(Math.max(0, Math.round(x)), s - 1));
        double last = i == 0 ? Double.NaN : getValues().get(i - 1).value().doubleValue();
        double current = getValues().get(i).value().doubleValue();

        return List.of(new ChartLegendString(getDescription(), current, getBarColor(last, current)));
    }

    @Override
    public Range getYDrawingRange(double startX, double endX) {
        return TimelineDrawing.calculateYDrawingRange(startX, endX, getValues(),
                (m, v) -> MathUtil.getMinIgnoreNan(m, v.doubleValue()),
                (m, v) -> MathUtil.getMaxIgnoreNan(m, v.doubleValue()));
    }
}
