module org.example.projetofinal_calcnumerico {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.projetofinal_calcnumerico to javafx.fxml;
    exports org.example.projetofinal_calcnumerico;
}