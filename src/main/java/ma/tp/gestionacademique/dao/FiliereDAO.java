package ma.tp.gestionacademique.dao;
import ma.tp.gestionacademique.model.Cours;
import ma.tp.gestionacademique.model.Filiere;
import ma.tp.gestionacademique.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FiliereDAO {

    // CREATE : Ajouter une nouvelle filière
    public void addFiliere(Filiere f) throws SQLException {
        String sql = "INSERT INTO filiere (code, nom, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getNom());
            ps.setString(3, f.getDescription());
            ps.executeUpdate();
        }
    }

    public void addFiliereWithCours(Filiere f, List<Cours> coursList) throws SQLException {
        String sqlFiliere = "INSERT INTO filiere (code, nom, description) VALUES (?, ?, ?)";
        String sqlFiliereCours = "INSERT INTO filiere_cours (filiere_id, cours_id) VALUES (?, ?)";

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement psFiliere = conn.prepareStatement(sqlFiliere, Statement.RETURN_GENERATED_KEYS)) {
            psFiliere.setString(1, f.getCode());
            psFiliere.setString(2, f.getNom());
            psFiliere.setString(3, f.getDescription());
            psFiliere.executeUpdate();

            ResultSet rs = psFiliere.getGeneratedKeys();
            if (rs.next()) {
                f.setId(rs.getInt(1));
            }
        }

        try (PreparedStatement psFiliereCours = conn.prepareStatement(sqlFiliereCours)) {
            for (Cours c : coursList) {
                psFiliereCours.setInt(1, f.getId());
                psFiliereCours.setInt(2, c.getId());
                psFiliereCours.addBatch();
            }
            psFiliereCours.executeBatch();
        }

        conn.commit();
        conn.setAutoCommit(true);
        conn.close();
    }

    public void addCoursToFiliere(int filiereId, List<Cours> coursList) throws SQLException {
        String sql = "INSERT INTO filiere_cours (filiere_id, cours_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Cours c : coursList) {
                ps.setInt(1, filiereId);
                ps.setInt(2, c.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    // READ : Récupérer toutes les filières
    public List<Filiere> getAllFilieres() throws SQLException {
        List<Filiere> filieres = new ArrayList<>();
        String sql = "SELECT * FROM filiere";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                filieres.add(new Filiere(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("nom"),
                        rs.getString("description")
                ));
            }
        }
        return filieres;
    }


    // UPDATE : Modifier une filière
    public void updateFiliere(Filiere f) throws SQLException {
        String sql = "UPDATE filiere SET code=?, nom=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getNom());
            ps.setString(3, f.getDescription());
            ps.setInt(4, f.getId());
            ps.executeUpdate();
        }
    }

    public void updateFiliereCours(int filiereId, List<Cours> coursList) throws SQLException {
        String deleteSql = "DELETE FROM filiere_cours WHERE filiere_id = ?";
        String insertSql = "INSERT INTO filiere_cours (filiere_id, cours_id) VALUES (?, ?)";

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try (
                PreparedStatement psDelete = conn.prepareStatement(deleteSql);
                PreparedStatement psInsert = conn.prepareStatement(insertSql)
        ) {
            psDelete.setInt(1, filiereId);
            psDelete.executeUpdate();

            for (Cours c : coursList) {
                psInsert.setInt(1, filiereId);
                psInsert.setInt(2, c.getId());
                psInsert.addBatch();
            }
            psInsert.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }


    // DELETE : Supprimer une filière (avec règle métier)
    public boolean deleteFiliere(int id) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM eleve WHERE filiere_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Ne peut pas supprimer si filière contient des élèves
            }
        }

        String sql = "DELETE FROM filiere WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        return true; // Suppression réussie
    }

    public List<Cours> getCoursByFiliereId(int filiereId) {
        List<Cours> coursList = new ArrayList<>();

        String sql = """
        SELECT c.id, c.code, c.intitule
        FROM cours c
        JOIN filiere_cours fc ON c.id = fc.cours_id
        WHERE fc.filiere_id = ?
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, filiereId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Cours c = new Cours(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("intitule")
                );
                c.setFiliereId(filiereId); // setter l'id de la filière
                coursList.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coursList;
    }


    public boolean codeExiste(String code, Integer filiereId) throws SQLException {

        String sql = """
        SELECT COUNT(*)
        FROM filiere
        WHERE code = ?
        AND (? IS NULL OR id <> ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setObject(2, filiereId);
            ps.setObject(3, filiereId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }


}
