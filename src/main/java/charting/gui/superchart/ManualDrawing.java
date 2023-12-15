package charting.gui.superchart;

import charting.gui.chart.Drawing;
import charting.gui.chart.MouseEventDrawing;
import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 * Allows to manually draw an underlying {@link Drawing} by setting its coordinates via mouse events.
 */
public interface ManualDrawing extends MouseEventDrawing {
    int getRemainingClicks();

    /**
     * Gives information on how many clicks are left until all coordinates of the
     * underlying {@link Drawing} have been set.
     */
    ReadOnlyIntegerProperty remainingClicksProperty();

    default boolean isDone() {
        return getRemainingClicks() == 0;
    }

    Drawing getUnderlying();
}
