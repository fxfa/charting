package charting.gui.chart;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Displays a list of {@link Drawing Drawings}, as well as a legend.
 * The viewport of the Chart can be scaled and translated via mouse actions.
 */
public class Chart extends Control {
    private final ObservableList<Drawing> drawings = FXCollections.observableArrayList();

    private final StringProperty title = new SimpleStringProperty("");
    private final ObjectProperty<Bounds> viewport =
            new SimpleObjectProperty<>(new BoundingBox(0, 0, 100, 100));

    private final DoubleProperty zoomPerScrollTickX = new SimpleDoubleProperty(1.05);
    private final DoubleProperty zoomPerScrollTickY = new SimpleDoubleProperty(1.05);

    public Chart(String title) {
        this();
        setTitle(title);
    }

    public Chart() {
        getStyleClass().add("chart");

        setMinSize(0, 0);
        setPrefSize(1200, 800);
        setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Shifts the drawings. As a consequence, the viewport will be translated by -distance
     */
    public void shiftDrawings(double viewportDistanceX, double viewportDistanceY) {
        Bounds v = getViewport();

        setViewport(new BoundingBox(v.getMinX() - viewportDistanceX, v.getMinY() - viewportDistanceY,
                v.getWidth(), v.getHeight()));
    }

    /**
     * Zooms the drawings. As a consequence the viewport will be scaled by 1/factor.
     */
    public void zoomDrawings(double factorX, double factorY, double viewportPivotX, double viewportPivotY) {
        Bounds v = getViewport();

        factorX = 1 / factorX;
        factorY = 1 / factorY;

        double x = factorX * v.getMinX() + (1 - factorX) * viewportPivotX;
        double y = factorY * v.getMinY() + (1 - factorY) * viewportPivotY;
        double w = v.getMaxX() * factorX - v.getMinX() * factorX;
        double h = v.getMaxY() * factorY - v.getMinY() * factorY;

        setViewport(new BoundingBox(x, y, w, h));
    }

    public ObservableList<Drawing> getDrawings() {
        return drawings;
    }

    public double getZoomPerScrollTickX() {
        return zoomPerScrollTickX.get();
    }

    /**
     * The factor by which the drawings will be zoomed when a scroll event into the x direction occurs.
     */
    public DoubleProperty zoomPerScrollTickXProperty() {
        return zoomPerScrollTickX;
    }

    public void setZoomPerScrollTickX(double zoomPerScrollTickX) {
        this.zoomPerScrollTickX.set(zoomPerScrollTickX);
    }

    public double getZoomPerScrollTickY() {
        return zoomPerScrollTickY.get();
    }

    /**
     * The factor by which the drawings will be zoomed when a scroll event into the y direction occurs.
     */
    public DoubleProperty zoomPerScrollTickYProperty() {
        return zoomPerScrollTickY;
    }

    public void setZoomPerScrollTickY(double zoomPerScrollTickY) {
        this.zoomPerScrollTickY.set(zoomPerScrollTickY);
    }

    public String getTitle() {
        return titleProperty().get();
    }

    /**
     * The title of the {@link Chart} which will be displayed in the legend.
     */
    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        titleProperty().set(title);
    }

    public Bounds getViewport() {
        return viewportProperty().get();
    }

    /**
     * The viewport defines the {@link Chart}s display bounds.
     */
    public ObjectProperty<Bounds> viewportProperty() {
        return viewport;
    }

    public void setViewport(Bounds viewport) {
        viewportProperty().set(viewport);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ChartSkin(this);
    }
}
