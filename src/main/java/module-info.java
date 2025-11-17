module com.vinhtt.jarlauncher {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop; // Cho AWT/Swing interop nếu cần (ImageIO)

    // Mở package cho JavaFX FXML load
    opens com.vinhtt.jarlauncher to javafx.fxml;
    opens com.vinhtt.jarlauncher.view to javafx.fxml;

    // Mở model cho Jackson serialize/deserialize
    opens com.vinhtt.jarlauncher.model to com.fasterxml.jackson.databind;

    exports com.vinhtt.jarlauncher;
}