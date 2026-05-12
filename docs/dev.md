# ⚡ Smart Energy Manager - Dev

## 🏗️ Architecture

```
src/
  main/
    java/
      com/
        smartenergymanager/
          controller/
            MainController.java
          database/
            DatabaseConnection.java
          model/
            Device.java
            Consumption.java
          repository/
            DeviceRepository.java
          service/
            DeviceService.java
          App.java
    resources/
      com/
        smartenergymanager/
          css/
            styles.css
          view/
            MainView.fxml
      schema.sql
  test/
    java/
      com/
        smartenergymanager/
          service/
          repository/
    resources/
```

- `src/main/java` : contient tout le code Java de l'application.
  - `App.java` : point d'entrée JavaFX, lance la fenêtre principale.
  - `controller/` : contient les contrôleurs JavaFX, qui gèrent les événements de l'interface et font le lien entre la vue et la logique métier.
  - `database/` : contient les classes liées à l'accès SQLite, comme la connexion à la base.
  - `model/` : contient les classes métier de l'application.
  - `repository/` : contient la logique d'accès aux données, c'est-à-dire la lecture et l'écriture dans SQLite.
  - `service/` : contient la logique métier principale et les règles de gestion.
- `src/main/resources` : contient les ressources chargées par l'application au démarrage.
  - `schema.sql` : script SQL utilisé pour créer les tables SQLite au premier lancement.
  - `css/` : contient les feuilles de style JavaFX.
  - `view/` : contient les fichiers FXML qui décrivent l'interface graphique.
- `src/test/java` : contient les tests automatiques du projet.
  - `service/` dans `src/test/java` : contient les tests des services métier.
  - `repository/` dans `src/test/java` : contient les tests de la couche d'accès aux données.
- `src/test/resources` : contient les ressources utilisées uniquement par les tests si nécessaire.

---

## 📏 Conventions de code

### 📦 Maven

- `groupId` en reverse-domain, en minuscules.
- `artifactId` en `kebab-case`.
- Structure Maven obligatoire.

### 📁 Packages Java

- Noms de packages en minuscules.
- Pas de tiret dans un package.
- Base du projet: `com.smartenergymanager`.

### ☕ Fichiers Java

- Un fichier Java contient une classe publique principale.
- Nom du fichier identique au nom de la classe publique.
- Classes en `PascalCase`.

### 🏷️ Variables

- Variables locales en `camelCase`.
- Booléens : préfixes `is`, `has`, `can`, `should`.
- Noms explicites, jamais `data`, `tmp`, `value` sans contexte.

### 🔒 Constantes

- Constantes en `UPPER_SNAKE_CASE`.
- Pour une constante de classe : `private static final`.

### 🏛️ Classes

- Classes en `PascalCase`.
- Suffixes explicites `Controller`, `Repository`, `Service`, (pas de suffixe pour Model).

### ⚙️ Fonctions

- Méthodes en `camelCase`.
- Nom commence par un verbe.

### 🧪 Tests

- Classes de test unitaire suffixées `Test`.
- Noms de méthodes de test en `shouldResultWhenCondition`.
