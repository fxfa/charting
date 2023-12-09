package charting.gui.util;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Provides a way to check if a node is being rendered on a best effort basis.
 * Listening to the relevant node property changes is done via {@link WeakChangeListener WeakChangeListeners}.
 */
public final class NodeRenderingState {
    private final Node node;

    private final ReadOnlyBooleanWrapper rendering = new ReadOnlyBooleanWrapper(false);

    private final ChangeListener<Scene> sceneListener =
            (obs, oldVal, newVal) -> onSceneChange(oldVal, newVal);
    private final ChangeListener<Window> windowListener =
            (obs, oldVal, newVal) -> onWindowChange(oldVal, newVal);
    private final ChangeListener<Boolean> windowShowingListener =
            (obs, oldVal, newVal) -> onWindowShowingChange(newVal);
    private final ChangeListener<Boolean> windowIconifiedListener =
            (obs, oldVal, newVal) -> onWindowIconifiedChange(newVal);
    private final ChangeListener<Boolean> visibleListener =
            (obs, oldVal, newVal) -> onVisibleChange(newVal);
    private final ChangeListener<Boolean> managedListener =
            (obs, oldVal, newVal) -> onManagedChange(newVal);

    private final ChangeListener<Scene> weakSceneListener =
            new WeakChangeListener<>(sceneListener);
    private final ChangeListener<Window> weakWindowListener =
            new WeakChangeListener<>(windowListener);
    private final ChangeListener<Boolean> weakWindowShowingListener =
            new WeakChangeListener<>(windowShowingListener);
    private final ChangeListener<Boolean> weakWindowIconifiedListener =
            new WeakChangeListener<>(windowIconifiedListener);
    private final ChangeListener<Boolean> weakVisibleListener =
            new WeakChangeListener<>(visibleListener);
    private final ChangeListener<Boolean> weakManagedListener =
            new WeakChangeListener<>(managedListener);

    private boolean inScene;
    private boolean inWindow;
    private boolean windowShowing;
    private boolean windowNotIconified;
    private boolean visible;
    private boolean managed;

    public NodeRenderingState(Node node) {
        this.node = node;

        node.sceneProperty().addListener(sceneListener);
        onSceneChange(null, node.getScene());

        node.visibleProperty().addListener(weakVisibleListener);
        onVisibleChange(node.isVisible());

        node.managedProperty().addListener(weakManagedListener);
        onManagedChange(node.isManaged());
    }

    private void updateRendering() {
        rendering.set(inScene && inWindow && windowShowing && windowNotIconified && visible && managed);
    }

    private void onSceneChange(Scene oldScene, Scene newScene) {
        if (oldScene != null) {
            oldScene.windowProperty().removeListener(weakWindowListener);
        }

        if (newScene != null) {
            newScene.windowProperty().addListener(weakWindowListener);
            onWindowChange(oldScene == null ? null : oldScene.getWindow(), newScene.getWindow());
        }

        inScene = newScene != null;
        updateRendering();
    }

    private void onWindowChange(Window oldWindow, Window newWindow) {
        if (oldWindow != null) {
            oldWindow.showingProperty().removeListener(weakWindowShowingListener);

            if (oldWindow instanceof Stage s) {
                s.iconifiedProperty().removeListener(weakWindowIconifiedListener);
            }
        }

        if (newWindow != null) {
            newWindow.showingProperty().addListener(weakWindowShowingListener);
            onWindowShowingChange(newWindow.isShowing());

            if (newWindow instanceof Stage s) {
                s.iconifiedProperty().addListener(weakWindowIconifiedListener);
                onWindowIconifiedChange(s.isIconified());
            }
        }

        inWindow = newWindow != null;
        updateRendering();
    }

    private void onWindowShowingChange(boolean newVal) {
        windowShowing = newVal;
        updateRendering();
    }

    private void onWindowIconifiedChange(boolean newVal) {
        windowNotIconified = !newVal;
        updateRendering();
    }

    private void onVisibleChange(boolean newVal) {
        visible = newVal;
        updateRendering();
    }

    private void onManagedChange(boolean newVal) {
        managed = newVal;
        updateRendering();
    }

    public Node getNode() {
        return node;
    }

    public ReadOnlyBooleanProperty renderingProperty() {
        return rendering.getReadOnlyProperty();
    }

    public boolean isRendering() {
        return rendering.get();
    }

    /**
     * Removes all listeners from the node. Consequently, no more updates regarding the rendering state will be given.
     */
    public void dispose() {
        node.sceneProperty().removeListener(weakSceneListener);
        node.visibleProperty().removeListener(weakVisibleListener);
        node.managedProperty().removeListener(weakManagedListener);

        if (node.getScene() != null) {
            node.getScene().windowProperty().removeListener(weakWindowListener);

            if (node.getScene().getWindow() != null) {
                node.getScene().getWindow().showingProperty().removeListener(weakWindowShowingListener);

                if (node.getScene().getWindow() instanceof Stage s) {
                    s.iconifiedProperty().removeListener(weakWindowIconifiedListener);
                }
            }
        }
    }
}