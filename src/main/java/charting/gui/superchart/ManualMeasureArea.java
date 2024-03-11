package charting.gui.superchart;

import charting.gui.chart.*;
import charting.gui.drawings.MeasureArea;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.MouseButton;

import java.util.List;

public class ManualMeasureArea implements ManualDrawing {
    private final MeasureArea measureArea = new MeasureArea();

    private final IntegerProperty remaining = new SimpleIntegerProperty(getTotalClicks());

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext drawingContext) {
        return measureArea.getDrawables(drawingContext);
    }

    @Override
    public void onMousePositionChange(double viewportX, double viewportY, double canvasX, double canvasY) {
        if (remaining.get() == 2) {
            measureArea.setStartX(Math.round(viewportX));
            measureArea.setStartY(viewportY);
            measureArea.setEndX(Math.round(viewportX));
            measureArea.setEndY(viewportY);
        } else if (remaining.get() == 1) {
            measureArea.setEndX(Math.round(viewportX));
            measureArea.setEndY(viewportY);
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
    public MeasureArea getUnderlying() {
        return measureArea;
    }
}
