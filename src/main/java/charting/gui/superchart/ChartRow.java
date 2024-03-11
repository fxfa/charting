package charting.gui.superchart;

import charting.gui.chart.Chart;
import charting.gui.chart.Drawing;
import charting.util.Range;
import charting.util.Range2D;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Subscription;

import java.util.ArrayList;
import java.util.List;

class ChartRow {
    private final Chart chart = new Chart();
    private final ValueAxis valueAxis = new ValueAxis();

    private final RowConstraints constraints = new RowConstraints();

    private final SuperChart superChart;
    private final ChartContent content;
    private final TimeAxis timeAxis;

    private final MouseLine mouseHLine = new MouseLine(true);
    private final MouseLine mouseVLine = new MouseLine(false);
    private final Watermark watermark = new Watermark();

    private boolean hovered;

    private final EventHandler<MouseEvent> chartMouseEventFilter = this::onChartMouseEventFiltering;

    private final Subscription superChartSubscription;

    ChartRow(SuperChart superChart, ChartContent content, TimeAxis timeAxis) {
        this.superChart = superChart;
        this.content = content;
        this.timeAxis = timeAxis;

        GridPane.setHgrow(chart, Priority.ALWAYS);
        GridPane.setVgrow(chart, Priority.ALWAYS);
        GridPane.setVgrow(valueAxis, Priority.ALWAYS);

        Subscription s1 = superChart.userDrawingProperty().subscribe(this::onDrawingsChange);
        Subscription s2 = content.getDrawings().subscribe(this::onDrawingsChange);

        watermark.colorProperty().bind(superChart.watermarkColorProperty());
        watermark.shortNameProperty().bind(content.shortNameProperty());
        watermark.longNameProperty().bind(content.longNameProperty());

        mouseHLine.colorProperty().bind(superChart.mouseCrosshairsColorProperty());
        mouseVLine.colorProperty().bind(superChart.mouseCrosshairsColorProperty());
        mouseVLine.posProperty().bind(superChart.legendXProperty());

        valueAxis.axisProperty().bindBidirectional(content.valueAxisProperty());
        Subscription s3 = valueAxis.axisProperty().subscribe(this::updateViewport);
        Subscription s4 = timeAxis.axisProperty().subscribe(this::updateViewport);
        updateViewport();

        chart.titleProperty().bind(content.longNameProperty());
        chart.addEventFilter(MouseEvent.ANY, chartMouseEventFilter);
        Subscription s5 = chart.viewportProperty().subscribe(this::onChartViewportChange);

        onDrawingsChange();

        superChartSubscription = Subscription.combine(s1, s2, s3, s4, s5);
    }

    private void onChartMouseEventFiltering(MouseEvent e) {
        if (e.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
            timeAxis.setCursorMarkPosition(Double.NaN);
            valueAxis.setCursorMarkPosition(Double.NaN);

            hovered = false;
            onDrawingsChange();
        } else if (e.getEventType().equals(MouseEvent.MOUSE_MOVED) || e.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            timeAxis.setCursorMarkPosition(e.getX());
            valueAxis.setCursorMarkPosition(e.getY());
        } else if (e.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
            hovered = true;
            onDrawingsChange();
        } else if (e.getEventType().equals(MouseEvent.MOUSE_PRESSED) && e.getButton().equals(MouseButton.PRIMARY) &&
                superChart.getUserDrawing() != null) {
            content.getDrawings().add(superChart.getUserDrawing());

            if (!superChart.userDrawingProperty().isBound()) {
                superChart.setUserDrawing(null);
            }
        }
    }

    private void onDrawingsChange() {
        List<Drawing> drawings = new ArrayList<>();
        drawings.add(getWatermark());
        drawings.addAll(getContent().getDrawings());

        Drawing d = superChart.getUserDrawing();
        if (d != null && hovered) {
            drawings.add(d);
        }

        drawings.add(getMouseHLine());
        drawings.add(getMouseVLine());

        chart.getDrawings().setAll(drawings);
    }

    private void updateViewport() {
        Range2D b = new Range2D(timeAxis.getAxis().start(), valueAxis.getAxis().start(),
                timeAxis.getAxis().end(), valueAxis.getAxis().end());
        if (!b.equals(chart.getViewport())) {
            chart.setViewport(b);
        }
    }

    private void onChartViewportChange(Range2D viewport) {
        timeAxis.setAxis(new Range(viewport.startX(), viewport.endX()));
        valueAxis.setAxis(new Range(viewport.startY(), viewport.endY()));
    }

    RowConstraints getConstraints() {
        return constraints;
    }

    Chart getChart() {
        return chart;
    }

    ValueAxis getValueAxis() {
        return valueAxis;
    }

    ChartContent getContent() {
        return content;
    }

    MouseLine getMouseHLine() {
        return mouseHLine;
    }

    MouseLine getMouseVLine() {
        return mouseVLine;
    }

    Watermark getWatermark() {
        return watermark;
    }

    void dispose() {
        superChartSubscription.unsubscribe();

        watermark.colorProperty().unbind();
        watermark.shortNameProperty().unbind();
        watermark.longNameProperty().unbind();

        mouseHLine.colorProperty().unbind();
        mouseVLine.colorProperty().unbind();
        mouseVLine.posProperty().unbind();

        chart.titleProperty().unbind();
        chart.removeEventFilter(MouseEvent.ANY, chartMouseEventFilter);

        valueAxis.axisProperty().unbindBidirectional(content.valueAxisProperty());
    }
}
