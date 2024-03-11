package charting.gui.chart;

import charting.gui.drawings.NumberFormatUtil;
import javafx.scene.paint.Color;

import java.math.RoundingMode;
import java.util.Objects;

/**
 * A legend string consists of a description string and a colored value string.
 */
public final class ChartLegendString {
    private String description;
    private String value;
    private Color color;

    public ChartLegendString(String description, String value, Color color) {
        this.description = description;
        this.value = value;
        this.color = color;
    }

    public ChartLegendString(String description, double value, Color color) {
        this(description, value, 2, color);
    }

    public ChartLegendString(String description, double value, int precision, Color color) {
        this(description, value, precision, RoundingMode.HALF_UP, color);
    }

    public ChartLegendString(String description, double value, int precision, RoundingMode roundingMode, Color color) {
        this(description, NumberFormatUtil.format(value, precision, roundingMode), color);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ChartLegendString o)) {
            return false;
        }

        return description.equals(o.description) && value.equals(o.value) && color.equals(o.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, value, color);
    }
}
