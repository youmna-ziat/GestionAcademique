# Gestion Académique – JavaFX & JDBC

##  Description
Application desktop de gestion académique développée en JavaFX avec JDBC pur et MySQL.
Elle permet de gérer les filières, les élèves, les cours et les dossiers administratifs
en respectant des règles métier strictes.

---

##  Architecture du projet

Le projet respecte une architecture MVC :

- model : entités métier (Eleve, Filiere, Cours, DossierAdministratif)
- dao : accès aux données via JDBC (PreparedStatement)
- controller : logique métier et navigation JavaFX
- view : interfaces graphiques JavaFX (FXML)
- MainApp : point d’entrée de l’application

---

##  Choix techniques

- Java 17
- JavaFX (FXML)
- JDBC pur avec PreparedStatement
- MySQL comme SGBD
- Gestion explicite des transactions JDBC
- Relations :
    - 1–N : Filiere → Eleves
    - 1–1 : Eleve → DossierAdministratif
    - N–N : Eleve ↔ Cours, Filiere ↔ Cours

---

## ️ Base de données

- SGBD : MySQL
- Script SQL fourni : `database.sql`
- Contraintes d’intégrité assurées par :
    - clés primaires
    - clés étrangères
    - contraintes UNIQUE

---

##  Règles métier implémentées

- Un matricule élève est unique
- Un code filière et cours est unique
- Un élève ne peut suivre que les cours proposés par sa filière
- Un élève ne peut avoir qu’un seul dossier administratif
- Toute inscription aux cours est transactionnelle
- Statut élève : ACTIF / SUSPENDU

---

##  Difficultés rencontrées

- Gestion des relations N–N avec JDBC
- Utilisation correcte des transactions MySQL
- Synchronisation JavaFX / base de données
- Navigation multi-vues JavaFX
- Respect strict des règles métier

---

##  Exécution du projet

1. Importer le fichier `database.sql` dans MySQL
2. Vérifier les paramètres de connexion JDBC
3. Lancer `MainApp.java`
