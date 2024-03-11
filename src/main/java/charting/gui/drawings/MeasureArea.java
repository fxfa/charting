package charting.gui.drawings;

import charting.gui.chart.Line;
import charting.gui.chart.*;
import charting.util.MathUtil;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeasureArea implements Drawing {
    private final static Font FONT = new Font(16);

    private final DoubleProperty startX = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty startY = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty endX = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty endY = new SimpleDoubleProperty(Double.NaN);

    private final ObjectProperty<Color> bullishColor = new SimpleObjectProperty<>(Color.GREEN);
    private final ObjectProperty<Color> bearishColor = new SimpleObjectProperty<>(Color.RED);

    public MeasureArea() {
    }

    public MeasureArea(double startX, double startY, double endX, double endY) {
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

    public Color getBullishColor() {
        return bullishColor.get();
    }

    public ObjectProperty<Color> bullishColorProperty() {
        return bullishColor;
    }

    public void setBullishColor(Color bullishColor) {
        this.bullishColor.set(bullishColor);
    }

    public Color getBearishColor() {
        return bearishColor.get();
    }

    public ObjectProperty<Color> bearishColorProperty() {
        return bearishColor;
    }

    public void setBearishColor(Color bearishColor) {
        this.bearishColor.set(bearishColor);
    }

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (Double.isNaN(getStartX()) || Double.isNaN(getStartY()) ||
                Double.isNaN(getEndX()) || Double.isNaN(getEndY())) {
            return Collections.emptyList();
        }

        List<Drawable> drawables = new ArrayList<>();
        drawables.addAll(getMeasureAreaDrawables(context));
        drawables.addAll(getTextBoxDrawables(context));

        return drawables;
    }

    private List<? extends Drawable> getMeasureAreaDrawables(DrawingContext context) {
        double w = getEndX() - getStartX();
        double h = getEndY() - getStartY();
        double hLineY = getStartY() + h / 2;
        double vLineX = getStartX() + w / 2;
        Color c = getColor();

        Rect r = new Rect(getStartX(), getStartY(), w, h, getTransparentColor());
        Line l1 = new Line(getStartX(), hLineY, getEndX(), hLineY, c, 1);
        Line l2 = new Line(vLineX, getStartY(), vLineX, getEndY(), c, 1);

        List<Drawable> drawables = new ArrayList<>(List.of(r, l1, l2));

        if (Math.abs(context.toCanvasWidth(w)) >= 40 && Math.abs(context.toCanvasHeight(h)) >= 40) {
            double arrowLineW = context.toViewportWidth(10);
            double arrowLineH = context.toViewportHeight(10);
            boolean hArrowOnRightSide = getStartX() <= getEndX();
            double hArrowLineX2 = getEndX() + (hArrowOnRightSide ? -arrowLineW : arrowLineW);
            boolean vArrowOnTopSide = getStartY() <= getEndY();
            double vArrowLineY2 = getEndY() + (vArrowOnTopSide ? arrowLineH : -arrowLineH);

            Line horizontalArrowLine1 = new Line(getEndX(), hLineY,
                    hArrowLineX2, l1.getY1() - arrowLineH, c, 1);
            Line horizontalArrowLine2 = new Line(getEndX(), hLineY,
                    hArrowLineX2, l1.getY1() + arrowLineH, c, 1);
            Line verticalArrowLine1 = new Line(vLineX, getEndY(),
                    l2.getX1() + arrowLineW, vArrowLineY2, c, 1);
            Line verticalArrowLine2 = new Line(vLineX, getEndY(),
                    l2.getX1() - arrowLineW, vArrowLineY2, c, 1);

            drawables.addAll(List.of(horizontalArrowLine1, horizontalArrowLine2,
                    verticalArrowLine1, verticalArrowLine2));
        }

        return drawables;
    }

    private List<? extends Drawable> getTextBoxDrawables(DrawingContext context) {
        String text = getText();
        Bounds m = measure(text);
        boolean aboveMeasureArea = getStartY() <= getEndY();
        double xPadding = context.toViewportWidth(10);
        double yPadding = context.toViewportHeight(-10);
        double textX = getStartX() + (getEndX() - getStartX()) / 2;
        double textY = getEndY() + 2 * (aboveMeasureArea ? yPadding : -yPadding);
        double boxX = textX - context.toViewportWidth(m.getWidth() / 2) - xPadding;
        double boxY = getEndY() + (aboveMeasureArea ? yPadding : -yPadding);
        double boxW = context.toViewportWidth(m.getWidth()) + xPadding * 2;
        double boxH = (context.toViewportHeight(-m.getHeight()) + 2 * yPadding) * (aboveMeasureArea ? 1 : -1);
        Color c = getColor();

        Text t = new Text(text, textX, textY, c, FONT);
        t.setAlignment(TextAlignment.CENTER);
        t.setBaseline(aboveMeasureArea ? VPos.BOTTOM : VPos.TOP);
        Rect b = new Rect(boxX, boxY, boxW, boxH, Color.WHITE);

        return List.of(b, t);
    }

    private String getText() {
        double valueDif = (getEndY() - getStartY());
        double valuePctDif = (getEndY() - getStartY()) / Math.abs(getStartY()) * 100;
        double barDif = getEndX() - getStartX();
        return """
                %.2f (%.2f%%)
                %d bars""".formatted(valueDif, valuePctDif, (int) barDif);
    }

    private Bounds measure(String text) {
        javafx.scene.text.Text measureText = new javafx.scene.text.Text(text);
        measureText.setFont(FONT);
        measureText.setTextAlignment(TextAlignment.CENTER);
        measureText.setTextOrigin(VPos.BOTTOM);
        return measureText.getLayoutBounds();
    }

    private Color getColor() {
        return getStartY() <= getEndY() ? getBullishColor() : getBearishColor();
    }

    private Color getTransparentColor() {
        return getColor().deriveColor(
                0, 1, 1, 0.25);
    }
}
