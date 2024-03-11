package charting.gui.superchart;

import charting.gui.util.NodeLoader;
import charting.util.Range;
import javafx.beans.value.ChangeListener;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.TreeMap;

public class ValueAxis extends SuperChartAxis {
    private final TreeMap<Double, Mark<Double>> marks = new TreeMap<>();

    private final static double GAP = 32;
    private double gap;

    private double mouseDownY = Double.NaN;
    private Range mouseDownAxis;

    private final Font font = new Font(15);

    public ValueAxis() {
        NodeLoader.loadExisting(this);

        ChangeListener<Object> redrawListener = (obs, oldVal, newVal) -> redraw();
        widthProperty().addListener(redrawListener);
        heightProperty().addListener(redrawListener);
        axisProperty().addListener(redrawListener);
        cursorMarkPositionProperty().addListener(redrawListener);

        addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        addEventHandler(ScrollEvent.SCROLL, this::onScroll);
    }

    private void onMousePressed(MouseEvent e) {
        e.consume();

        mouseDownY = e.getY();
        mouseDownAxis = getAxis();
    }

    private void onMouseDragged(MouseEvent e) {
        e.consume();

        if (mouseDownAxis == null) {
            return;
        }

        double p = (mouseDownY - e.getY()) / (getCanvas().getHeight() * 0.1);
        setAxis(mouseDownAxis.scaled(Math.pow(0.85, p)));
    }

    private void onScroll(ScrollEvent e) {
        e.consume();

        if (getAxis() == null) {
            return;
        }

        setAxis(getAxis().scaled(e.getDeltaY() > 0 ? 1 / 1.05 : e.getDeltaY() < 0 ? 1.05 : 1));
    }

    private double getScale() {
        return getAxis() == null ? Double.NaN : (getCanvas().getHeight() / -getAxis().getLength());
    }

    private double getTranslate() {
        return getAxis() == null ? Double.NaN : -getAxis().end();
    }

    private void redraw() {
        if (getCanvas().getHeight() == 0) {
            return;
        }

        gap = GAP / getScale();

        marks.clear();

        double start = -getTranslate();
        double end = getCanvas().getHeight() / getScale() - getTranslate();
        loadMarks(start, end);
        drawMarks(start, end);
    }

    private void loadMarks(double start, double end) {
        double interval = Math.abs(35 / getScale());

        int e = (int) Math.floor(Math.log10(interval));

        interval = interval / Math.pow(10, e);
        if (interval <= 1) {
            interval = 1;
        } else if (interval <= 2.5) {
            interval = 2.5;
        } else if (interval <= 5) {
            interval = 5;
        } else if (interval <= 7.5) {
            interval = 7.5;
        } else {
            interval = 10;
        }
        interval *= Math.pow(10, e);
        interval *= Math.signum(getScale());

        long i = (long) (start / interval);
        double pos = i * interval;
        do {
            Mark<Double> mark = getMark(pos);
            marks.put(mark.pos(), mark);

            i++;
            pos = i * interval;
        } while (getScale() > 0 && pos < end || getScale() < 0 && pos > end);
    }

    private Mark<Double> getMark(double d) {
        d = Math.floor(d * 100) / 100;
        return new Mark<>(d, String.valueOf(d), d);
    }

    private void drawMarks(double start, double end) {
        GraphicsContext gc = getCanvas().getGraphicsContext2D();
        gc.clearRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font(15));
        gc.setFill(getMarkColor());
        for (Mark<Double> m : marks.values()) {
            if (getScale() < 0 && m.pos() < end + gap || getScale() > 0 && m.pos() > end + gap) {
                break;
            }

            if (getScale() < 0 && m.pos() <= start - gap || getScale() > 0 && m.pos() >= start - gap) {
                gc.fillText(m.item().toString(), getCanvas().getWidth() / 2, (m.pos() + getTranslate()) * getScale());
            }
        }

        if (!Double.isNaN(getCursorMarkPosition())) {
            Mark<Double> mark = getMark(start + getCursorMarkPosition() / getScale());

            Text t = new Text(mark.item().toString());
            t.setFont(font);
            t.setTextAlignment(TextAlignment.CENTER);
            t.setTextOrigin(VPos.CENTER);
            double h = t.getLayoutBounds().getHeight();
            double padding = 10;

            gc.setFill(getCursorMarkBackgroundColor());
            gc.fillRoundRect(1, (mark.pos() + getTranslate()) * getScale() - h / 2 - padding,
                    getCanvas().getWidth() - 1, h + padding * 2, 12, 12);
            gc.setFill(getMarkColor());
            gc.fillText(mark.item().toString(), getCanvas().getWidth() / 2, (mark.pos() + getTranslate()) * getScale());
        }
    }
}
