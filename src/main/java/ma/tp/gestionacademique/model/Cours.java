package ma.tp.gestionacademique.model;

public class Cours {
    private int id;
    private String code;
    private String intitule;
    private int filiereId;
    public Cours() {}

    public Cours(int id, String code, String intitule) {
        this.id = id;
        this.code = code;
        this.intitule = intitule;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public int getFiliereId() { return filiereId; }
    public void setFiliereId(int filiereId) { this.filiereId = filiereId; }
    @Override
    public String toString() {
        return intitule;  // ou "code + " - " + intitule" si tu veux afficher les deux
    }
}

