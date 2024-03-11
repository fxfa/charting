package charting.gui.chart;


import charting.util.Range2D;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ChartTest {
    @BeforeAll
    static void beforeAll() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    void translateViewport() {
        Chart chart = spy(new Chart());
        chart.setViewport(new Range2D(0, 0, 100, 100));

        when(chart.getWidth()).thenReturn(100d);
        when(chart.getHeight()).thenReturn(100d);

        chart.shiftDrawings(100, 100);

        assertEquals(new Range2D(-100, -100, 0, 0), chart.getViewport());
    }

    @Test
    void scaleViewport() {
        Chart chart = spy(new Chart());
        chart.setViewport(new Range2D(0, 0, 100, 100));

        when(chart.getWidth()).thenReturn(100d);
        when(chart.getHeight()).thenReturn(100d);

        chart.zoomDrawings(2, 2, 0, 0);

        assertEquals(new Range2D(0, 0, 50, 50), chart.getViewport());

        chart.zoomDrawings(0.5, 0.5, 50, 50);

        assertEquals(new Range2D(-50, -50, 50, 50), chart.getViewport());
    }
}
