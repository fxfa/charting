package charting.gui.drawings;

import charting.data.Candle;
import charting.gui.chart.*;
import charting.timeline.Timeline;
import charting.timeline.Timestamped;
import charting.util.MathUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class VolumeChart implements LegendDrawing {
    private final static double BAR_WIDTH = 0.8;
    private final static double HEIGHT_PERCENTAGE = 0.2;

    private final ObjectProperty<Timeline<? extends Candle>> candleTimeline = new SimpleObjectProperty<>();

    private final ObjectProperty<Color> bullishColor = new SimpleObjectProperty<>(Color.GREEN);
    private final ObjectProperty<Color> bearishColor = new SimpleObjectProperty<>(Color.RED);

    public VolumeChart() {
    }

    public VolumeChart(Timeline<? extends Candle> candleTimeline) {
        setCandleTimeline(candleTimeline);
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

    public Timeline<? extends Candle> getCandleTimeline() {
        return candleTimeline.get();
    }

    public ObjectProperty<Timeline<? extends Candle>> candleTimelineProperty() {
        return candleTimeline;
    }

    public void setCandleTimeline(Timeline<? extends Candle> candleTimeline) {
        this.candleTimeline.set(candleTimeline);
    }

    @Override
    public List<ChartLegendString> getLegend(double x) {
        int s = getCandleTimeline() == null ? 0 : getCandleTimeline().size();

        if (s == 0) {
            return Collections.emptyList();
        }

        int i = (int) (Double.isNaN(x) ? s - 1 : Math.min(Math.max(0, Math.round(x)), s - 1));
        Candle c = getCandleTimeline().get(i).value();

        double volume = c.getVolume().doubleValue();

        return List.of(new ChartLegendString("Vol: ", volume, getColor(c)));
    }

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (getCandleTimeline() == null) {
            return Collections.emptyList();
        }

        List<Rect> bars = new LinkedList<>();

        int s = getStartIndex(context);
        int e = getEndIndex(context);

        double highestVolume = getHighestVolume(s, e);
        double heightSection = context.getViewport().getHeight() * HEIGHT_PERCENTAGE;

        var it = getCandleTimeline().listIterator(s);
        while (it.nextIndex() < e) {
            Candle candle = it.next().value();
            double volume = candle.getVolume().doubleValue();
            double barX = it.previousIndex() - 0.5 + (1 - BAR_WIDTH) / 2;
            double height = heightSection * (volume / highestVolume);

            bars.add(new Rect(barX, context.getViewport().startY(), BAR_WIDTH, height, getBarColor(candle)));
        }

        return bars;
    }

    private int getStartIndex(DrawingContext c) {
        return (int) Math.min(Math.max(c.getViewport().startX(), 0), getCandleTimeline().size());
    }

    private int getEndIndex(DrawingContext c) {
        return (int) Math.min(c.getViewport().endX() + 2, getCandleTimeline().size());
    }

    private double getHighestVolume(int start, int end) {
        ListIterator<? extends Timestamped<? extends Candle>> it = getCandleTimeline().listIterator(start);

        double highestVolume = 0;

        while (it.hasNext() && it.nextIndex() <= end) {
            double v = it.next().value().getVolume().doubleValue();
            v = Double.isNaN(v) ? 0 : v;

            highestVolume = Math.max(highestVolume, v);
        }

        return highestVolume;
    }

    private Color getBarColor(Candle c) {
        return getColor(c).deriveColor(
                0, 1, 1, 0.5);
    }

    private Color getColor(Candle c) {
        return c.getOpen().doubleValue() <= c.getClose().doubleValue() ? getBullishColor() : getBearishColor();
    }
}