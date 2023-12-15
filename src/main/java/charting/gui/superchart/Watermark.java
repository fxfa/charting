package charting.gui.superchart;

import charting.gui.chart.Drawable;
import charting.gui.chart.Drawing;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.Text;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Collections;
import java.util.List;

public class Watermark implements Drawing {
    private final static Font SHORT_NAME_FONT = new Font(140);
    private final static Font LONG_NAME_FONT = new Font(70);

    private final ObjectProperty<Color> color =
            new SimpleObjectProperty<>(new Color(0.5, 0.5, 0.5, 0.15));
    private final StringProperty shortName = new SimpleStringProperty();
    private final StringProperty longName = new SimpleStringProperty();

    public Watermark() {
    }

    public Watermark(String shortName, String longName) {
        setShortName(shortName);
        setLongName(longName);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public String getShortName() {
        return shortName.get();
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getLongName() {
        return longName.get();
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext drawingContext) {
        boolean shortNameEmpty = getShortName() == null || getShortName().isEmpty();
        boolean longNameEmpty = getLongName() == null || getLongName().isEmpty();

        if (shortNameEmpty && longNameEmpty) {
            return Collections.emptyList();
        }

        Text shortNameText = new Text(getShortName(), drawingContext.getViewport().getCenterX(),
                drawingContext.getViewport().getCenterY(), getColor(), SHORT_NAME_FONT);
        shortNameText.setBaseline(longNameEmpty ? VPos.CENTER : VPos.BOTTOM);
        Text longNameText = new Text(getLongName(), drawingContext.getViewport().getCenterX(),
                drawingContext.getViewport().getCenterY(), getColor(), LONG_NAME_FONT);
        longNameText.setBaseline(shortNameEmpty ? VPos.CENTER : VPos.TOP);

        return List.of(shortNameText, longNameText);
    }
}
