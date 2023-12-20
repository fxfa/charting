package charting.gui.drawings;

import charting.gui.chart.ChartLegendString;
import charting.gui.chart.Drawable;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.LegendDrawing;
import charting.indicators.Sma;
import charting.timeline.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.util.List;

public class SmaChart implements LegendDrawing {
    private final IntegerProperty length = new SimpleIntegerProperty(10);

    private final LineChart delegate = new LineChart();

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
        delegate.setDescription("SMA: ");
        delegate.setColor(Color.LIGHTGOLDENRODYELLOW);

        baseProperty().addListener((obs, oldVal, newVal) -> updateSma());
        length.addListener((obs, oldVal, newVal) -> updateSma());
    }

    private void updateSma() {
        if (getBase() == null) {
            delegate.setValues(null);
        } else {
            delegate.setValues(new Sma(getBase(), getLength()));
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
        return delegate.getColor();
    }

    public ObjectProperty<Color> colorProperty() {
        return delegate.colorProperty();
    }

    public void setColor(Color color) {
        delegate.setColor(color);
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
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        return delegate.getDrawables(context);
    }

    @Override
    public List<ChartLegendString> getLegend(double x) {
        return delegate.getLegend(x);
    }
}
