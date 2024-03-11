package charting.gui.drawings;

import charting.data.Candle;
import charting.gui.chart.Line;
import charting.gui.chart.*;
import charting.timeline.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CandleChart implements LegendDrawing {
    private final static double BAR_WIDTH = 0.8;

    private final ObjectProperty<Timeline<? extends Candle>> candleTimeline = new SimpleObjectProperty<>();

    private final ObjectProperty<Color> bullishColor = new SimpleObjectProperty<>(Color.GREEN);
    private final ObjectProperty<Color> bearishColor = new SimpleObjectProperty<>(Color.RED);

    private final HorizontalLine priceLine = new HorizontalLine();

    public CandleChart() {
        priceLine.setDashed(true);
    }

    public CandleChart(Timeline<? extends Candle> candleTimeline) {
        this();
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

        double open = Math.round(c.getOpen().doubleValue() * 100) / 100d;
        double high = Math.round(c.getHigh().doubleValue() * 100) / 100d;
        double low = Math.round(c.getLow().doubleValue() * 100) / 100d;
        double close = Math.round(c.getClose().doubleValue() * 100) / 100d;

        Color color = getColor(c);

        List<ChartLegendString> strings = new ArrayList<>();
        strings.add(new ChartLegendString("O: ", toPlainString(open), color));
        strings.add(new ChartLegendString("   H: ", toPlainString(high), color));
        strings.add(new ChartLegendString("   L: ", toPlainString(low), color));
        strings.add(new ChartLegendString("   C: ", toPlainString(close), color));
        strings.add(new ChartLegendString("    Δ: ",
                (getAbsDiff(i) + " " + getPctDiff(i)).stripTrailing(), color));

        return strings;
    }

    private String getAbsDiff(int i) {
        if (i > 0) {
            double close = getCandleTimeline().get(i).value().getClose().doubleValue();
            double previousClose = getCandleTimeline().get(i - 1).value().getClose().doubleValue();
            return toPlainString(Math.round((close - previousClose) * 100) / 100d);
        }

        return "";
    }

    private String getPctDiff(int i) {
        if (i > 0) {
            double close = getCandleTimeline().get(i).value().getClose().doubleValue();
            double previousClose = getCandleTimeline().get(i - 1).value().getClose().doubleValue();
            double pctDiff = Math.round((close - previousClose) / previousClose * 100 * 100) / 100d;
            return String.format("(%s%s%%)", pctDiff >= 0 ? "+" : "", toPlainString(pctDiff));
        }

        return "";
    }

    private String toPlainString(double v) {
        return BigDecimal.valueOf(v).stripTrailingZeros().toPlainString();
    }

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext context) {
        if (getCandleTimeline() == null || getCandleTimeline().isEmpty()) {
            return Collections.emptyList();
        }

        List<Drawable> drawables = new LinkedList<>();

        var it = getCandleTimeline().listIterator(getStartIndex(context));
        while (it.nextIndex() < getEndIndex(context)) {
            int i = it.nextIndex();
            Candle c = it.next().value();

            drawables.add(getBody(c, i));
            drawables.add(getShadow(c, i));
        }

        adjustPriceLine();
        drawables.addAll(priceLine.getDrawables(context));

        return drawables;
    }

    private int getStartIndex(DrawingContext c) {
        return (int) Math.min(Math.max(c.getViewport().startX(), 0), getCandleTimeline().size());
    }

    private int getEndIndex(DrawingContext c) {
        return (int) Math.min(c.getViewport().endX() + 2, getCandleTimeline().size());
    }

    private Drawable getBody(Candle c, int i) {
        double open = c.getOpen().doubleValue();
        double close = c.getClose().doubleValue();

        double bodyX = i - 0.5 + (1 - BAR_WIDTH) / 2;

        if (open == close) {
            return new Line(bodyX, open, bodyX + BAR_WIDTH, open, getColor(c), 1);
        }

        double top = Math.max(open, close);
        double bottom = Math.min(open, close);
        return new Rect(bodyX, bottom, BAR_WIDTH, top - bottom, getColor(c));
    }

    private Line getShadow(Candle c, int i) {
        double bodyX = i - 0.5 + (1 - BAR_WIDTH) / 2;
        double shadowX = bodyX + BAR_WIDTH / 2;
        return new Line(shadowX, c.getHigh().doubleValue(), shadowX, c.getLow().doubleValue(), getColor(c));
    }

    private void adjustPriceLine() {
        Candle c = getCandleTimeline().last().value();
        priceLine.setColor(getColor(c));
        priceLine.setY(c.getClose().doubleValue());
    }

    private Color getColor(Candle c) {
        return c.getOpen().doubleValue() < c.getClose().doubleValue() ? getBullishColor() : getBearishColor();
    }
}