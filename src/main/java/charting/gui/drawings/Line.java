package charting.gui.drawings;

import charting.gui.chart.Drawable;
import charting.gui.chart.Drawing;
import charting.gui.chart.DrawingContext;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

public class Line implements Drawing {
    private final DoubleProperty startX = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty startY = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty endX = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty endY = new SimpleDoubleProperty(Double.NaN);

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);

    public Line() {
    }

    public Line(double startX, double startY, double endX, double endY) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
    }

    public double getStartX() {
        return startX.get();
    }

    public DoubleProperty startXProperty() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX.set(startX);
    }

    public double getStartY() {
        return startY.get();
    }

    public DoubleProperty startYProperty() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY.set(startY);
    }

    public double getEndX() {
        return endX.get();
    }

    public DoubleProperty endXProperty() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX.set(endX);
    }

    public double getEndY() {
        return endY.get();
    }

    public DoubleProperty endYProperty() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY.set(endY);
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

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (Double.isNaN(getStartX()) && Double.isNaN(getStartY()) &&
                Double.isNaN(getEndX()) && Double.isNaN(getEndY())) {
            return Collections.emptyList();
        }

        return List.of(new charting.gui.chart.Line(getStartX(), getStartY(), getEndX(), getEndY(), getColor()));
    }
}
