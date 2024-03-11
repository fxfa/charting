package charting.gui.drawings;

import charting.gui.chart.LegendDrawing;
import charting.timeline.Timeline;
import charting.util.Preconditions;
import charting.util.Range;

import java.util.function.ToDoubleBiFunction;

public interface TimelineDrawing extends LegendDrawing {
    Range getYDrawingRange(double startX, double endX);

    default Range getYDrawingRange(Range rangeX) {
        return getYDrawingRange(rangeX.start(), rangeX.end());
    }

    static <T> Range calculateYDrawingRange(double startX, double endX, Timeline<? extends T> timeline,
                                             ToDoubleBiFunction<? super Double, ? super T> minAccumulator,
                                             ToDoubleBiFunction<? super Double, ? super T> maxAccumulator) {
        Preconditions.checkArgument(startX <= endX);

        double minY = Double.NaN;
        double maxY = Double.NaN;

        if (timeline != null) {
            startX = Math.min(Math.max(0, startX), timeline.size());

            var it = timeline.listIterator((int) startX);
            while (it.hasNext() && it.nextIndex() < endX) {
                T t = it.next().value();
                minY = minAccumulator.applyAsDouble(minY, t);
                maxY = maxAccumulator.applyAsDouble(maxY, t);
            }
        }

        return new Range(minY, maxY);
    }
}
