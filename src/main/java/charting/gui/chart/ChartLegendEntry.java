package charting.gui.chart;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

class ChartLegendEntry extends TextFlow {
    private final ObjectProperty<List<ChartLegendString>> content = new SimpleObjectProperty<>();

    ChartLegendEntry() {
        setMaxWidth(USE_PREF_SIZE);

        getStyleClass().add("chart-legend-entry");

        content.subscribe(this::onContentChange);
    }

    private void onContentChange(List<ChartLegendString> oldVal, List<ChartLegendString> newVal) {
        if (oldVal != null && oldVal.equals(newVal)) {
            return;
        }

        getChildren().clear();

        for (ChartLegendString s : newVal) {
            if (s != null && s.getDescription() != null && !s.getDescription().isEmpty()) {
                Text description = new Text(s.getDescription());
                description.getStyleClass().addAll("text", "description");
                getChildren().add(description);
            }

            if (s != null && s.getValue() != null && !s.getValue().isEmpty()) {
                Text value = new Text(s.getValue());
                value.getStyleClass().addAll("text", "value");
                value.setFill(s.getColor());
                getChildren().add(value);
            }
        }
    }

    void setContent(List<ChartLegendString> content) {
        this.content.set(content);
    }
}
