package charting.gui.chart;

/**
 * A drawing which will be notified about mouse events.
 */
public interface MouseEventDrawing extends Drawing {
    void onMousePositionChange(double viewportX, double viewportY, double canvasX, double canvasY);

    void onMouseButtonEvent(MouseButtonEvent event);
}
