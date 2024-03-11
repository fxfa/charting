package charting.gui.superchart;

import charting.gui.chart.Drawable;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.MouseButtonEvent;
import charting.gui.drawings.HorizontalLine;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.MouseButton;

import java.util.List;

public class ManualHorizontalLine implements ManualDrawing {
    private final HorizontalLine horizontalLine = new HorizontalLine();

    private final IntegerProperty remaining = new SimpleIntegerProperty(getTotalClicks());

    @Override
    public List<? extends Drawable> getDrawables(DrawingContext drawingContext) {
        return horizontalLine.getDrawables(drawingContext);
    }

    @Override
    public void onMousePositionChange(double viewportX, double viewportY, double canvasX, double canvasY) {
        if (remaining.get() == 1) {
            horizontalLine.setY(viewportY);
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
        return 1;
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
    public HorizontalLine getUnderlying() {
        return horizontalLine;
    }
}
