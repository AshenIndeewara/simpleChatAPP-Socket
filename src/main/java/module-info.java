module lk.ijse.simplechatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens lk.ijse.simplechatapp to javafx.fxml;
    exports lk.ijse.simplechatapp;
}