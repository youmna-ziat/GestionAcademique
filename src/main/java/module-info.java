module ma.tp.gestionacademique {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens ma.tp.gestionacademique to javafx.fxml;
    opens ma.tp.gestionacademique.controller to javafx.fxml; // <-- ajouter cette ligne
    exports ma.tp.gestionacademique;
    exports ma.tp.gestionacademique.model;
    exports ma.tp.gestionacademique.dao;
    exports ma.tp.gestionacademique.util;
}
