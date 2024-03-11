package charting.gui.chart;

import charting.gui.util.NodeDragDistance;
import charting.gui.util.NodeRenderingState;
import charting.util.Range2D;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ChartSkin extends SkinBase<Chart> {
    private final ChartCanvas chartCanvas = new ChartCanvas();
    private final ChartLegend chartLegend = new ChartLegend();

    private final AnimationTimer animationTimer = new AnimationTimer() {
        final static int UPDATE_DELAY_MILLIS = 10;

        long t;

        @Override
        public void handle(long timestamp) {
            // Limit the update rate to avoid lags for bigger charts.
            if ((timestamp - t) / 1000000 >= UPDATE_DELAY_MILLIS && getSkinnable() != null) {
                updateCanvas();
                updateLegend();
                t = timestamp;
            }
        }
    };

    private final NodeRenderingState renderingState;

    private final NodeDragDistance dragDistance;

    private double mouseX = Double.NaN;
    private double mouseY = Double.NaN;

    private final EventHandler<MouseEvent> mouseEventFilter = this::onMouseEvent;
    private final EventHandler<ScrollEvent> scrollEventFilter = this::onScroll;

    private final ChangeListener<Range2D> viewportMousePositionChangeListener =
            (obs, oldVal, newVal) -> onViewportMousePositionChange();

    public ChartSkin(Chart chart) {
        super(chart);

        chartCanvas.widthProperty().bind(chart.widthProperty());
        chartCanvas.heightProperty().bind(chart.heightProperty());
        chartCanvas.viewportProperty().bind(chart.viewportProperty());

        chartLegend.titleProperty().bind(chart.titleProperty());

        renderingState = new NodeRenderingState(chart);
        renderingState.renderingProperty().addListener((obs, oldVal, newVal) -> onRenderingStateChange(newVal));
        onRenderingStateChange(renderingState.isRendering());

        dragDistance = new NodeDragDistance(chart);
        dragDistance.dragDistanceXProperty().addListener((obs, oldVal, newVal) -> chart.shiftDrawings(
                toViewportWidth(newVal.doubleValue() - oldVal.doubleValue()), 0));
        dragDistance.dragDistanceYProperty().addListener((obs, oldVal, newVal) -> chart.shiftDrawings(
                0, toViewportHeight(newVal.doubleValue() - oldVal.doubleValue())));

        chart.addEventFilter(MouseEvent.ANY, mouseEventFilter);
        chart.addEventFilter(ScrollEvent.SCROLL, scrollEventFilter);

        chart.viewportProperty().addListener(viewportMousePositionChangeListener);

        getChildren().addAll(chartCanvas, chartLegend);
    }

    private void onRenderingStateChange(boolean rendering) {
        if (rendering && getSkinnable() != null) {
            animationTimer.start();
        } else {
            animationTimer.stop();
        }
    }

    private void onScroll(ScrollEvent e) {
        if (getSkinnable() == null) {
            return;
        }

        if (e.getDeltaY() != 0) {
            double f = e.getDeltaY() > 0 ?
                    getSkinnable().getZoomPerScrollTickX() : 1 / getSkinnable().getZoomPerScrollTickX();
            getSkinnable().zoomDrawings(f, 1, e.isControlDown() ?
                    toViewportX(e.getX()) : getSkinnable().getViewport().endX(), 0);
        }
        if (e.getDeltaX() != 0) {
            double f = e.getDeltaX() > 0 ?
                    getSkinnable().getZoomPerScrollTickY() : 1 / getSkinnable().getZoomPerScrollTickY();
            getSkinnable().zoomDrawings(1, f, 0, toViewportY(e.getY()));
        }
    }

    private void onMouseEvent(MouseEvent e) {
        double mx;
        double my;

        if (e.getEventType().equals(MouseEvent.MOUSE_EXITED) ||
                e.getEventType().equals(MouseEvent.MOUSE_EXITED_TARGET)) {
            mx = Double.NaN;
            my = Double.NaN;
        } else {
            mx = e.getX();
            my = e.getY();
        }

        if (!Double.valueOf(mx).equals(mouseX) || !Double.valueOf(my).equals(mouseY)) {
            mouseX = mx;
            mouseY = my;
            onViewportMousePositionChange();
        }

        if (e.getEventType().equals(MouseEvent.MOUSE_PRESSED) || e.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            onMouseButtonEvent(e);
        }
    }

    private void onViewportMousePositionChange() {
        if (getSkinnable() == null) {
            return;
        }

        double x = toViewportX(mouseX);
        double y = toViewportY(mouseY);

        if (!getSkinnable().legendXProperty().isBound()) {
            getSkinnable().setLegendX(x);
        }

        for (Drawing d : getSkinnable().getDrawings()) {
            if (d instanceof MouseEventDrawing m) {
                m.onMousePositionChange(x, y, mouseX, mouseY);
            }
        }
    }

    private void onMouseButtonEvent(MouseEvent e) {
        if (getSkinnable() == null) {
            return;
        }

        MouseButtonEvent buttonEvent = new MouseButtonEvent(e,
                toViewportX(mouseX), toViewportY(mouseY), mouseX, mouseY);

        for (int i = getSkinnable().getDrawings().size() - 1; i >= 0; i--) {
            if (buttonEvent.isConsumed()) {
                break;
            }

            if (getSkinnable().getDrawings().get(i) instanceof MouseEventDrawing m) {
                m.onMouseButtonEvent(buttonEvent);
            }
        }
    }

    private void updateCanvas() {
        chartCanvas.update(getSkinnable().getDrawings());
    }

    private void updateLegend() {
        chartLegend.update(getSkinnable().getDrawings(), getSkinnable().getLegendX());
    }

    public Bounds getCanvasBounds() {
        return chartCanvas.getBoundsInLocal();
    }

    public ReadOnlyObjectProperty<Bounds> canvasBoundsProperty() {
        return chartCanvas.boundsInLocalProperty();
    }

    public double toViewportX(double canvasX) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return getChartCanvasRange2D().toTargetX(canvasX, v);
    }

    public double toViewportY(double canvasY) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return getChartCanvasRange2D().toTargetY(chartCanvas.getHeight() - canvasY, v);
    }

    public double toViewportWidth(double canvasWidth) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return getChartCanvasRange2D().toTargetWidth(canvasWidth, v);
    }

    public double toViewportHeight(double canvasHeight) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return -getChartCanvasRange2D().toTargetHeight(canvasHeight, v);
    }

    public double toCanvasX(double viewportX) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return v.toTargetX(viewportX, getChartCanvasRange2D());
    }

    public double toCanvasY(double viewportY) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return chartCanvas.getHeight() - v.toTargetY(viewportY, getChartCanvasRange2D());
    }

    public double toCanvasWidth(double canvasWidth) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return v.toTargetWidth(canvasWidth, getChartCanvasRange2D());
    }

    public double toCanvasHeight(double canvasHeight) {
        Range2D v = getSkinnable() == null ? Range2D.NAN : getSkinnable().getViewport();
        return -v.toTargetHeight(canvasHeight, getChartCanvasRange2D());
    }

    private Range2D getChartCanvasRange2D() {
        Bounds b = chartCanvas.getBoundsInLocal();
        return new Range2D(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY());
    }

    @Override
    public void dispose() {
        chartCanvas.widthProperty().unbind();
        chartCanvas.heightProperty().unbind();
        chartCanvas.viewportProperty().unbind();

        chartLegend.titleProperty().unbind();

        dragDistance.dispose();
        renderingState.dispose();

        getSkinnable().removeEventHandler(MouseEvent.ANY, mouseEventFilter);
        getSkinnable().removeEventHandler(ScrollEvent.SCROLL, scrollEventFilter);

        getSkinnable().viewportProperty().removeListener(viewportMousePositionChangeListener);

        getChildren().removeAll(chartCanvas, chartLegend);

        animationTimer.stop();

        super.dispose();
    }
}
