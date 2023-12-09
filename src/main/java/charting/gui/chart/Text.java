package charting.gui.chart;

import javafx.geometry.VPos;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public final class Text implements Drawable {
    private String text;
    private double x;
    private double y;
    private Paint paint;
    private Font font;
    private boolean stroked;
    private VPos baseline = VPos.CENTER;
    private TextAlignment alignment = TextAlignment.CENTER;

    public Text(String text, double x, double y, Paint paint, Font font, boolean stroked) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.paint = paint;
        this.font = font;
        this.stroked = stroked;
    }

    public Text(String text, double x, double y, Paint paint, Font font) {
        this(text, x, y, paint, font, false);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public boolean isStroked() {
        return stroked;
    }

    public void setStroked(boolean stroked) {
        this.stroked = stroked;
    }

    public VPos getBaseline() {
        return baseline;
    }

    public void setBaseline(VPos baseline) {
        this.baseline = baseline;
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
    }
}
