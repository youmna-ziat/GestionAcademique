
CREATE TABLE filiere (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         code VARCHAR(20) UNIQUE NOT NULL,
                         nom VARCHAR(100) NOT NULL,
                         description TEXT
);

CREATE TABLE eleve (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       matricule VARCHAR(20) UNIQUE NOT NULL,
                       nom VARCHAR(100),
                       prenom VARCHAR(100),
                       email VARCHAR(100),
                       statut VARCHAR(20),
                       filiere_id INT,
                       FOREIGN KEY (filiere_id) REFERENCES filiere(id)
);

CREATE TABLE cours (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       code VARCHAR(20) UNIQUE NOT NULL,
                       intitule VARCHAR(100)
);

CREATE TABLE dossier_administratif (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       numero_inscription VARCHAR(20) UNIQUE NOT NULL,
                                       date_creation DATE,
                                       eleve_id INT UNIQUE,
                                       FOREIGN KEY (eleve_id) REFERENCES eleve(id)
);

CREATE TABLE eleve_cours (
                             eleve_id INT,
                             cours_id INT,
                             PRIMARY KEY (eleve_id, cours_id),
                             FOREIGN KEY (eleve_id) REFERENCES eleve(id),
                             FOREIGN KEY (cours_id) REFERENCES cours(id)
);

CREATE TABLE filiere_cours (
                               filiere_id INT,
                               cours_id INT,
                               PRIMARY KEY (filiere_id, cours_id),
                               FOREIGN KEY (filiere_id) REFERENCES filiere(id),
                               FOREIGN KEY (cours_id) REFERENCES cours(id)
);
