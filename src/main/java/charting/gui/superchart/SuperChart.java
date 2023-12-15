package charting.gui.superchart;

import charting.gui.chart.Drawing;
import charting.gui.superchart.indicatorspane.Indicator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

/**
 * A charting control that provides advanced functionality like indicators or manual drawing.
 */
public class SuperChart extends Control {
    private static final PseudoClass DRAWING_PSEUDO_CLASS = PseudoClass.getPseudoClass("drawing");

    private static final StyleablePropertyFactory<SuperChart> FACTORY =
            new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    private static final CssMetaData<SuperChart, Color> WATERMARK_COLOR =
            FACTORY.createColorCssMetaData("-fx-watermark-color", s -> s.watermarkColor,
                    new Color(0.5, 0.5, 0.5, 0.5));
    private static final CssMetaData<SuperChart, Color> MOUSE_CROSSHAIRS_COLOR =
            FACTORY.createColorCssMetaData("-fx-mouse-crosshairs-color", s -> s.mouseCrosshairsColor,
                    Color.GRAY);

    private final StyleableObjectProperty<Color> watermarkColor =
            new SimpleStyleableObjectProperty<>(WATERMARK_COLOR, this, "watermarkColor",
                    WATERMARK_COLOR.getInitialValue(this));
    private final StyleableObjectProperty<Color> mouseCrosshairsColor =
            new SimpleStyleableObjectProperty<>(MOUSE_CROSSHAIRS_COLOR, this, "mouseCrossHairColor",
                    MOUSE_CROSSHAIRS_COLOR.getInitialValue(this));

    private final StringProperty longName = new SimpleStringProperty();
    private final StringProperty shortName = new SimpleStringProperty();
    private final ObjectProperty<Bounds> viewport =
            new SimpleObjectProperty<>(new BoundingBox(0, 0, 100, 100));

    private final ObservableList<Indicator> indicators = FXCollections.observableArrayList();
    private final ObjectProperty<Consumer<Indicator>> indicatorSelectionHandler = new SimpleObjectProperty<>();

    private final ObjectProperty<TimeAxisPosConverter> timeAxisPosConverter =
            new SimpleObjectProperty<>(TimeAxisPosConverter.of(Instant::getEpochSecond, Instant::ofEpochSecond));

    private final ObjectProperty<ManualDrawing> activeManualDrawing = new SimpleObjectProperty<>();

    private final ObservableList<Drawing> drawings = FXCollections.observableArrayList();

    private final ChangeListener<Number> manualDrawingRemainingClicksChangeListener =
            (obs, oldVal, newVal) -> onManualDrawingRemainingClicksChange();

    public SuperChart() {
        getStyleClass().add("super-chart");

        activeManualDrawing.addListener((obs, oldVal, newVal) -> onActiveManualDrawingChange(oldVal, newVal));
    }

    private void onActiveManualDrawingChange(ManualDrawing oldVal, ManualDrawing newVal) {
        pseudoClassStateChanged(DRAWING_PSEUDO_CLASS, newVal != null);

        if (oldVal != null) {
            oldVal.remainingClicksProperty().removeListener(manualDrawingRemainingClicksChangeListener);
        }
        if (newVal != null) {
            newVal.remainingClicksProperty().addListener(manualDrawingRemainingClicksChangeListener);
            onManualDrawingRemainingClicksChange();
        }
    }

    private void onManualDrawingRemainingClicksChange() {
        if (getActiveManualDrawing().isDone()) {
            getDrawings().add(getActiveManualDrawing().getUnderlying());
            setActiveManualDrawing(null);
        }
    }

    public ManualDrawing getActiveManualDrawing() {
        return activeManualDrawing.get();
    }

    /**
     * The {@link ManualDrawing}s underlying {@link Drawing} will automatically be added to
     * {@link SuperChart#getDrawings()} once it is done.
     */
    public ObjectProperty<ManualDrawing> activeManualDrawingProperty() {
        return activeManualDrawing;
    }

    public void setActiveManualDrawing(ManualDrawing activeManualDrawing) {
        this.activeManualDrawing.set(activeManualDrawing);
    }

    public Consumer<Indicator> getIndicatorSelectionHandler() {
        return indicatorSelectionHandler.get();
    }

    /**
     * This handler will be called whenever an {@link Indicator} is selected.
     */
    public ObjectProperty<Consumer<Indicator>> indicatorSelectionHandlerProperty() {
        return indicatorSelectionHandler;
    }

    public void setIndicatorSelectionHandler(Consumer<Indicator> indicatorSelectionHandler) {
        this.indicatorSelectionHandler.set(indicatorSelectionHandler);
    }

    /**
     * The {@link Indicator}s which can be added to this {@link SuperChart}.
     */
    public ObservableList<Indicator> getIndicators() {
        return indicators;
    }

    public TimeAxisPosConverter getTimeAxisPosConverter() {
        return timeAxisPosConverter.get();
    }

    public ObjectProperty<TimeAxisPosConverter> timeAxisPosConverterProperty() {
        return timeAxisPosConverter;
    }

    public void setTimeAxisPosConverter(TimeAxisPosConverter timeAxisPosConverter) {
        this.timeAxisPosConverter.set(timeAxisPosConverter);
    }

    public ObservableList<Drawing> getDrawings() {
        return drawings;
    }

    public Color getMouseCrosshairsColor() {
        return mouseCrosshairsColor.get();
    }

    public StyleableObjectProperty<Color> mouseCrosshairsColorProperty() {
        return mouseCrosshairsColor;
    }

    public void setMouseCrosshairsColor(Color mouseCrosshairsColor) {
        this.mouseCrosshairsColor.set(mouseCrosshairsColor);
    }

    public Color getWatermarkColor() {
        return watermarkColor.get();
    }

    public StyleableObjectProperty<Color> watermarkColorProperty() {
        return watermarkColor;
    }

    public void setWatermarkColor(Color watermarkColor) {
        this.watermarkColor.set(watermarkColor);
    }

    public Bounds getViewport() {
        return viewport.get();
    }

    /**
     * The viewport defines the {@link SuperChart}s display bounds.
     */
    public ObjectProperty<Bounds> viewportProperty() {
        return viewport;
    }

    public void setViewport(Bounds viewport) {
        this.viewport.set(viewport);
    }

    public String getLongName() {
        return longName.getName();
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getShortName() {
        return shortName.get();
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SuperChartSkin(this);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData();
    }
}
