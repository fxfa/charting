package charting.gui.chart;

import charting.gui.util.NodeDragDistance;
import charting.gui.util.NodeRenderingState;
import charting.util.Range2D;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Subscription;

public class ChartSkin extends SkinBase<Chart> {
    private final Pane pane = new Pane();
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

    private final ReadOnlyObjectWrapper<Point2D> viewportMousePos = new ReadOnlyObjectWrapper<>();

    private final Subscription chartSubscription;

    public ChartSkin(Chart chart) {
        super(chart);

        Subscription s1 = chart.widthProperty().subscribe(this::updateChartCanvasSize);
        Subscription s2 = chart.heightProperty().subscribe(this::updateChartCanvasSize);
        Subscription s3 = chart.insetsProperty().subscribe(this::updateChartCanvasSize);
        Subscription s4 = chart.viewportProperty().subscribe(this::onViewportChange);

        Subscription s5 = viewportMousePos.subscribe(this::onViewportMousePositionChange);
        viewportMousePos.set(new Point2D(Double.NaN, Double.NaN));

        chartLegend.setMinWidth(Region.USE_PREF_SIZE);
        chartLegend.setMinHeight(Region.USE_PREF_SIZE);
        chartLegend.setMaxWidth(Region.USE_PREF_SIZE);
        chartLegend.setMaxHeight(Region.USE_PREF_SIZE);
        chartLegend.titleProperty().bind(chart.titleProperty());

        renderingState = new NodeRenderingState(chart);
        renderingState.renderingProperty().subscribe(this::onRenderingStateChange);

        dragDistance = new NodeDragDistance(chartCanvas);
        dragDistance.dragDistanceXProperty().subscribe(this::onDragDistanceXChange);
        dragDistance.dragDistanceYProperty().subscribe(this::onDragDistanceYChange);

        chartCanvas.viewportProperty().bind(chart.viewportProperty());
        chartCanvas.addEventFilter(MouseEvent.ANY, mouseEventFilter);
        chartCanvas.addEventFilter(ScrollEvent.SCROLL, scrollEventFilter);

        chartSubscription = Subscription.combine(s1, s2, s3, s4, s5);

        updateChartCanvasSize();

        pane.getChildren().addAll(chartCanvas, chartLegend);
        getChildren().add(pane);
    }

    private void updateChartCanvasSize() {
        Insets i = getSkinnable().getInsets();
        i = i == null ? Insets.EMPTY : i;

        double w = getSkinnable().getWidth() - i.getLeft() - i.getRight();
        double h = getSkinnable().getHeight() - i.getTop() - i.getBottom();

        chartCanvas.setWidth(w);
        chartCanvas.setHeight(h);
    }

    private void onRenderingStateChange(boolean rendering) {
        if (rendering) {
            animationTimer.start();
        } else {
            animationTimer.stop();
        }
    }

    private void onDragDistanceXChange(Number oldVal, Number newVal) {
        getSkinnable().shiftDrawings(
                toViewportWidth(newVal.doubleValue() - oldVal.doubleValue()), 0);
    }

    private void onDragDistanceYChange(Number oldVal, Number newVal) {
        getSkinnable().shiftDrawings(
                0, toViewportHeight(newVal.doubleValue() - oldVal.doubleValue()));
    }

    private void onScroll(ScrollEvent e) {
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
            viewportMousePos.set(new Point2D(toViewportX(mouseX), toViewportY(mouseY)));
        }

        if (e.getEventType().equals(MouseEvent.MOUSE_PRESSED) || e.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            onMouseButtonEvent(e);
        }
    }

    private void onViewportChange() {
        viewportMousePos.set(new Point2D(toViewportX(mouseX), toViewportY(mouseY)));
    }

    private void onViewportMousePositionChange(Point2D oldPos, Point2D newPos) {
        if (!getSkinnable().legendXProperty().isBound() &&
                oldPos != null && !Double.valueOf(newPos.getX()).equals(oldPos.getX())) {
            getSkinnable().setLegendX(newPos.getX());
        }

        for (Drawing d : getSkinnable().getDrawings()) {
            if (d instanceof MouseEventDrawing m) {
                m.onMousePositionChange(newPos.getX(), newPos.getY(), mouseX, mouseY);
            }
        }
    }

    private void onMouseButtonEvent(MouseEvent e) {
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

    public Point2D getViewportMousePos() {
        return viewportMousePos.get();
    }

    public ReadOnlyObjectProperty<Point2D> viewportMousePosProperty() {
        return viewportMousePos.getReadOnlyProperty();
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
        chartSubscription.unsubscribe();

        chartCanvas.viewportProperty().unbind();
        chartCanvas.removeEventHandler(MouseEvent.ANY, mouseEventFilter);
        chartCanvas.removeEventHandler(ScrollEvent.SCROLL, scrollEventFilter);

        chartLegend.titleProperty().unbind();

        dragDistance.dispose();
        renderingState.dispose();

        getChildren().remove(pane);

        animationTimer.stop();

        super.dispose();
    }
}
