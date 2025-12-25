package ma.tp.gestionacademique.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Filiere {
    private int id;
    private String code;
    private String nom;
    private String description;

    // ===== Cours associés =====
    private List<Cours> coursAssocies = new ArrayList<>();

    public Filiere() {}

    public Filiere(int id, String code, String nom, String description) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
    }

    // ===== Getters & Setters =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // ===== Cours associés =====
    public List<Cours> getCoursAssocies() {
        return coursAssocies;
    }

    public void setCoursAssocies(List<Cours> coursAssocies) {
        this.coursAssocies = coursAssocies;
    }

    // Retourne la liste des noms des cours (pour TableView)
    public List<String> getCoursNoms() {
        List<String> noms = new ArrayList<>();
        for (Cours c : coursAssocies) {
            noms.add(c.getIntitule()); // <-- ici, on utilise getIntitule()
        }
        return noms;
    }

}
