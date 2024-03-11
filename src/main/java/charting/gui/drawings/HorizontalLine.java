package charting.gui.drawings;

import charting.gui.chart.Drawable;
import charting.gui.chart.Drawing;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.Line;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

import java.util.Collections;
import java.util.List;

public class HorizontalLine implements Drawing {
    private final DoubleProperty y = new SimpleDoubleProperty(Double.NaN);

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);
    private final BooleanProperty dashed = new SimpleBooleanProperty();

    public HorizontalLine() {
    }

    public HorizontalLine(double y) {
        setY(y);
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
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

    public boolean isDashed() {
        return dashed.get();
    }

    public BooleanProperty dashedProperty() {
        return dashed;
    }

    public void setDashed(boolean dashed) {
        this.dashed.set(dashed);
    }

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (Double.isNaN(getY())) {
            return Collections.emptyList();
        }

        Line line = new Line(context.getViewport().startX(), getY(),
                context.getViewport().endX(), getY(), getColor());

        if (isDashed()) {
            line.setLineDashes(new double[]{1, 2});
            line.setLineDashesOffset(0.5);
            line.setLineCap(StrokeLineCap.BUTT);
        }

        return List.of(line);
    }
}
