package ma.tp.gestionacademique.model;

public class Eleve {
    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String statut; // ACTIF / SUSPENDU
    private int filiereId;
    private String filiereNom;

    public Eleve() {}

    public Eleve(int id, String matricule, String nom, String prenom, String email, String statut, int filiereId) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.statut = statut;
        this.filiereId = filiereId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getFiliereId() { return filiereId; }
    public void setFiliereId(int filiereId) { this.filiereId = filiereId; }
    public String getFiliereNom() {
        return filiereNom;
    }

    public void setFiliereNom(String filiereNom) {
        this.filiereNom = filiereNom;
    }

}
