package charting.gui.chart;

import javafx.scene.paint.Paint;

public final class Polygon implements Drawable {
    private double[] xs;
    private double[] ys;
    private Paint paint;
    private boolean stroked;

    public Polygon(double[] xs, double[] ys, Paint paint, boolean stroked) {
        this.xs = xs;
        this.ys = ys;
        this.paint = paint;
        this.stroked = stroked;
    }

    public Polygon(double[] xs, double[] ys, Paint paint) {
        this(xs, ys, paint, false);
    }

    public double[] getXs() {
        return xs;
    }

    public void setXs(double[] xs) {
        this.xs = xs;
    }

    public double[] getYs() {
        return ys;
    }

    public void setYs(double[] ys) {
        this.ys = ys;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean isStroked() {
        return stroked;
    }

    public void setStroked(boolean stroked) {
        this.stroked = stroked;
    }
}
