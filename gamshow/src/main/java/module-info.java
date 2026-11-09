module csa.sti {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;

    opens csa.sti to javafx.fxml;
    exports csa.sti;
}
