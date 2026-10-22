module csa.sti {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens csa.sti to javafx.fxml;
    exports csa.sti;
}
