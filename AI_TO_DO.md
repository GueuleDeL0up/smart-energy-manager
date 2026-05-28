# AI_TO_DO — Smart Energy Manager

> Suivi des tâches pour l'assistant IA. Mis à jour au fur et à mesure de l'avancement.
> Légende : ✅ Fait | 🔄 En cours | ⬜ À faire | 🎁 Bonus

---

## BLOC 0 — Infrastructure & Fondations

| # | Tâche | Statut | Notes |
|---|-------|--------|-------|
| 0.1 | Squelette Maven (pom.xml, App.java) | ✅ | JavaFX 21, SQLite JDBC, Java 25 |
| 0.2 | `DatabaseConnection.java` — Singleton SQLite | ✅ | `jdbc:sqlite:smart_energy.db` |
| 0.3 | Classes modèles (Batiment, Releve, Alerte, Device…) | ✅ | Héritage complet, pattern Prototype |
| 0.4 | `BatimentService.java` — CRUD + duplication | ✅ | Prototype pattern pour cloner() |
| 0.5 | `schema.sql` — Définir toutes les tables | ⬜ | **Bloquant** pour toute la persistance |

### Tables à créer dans `schema.sql`
- `batiments` (id, type, nom, adresse, surface, champs_specifiques…)
- `releves` (id, batiment_id, date_heure, type_energie, quantite, cout_estime)
- `alertes` (id, batiment_id, message, date, severite)
- `devices` (id, batiment_id, name, type)
- `consumptions` (id, device_id, timestamp, value, unit)

---

## BLOC 1 — Couche Persistance (Repository)

| # | Tâche | Statut | Notes |
|---|-------|--------|-------|
| 1.1 | Interface `BatimentRepository` | ✅ | save, findById, findAll, delete |
| 1.2 | Interface `DeviceRepository` | ✅ | save, findById, findAll, delete |
| 1.3 | Interface `ReleveRepository` | ⬜ | save, findByBatiment, findAll, delete |
| 1.4 | `SQLiteBatimentRepository` — implémentation | ⬜ | PreparedStatement, gestion sous-types |
| 1.5 | `SQLiteReleveRepository` — implémentation | ⬜ | Requêtes filtrées par bâtiment/date |
| 1.6 | `SQLiteDeviceRepository` — implémentation | ⬜ | Lien avec consumptions |
| 1.7 | Injecter les repositories dans les services | ⬜ | Brancher BatimentService → SQLiteBatimentRepository |

### Concepts clés pour l'étudiant
- Utiliser `PreparedStatement` (jamais de concaténation SQL → injection SQL)
- Pattern DAO / Repository : séparer la logique SQL de la logique métier
- Mapper les `ResultSet` vers les objets modèles avec une méthode `mapRow()`

---

## BLOC 2 — Vues FXML (feat/1-creer-vue-principale)

| # | Fichier | Statut | Composants principaux |
|---|---------|--------|-----------------------|
| 2.1 | `MainLayout.fxml` | ⬜ | BorderPane + Sidebar (VBox) + zone centrale |
| 2.2 | `DashboardView.fxml` | ⬜ | GridPane de KPI + zone alertes |
| 2.3 | `BatimentsList.fxml` | ⬜ | TableView + boutons Créer/Modifier/Supprimer/Cloner |
| 2.4 | `BatimentForm.fxml` | ⬜ | GridPane formulaire + ComboBox type bâtiment |
| 2.5 | `ConsommationsList.fxml` | ⬜ | TableView + boutons Saisie/Import CSV/Générer |
| 2.6 | `ConsommationForm.fxml` | ⬜ | DatePicker, ComboBox énergie, TextField quantité/coût |
| 2.7 | `StatistiquesView.fxml` | ⬜ | LineChart + BarChart + PieChart |
| 2.8 | `MainView.fxml` (actuel) → remplacer par 2.1 | 🔄 | Placeholder "Cliquez-moi !" à remplacer |

### Consignes FXML
- `fx:id` en camelCase : `tblBatiments`, `lblTotalConso`, `btnImportCSV`, `chartConsommation`
- Utiliser `HBox.hgrow="ALWAYS"` et `VBox.vgrow="ALWAYS"` pour le redimensionnement
- Ne pas mettre de logique Java dans le FXML — uniquement les références `fx:id` et `onAction`

---

## BLOC 3 — Contrôleurs (MVC)

| # | Contrôleur | Statut | Responsabilités |
|---|------------|--------|-----------------|
| 3.1 | `MainController.java` | ⬜ | Navigation entre vues, chargement FXML dynamique |
| 3.2 | `DashboardController.java` | ⬜ | Calculs KPI (jour/mois/an), rafraîchissement alertes |
| 3.3 | `BatimentsController.java` | ⬜ | CRUD bâtiments, alimenter TableView, ouvrir formulaire |
| 3.4 | `BatimentFormController.java` | ⬜ | Valider formulaire, afficher champs dynamiques par type |
| 3.5 | `ConsommationsController.java` | ⬜ | Lister relevés, déclencher import CSV, générer données |
| 3.6 | `ConsommationFormController.java` | ⬜ | Saisie manuelle d'un relevé |
| 3.7 | `StatistiquesController.java` | ⬜ | Alimenter les charts JavaFX depuis les services |

### Concepts clés pour l'étudiant
- `initialize()` : injecter le service, charger les données initiales
- `ObservableList` + `TableView.setItems()` pour la liaison données ↔ tableau
- `FXMLLoader.load()` pour le chargement dynamique de vues dans `MainController`
- Ne jamais appeler la BDD directement depuis un contrôleur — passer par le service

---

## BLOC 4 — Services Métier

| # | Service | Statut | Méthodes à implémenter |
|---|---------|--------|------------------------|
| 4.1 | `BatimentService` — CRUD complet | ✅ | creer, modifier, supprimer, dupliquer |
| 4.2 | `BatimentService` — brancher persistance | ⬜ | Appeler le repository pour save/delete |
| 4.3 | `AnalyseService.detecterAnomalies()` | ⬜ | Comparer relevé à la moyenne ± seuil |
| 4.4 | `AnalyseService.estimerFactureMensuelle()` | ⬜ | Somme des coûts sur le mois courant |
| 4.5 | `AnalyseService.identifierBatimentPlusConsommateur()` | ⬜ | Max sur tous les bâtiments |
| 4.6 | `AnalyseService.calculerEvolution()` | ⬜ | Comparaison mois N vs mois N-1 en % |
| 4.7 | `ImportExportService.importCSV()` | ⬜ | Parser CSV → List<Releve> via BufferedReader |
| 4.8 | `ImportExportService.genererDonneesTest()` | ⬜ | Insérer N relevés aléatoires réalistes |
| 4.9 | `DeviceService` — compléter | ⬜ | removeDevice, getByBatiment, updateDevice |

---

## BLOC 5 — Style & Ergonomie

| # | Tâche | Statut | Notes |
|---|-------|--------|-------|
| 5.1 | `styles.css` — couleurs et typographie de base | ⬜ | Thème sombre ou clair cohérent |
| 5.2 | Icônes pour le menu de navigation | ⬜ | FontAwesomeFX ou SVG inline |
| 5.3 | Feedback visuel (loading spinner, messages d'erreur) | ⬜ | Label/Alert JavaFX |

---

## BLOC 6 — Tests

| # | Tâche | Statut | Notes |
|---|-------|--------|-------|
| 6.1 | Tests unitaires `BatimentServiceTest` | ⬜ | JUnit 5, mocker le repository |
| 6.2 | Tests unitaires `AnalyseServiceTest` | ⬜ | Cas limites : liste vide, un seul relevé |
| 6.3 | Tests d'intégration repository (base SQLite en mémoire) | ⬜ | `:memory:` pour isolation des tests |

---

## 🎁 BLOC 7 — Fonctionnalités Bonus

| # | Tâche | Statut | Notes |
|---|-------|--------|-------|
| 7.1 | `ExternalApiService` — météo réelle (OpenWeatherMap) | ⬜ | HTTP GET + parsing JSON |
| 7.2 | `ExternalApiService` — tarifs énergétiques | ⬜ | CSV ou API publique |
| 7.3 | Authentification utilisateur (login/mot de passe) | ⬜ | Hash bcrypt local |
| 7.4 | Prédiction de consommation (moyenne mobile) | ⬜ | Série temporelle simple |
| 7.5 | Export PDF du rapport mensuel | ⬜ | Apache PDFBox ou iText |

---

## Ordre de réalisation recommandé

```
0.5 schema.sql
  → 1.4 SQLiteBatimentRepository + 1.5 SQLiteReleveRepository
    → 2.1 MainLayout.fxml + 2.3 BatimentsList.fxml
      → 3.1 MainController + 3.3 BatimentsController
        → 2.2 DashboardView.fxml + 4.4 AnalyseService (KPI)
          → 2.7 StatistiquesView.fxml + 3.7 StatistiquesController
            → 4.7 ImportExportService (CSV)
              → 5.x Style CSS
                → 6.x Tests
                  → 7.x Bonus
```
