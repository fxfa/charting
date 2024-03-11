package charting.gui.drawings;

import charting.gui.chart.ChartLegendString;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.LegendDrawing;
import charting.gui.chart.Line;
import charting.timeline.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LineChart implements LegendDrawing {
    private final ObjectProperty<Timeline<? extends Number>> values = new SimpleObjectProperty<>();

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);

    private final StringProperty description = new SimpleStringProperty("");

    public LineChart() {
    }

    public LineChart(Timeline<? extends Number> values) {
        setValues(values);
    }

    public LineChart(Timeline<? extends Number> values, String description) {
        setValues(values);
        setDescription(description);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
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

    @Override
    public List<ChartLegendString> getLegend(double x) {
        int s = getValues() == null ? 0 : getValues().size();

        if (s == 0) {
            return Collections.emptyList();
        }

        int i = (int) (Double.isNaN(x) ? s - 1 : Math.min(Math.max(0, Math.round(x)), s - 1));
        double v = getValues().get(i).value().doubleValue();

        return List.of(new ChartLegendString(getDescription(), v, getColor()));
    }

    @Override
    public List<Line> getDrawables(DrawingContext context) {
        if (getValues() == null) {
            return Collections.emptyList();
        }

        List<Line> lines = new LinkedList<>();

        Number last = null;

        var it = getValues().listIterator(getStartIndex(context));
        while (it.nextIndex() < getEndIndex(context)) {
            int i = it.nextIndex();
            Number current = it.next().value();

            if (last == null) {
                last = current;
            }

            if (!Double.isNaN(last.doubleValue()) && !Double.isNaN(current.doubleValue())) {
                lines.add(new Line(i - 1, last.doubleValue(), i, current.doubleValue(),
                        getColor(), 1.25));
            }

            last = current;
        }

        return lines;
    }

    private int getStartIndex(DrawingContext c) {
        return (int) Math.min(Math.max(c.getViewport().startX(), 0), getValues().size());
    }

    private int getEndIndex(DrawingContext c) {
        return (int) Math.min(c.getViewport().endX() + 2, getValues().size());
    }
}
