package charting.gui.drawings;

import charting.indicators.Sma;
import charting.timeline.Timeline;
import charting.util.Range;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class SmaChart extends DelegateLegendDrawing implements TimelineDrawing {
    private final LineChart line = new LineChart();

    private final IntegerProperty length = new SimpleIntegerProperty(10);
    private final ObjectProperty<Timeline<? extends Number>> base = new SimpleObjectProperty<>();

    public SmaChart(Timeline<? extends Number> base, int length) {
        this();
        setBase(base);
        setLength(length);
    }

    public SmaChart(Timeline<? extends Number> base) {
        this();
        setBase(base);
    }

    public SmaChart() {
        line.setDescription("SMA: ");
        line.setColor(Color.LIGHTGOLDENRODYELLOW);

        setDelegates(line);

        base.subscribe(this::updateSma);
        length.subscribe(this::updateSma);
    }

    private void updateSma() {
        if (getBase() == null) {
            line.setValues(null);
        } else {
            line.setValues(new Sma(getBase(), getLength()));
        }
    }

    public Timeline<? extends Number> getBase() {
        return base.get();
    }

    public ObjectProperty<Timeline<? extends Number>> baseProperty() {
        return base;
    }

    public void setBase(Timeline<? extends Number> base) {
        this.base.set(base);
    }

    public Color getColor() {
        return line.getColor();
    }

    public ObjectProperty<Color> colorProperty() {
        return line.colorProperty();
    }

    public void setColor(Color color) {
        line.setColor(color);
    }

    public int getLength() {
        return length.get();
    }

    public IntegerProperty lengthProperty() {
        return length;
    }

    public void setLength(int length) {
        this.length.set(length);
    }

    @Override
    public Range getYDrawingRange(double startX, double endX) {
        return line.getYDrawingRange(startX, endX);
    }
}
