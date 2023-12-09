module charting {
    requires javafx.controls;
    requires javafx.fxml;

    exports charting.gui.util to javafx.graphics;
    opens charting.gui.util to javafx.fxml;
    exports charting.gui.chart to javafx.graphics;
    opens charting.gui.chart to javafx.fxml;
}