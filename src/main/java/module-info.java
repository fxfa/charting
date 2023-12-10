module charting {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;

    exports charting.gui.util to javafx.graphics;
    opens charting.gui.util to javafx.fxml;
    exports charting.gui.chart to javafx.graphics;
    opens charting.gui.chart to javafx.fxml;
    exports charting.gui.drawings to javafx.graphics;
    opens charting.gui.drawings to javafx.fxml;
    exports charting.gui.superchart.indicatorspane to javafx.graphics;
    opens charting.gui.superchart.indicatorspane to javafx.fxml;
}