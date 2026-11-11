module csa.sti {
    // JavaFX UI controls, FXML loading, and the graphics/application lifecycle.
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    // Fast Money retrieves survey data from Google Sheets over HTTP.
    requires java.net.http;

    // FXMLLoader uses reflection to inject controllers and invoke their handlers.
    opens csa.sti to javafx.fxml;
    // Make application types available to other modules.
    exports csa.sti;
}
