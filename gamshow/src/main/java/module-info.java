module csa.sti {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens csa.sti.ui to javafx.fxml;
    exports csa.sti.ui;
}
