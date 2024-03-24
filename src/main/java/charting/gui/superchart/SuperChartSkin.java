package charting.gui.superchart;

import charting.gui.chart.Chart;
import charting.gui.chart.Drawing;
import charting.gui.superchart.indicatorspane.Indicator;
import charting.gui.superchart.indicatorspane.IndicatorsPane;
import charting.gui.util.NodeLoader;
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
import javafx.scene.layout.*;
import javafx.util.Subscription;

import java.util.ArrayList;
import java.util.List;

public class SuperChartSkin extends SkinBase<SuperChart> {
    private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");

    private final StackPane root;

    @FXML
    private HBox mainPane;

    @FXML
    private GridPane chartPane;
    @FXML
    private TimeAxis timeAxis;
    @FXML
    private Region chartAxisCorner;

    @FXML
    private Button trendLineButton;
    @FXML
    private Button horizontalLineButton;
    @FXML
    private Button measureAreaButton;

    @FXML
    private IndicatorsPane indicatorsPane;

    private final List<ChartRow> chartRows = new ArrayList<>();
    private final List<DividerRow> dividerRows = new ArrayList<>();

    private final EventHandler<KeyEvent> keyEventFilter = this::onKeyEvent;

    private final ListChangeListener<Indicator> indicatorsListener =
            c -> indicatorsPane.getIndicators().setAll(c.getList());
    private final ListChangeListener<ChartContent> chartContentsListener = this::onSubChartsChange;

    private final Subscription superChartSubscription;

    public SuperChartSkin(SuperChart superChart) {
        super(superChart);

        root = NodeLoader.loadNew(this);
        root.minWidthProperty().bind(superChart.widthProperty());
        root.minHeightProperty().bind(superChart.heightProperty());

        timeAxis.axisProperty().bindBidirectional(superChart.timeAxisProperty());
        timeAxis.timeAxisPosConverterProperty().bind(superChart.timeAxisPosConverterProperty());

        indicatorsPane.setIndicatorSelectionHandler(this::onIndicatorSelection);
        indicatorsPane.layoutBoundsProperty().subscribe(this::positionIndicatorsPane);

        Subscription s1 = superChart.userDrawingProperty().subscribe(this::onUserDrawingChange);
        Subscription s2 = superChart.getIndicators().subscribe(this::updateIndicators);
        Subscription s3 = superChart.layoutBoundsProperty().subscribe(this::positionIndicatorsPane);
        superChart.getChartContents().addListener(chartContentsListener);
        superChart.getIndicators().addListener(indicatorsListener);
        superChart.addEventFilter(KeyEvent.KEY_PRESSED, keyEventFilter);

        mainPane.addEventFilter(MouseEvent.ANY, this::onMouseEvent);

        superChartSubscription = Subscription.combine(s1, s2, s3);

        updateIndicators();
        addSubCharts(superChart.getChartContents());

        getChildren().add(root);
    }

    private void updateIndicators() {
        if (getSkinnable() != null) {
            indicatorsPane.getIndicators().setAll(getSkinnable().getIndicators());
        }
    }

    private void onSubChartsChange(ListChangeListener.Change<? extends ChartContent> change) {
        while (change.next()) {
            removeSubCharts(change.getRemoved());
            addSubCharts(change.getAddedSubList());
        }
    }

    private void removeSubCharts(List<? extends ChartContent> chartContents) {
        for (ChartContent c : chartContents) {
            ChartRow row = chartRows.stream().filter(r -> r.getContent().equals(c)).findFirst().orElseThrow();
            row.dispose();

            chartRows.remove(row);

            if (!dividerRows.isEmpty()) {
                dividerRows.remove(dividerRows.size() - 1);
            }
        }

        repopulateChartPane();
    }

    private void addSubCharts(List<? extends ChartContent> chartContents) {
        double h = chartContents.size() / ((double) chartRows.size() + chartContents.size()) * chartPane.getHeight();
        shrinkChartRows(h);

        for (ChartContent content : chartContents) {
            ChartRow chartRow = new ChartRow(getSkinnable(), content, timeAxis);
            chartRow.getChart().legendXProperty().addListener((obs, oldVal, newVal) ->
                    onChartLegendXChange(chartRow, newVal.doubleValue()));
            chartRows.add(chartRow);

            if (chartRows.size() > 1) {
                DividerRow d = new DividerRow(chartPane);
                d.setChartRows(chartRows.get(chartRows.size() - 2), chartRow);
                dividerRows.add(d);
            }

            chartRow.getConstraints().setMinHeight(60);
            chartRow.getConstraints().setPrefHeight(h / chartContents.size());
            chartRow.getConstraints().setMaxHeight(Double.POSITIVE_INFINITY);
        }

        repopulateChartPane();
    }

    private void repopulateChartPane() {
        chartPane.getChildren().clear();
        chartPane.getRowConstraints().clear();

        for (int i = 0; i < chartRows.size() + dividerRows.size() + 1; i++) {
            chartPane.getRowConstraints().add(new RowConstraints());
        }

        for (int i = 0; i < chartRows.size(); i++) {
            ChartRow r = chartRows.get(i);
            chartPane.addRow(i * 2, r.getChart(), r.getValueAxis());
            chartPane.getRowConstraints().set(i * 2, r.getConstraints());
        }

        for (int i = 0; i < dividerRows.size(); i++) {
            chartPane.addRow(i * 2 + 1, dividerRows.get(i).getDivider());
        }

        chartPane.addRow(chartPane.getRowCount(), timeAxis, chartAxisCorner);
    }

    private void updateSubChartPrefHeights() {
        chartPane.layout();
        for (ChartRow r : chartRows) {
            double h = chartPane.getCellBounds(0, GridPane.getRowIndex(r.getChart())).getHeight();
            r.getConstraints().setPrefHeight(h);
        }
    }

    private void shrinkChartRows(double height) {
        updateSubChartPrefHeights();

        double totalChartHeight = chartPane.getChildren().stream()
                .filter(c -> c instanceof Chart)
                .mapToDouble(c -> chartPane.getCellBounds(0, GridPane.getRowIndex(c)).getHeight())
                .sum();

        for (ChartRow r : chartRows) {
            RowConstraints c = r.getConstraints();

            double d = height / totalChartHeight * c.getPrefHeight();
            d = Math.min(d, c.getPrefHeight() - c.getMinHeight());
            totalChartHeight -= c.getPrefHeight();
            c.setPrefHeight(c.getPrefHeight() - d);
            height -= d;
        }
    }

    private void onUserDrawingChange(Drawing d) {
        trendLineButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, false);
        horizontalLineButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, false);
        measureAreaButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, false);

        if (d instanceof ManualLine) {
            trendLineButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, true);
        } else if (d instanceof ManualHorizontalLine) {
            horizontalLineButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, true);
        } else if (d instanceof ManualMeasureArea) {
            measureAreaButton.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, true);
        }
    }

    private void onChartLegendXChange(ChartRow c, double x) {
        if (getSkinnable() == null) {
            return;
        }

        if (getSkinnable().legendXProperty().isBound()) {
            c.getChart().setLegendX(getSkinnable().getLegendX());
        } else {
            getSkinnable().setLegendX(x);
            for (ChartRow s : chartRows) {
                s.getChart().setLegendX(x);
            }
        }
    }

    private void onIndicatorSelection(Indicator indicator) {
        if (getSkinnable() != null && getSkinnable().getIndicatorSelectionHandler() != null) {
            getSkinnable().getIndicatorSelectionHandler().accept(indicator);
        }
    }

    private void onMouseEvent(MouseEvent e) {
        if (e.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            hideIndicatorsPane();
        }

        if (getSkinnable() != null) {
            if (e.getEventType().equals(MouseEvent.MOUSE_RELEASED) && e.getButton().equals(MouseButton.SECONDARY)) {
                getSkinnable().setUserDrawing(null);
            }
        }
    }

    private void onKeyEvent(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            hideIndicatorsPane();

            if (getSkinnable() != null) {
                getSkinnable().setUserDrawing(null);
            }
        }
    }

    @FXML
    private void onTrendLineButtonAction() {
        onDrawingButtonAction(new ManualLine());
    }

    @FXML
    private void onHorizontalLineButtonAction() {
        onDrawingButtonAction(new ManualHorizontalLine());
    }

    @FXML
    private void onMeasureAreaButtonAction() {
        onDrawingButtonAction(new ManualMeasureArea());
    }

    private void onDrawingButtonAction(Drawing d) {
        SuperChart s = getSkinnable();

        if (s == null || s.userDrawingProperty().isBound()) {
            return;
        }

        if (s.getUserDrawing() != null && s.getUserDrawing().getClass().equals(d.getClass())) {
            s.setUserDrawing(null);
        } else {
            s.setUserDrawing(d);
        }
    }

    @FXML
    private void onIndicatorButtonAction() {
        if (getSkinnable() != null) {
            getSkinnable().setUserDrawing(null);
        }

        showIndicatorsPane();
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
        if (getSkinnable() != null) {
            Bounds b1 = getSkinnable().getLayoutBounds();
            Bounds b2 = indicatorsPane.getLayoutBounds();
            indicatorsPane.relocate(Math.round(b1.getCenterX() - b2.getCenterX()),
                    Math.round(b1.getCenterY() - b2.getCenterY()));
            indicatorsPane.setTranslateX(0);
            indicatorsPane.setTranslateY(0);
        }
    }

    @Override
    public void dispose() {
        root.minWidthProperty().unbind();
        root.minHeightProperty().unbind();

        timeAxis.axisProperty().unbindBidirectional(getSkinnable().timeAxisProperty());
        timeAxis.timeAxisPosConverterProperty().unbind();

        getSkinnable().getChartContents().removeListener(chartContentsListener);
        getSkinnable().getIndicators().removeListener(indicatorsListener);
        getSkinnable().removeEventFilter(KeyEvent.KEY_PRESSED, keyEventFilter);

        superChartSubscription.unsubscribe();

        getChildren().remove(root);

        super.dispose();
    }
}
