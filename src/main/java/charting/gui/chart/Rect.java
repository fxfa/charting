package charting.gui.chart;

import javafx.scene.paint.Paint;

public final class Rect implements Drawable {
    private double x;
    private double y;
    private double width;
    private double height;
    private Paint paint;
    private boolean stroked;

    public Rect(double x, double y, double width, double height, Paint paint, boolean stroked) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.paint = paint;
        this.stroked = stroked;
    }

    public Rect(double x, double y, double width, double height, Paint paint) {
        this(x, y, width, height, paint, false);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
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
