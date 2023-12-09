package charting.gui.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * A {@link ListCell} without any text that uses another node as its graphic.
 */
public class GraphicListCell<T, G extends Node> extends ListCell<T> {
    private final ObjectProperty<G> itemGraphic = new SimpleObjectProperty<>();

    public GraphicListCell() {
        setText(null);
        setPadding(Insets.EMPTY);

        itemGraphic.addListener((obs, oldVal, newVal) -> updateGraphic());
    }

    public GraphicListCell(G itemGraphic) {
        this();
        setItemGraphic(itemGraphic);
    }

    private void updateGraphic() {
        if (!isEmpty()) {
            setGraphic(getItemGraphic());
        }
    }

    @Override
    protected final void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            updateGraphic();
        }
    }

    public G getItemGraphic() {
        return itemGraphic.get();
    }

    public ObjectProperty<G> itemGraphicProperty() {
        return itemGraphic;
    }

    public void setItemGraphic(G itemGraphic) {
        this.itemGraphic.set(itemGraphic);
    }
}
