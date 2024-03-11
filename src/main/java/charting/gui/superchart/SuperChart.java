package charting.gui.superchart;

import charting.gui.chart.Drawing;
import charting.gui.superchart.indicatorspane.Indicator;
import charting.util.Range;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
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

    private final ObservableList<Indicator> indicators = FXCollections.observableArrayList();
    private final ObjectProperty<Consumer<Indicator>> indicatorSelectionHandler = new SimpleObjectProperty<>();

    private final ObjectProperty<Range> timeAxis = new SimpleObjectProperty<>(new Range(0, 100));
    private final ObjectProperty<TimeAxisPosConverter> timeAxisPosConverter =
            new SimpleObjectProperty<>(TimeAxisPosConverter.of(Instant::getEpochSecond, Instant::ofEpochSecond));

    private final DoubleProperty legendX = new SimpleDoubleProperty();

    private final ObservableList<ChartContent> chartContents = FXCollections.observableArrayList();
    private final ObjectProperty<Drawing> userDrawing = new SimpleObjectProperty<>();

    public SuperChart() {
        getStyleClass().add("super-chart");
    }

    public double getLegendX() {
        return legendX.get();
    }

    public DoubleProperty legendXProperty() {
        return legendX;
    }

    public void setLegendX(double legendX) {
        this.legendX.set(legendX);
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

    public ObservableList<ChartContent> getChartContents() {
        return chartContents;
    }

    public Drawing getUserDrawing() {
        return userDrawing.get();
    }

    /**
     * A {@link Drawing} which will be added to a {@link ChartContent} when its specific sub chart is clicked.
     */
    public ObjectProperty<Drawing> userDrawingProperty() {
        return userDrawing;
    }

    public void setUserDrawing(Drawing userDrawing) {
        this.userDrawing.set(userDrawing);
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

    public TimeAxisPosConverter getTimeAxisPosConverter() {
        return timeAxisPosConverter.get();
    }

    public ObjectProperty<TimeAxisPosConverter> timeAxisPosConverterProperty() {
        return timeAxisPosConverter;
    }

    public void setTimeAxisPosConverter(TimeAxisPosConverter timeAxisPosConverter) {
        this.timeAxisPosConverter.set(timeAxisPosConverter);
    }

    public Range getTimeAxis() {
        return timeAxis.get();
    }

    public ObjectProperty<Range> timeAxisProperty() {
        return timeAxis;
    }

    public void setTimeAxis(Range timeAxis) {
        this.timeAxis.set(timeAxis);
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
