package ma.tp.gestionacademique.dao;
import ma.tp.gestionacademique.model.DossierAdministratif;
import ma.tp.gestionacademique.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DossierAdministratifDAO {

    // CREATE
    public void addDossier(DossierAdministratif d) throws SQLException {
        // Vérifier qu'il n'existe pas déjà un dossier pour cet élève
        String checkSql = "SELECT COUNT(*) FROM dossier_administratif WHERE eleve_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, d.getEleveId());
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Un dossier administratif existe déjà pour cet élève.");
            }
        }

        String sql = "INSERT INTO dossier_administratif (numero_inscription, date_creation, eleve_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNumeroInscription());
            ps.setDate(2, d.getDateCreation());
            ps.setInt(3, d.getEleveId());
            ps.executeUpdate();
        }
    }

    // READ
    public List<DossierAdministratif> getAllDossiers() throws SQLException {
        List<DossierAdministratif> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossier_administratif";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                DossierAdministratif d = new DossierAdministratif(
                        rs.getInt("id"),
                        rs.getString("numero_inscription"),
                        rs.getDate("date_creation"),
                        rs.getInt("eleve_id")
                );
                dossiers.add(d);
            }
        }
        return dossiers;
    }

    // UPDATE
    public void updateDossier(DossierAdministratif d) throws SQLException {
        String sql = "UPDATE dossier_administratif SET numero_inscription=?, date_creation=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNumeroInscription());
            ps.setDate(2, d.getDateCreation());
            ps.setInt(3, d.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void deleteDossier(int id) throws SQLException {
        String sql = "DELETE FROM dossier_administratif WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // READ : récupérer un dossier par l'ID de l'élève
    public DossierAdministratif getDossierByEleveId(int eleveId) throws SQLException {
        String sql = "SELECT * FROM dossier_administratif WHERE eleve_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DossierAdministratif(
                            rs.getInt("id"),
                            rs.getString("numero_inscription"),
                            rs.getDate("date_creation"),
                            rs.getInt("eleve_id")
                    );
                }
            }
        }
        return null; // aucun dossier trouvé
    }

    // Récupérer tous les dossiers d'un élève
    public List<DossierAdministratif> getAllDossiersByEleveId(int eleveId) throws SQLException {
        List<DossierAdministratif> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossier_administratif WHERE eleve_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DossierAdministratif d = new DossierAdministratif(
                            rs.getInt("id"),
                            rs.getString("numero_inscription"),
                            rs.getDate("date_creation"),
                            rs.getInt("eleve_id")
                    );
                    dossiers.add(d);
                }
            }
        }
        return dossiers;
    }

    public String getNomEleveById(int eleveId) throws SQLException {
        String sql = "SELECT nom, prenom FROM eleve WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom") + " " + rs.getString("prenom");
                }
            }
        }
        return "Inconnu";
    }


}
