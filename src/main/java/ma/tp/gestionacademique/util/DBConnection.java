package ma.tp.gestionacademique.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_academique";
    private static final String USER = "root"; // ton utilisateur MySQL
    private static final String PASSWORD = ""; // ton mot de passe MySQL

    private static Connection connection;

    // Méthode pour obtenir la connexion
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
