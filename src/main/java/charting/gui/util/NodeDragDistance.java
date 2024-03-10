package charting.gui.util;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Installs {@link WeakEventHandler WeakEventHandlers} for a nodes drag-relevant mouse events and
 * then cumulates and stores the x and y drag-distances of that node.
 */
public final class NodeDragDistance {
    private final Node node;

    private final EventHandler<MouseEvent> mousePressHandler = this::onMousePress;
    private final EventHandler<MouseEvent> mouseDragHandler = this::onMouseDrag;
    private final EventHandler<MouseEvent> mouseReleaseHandler = this::onMouseRelease;
    private final EventHandler<MouseEvent> weakMousePressHandler = new WeakEventHandler<>(mousePressHandler);
    private final EventHandler<MouseEvent> weakMouseDragHandler = new WeakEventHandler<>(mouseDragHandler);
    private final EventHandler<MouseEvent> weakMouseReleaseHandler = new WeakEventHandler<>(mouseReleaseHandler);

    private double mouseDownX = Double.NaN;
    private double mouseDownY = Double.NaN;

    private double lastDragDistanceX;
    private double lastDragDistanceY;

    private final ReadOnlyDoubleWrapper dragDistanceX = new ReadOnlyDoubleWrapper(0);
    private final ReadOnlyDoubleWrapper dragDistanceY = new ReadOnlyDoubleWrapper(0);
    private final ReadOnlyBooleanWrapper dragging = new ReadOnlyBooleanWrapper(false);

    private boolean consumingEvents = true;

    public NodeDragDistance(Node node) {
        this.node = node;

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, weakMousePressHandler);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, weakMouseDragHandler);
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, weakMouseReleaseHandler);
    }

    /**
     * Whether drag events should be consumed or not.
     * Default value: true
     */
    public void setConsumingEvents(boolean consumingEvents) {
        this.consumingEvents = consumingEvents;
    }

    public boolean isConsumingEvents() {
        return consumingEvents;
    }

    private void onMousePress(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            startDrag(e);

            if (consumingEvents) {
                e.consume();
            }
        }
    }

    private void onMouseDrag(MouseEvent e) {
        // An event filter might have consumed the preceding mouse press event,
        // therefore we need to check if the dragging property is true.
        if (e.getButton() == MouseButton.PRIMARY) {
            if (isDragging()) {
                updateDragDistance(e);

                if (consumingEvents) {
                    e.consume();
                }
            }
        }
    }

    private void onMouseRelease(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (isDragging()) {
                updateDragDistance(e);
                stopDrag(e);

                if (consumingEvents) {
                    e.consume();
                }
            }
        }
    }

    private void startDrag(MouseEvent e) {
        mouseDownX = e.getScreenX();
        mouseDownY = e.getScreenY();

        dragging.set(true);
    }

    private void updateDragDistance(MouseEvent e) {
        if (!Double.isNaN(mouseDownX)) {
            dragDistanceX.set(e.getScreenX() - mouseDownX + lastDragDistanceX);
            dragDistanceY.set(e.getScreenY() - mouseDownY + lastDragDistanceY);
        }
    }

    private void stopDrag(MouseEvent e) {
        lastDragDistanceX += e.getScreenX() - mouseDownX;
        lastDragDistanceY += e.getScreenY() - mouseDownY;

        mouseDownX = Double.NaN;
        mouseDownY = Double.NaN;

        dragging.set(false);
    }

    public void reset() {
        lastDragDistanceX = 0;
        lastDragDistanceY = 0;
        dragDistanceX.set(0);
        dragDistanceY.set(0);
    }

    public double getDragDistanceX() {
        return dragDistanceX.get();
    }

    public ReadOnlyDoubleProperty dragDistanceXProperty() {
        return dragDistanceX.getReadOnlyProperty();
    }

    public double getDragDistanceY() {
        return dragDistanceY.get();
    }

    public ReadOnlyDoubleProperty dragDistanceYProperty() {
        return dragDistanceY.getReadOnlyProperty();
    }

    public boolean isDragging() {
        return dragging.get();
    }

    public ReadOnlyBooleanProperty draggingProperty() {
        return dragging.getReadOnlyProperty();
    }

    public Node getNode() {
        return node;
    }

    /**
     * Removes all listeners from the node. Consequently, no more updates regarding the rendering state will be given.
     */
    public void dispose() {
        node.removeEventHandler(MouseEvent.MOUSE_PRESSED, weakMousePressHandler);
        node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, weakMouseDragHandler);
        node.removeEventHandler(MouseEvent.MOUSE_RELEASED, weakMouseReleaseHandler);
    }
}