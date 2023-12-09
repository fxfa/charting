package charting.gui.chart;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

class ChartLegend extends VBox {
    private final Label titleLabel = new Label();
    private final VBox entryBox = new VBox();

    private final List<ChartLegendEntry> entries = new ArrayList<>();

    ChartLegend() {
        getStyleClass().add("chart-legend");
        titleLabel.getStyleClass().add("chart-legend-title");
        entryBox.getStyleClass().add("chart-legend-entry-box");

        setBackground(null);

        hideTitleLabel();

        titleLabel.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                hideTitleLabel();
            } else {
                showTitleLabel();
            }
        });

        titleLabel.setTextFill(Color.BLACK);

        getChildren().addAll(titleLabel, entryBox);
    }

    private void hideTitleLabel() {
        titleLabel.setVisible(false);
        titleLabel.setManaged(false);
    }

    private void showTitleLabel() {
        titleLabel.setVisible(true);
        titleLabel.setManaged(true);
    }

    void update(List<? extends Drawing> drawings, double x) {
        List<List<ChartLegendString>> strings = getLegendEntries(drawings,x);

        while (entries.size() > strings.size()) {
            entryBox.getChildren().remove(entries.remove(entries.size() - 1));
        }

        while (entries.size() < strings.size()) {
            ChartLegendEntry e = new ChartLegendEntry();
            entries.add(e);
            entryBox.getChildren().add(e);
        }

        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setContent(strings.get(i));
        }
    }

    private List<List<ChartLegendString>> getLegendEntries(List<? extends Drawing> drawings, double x) {
        return drawings.stream()
                .filter(d -> d instanceof LegendDrawing)
                .map(l -> ((LegendDrawing) l).getLegend(x))
                .filter(s -> s != null && !s.isEmpty())
                .toList();
    }

    StringProperty titleProperty() {
        return titleLabel.textProperty();
    }
}
