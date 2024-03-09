package charting.gui.superchart.indicatorspane;

import charting.gui.util.GraphicListCell;
import charting.gui.util.NodeDragDistance;
import charting.gui.util.NodeLoader;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class IndicatorsPaneSkin extends SkinBase<IndicatorsPane> {
    private static final PseudoClass DRAGGING_PSEUDO_CLASS = PseudoClass.getPseudoClass("dragging");

    private final VBox root;
    @FXML
    private HBox header;
    @FXML
    private ListView<Indicator> listView;

    private final Set<IndicatorListCellGraphic> listCellGraphics = Collections.newSetFromMap(new WeakHashMap<>());

    private final NodeDragDistance dragDistance;

    private final ChangeListener<Boolean> managedListener = (obs, oldVal, newVal) -> listView.requestFocus();

    public IndicatorsPaneSkin(IndicatorsPane indicatorsPane) {
        super(indicatorsPane);

        root = NodeLoader.loadNew(this);

        listView.setItems(indicatorsPane.getIndicators());
        listView.focusedProperty().addListener((obs, oldVal, newVal) -> onListViewFocusChange(newVal));
        listView.setCellFactory(l -> createListCell());
        listView.addEventHandler(KeyEvent.KEY_PRESSED, this::onListViewKeyPress);

        // Necessary to make cell selection via arrow keys work properly.
        indicatorsPane.managedProperty().addListener(managedListener);
        if (indicatorsPane.isManaged()) {
            listView.requestFocus();
        }

        dragDistance = new NodeDragDistance(header);
        dragDistance.draggingProperty().addListener((obs, oldVal, newVal) ->
                pseudoClassStateChanged(DRAGGING_PSEUDO_CLASS, newVal));
        dragDistance.dragDistanceXProperty().addListener((obs, oldVal, newVal) -> indicatorsPane.setTranslateX(
                indicatorsPane.getTranslateX() + newVal.doubleValue() - oldVal.doubleValue()));
        dragDistance.dragDistanceYProperty().addListener((obs, oldVal, newVal) -> indicatorsPane.setTranslateY(
                indicatorsPane.getTranslateY() + newVal.doubleValue() - oldVal.doubleValue()));

        getChildren().add(root);
    }

    private void onListViewFocusChange(boolean newVal) {
        if (newVal) {
            listView.getSelectionModel().clearSelection();
        }
    }

    private ListCell<Indicator> createListCell() {
        GraphicListCell<Indicator, IndicatorListCellGraphic> c = new GraphicListCell<>(new IndicatorListCellGraphic());
        c.getItemGraphic().indicatorProperty().bind(c.itemProperty());

        c.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (c.isEmpty()) {
                listView.getSelectionModel().clearSelection();
            } else {
                onIndicatorCellClick(c.getItemGraphic());
            }
        });

        listCellGraphics.add(c.getItemGraphic());

        return c;
    }

    private void onListViewKeyPress(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER)) {
            for (IndicatorListCellGraphic g : listCellGraphics) {
                if (listView.getSelectionModel().getSelectedItem() == g.indicatorProperty().get()) {
                    onIndicatorCellClick(g);
                    break;
                }
            }
            e.consume();
        }
    }

    @FXML
    private void onCloseButtonClick() {
        if (getSkinnable() != null && getSkinnable().getCloseButtonActionHandler() != null) {
            getSkinnable().getCloseButtonActionHandler().run();
        }
    }

    private void onIndicatorCellClick(IndicatorListCellGraphic graphic) {
        graphic.playSelectionAnimation();

        if (getSkinnable() != null && getSkinnable().getIndicatorSelectionHandler() != null) {
            getSkinnable().getIndicatorSelectionHandler().accept(graphic.indicatorProperty().get());
        }
    }

    @Override
    public void dispose() {
        root.minWidthProperty().unbind();
        root.minHeightProperty().unbind();

        getSkinnable().managedProperty().removeListener(managedListener);

        dragDistance.dispose();

        getChildren().remove(root);

        super.dispose();
    }
}
