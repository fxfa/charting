package charting.gui.chart;

import charting.util.Range2D;

/**
 * Contains information about a {@link Chart}s viewport as well as its actual width and height.
 */
public final class DrawingContext {
    private final Range2D viewport;
    private final Range2D canvasBounds;

    public DrawingContext(Range2D viewport, double canvasWidth, double canvasHeight) {
        this.viewport = viewport;
        this.canvasBounds = new Range2D(0, 0, canvasWidth, canvasHeight);
    }

    public Range2D getViewport() {
        return viewport;
    }

    public double getCanvasWidth() {
        return canvasBounds.getWidth();
    }

    public double getCanvasHeight() {
        return canvasBounds.getHeight();
    }

    public double toViewportX(double canvasX) {
        return canvasBounds.toTargetX(canvasX, viewport);
    }

    public double toViewportY(double canvasY) {
        return canvasBounds.toTargetY(canvasBounds.getHeight() - canvasY, viewport);
    }

    public double toCanvasX(double viewportX) {
        return viewport.toTargetX(viewportX, canvasBounds);
    }

    public double toCanvasY(double viewportY) {
        return canvasBounds.getHeight() - viewport.toTargetY(viewportY, canvasBounds);
    }

    public double toCanvasWidth(double viewportW) {
        return viewport.toTargetWidth(viewportW, canvasBounds);
    }

    public double toCanvasHeight(double viewportH) {
        return -viewport.toTargetHeight(viewportH, canvasBounds);
    }

    public double toViewportWidth(double canvasW) {
        return canvasBounds.toTargetWidth(canvasW, getViewport());
    }

    public double toViewportHeight(double canvasH) {
        return -canvasBounds.toTargetHeight(canvasH, getViewport());
    }
}
