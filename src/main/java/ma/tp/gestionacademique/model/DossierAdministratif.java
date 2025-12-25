package ma.tp.gestionacademique.model;

import java.sql.Date;

public class DossierAdministratif {
    private int id;
    private String numeroInscription;
    private Date dateCreation;
    private int eleveId;

    public DossierAdministratif() {}

    public DossierAdministratif(int id, String numeroInscription, Date dateCreation, int eleveId) {
        this.id = id;
        this.numeroInscription = numeroInscription;
        this.dateCreation = dateCreation;
        this.eleveId = eleveId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumeroInscription() { return numeroInscription; }
    public void setNumeroInscription(String numeroInscription) { this.numeroInscription = numeroInscription; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public int getEleveId() { return eleveId; }
    public void setEleveId(int eleveId) { this.eleveId = eleveId; }

    @Override
    public String toString() {
        return "Élève ID: " + eleveId +
                " | Numéro: " + numeroInscription +
                " | Date: " + dateCreation;
    }

}
