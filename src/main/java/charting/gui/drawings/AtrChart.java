package charting.gui.drawings;

import charting.data.Candle;
import charting.indicators.Atr;
import charting.timeline.Timeline;
import charting.util.Range;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class AtrChart extends DelegateLegendDrawing implements TimelineDrawing {
    private final LineChart line = new LineChart();

    private final ObjectProperty<Timeline<? extends Candle>> base = new SimpleObjectProperty<>();
    private final IntegerProperty length = new SimpleIntegerProperty(14);

    public AtrChart(Timeline<? extends Candle> base, int length) {
        this();
        setBase(base);
        setLength(length);
    }

    public AtrChart(Timeline<? extends Candle> base) {
        this();
        setBase(base);
    }

    public AtrChart() {
        line.setDescription("ATR: ");
        line.setColor(Color.CRIMSON);

        setDelegates(line);

        base.subscribe(this::updateAtr);
        length.subscribe(this::updateAtr);
    }

    private void updateAtr() {
        if (getBase() == null) {
            line.setValues(null);
        } else {
            line.setValues(new Atr(getBase(), getLength()));
        }
    }

    public Timeline<? extends Candle> getBase() {
        return base.get();
    }

    public ObjectProperty<Timeline<? extends Candle>> baseProperty() {
        return base;
    }

    public void setBase(Timeline<? extends Candle> base) {
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
