package ma.tp.gestionacademique.dao;
import ma.tp.gestionacademique.model.Cours;
import ma.tp.gestionacademique.model.Eleve;
import ma.tp.gestionacademique.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EleveDAO {

    // CREATE : Ajouter un élève
    public void addEleve(Eleve e) throws SQLException {
        String sql = "INSERT INTO eleve (matricule, nom, prenom, email, statut, filiere_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getMatricule());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getStatut());
            ps.setInt(6, e.getFiliereId());
            ps.executeUpdate();

            // Récupérer l'id généré
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                e.setId(rs.getInt(1));
            }
        }
    }

    public void addEleveWithCours(Eleve e, List<Cours> coursList) throws SQLException {
        String sqlEleve = "INSERT INTO eleve (matricule, nom, prenom, email, statut, filiere_id) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEleveCours = "INSERT INTO eleve_cours (eleve_id, cours_id) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // début transaction

            // 1️⃣ Ajouter l'élève
            try (PreparedStatement psEleve = conn.prepareStatement(sqlEleve, Statement.RETURN_GENERATED_KEYS)) {
                psEleve.setString(1, e.getMatricule());
                psEleve.setString(2, e.getNom());
                psEleve.setString(3, e.getPrenom());
                psEleve.setString(4, e.getEmail());
                psEleve.setString(5, e.getStatut());
                psEleve.setInt(6, e.getFiliereId());
                psEleve.executeUpdate();

                ResultSet rs = psEleve.getGeneratedKeys();
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }

            // 2️⃣ Inscrire aux cours sélectionnés
            try (PreparedStatement psEleveCours = conn.prepareStatement(sqlEleveCours)) {
                for (Cours c : coursList) {
                    // Vérifier que le cours appartient bien à la filière
                    if (c.getFiliereId() != e.getFiliereId()) {
                        throw new SQLException("Le cours " + c.getIntitule() + " n'appartient pas à la filière de l'élève.");
                    }
                    psEleveCours.setInt(1, e.getId());
                    psEleveCours.setInt(2, c.getId());
                    psEleveCours.addBatch();
                }
                psEleveCours.executeBatch();
            }

            conn.commit(); // tout ok, valider transaction

        } catch (SQLException ex) {
            if (conn != null) conn.rollback(); // rollback si erreur
            throw ex;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) conn.close();
        }
    }



    // READ
    public List<Eleve> getAllEleves() throws SQLException {
        List<Eleve> eleves = new ArrayList<>();
        String sql = "SELECT e.*, f.nom AS filiere_nom\n" +
                "FROM eleve e\n" +
                "LEFT JOIN filiere f ON e.filiere_id = f.id\n";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Eleve e = new Eleve(
                        rs.getInt("id"),
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("statut"),
                        rs.getInt("filiere_id")

                );
                e.setFiliereNom(rs.getString("filiere_nom"));
                eleves.add(e);
            }
        }
        return eleves;
    }

    // UPDATE
    public void updateEleve(Eleve e) throws SQLException {
        String sql = "UPDATE eleve SET matricule=?, nom=?, prenom=?, email=?, statut=?, filiere_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getMatricule());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getStatut());
            ps.setInt(6, e.getFiliereId());
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        }
    }

    public void updateEleveWithCours(Eleve e, List<Cours> coursList) throws SQLException {
        String sqlUpdateEleve = "UPDATE eleve SET matricule=?, nom=?, prenom=?, email=?, statut=?, filiere_id=? WHERE id=?";
        String sqlDeleteCours = "DELETE FROM eleve_cours WHERE eleve_id=?";
        String sqlInsertCours = "INSERT INTO eleve_cours (eleve_id, cours_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // transaction

            // 1️⃣ Mettre à jour l'élève
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateEleve)) {
                ps.setString(1, e.getMatricule());
                ps.setString(2, e.getNom());
                ps.setString(3, e.getPrenom());
                ps.setString(4, e.getEmail());
                ps.setString(5, e.getStatut());
                ps.setInt(6, e.getFiliereId());
                ps.setInt(7, e.getId());
                ps.executeUpdate();
            }

            // 2️⃣ Supprimer les cours existants
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteCours)) {
                ps.setInt(1, e.getId());
                ps.executeUpdate();
            }

            // 3️⃣ Ajouter les nouveaux cours
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertCours)) {
                for (Cours c : coursList) {
                    if (c.getFiliereId() != e.getFiliereId()) {
                        throw new SQLException("Le cours " + c.getIntitule() + " n'appartient pas à la filière de l'élève.");
                    }
                    ps.setInt(1, e.getId());
                    ps.setInt(2, c.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit(); // valider transaction

        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


    // DELETE
    public void deleteEleve(int id) throws SQLException {
        String sql = "DELETE FROM eleve WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Inscrire un élève à des cours (transactionnelle)
    public void inscrireCours(int eleveId, List<Integer> coursIds) throws SQLException {
        String checkStatut = "SELECT statut FROM eleve WHERE id=?";
        String insertSql = "INSERT INTO eleve_cours (eleve_id, cours_id) VALUES (?, ?)";

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Vérifier que l'élève est ACTIF
            try (PreparedStatement psCheck = conn.prepareStatement(checkStatut)) {
                psCheck.setInt(1, eleveId);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getString("statut").equals("SUSPENDU")) {
                    throw new SQLException("Élève suspendu, impossible de l’inscrire à un cours.");
                }
            }

            // Inscrire l'élève à chaque cours
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (int coursId : coursIds) {
                    ps.setInt(1, eleveId);
                    ps.setInt(2, coursId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}