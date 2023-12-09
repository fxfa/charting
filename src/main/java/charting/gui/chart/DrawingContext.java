package charting.gui.chart;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 * Contains information about a {@link Chart}s viewport as well as its actual width and height.
 */
public final class DrawingContext {
    private final Bounds viewport;
    private final Bounds canvasBounds;

    public DrawingContext(Bounds viewport, double canvasWidth, double canvasHeight) {
        this.viewport = viewport;
        this.canvasBounds = new BoundingBox(0, 0, canvasWidth, canvasHeight);
    }

    public Bounds getViewport() {
        return viewport;
    }

    public double getCanvasWidth() {
        return canvasBounds.getWidth();
    }

    public double getCanvasHeight() {
        return canvasBounds.getHeight();
    }

    public double toViewportX(double canvasX) {
        return ViewportUtil.toTargetX(canvasBounds, canvasX, viewport);
    }

    public double toViewportY(double canvasY) {
        return ViewportUtil.toTargetY(canvasBounds, canvasBounds.getHeight() - canvasY, viewport);
    }

    public double toCanvasX(double viewportX) {
        return ViewportUtil.toTargetX(viewport, viewportX, canvasBounds);
    }

    public double toCanvasY(double viewportY) {
        return canvasBounds.getHeight() - ViewportUtil.toTargetY(viewport, viewportY, canvasBounds);
    }

    public double toCanvasWidth(double viewportW) {
        return ViewportUtil.toTargetWidth(getViewport(), viewportW, canvasBounds);
    }

    public double toCanvasHeight(double viewportH) {
        return -ViewportUtil.toTargetHeight(getViewport(), viewportH, canvasBounds);
    }

    public double toViewportWidth(double canvasW) {
        return ViewportUtil.toTargetWidth(canvasBounds, canvasW, getViewport());
    }

    public double toViewportHeight(double canvasH) {
        return -ViewportUtil.toTargetHeight(canvasBounds, canvasH, getViewport());
    }
}
