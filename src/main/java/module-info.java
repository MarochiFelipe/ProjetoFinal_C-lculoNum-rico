module org.example.projetofinal_calcnumerico {
    requires javafx.controls;
    requires javafx.fxml;
    requires exp4j;

    opens org.example.projetofinal_calcnumerico to javafx.fxml;
    opens org.example.projetofinal_calcnumerico.controller to javafx.fxml;

    exports org.example.projetofinal_calcnumerico;
    exports org.example.projetofinal_calcnumerico.controller;
    exports org.example.projetofinal_calcnumerico.model;
    exports org.example.projetofinal_calcnumerico.util;
}