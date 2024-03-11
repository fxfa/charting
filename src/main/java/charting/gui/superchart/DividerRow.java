package charting.gui.superchart;

import charting.gui.util.NodeDragDistance;
import javafx.css.PseudoClass;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

class DividerRow {
    private final static PseudoClass DRAGGING_PSEUDO_CLASS = PseudoClass.getPseudoClass("dragging");

    private final NodeDragDistance dragDistance;

    private final GridPane chartPane;

    private final Region divider = new Region();

    private ChartRow upper;
    private ChartRow lower;

    DividerRow(GridPane chartPane) {
        this.chartPane = chartPane;

        divider.getStyleClass().add("chart-divider");
        divider.setMinHeight(4);
        divider.setMaxHeight(4);
        divider.setPrefHeight(4);

        GridPane.setHgrow(divider, Priority.ALWAYS);
        GridPane.setColumnSpan(divider, 2);

        dragDistance = new NodeDragDistance(divider);
        dragDistance.dragDistanceYProperty().subscribe(this::onDragDistanceYChange);
        dragDistance.draggingProperty().subscribe(d -> divider.pseudoClassStateChanged(DRAGGING_PSEUDO_CLASS, d));
    }

    private void onDragDistanceYChange(Number o, Number n) {
        updateSubChartPrefHeights();

        int i1 = GridPane.getRowIndex(upper.getChart());
        int i2 = GridPane.getRowIndex(lower.getChart());

        double diff = n.doubleValue() - o.doubleValue();
        if (diff < 0) {
            RowConstraints c1 = chartPane.getRowConstraints().get(i1);
            RowConstraints c2 = chartPane.getRowConstraints().get(i2);

            double p1 = chartPane.getCellBounds(0, i1).getHeight();

            c1.setPrefHeight(chartPane.getCellBounds(0, i1).getHeight() + diff);

            if (c1.getPrefHeight() <= c1.getMinHeight()) {
                c1.setPrefHeight(c1.getMinHeight());
            }

            c2.setPrefHeight(chartPane.getCellBounds(0, i2).getHeight() + (p1 - c1.getPrefHeight()));

            chartPane.layout();
        } else {
            RowConstraints c1 = chartPane.getRowConstraints().get(i1);
            RowConstraints c2 = chartPane.getRowConstraints().get(i2);

            double p2 = chartPane.getCellBounds(0, i2).getHeight();

            c2.setPrefHeight(chartPane.getCellBounds(0, i2).getHeight() - diff);

            if (c2.getPrefHeight() <= c2.getMinHeight()) {
                c2.setPrefHeight(c2.getMinHeight());
            }

            c1.setPrefHeight(chartPane.getCellBounds(0, i1).getHeight() + (p2 - c2.getPrefHeight()));

            chartPane.layout();
        }
    }

    private void updateSubChartPrefHeights() {
        chartPane.layout();
        for (int j = 0; j < chartPane.getRowConstraints().size(); j += 2) {
            chartPane.getRowConstraints().get(j).setPrefHeight(chartPane.getCellBounds(0, j).getHeight());
        }
    }

    void setChartRows(ChartRow upper, ChartRow lower) {
        this.upper = upper;
        this.lower = lower;
    }

    Region getDivider() {
        return divider;
    }
}
