package charting.gui.chart;

import java.util.List;

/**
 * A drawing supplies a list of {@link Drawable Drawables} which will be drawn by the {@link Chart}.
 */
public interface Drawing {
    /**
     * Provides the list of {@link Drawable drawables} for this {@link Drawing}.
     * {@link Drawable} coordinates are based on the viewport.
     */
    List<? extends Drawable> getDrawables(DrawingContext drawingContext);
}
