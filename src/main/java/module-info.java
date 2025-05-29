module adje.soro.diakite.diskut {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires mysql.connector.j;


    opens adje.soro.diakite.diskut to javafx.fxml;
    exports adje.soro.diakite.diskut;
    exports adje.soro.diakite.diskut.reunions.controller;
    opens adje.soro.diakite.diskut.reunions.controller to javafx.fxml;
}