package charting.gui.chart;

import charting.util.Preconditions;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public final class MouseButtonEvent {
    private final MouseEvent event;
    private final double viewportX;
    private final double viewportY;
    private final double canvasX;
    private final double canvasY;

    public MouseButtonEvent(MouseEvent event, double viewportX, double viewportY, double canvasX, double canvasY) {
        Preconditions.checkArgument(event.getEventType().equals(MouseEvent.MOUSE_PRESSED) ||
                event.getEventType().equals(MouseEvent.MOUSE_RELEASED));

        this.event = event;
        this.viewportX = viewportX;
        this.viewportY = viewportY;
        this.canvasX = canvasX;
        this.canvasY = canvasY;
    }

    public MouseButton getButton() {
        return event.getButton();
    }

    public boolean isPressedEvent() {
        return event.getEventType().equals(MouseEvent.MOUSE_PRESSED);
    }

    public boolean isReleasedEvent() {
        return !isPressedEvent();
    }

    public double getViewportX() {
        return viewportX;
    }

    public double getViewportY() {
        return viewportY;
    }

    public double getCanvasX() {
        return canvasX;
    }

    public double getCanvasY() {
        return canvasY;
    }

    public boolean isShiftDown() {
        return event.isShiftDown();
    }

    public boolean isAltDown() {
        return event.isAltDown();
    }

    public boolean isControlDown() {
        return event.isControlDown();
    }

    public void consume() {
        event.consume();
    }

    public boolean isConsumed() {
        return event.isConsumed();
    }
}
