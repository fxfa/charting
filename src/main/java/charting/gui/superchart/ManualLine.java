package charting.gui.superchart;


import charting.gui.chart.*;
import charting.gui.drawings.Line;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.MouseButton;

import java.util.List;

public class ManualLine implements ManualDrawing {
    private final charting.gui.drawings.Line line = new charting.gui.drawings.Line();

    private final IntegerProperty remaining = new SimpleIntegerProperty(getTotalClicks());

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext drawingContext) {
        return line.getDrawables(drawingContext);
    }

    @Override
    public void onMousePositionChange(double viewportX, double viewportY, double canvasX, double canvasY) {
        if (remaining.get() == 2) {
            line.setStartX(Math.round(viewportX));
            line.setStartY(viewportY);
            line.setEndX(Math.round(viewportX));
            line.setEndY(viewportY);
        } else if (remaining.get() == 1 && !Double.isNaN(viewportX) && !Double.isNaN(viewportY)) {
            line.setEndX(Math.round(viewportX));
            line.setEndY(viewportY);
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent event) {
        if (remaining.get() > 0 && event.getButton() == MouseButton.PRIMARY && event.isReleasedEvent()) {
            remaining.set(remaining.get() - 1);
        }
    }

    @Override
    public int getTotalClicks() {
        return 2;
    }

    @Override
    public int getRemainingClicks() {
        return remaining.get();
    }

    @Override
    public ReadOnlyIntegerProperty remainingClicksProperty() {
        return remaining;
    }

    @Override
    public Line getUnderlying() {
        return line;
    }
}
