package charting.gui.superchart.indicatorspane;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class IndicatorListCellGraphic extends Label {
    private static final StyleablePropertyFactory<IndicatorListCellGraphic> FACTORY =
            new StyleablePropertyFactory<>(Label.getClassCssMetaData());

    private static final CssMetaData<IndicatorListCellGraphic, Color> SELECTION_HIGHLIGHT_COLOR =
            FACTORY.createColorCssMetaData("-fx-selection-highlight-color", s -> s.selectionHighlightColor,
                    new Color(1, 1, 1, 0.5));
    private static final CssMetaData<IndicatorListCellGraphic, Duration> SELECTION_HIGHLIGHT_DURATION =
            FACTORY.createDurationCssMetaData("-fx-selection-highlight-duration",
                    s -> s.selectionHighlightDuration, Duration.millis(500));

    private final ObjectProperty<Indicator> indicator = new SimpleObjectProperty<>();

    private final StyleableObjectProperty<Color> selectionHighlightColor = new SimpleStyleableObjectProperty<>(
            SELECTION_HIGHLIGHT_COLOR, this, "selectionHighlightColor",
            SELECTION_HIGHLIGHT_COLOR.getInitialValue(this));
    private final StyleableObjectProperty<Duration> selectionHighlightDuration = new SimpleStyleableObjectProperty<>(
            SELECTION_HIGHLIGHT_DURATION, this, "selectionHighlightDuration",
            SELECTION_HIGHLIGHT_DURATION.getInitialValue(this));

    private final DoubleProperty selectionHighlightOpacityFactor = new SimpleDoubleProperty();
    private BackgroundFill highlightFill;
    private final Timeline timeline = new Timeline();

    IndicatorListCellGraphic() {
        setMaxWidth(Double.POSITIVE_INFINITY);

        getStyleClass().add("indicators-pane-list-entry");

        indicator.addListener((obs, oldVal, newVal) -> setText(newVal == null ? "" : newVal.toString()));

        selectionHighlightOpacityFactor.addListener((obs, oldVal, newVal) -> mixInHighlightColor());
    }

    private void mixInHighlightColor() {
        Optional<Background> b = Optional.ofNullable(getBackground());
        List<BackgroundFill> fills = b.map(Background::getFills).map(ArrayList::new).orElse(new ArrayList<>());
        List<BackgroundImage> images = b.map(Background::getImages).orElse(null);

        mixInHighlightColor(fills);

        setBackground(new Background(fills, images));
    }

    private void mixInHighlightColor(List<BackgroundFill> fills) {
        if (highlightFill != null && fills.contains(highlightFill)) {
            fills.remove(fills.lastIndexOf(highlightFill));
        }

        if (selectionHighlightOpacityFactor.get() == 0) {
            highlightFill = null;
            return;
        }

        Color c = selectionHighlightColorProperty().get().deriveColor(
                0, 1, 1, selectionHighlightOpacityFactor.get());
        if (fills.isEmpty()) {
            highlightFill = new BackgroundFill(c, null, null);
        } else {
            BackgroundFill f = fills.get(fills.size() - 1);
            highlightFill = new BackgroundFill(c, f.getRadii(), f.getInsets());
        }

        fills.add(highlightFill);
    }

    void playSelectionAnimation() {
        selectionHighlightOpacityFactor.set(1);

        Duration d = Optional.ofNullable(selectionHighlightDurationProperty().getValue())
                .orElse(SELECTION_HIGHLIGHT_DURATION.getInitialValue(this));
        KeyFrame f = new KeyFrame(d, new KeyValue(selectionHighlightOpacityFactor, 0));
        timeline.getKeyFrames().setAll(f);
        timeline.playFromStart();
    }

    ObjectProperty<Indicator> indicatorProperty() {
        return indicator;
    }

    ObjectProperty<Color> selectionHighlightColorProperty() {
        return selectionHighlightColor;
    }

    StyleableObjectProperty<Duration> selectionHighlightDurationProperty() {
        return selectionHighlightDuration;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData();
    }
}
