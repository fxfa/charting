package charting.gui.superchart;

import charting.util.Range;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.List;

public abstract class SuperChartAxis extends Region {
    private static final StyleablePropertyFactory<SuperChartAxis> FACTORY =
            new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    private static final CssMetaData<SuperChartAxis, Color> MARK_COLOR =
            FACTORY.createColorCssMetaData("-fx-mark-color", s -> s.markColor, Color.WHITE);
    private static final CssMetaData<SuperChartAxis, Color> CURSOR_MARK_BACKGROUND_COLOR =
            FACTORY.createColorCssMetaData("-fx-cursor-mark-background-color",
                    s -> s.cursorMarkBackgroundColor, Color.GRAY);

    private final StyleableObjectProperty<Color> markColor =
            new SimpleStyleableObjectProperty<>(MARK_COLOR, this, "markColor",
                    MARK_COLOR.getInitialValue(this));
    private final StyleableObjectProperty<Color> cursorMarkBackgroundColor =
            new SimpleStyleableObjectProperty<>(CURSOR_MARK_BACKGROUND_COLOR, this,
                    "cursorMarkBackgroundColor", CURSOR_MARK_BACKGROUND_COLOR.getInitialValue(this));

    private final ObjectProperty<Range> axis = new SimpleObjectProperty<>(new Range(0, 100));

    private final DoubleProperty cursorMarkPosition = new SimpleDoubleProperty(Double.NaN);

    private final Canvas canvas = new Canvas();

    protected SuperChartAxis() {
        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
    }

    public Color getMarkColor() {
        return markColor.get();
    }

    public StyleableObjectProperty<Color> markColorProperty() {
        return markColor;
    }

    public void setMarkColor(Color markColor) {
        this.markColor.set(markColor);
    }

    public Color getCursorMarkBackgroundColor() {
        return cursorMarkBackgroundColor.get();
    }

    public StyleableObjectProperty<Color> cursorMarkBackgroundColorProperty() {
        return cursorMarkBackgroundColor;
    }

    public void setCursorMarkBackgroundColor(Color cursorMarkBackgroundColor) {
        this.cursorMarkBackgroundColor.set(cursorMarkBackgroundColor);
    }

    public Range getAxis() {
        return axis.get();
    }

    public ObjectProperty<Range> axisProperty() {
        return axis;
    }

    public void setAxis(Range axis) {
        this.axis.set(axis);
    }

    public double getCursorMarkPosition() {
        return cursorMarkPosition.get();
    }

    public DoubleProperty cursorMarkPositionProperty() {
        return cursorMarkPosition;
    }

    public void setCursorMarkPosition(double cursorMarkPosition) {
        this.cursorMarkPosition.set(cursorMarkPosition);
    }

    protected Canvas getCanvas() {
        return canvas;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    public record Mark<T>(T item, String string, double pos) {
    }
}
