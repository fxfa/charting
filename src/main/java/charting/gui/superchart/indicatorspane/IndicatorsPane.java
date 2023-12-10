package charting.gui.superchart.indicatorspane;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.function.Consumer;

/**
 * A closeable dialog control that contains a list of selectable indicators.
 */
public class IndicatorsPane extends Control {
    private final ObservableList<Indicator> indicators = FXCollections.observableArrayList();

    private final ObjectProperty<Consumer<Indicator>> indicatorSelectionHandler = new SimpleObjectProperty<>();
    private final ObjectProperty<Runnable> closeButtonActionHandler = new SimpleObjectProperty<>();

    public IndicatorsPane() {
        getStyleClass().add("indicators-pane");
    }

    public ObservableList<Indicator> getIndicators() {
        return indicators;
    }

    public Consumer<Indicator> getIndicatorSelectionHandler() {
        return indicatorSelectionHandler.get();
    }

    /**
     * This handler will be invoked whenever an {@link Indicator} is selected.
     */
    public ObjectProperty<Consumer<Indicator>> indicatorSelectionHandlerProperty() {
        return indicatorSelectionHandler;
    }

    public void setIndicatorSelectionHandler(Consumer<Indicator> indicatorSelectionHandler) {
        this.indicatorSelectionHandler.set(indicatorSelectionHandler);
    }

    public Runnable getCloseButtonActionHandler() {
        return closeButtonActionHandler.get();
    }

    /**
     * This handler will be invoked whenever the panes close button is clicked.
     */
    public ObjectProperty<Runnable> closeButtonActionHandlerProperty() {
        return closeButtonActionHandler;
    }

    public void setCloseButtonActionHandler(Runnable closeButtonActionHandler) {
        this.closeButtonActionHandler.set(closeButtonActionHandler);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new IndicatorsPaneSkin(this);
    }
}
