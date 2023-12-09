package charting.gui.chart;

import java.util.List;

/**
 * A type of {@link Drawable} that has a legend entry.
 */
public interface LegendDrawing extends Drawing {
    List<ChartLegendString> getLegend(double x);
}
