package charting.gui.superchart;

import charting.gui.chart.Line;
import charting.gui.chart.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

public class MouseCrosshairs implements MouseEventDrawing {
    private final static double[] LINE_DASHES = {6};

    private double x = Double.NaN;
    private double y = Double.NaN;

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GRAY);

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
    public List<? extends Drawable> getDrawables(DrawingContext drawingContext) {
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return Collections.emptyList();
        }

        charting.gui.chart.Line horizontalLine = new charting.gui.chart.Line(drawingContext.getViewport().getMinX(), y,
                drawingContext.getViewport().getMaxX(), y, getColor(), 1.3);
        horizontalLine.setLineDashes(LINE_DASHES);

        double x = Math.round(this.x);
        charting.gui.chart.Line verticalLine = new Line(x, drawingContext.getViewport().getMinY(), x,
                drawingContext.getViewport().getMaxY(), getColor(), 1.3);
        verticalLine.setLineDashes(LINE_DASHES);

        return List.of(horizontalLine, verticalLine);
    }

    @Override
    public void onMousePositionChange(double viewportX, double viewportY, double canvasX, double canvasY) {
        x = viewportX;
        y = viewportY;
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent event) {
    }
}
