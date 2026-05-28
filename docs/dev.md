# Smart Energy Manager — Documentation développeur

## Lancer le projet

```bash
mvn javafx:run        # démarre l'application
mvn test              # lance les 24 tests JUnit
mvn clean compile     # compile sans lancer
```

La base de données `smart_energy.db` est créée automatiquement à la racine au premier lancement. Elle est ignorée par git (`.gitignore`).

---

## Architecture

L'application suit le pattern **MVC** strict avec une couche **Repository** pour la persistance :

```
Vue (FXML)  ←→  Contrôleur  ←→  Service  ←→  Repository  ←→  SQLite
```

Aucune logique métier dans les contrôleurs. Aucun SQL dans les services.

---

## Structure des sources

```
src/
├── main/
│   ├── java/com/smartenergymanager/
│   │   ├── App.java                            Point d'entrée JavaFX
│   │   ├── controller/
│   │   │   ├── MainController.java             Navigation entre les vues
│   │   │   ├── DashboardController.java        KPI + derniers relevés
│   │   │   ├── BatimentsController.java        Liste CRUD des bâtiments
│   │   │   ├── BatimentFormController.java     Formulaire création/édition
│   │   │   ├── ConsommationsController.java    Liste + filtre des relevés
│   │   │   ├── ConsommationFormController.java Formulaire saisie relevé
│   │   │   └── StatistiquesController.java     Graphiques + prédiction
│   │   ├── database/
│   │   │   └── DatabaseConnection.java         Singleton SQLite + init schema
│   │   ├── model/
│   │   │   ├── TypeEnergie.java                Enum (ELECTRICITE, EAU, GAZ…)
│   │   │   ├── Batiment.java                   Classe abstraite commune
│   │   │   ├── Maison.java                     Sous-type : nbPieces
│   │   │   ├── Appartement.java                Sous-type : etage, numAppartement
│   │   │   ├── Bureau.java                     Sous-type : entreprise, nbPostes
│   │   │   ├── BatimentUniversitaire.java      Sous-type : campus
│   │   │   ├── Releve.java                     Relevé de consommation
│   │   │   └── Alerte.java                     Alerte d'anomalie
│   │   ├── repository/
│   │   │   ├── BatimentRepository.java         Interface CRUD bâtiments
│   │   │   ├── ReleveRepository.java           Interface CRUD relevés
│   │   │   ├── SQLiteBatimentRepository.java   Implémentation SQLite
│   │   │   └── SQLiteReleveRepository.java     Implémentation SQLite
│   │   └── service/
│   │       ├── BatimentService.java            CRUD + duplication (Prototype)
│   │       ├── ReleveService.java              CRUD relevés
│   │       ├── AnalyseService.java             Anomalies, factures, évolution
│   │       ├── ImportExportService.java        Génération données test + CSV
│   │       └── PredictionService.java          Moyenne mobile pondérée
│   └── resources/com/smartenergymanager/
│       ├── view/
│       │   ├── MainView.fxml                   Layout principal (sidebar + contenu)
│       │   ├── DashboardView.fxml              Tableau de bord
│       │   ├── BatimentsList.fxml              Liste des bâtiments
│       │   ├── BatimentForm.fxml               Formulaire bâtiment
│       │   ├── ConsommationsList.fxml          Liste des relevés
│       │   ├── ConsommationForm.fxml           Formulaire relevé
│       │   └── StatistiquesView.fxml           Graphiques + prévision
│       ├── css/
│       │   ├── styles.css                      Variables globales + inputs
│       │   ├── main-view.css                   Sidebar + navigation
│       │   ├── batiments-view.css              Composants partagés (toolbar, boutons, tableaux)
│       │   ├── dashboard-view.css              KPI cards
│       │   ├── consommations-view.css          Spécifique consommations
│       │   └── statistiques-view.css           Graphiques + série prédiction
│       └── schema.sql                          Définition des tables SQLite
└── test/
    └── java/com/smartenergymanager/
        ├── service/
        │   ├── BatimentServiceTest.java        7 tests unitaires (repo en mémoire)
        │   └── AnalyseServiceTest.java         8 tests unitaires
        └── repository/
            └── SQLiteBatimentRepositoryTest.java  9 tests d'intégration (:memory:)
```

---

## Patterns de conception utilisés

| Pattern | Où |
|---|---|
| **MVC** | Architecture globale |
| **Repository** | `BatimentRepository`, `ReleveRepository` + implémentations SQLite |
| **Singleton** | `DatabaseConnection` |
| **Prototype** | `Batiment.cloner()` + `BatimentService.dupliquerBatiment()` |
| **Single Table Inheritance** | Tous les sous-types de `Batiment` dans une seule table SQL avec discriminateur `type` |

---

## Base de données

Trois tables dans `smart_energy.db` :

| Table | Description |
|---|---|
| `batiments` | Single Table Inheritance — toutes les sous-classes dans une table, colonnes spécifiques nullables |
| `releves` | FK `batiment_id` avec `ON DELETE CASCADE` |
| `alertes` | FK `batiment_id` nullable avec `ON DELETE SET NULL` |

Les clés étrangères SQLite sont activées au démarrage via `PRAGMA foreign_keys = ON`.

Pour les tests d'intégration, `DatabaseConnection.reinitialiser("jdbc:sqlite::memory:")` ouvre une base isolée en mémoire.

---

## Conventions de code

- **Packages** : minuscules, base `com.smartenergymanager`
- **Classes** : `PascalCase`, suffixes explicites (`Controller`, `Service`, `Repository`)
- **Méthodes** : `camelCase`, nom commence par un verbe
- **Constantes** : `UPPER_SNAKE_CASE`, `private static final`
- **SQL** : uniquement via `PreparedStatement` — jamais de concaténation de chaînes
- **Commentaires** : uniquement quand le *pourquoi* est non-évident

---

## Diagramme de classes

Source PlantUML : [`docs/uml/class_diagram.puml`](uml/class_diagram.puml)
