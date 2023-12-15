package charting.gui.superchart;

import charting.gui.chart.Drawable;
import charting.gui.chart.Drawing;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.MouseButtonEvent;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SuperChartTest {
    @BeforeAll
    static void beforeAll() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    void manualDrawingAddsUnderlyingDrawingOnceDone() {
        SuperChart superChart = new SuperChart();

        TestManualDrawing manualDrawing = new TestManualDrawing();

        superChart.setActiveManualDrawing(manualDrawing);

        assertEquals(0, superChart.getDrawings().size());

        manualDrawing.finish();

        assertEquals(1, superChart.getDrawings().size());
        assertNull(superChart.getActiveManualDrawing());
    }

    private static class TestManualDrawing implements ManualDrawing {
        private final ReadOnlyIntegerWrapper remainingClicks = new ReadOnlyIntegerWrapper(1);

        private final Drawing drawing = drawingContext -> Collections.emptyList();

        void finish() {
            remainingClicks.set(0);
        }

        @Override
        public List<? extends Drawable> getDrawables(DrawingContext drawingContext) {
            return drawing.getDrawables(drawingContext);
        }

        @Override
        public void onMousePositionChange(double viewportX, double viewportY, double canvasX, double canvasY) {
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent event) {
        }

        @Override
        public int getRemainingClicks() {
            return remainingClicks.get();
        }

        @Override
        public ReadOnlyIntegerProperty remainingClicksProperty() {
            return remainingClicks.getReadOnlyProperty();
        }

        @Override
        public Drawing getUnderlying() {
            return drawing;
        }
    }
}
