package charting.gui.superchart;

import charting.gui.chart.Drawable;
import charting.gui.chart.Drawing;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.Text;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
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

    private final javafx.scene.text.Text measureText = new javafx.scene.text.Text();

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

        Text shortText = new Text(getShortName(), drawingContext.getViewport().getCenterX(),
                drawingContext.getViewport().getCenterY(), getColor(), SHORT_NAME_FONT);
        Text longText = new Text(getLongName(), drawingContext.getViewport().getCenterX(),
                drawingContext.getViewport().getCenterY(), getColor(), LONG_NAME_FONT);

        if (shortNameEmpty) {
            return List.of(longText);
        }

        if (longNameEmpty) {
            return List.of(shortText);
        }

        shortText.setBaseline(VPos.TOP);
        longText.setBaseline(VPos.TOP);

        Bounds shortBounds = getTextBounds(shortText);
        Bounds longBounds = getTextBounds(longText);

        double y = drawingContext.getCanvasHeight() / 2 - (shortBounds.getHeight() + longBounds.getHeight()) / 2;

        shortText.setY(drawingContext.toViewportY(y));
        longText.setY(drawingContext.toViewportY(y + shortBounds.getHeight()));

        return List.of(shortText, longText);
    }

    private Bounds getTextBounds(Text text) {
        measureText.setText(text.getText());
        measureText.setFont(text.getFont());
        measureText.setTextOrigin(text.getBaseline());
        measureText.setTextAlignment(text.getAlignment());
        return measureText.getLayoutBounds();
    }
}
