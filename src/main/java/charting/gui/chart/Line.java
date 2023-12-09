package charting.gui.chart;

import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;

public final class Line implements Drawable {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private Paint paint;
    private double width;
    private double[] lineDashes;
    private double lineDashesOffset;
    private StrokeLineCap lineCap = StrokeLineCap.ROUND;

    public Line(double x1, double y1, double x2, double y2, Paint paint, double width) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.paint = paint;
        this.width = width;
    }

    public Line(double x1, double y1, double x2, double y2, Paint paint) {
        this(x1, y1, x2, y2, paint, 1);
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double[] getLineDashes() {
        return lineDashes;
    }

    public void setLineDashes(double[] lineDashes) {
        this.lineDashes = lineDashes;
    }

    public double getLineDashesOffset() {
        return lineDashesOffset;
    }

    public void setLineDashesOffset(double lineDashesOffset) {
        this.lineDashesOffset = lineDashesOffset;
    }

    public StrokeLineCap getLineCap() {
        return lineCap;
    }

    public void setLineCap(StrokeLineCap lineCap) {
        this.lineCap = lineCap;
    }
}
