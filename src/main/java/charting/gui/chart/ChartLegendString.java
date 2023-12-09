package charting.gui.chart;

import javafx.scene.paint.Color;

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
}
