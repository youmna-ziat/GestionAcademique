package ma.tp.gestionacademique.dao;
import ma.tp.gestionacademique.model.Cours;
import ma.tp.gestionacademique.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursDAO {

    // CREATE
    public void addCours(Cours c) throws SQLException {
        String sql = "INSERT INTO cours (code, intitule) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getIntitule());
            ps.executeUpdate();
        }
    }

    // READ
    public List<Cours> getAllCours() throws SQLException {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT * FROM cours";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cours c = new Cours(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("intitule")
                );
                coursList.add(c);
            }
        }
        return coursList;
    }

    // UPDATE
    public void updateCours(Cours c) throws SQLException {
        String sql = "UPDATE cours SET code=?, intitule=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getIntitule());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void deleteCours(int id) throws SQLException {
        String sql = "DELETE FROM cours WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
