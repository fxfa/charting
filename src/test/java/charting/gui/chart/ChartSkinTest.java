package charting.gui.chart;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
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
        chart.setViewport(new BoundingBox(0, 0, 100, 100));

        DoubleProperty w = new SimpleDoubleProperty(200);
        DoubleProperty h = new SimpleDoubleProperty(200);

        when(chart.widthProperty()).thenReturn(w);
        when(chart.heightProperty()).thenReturn(h);

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
