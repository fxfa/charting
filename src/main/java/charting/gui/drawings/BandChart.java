package charting.gui.drawings;

import charting.gui.chart.ChartLegendString;
import charting.gui.chart.Drawable;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.Polygon;
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
import java.util.List;

public class BandChart implements TimelineDrawing {
    private final ObjectProperty<Timeline<Range>> values = new SimpleObjectProperty<>();

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);

    private final StringProperty description = new SimpleStringProperty("");

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public Timeline<Range> getValues() {
        return values.get();
    }

    public ObjectProperty<Timeline<Range>> valuesProperty() {
        return values;
    }

    public void setValues(Timeline<Range> values) {
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
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (getValues() == null) {
            return Collections.emptyList();
        }

        int from = getStartIndex(context);
        int to = getEndIndex(context);

        int points = (to - from) * 2;
        double[] xs = new double[points];
        double[] ys = new double[points];

        var it = getValues().listIterator(from);
        while (it.nextIndex() < to) {
            int i = it.nextIndex();
            Range r = it.next().value();

            int i1 = i - from;
            int i2 = points - 1 - (i - from);

            xs[i1] = i;
            xs[i2] = i;
            ys[i1] = r.start();
            ys[i2] = r.end();
        }

        return List.of(new Polygon(xs, ys, getColor()));
    }

    private int getStartIndex(DrawingContext c) {
        return (int) Math.min(Math.max(c.getViewport().startX(), 0), getValues().size());
    }

    private int getEndIndex(DrawingContext c) {
        return (int) Math.min(c.getViewport().endX() + 2, getValues().size());
    }

    @Override
    public List<ChartLegendString> getLegend(double x) {
        int s = getValues() == null ? 0 : getValues().size();

        if (s == 0) {
            return Collections.emptyList();
        }

        int i = (int) (Double.isNaN(x) ? s - 1 : Math.min(Math.max(0, Math.round(x)), s - 1));
        Range r = getValues().get(i).value();

        String v = "{" + NumberFormatUtil.format(r.start()) + ", " + NumberFormatUtil.format(r.end()) + "}";
        return List.of(new ChartLegendString(getDescription(), v, getColor()));
    }

    @Override
    public Range getYDrawingRange(double startX, double endX) {
        Preconditions.checkArgument(startX <= endX);

        double minY = Double.NaN;
        double maxY = Double.NaN;

        if (getValues() != null) {
            startX = Math.min(Math.max(0, startX), getValues().size());

            var it = getValues().listIterator((int) startX);
            while (it.hasNext() && it.nextIndex() < endX) {
                Range r = it.next().value();
                minY = MathUtil.getMinIgnoreNan(minY, r.start(), r.end());
                maxY = MathUtil.getMaxIgnoreNan(maxY, r.start(), r.end());
            }
        }

        return new Range(minY, maxY);
    }
}
