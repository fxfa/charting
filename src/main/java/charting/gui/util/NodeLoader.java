package charting.gui.util;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public final class NodeLoader {
    private NodeLoader() {
    }

    /**
     * Loads a supplied object with an FXML by using the object as controller and root.
     * The FXML path will be calculated from the canonical name of the objects class
     * (e.g.: com.foo.ExampleClass -> com/foo/ExampleClass.fxml).
     */
    public static void loadExisting(Object instance) {
        loadExisting(getResourcePath(instance), instance);
    }

    /**
     * Loads a supplied object with an FXML by using the object as controller and root.
     */
    public static void loadExisting(String path, Object instance) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(NodeLoader.class.getResource(path));
        fxmlLoader.setController(instance);
        fxmlLoader.setRoot(instance);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Loads a new object from FXML and sets a supplied object as its controller.
     */
    public static <T> T loadNew(String path, Object controller) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(NodeLoader.class.getResource(path));
        fxmlLoader.setController(controller);

        T root;
        try {
            root = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return root;
    }

    /**
     * Loads a new object from FXML and sets a supplied object as its controller.
     * The FXML path will be calculated from the canonical name of the objects class
     * (e.g.: com.foo.ExampleClass -> com/foo/ExampleClass.fxml).
     */
    public static <T> T loadNew(Object controller) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(NodeLoader.class.getResource(getResourcePath(controller)));
        fxmlLoader.setController(controller);

        T root;
        try {
            root = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return root;
    }

    private static String getResourcePath(Object o) {
        return "/" + o.getClass().getCanonicalName().replace('.', '/') + ".fxml";
    }
}
