package charting.gui.superchart;

import charting.gui.chart.Chart;
import charting.gui.chart.Drawing;
import charting.gui.superchart.indicatorspane.Indicator;
import charting.gui.superchart.indicatorspane.IndicatorsPane;
import charting.gui.util.NodeLoader;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SuperChartSkin extends SkinBase<SuperChart> {
    private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");

    private final StackPane root;

    @FXML
    private Chart chart;
    @FXML
    private ValueAxis valueAxis;
    @FXML
    private TimeAxis timeAxis;

    @FXML
    private VBox objectToolbar;
    @FXML
    private Button trendLineButton;
    @FXML
    private Button horizontalLineButton;
    @FXML
    private Button measureAreaButton;
    @FXML
    private Button indicatorButton;

    @FXML
    private IndicatorsPane indicatorsPane;

    private final Watermark watermark = new Watermark();
    private final MouseCrosshairs mouseCrosshairs = new MouseCrosshairs();

    private final EventHandler<MouseEvent> chartMouseEventFilter = this::onChartMouseEventFiltering;
    private final EventHandler<KeyEvent> keyEventFilter = this::onKeyEvent;

    private final ListChangeListener<Indicator> indicatorsListener =
            c -> indicatorsPane.getIndicators().setAll(c.getList());
    private final ListChangeListener<Drawing> drawingsListener = c -> updateChartDrawings();

    private final ChangeListener<Bounds> layoutBoundsListener = (obs, oldVal, newVal) -> positionIndicatorsPane();

    private final ChangeListener<ManualDrawing> activeManualDrawingListener =
            (obs, oldVal, newVal) -> onActiveManualDrawingChange(newVal);

    public SuperChartSkin(SuperChart superChart) {
        super(superChart);

        consumeMouseEvents(false);

        root = NodeLoader.loadNew(this);
        root.minWidthProperty().bind(superChart.widthProperty());
        root.minHeightProperty().bind(superChart.heightProperty());

        chart.viewportProperty().bindBidirectional(superChart.viewportProperty());
        chart.titleProperty().bind(superChart.longNameProperty());
        chart.addEventFilter(MouseEvent.ANY, chartMouseEventFilter);

        watermark.shortNameProperty().bind(superChart.shortNameProperty());
        watermark.longNameProperty().bind(superChart.longNameProperty());
        watermark.colorProperty().bind(superChart.watermarkColorProperty());

        mouseCrosshairs.colorProperty().bind(superChart.mouseCrosshairsColorProperty());

        timeAxis.viewportProperty().bindBidirectional(superChart.viewportProperty());
        timeAxis.timeAxisPosConverterProperty().bind(superChart.timeAxisPosConverterProperty());

        valueAxis.viewportProperty().bindBidirectional(superChart.viewportProperty());

        indicatorsPane.setCloseButtonActionHandler(this::hideIndicatorsPane);
        indicatorsPane.setIndicatorSelectionHandler(this::onIndicatorSelection);
        indicatorsPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> positionIndicatorsPane());
        indicatorsPane.getIndicators().setAll(superChart.getIndicators());

        superChart.getIndicators().addListener(indicatorsListener);
        superChart.layoutBoundsProperty().addListener(layoutBoundsListener);
        superChart.activeManualDrawingProperty().addListener(activeManualDrawingListener);
        superChart.getDrawings().addListener(drawingsListener);
        superChart.addEventFilter(KeyEvent.KEY_RELEASED, keyEventFilter);

        updateChartDrawings();

        getChildren().add(root);
    }

    private void updateChartDrawings() {
        if (getSkinnable() == null) {
            chart.getDrawings().clear();
        }

        List<Drawing> drawings = new ArrayList<>();
        drawings.add(watermark);
        drawings.addAll(getSkinnable().getDrawings());
        if (getSkinnable().getActiveManualDrawing() != null) {
            drawings.add(getSkinnable().getActiveManualDrawing());
        }
        drawings.add(mouseCrosshairs);

        chart.getDrawings().setAll(drawings);
    }

    private void onActiveManualDrawingChange(ManualDrawing newVal) {
        if (newVal != null) {
            hideIndicatorsPane();
        }

        trendLineButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, newVal instanceof ManualLine);
        horizontalLineButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, newVal instanceof ManualHorizontalLine);
        measureAreaButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, newVal instanceof ManualMeasureArea);
        updateChartDrawings();
    }

    private void onIndicatorSelection(Indicator indicator) {
        if (getSkinnable() != null && getSkinnable().getIndicatorSelectionHandler() != null) {
            getSkinnable().getIndicatorSelectionHandler().accept(indicator);
        }
    }

    private void onChartMouseEventFiltering(MouseEvent e) {
        if (getSkinnable() == null) {
            return;
        }

        if (e.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            hideIndicatorsPane();
        } else if (e.getEventType().equals(MouseEvent.MOUSE_RELEASED) && e.getButton().equals(MouseButton.SECONDARY)) {
            getSkinnable().setActiveManualDrawing(null);
        }

        if (e.getEventType().equals(MouseEvent.MOUSE_EXITED) ||
                e.getEventType().equals(MouseEvent.MOUSE_EXITED_TARGET)) {
            timeAxis.setCursorMarkPosition(Double.NaN);
            valueAxis.setCursorMarkPosition(Double.NaN);
        } else {
            timeAxis.setCursorMarkPosition(e.getX());
            valueAxis.setCursorMarkPosition(e.getY());
        }
    }

    private void onKeyEvent(KeyEvent e) {
        if (getSkinnable() != null && e.getCode() == KeyCode.ESCAPE) {
            hideIndicatorsPane();
            getSkinnable().setActiveManualDrawing(null);
        }
    }

    @FXML
    private void onTrendLineButtonAction() {
        hideIndicatorsPane();

        if (getSkinnable() == null) {
            return;
        }

        if (getSkinnable().getActiveManualDrawing() instanceof ManualLine) {
            getSkinnable().setActiveManualDrawing(null);
        } else {
            getSkinnable().setActiveManualDrawing(new ManualLine());
        }
    }

    @FXML
    private void onHorizontalLineButtonAction() {
        hideIndicatorsPane();

        if (getSkinnable() == null) {
            return;
        }

        if (getSkinnable().getActiveManualDrawing() instanceof ManualHorizontalLine) {
            getSkinnable().setActiveManualDrawing(null);
        } else {
            getSkinnable().setActiveManualDrawing(new ManualHorizontalLine());
        }
    }

    @FXML
    private void onMeasureAreaButtonAction() {
        hideIndicatorsPane();

        if (getSkinnable() == null) {
            return;
        }

        if (getSkinnable().getActiveManualDrawing() instanceof ManualMeasureArea) {
            getSkinnable().setActiveManualDrawing(null);
        } else {
            getSkinnable().setActiveManualDrawing(new ManualMeasureArea());
        }
    }

    @FXML
    private void onIndicatorButtonAction() {
        if (getSkinnable() == null) {
            return;
        }

        getSkinnable().setActiveManualDrawing(null);

        if (indicatorsPane.isVisible()) {
            hideIndicatorsPane();
        } else {
            showIndicatorsPane();
        }
    }

    private void hideIndicatorsPane() {
        indicatorsPane.setVisible(false);
        indicatorsPane.setManaged(false);
    }

    private void showIndicatorsPane() {
        positionIndicatorsPane();
        indicatorsPane.setVisible(true);
        indicatorsPane.setManaged(true);
    }

    private void positionIndicatorsPane() {
        if (getSkinnable() == null) {
            return;
        }

        Bounds b1 = getSkinnable().getLayoutBounds();
        Bounds b2 = indicatorsPane.getLayoutBounds();
        indicatorsPane.relocate(Math.round(b1.getCenterX() - b2.getCenterX()),
                Math.round(b1.getCenterY() - b2.getCenterY()));
        indicatorsPane.setTranslateX(0);
        indicatorsPane.setTranslateY(0);
    }

    @Override
    public void dispose() {
        root.minWidthProperty().unbind();
        root.minHeightProperty().unbind();

        chart.viewportProperty().unbindBidirectional(getSkinnable().viewportProperty());
        chart.titleProperty().unbind();
        chart.removeEventFilter(MouseEvent.ANY, chartMouseEventFilter);

        watermark.shortNameProperty().unbind();
        watermark.longNameProperty().unbind();
        watermark.colorProperty().unbind();

        mouseCrosshairs.colorProperty().unbind();

        timeAxis.viewportProperty().unbindBidirectional(getSkinnable().viewportProperty());
        timeAxis.timeAxisPosConverterProperty().unbind();

        valueAxis.viewportProperty().unbindBidirectional(getSkinnable().viewportProperty());

        getSkinnable().getIndicators().removeListener(indicatorsListener);
        getSkinnable().layoutBoundsProperty().removeListener(layoutBoundsListener);
        getSkinnable().activeManualDrawingProperty().removeListener(activeManualDrawingListener);
        getSkinnable().getDrawings().removeListener(drawingsListener);
        getSkinnable().removeEventFilter(KeyEvent.KEY_RELEASED, keyEventFilter);

        getChildren().remove(root);

        super.dispose();
    }
}
