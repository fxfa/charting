package charting.gui.superchart;

import charting.gui.chart.Drawing;
import charting.util.Range;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Contains the data to be presented in a separate sub chart.
 */
public class ChartContent {
    private final ObjectProperty<Range> valueAxis = new SimpleObjectProperty<>(new Range(0, 100));
    private final ObservableList<Drawing> drawings = FXCollections.observableArrayList();
    private final StringProperty longName = new SimpleStringProperty("");
    private final StringProperty shortName = new SimpleStringProperty("");

    public Range getValueAxis() {
        return valueAxis.get();
    }

    public ObjectProperty<Range> valueAxisProperty() {
        return valueAxis;
    }

    public void setValueAxis(Range valueAxis) {
        this.valueAxis.set(valueAxis);
    }

    /**
     * The sub charts displayed drawings.
     */
    public ObservableList<Drawing> getDrawings() {
        return drawings;
    }

    public String getLongName() {
        return longName.get();
    }

    /**
     * The sub charts displayed long name.
     */
    public StringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getShortName() {
        return shortName.get();
    }

    /**
     * The sub charts displayed short name.
     */
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }
}