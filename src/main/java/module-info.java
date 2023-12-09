module charting {
    requires javafx.controls;
    requires javafx.fxml;

    exports charting.gui.util to javafx.graphics;
    opens charting.gui.util to javafx.fxml;
}