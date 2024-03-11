package charting.gui.chart;

import charting.util.Range2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

class ChartCanvas extends Canvas {
    private final ObjectProperty<Range2D> viewport = new SimpleObjectProperty<>(new Range2D(0, 0, 100, 100));

    ChartCanvas() {
        getStyleClass().add("chart-canvas");
    }

    void update(List<Drawing> drawings) {
        Range2D v = viewport.get() == null ? Range2D.NAN : viewport.get();
        DrawingContext drawingContext = new DrawingContext(v, getWidth(), getHeight());

        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

        drawings.forEach(drawing -> {
            if (drawing != null) {
                drawing.getDrawables(drawingContext).forEach(d -> draw(drawingContext, d));
            }
        });
    }

    private void draw(DrawingContext c, Drawable drawable) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.save();

        if (drawable instanceof Rect r) {
            drawRect(gc, c, r);
        } else if (drawable instanceof Line l) {
            drawLine(gc, c, l);
        } else if (drawable instanceof Text t) {
            drawText(gc, c, t);
        } else if (drawable instanceof Polygon p) {
            drawPolygon(gc, c, p);
        }

        gc.restore();
    }

    private void drawRect(GraphicsContext gc, DrawingContext c, Rect r) {
        double x1 = c.toCanvasX(r.getX());
        double y1 = c.toCanvasY(r.getY());
        double x2 = x1 + c.toCanvasWidth(r.getWidth());
        double y2 = y1 + c.toCanvasHeight(r.getHeight());

        if (r.isStroked()) {
            gc.setStroke(r.getPaint());
            gc.strokeRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        } else {
            gc.setFill(r.getPaint());
            gc.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        }
    }

    private void drawLine(GraphicsContext gc, DrawingContext c, Line l) {
        gc.setStroke(l.getPaint());
        gc.setLineWidth(l.getWidth());
        gc.setLineDashes(l.getLineDashes());
        gc.setLineDashOffset(l.getLineDashesOffset());
        gc.setLineCap(l.getLineCap());

        double offs = l.getWidth() < 1 || Math.round(l.getWidth()) % 2 == 1 ? 0.5 : 0;
        gc.strokeLine(Math.round(c.toCanvasX(l.getX1())) + offs,
                Math.round(c.toCanvasY(l.getY1())) + offs,
                Math.round(c.toCanvasX(l.getX2())) + offs,
                Math.round(c.toCanvasY(l.getY2())) + offs);
    }

    private void drawText(GraphicsContext gc, DrawingContext c, Text t) {
        gc.setTextBaseline(t.getBaseline());
        gc.setTextAlign(t.getAlignment());
        gc.setFont(t.getFont());

        if (t.isStroked()) {
            gc.setStroke(t.getPaint());
            gc.strokeText(t.getText(), c.toCanvasX(t.getX()), c.toCanvasY(t.getY()));
        } else {
            gc.setFill(t.getPaint());
            gc.fillText(t.getText(), c.toCanvasX(t.getX()), c.toCanvasY(t.getY()));
        }
    }

    private void drawPolygon(GraphicsContext gc, DrawingContext c, Polygon p) {
        double[] xs = new double[p.getXs() == null ? 0 : p.getXs().length];
        double[] ys = new double[p.getYs() == null ? 0 : p.getYs().length];

        for (int i = 0; i < xs.length; i++) {
            xs[i] = c.toCanvasX(p.getXs()[i]);
        }
        for (int i = 0; i < ys.length; i++) {
            ys[i] = c.toCanvasY(p.getYs()[i]);
        }

        if (p.isStroked()) {
            gc.setStroke(p.getPaint());
            gc.strokePolygon(xs, ys, Math.min(xs.length, ys.length));
        } else {
            gc.setFill(p.getPaint());
            gc.fillPolygon(xs, ys, Math.min(xs.length, ys.length));
        }
    }

    ObjectProperty<Range2D> viewportProperty() {
        return viewport;
    }
}
