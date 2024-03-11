package charting.gui.superchart;

import charting.gui.chart.*;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

public class MouseLine implements MouseEventDrawing {
    private final static double[] LINE_DASHES = {6};

    private final BooleanProperty horizontal = new SimpleBooleanProperty();
    private final DoubleProperty pos = new SimpleDoubleProperty(Double.NaN);
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GRAY);

    public MouseLine() {
    }

    public MouseLine(boolean horizontal) {
        setHorizontal(horizontal);
    }

    public boolean isHorizontal() {
        return horizontal.get();
    }

    public BooleanProperty horizontalProperty() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal.set(horizontal);
    }

    public double getPos() {
        return pos.get();
    }

    public DoubleProperty posProperty() {
        return pos;
    }

    public void setPos(double pos) {
        this.pos.set(pos);
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
    public List<? extends Drawable> getDrawables(DrawingContext drawingContext) {
        if (Double.isNaN(getPos())) {
            return Collections.emptyList();
        }


        Line l;

        if (isHorizontal()) {
            double p = getPos();
            l = new Line(drawingContext.getViewport().startX(), p,
                    drawingContext.getViewport().endX(), p, getColor(), 1.3);
        } else {
            double p = Math.round(getPos());
            l = new Line(p, drawingContext.getViewport().startY(), p,
                    drawingContext.getViewport().endY(), getColor(), 1.3);
        }

        l.setLineDashes(LINE_DASHES);

        return List.of(l);
    }

    @Override
    public void onMousePositionChange(double viewportX, double viewportY, double canvasX, double canvasY) {
         if (!pos.isBound()) {
            if (isHorizontal()) {
                setPos(viewportY);
            } else {
                setPos(viewportX);
            }
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent event) {
    }
}
