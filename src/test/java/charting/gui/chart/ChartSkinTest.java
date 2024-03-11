package charting.gui.chart;

import charting.util.Range2D;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ChartSkinTest {
    private ChartSkin skin;

    @BeforeAll
    static void beforeAll() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void beforeEach() {
        Chart chart = spy(new Chart());
        chart.setViewport(new Range2D(0, 0, 100, 100));

        when(chart.getWidth()).thenReturn(200d);
        when(chart.getHeight()).thenReturn(200d);

        skin = new ChartSkin(chart);
    }

    @Test
    void toViewportX() {
        assertEquals(15, skin.toViewportX(30));
    }

    @Test
    void toViewportY() {
        assertEquals(85, skin.toViewportY(30));
    }

    @Test
    void toViewportWidth() {
        assertEquals(15, skin.toViewportWidth(30));
    }

    @Test
    void toViewportHeight() {
        assertEquals(-15, skin.toViewportHeight(30));
    }

    @Test
    void toCanvasX() {
        assertEquals(60, skin.toCanvasX(30));
    }

    @Test
    void toCanvasY() {
        assertEquals(140, skin.toCanvasY(30));
    }

    @Test
    void toCanvasWidth() {
        assertEquals(60, skin.toCanvasWidth(30));
    }

    @Test
    void toCanvasHeight() {
        assertEquals(-60, skin.toCanvasHeight(30));
    }
}
