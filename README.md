# ⚡ Smart Energy Manager

Application de bureau développée en **JavaFX** (architecture MVC) permettant de gérer plusieurs bâtiments et de suivre leurs consommations énergétiques dans le temps.

> Module II.1102 — Projet Algorithmique et Programmation Java (S2 2025-2026)

---

## Fonctionnalités

- 🏢 **Gestion des bâtiments** — Créer, modifier, supprimer et cloner des bâtiments (maison, appartement, bureau, bâtiment universitaire) avec leurs caractéristiques propres.
- ⚡ **Suivi des consommations** — Saisie manuelle de relevés (électricité, eau, gaz, chauffage, climatisation, production solaire) avec date, quantité et coût estimé.
- 📊 **Tableau de bord** — Vue synthétique avec les KPI du jour et du mois (consommation totale, coût estimé, bâtiment le plus consommateur) et l'historique des derniers relevés.
- 📈 **Statistiques** — Courbe d'évolution mensuelle, répartition par type d'énergie (PieChart) et comparatif entre bâtiments (BarChart).
- 🔮 **Prédiction** — Estimation de la consommation du mois suivant par moyenne mobile pondérée, affichée en pointillés sur le graphique d'évolution.
- 🔍 **Analyse** — Détection des anomalies de consommation (seuil μ + 2σ), estimation des factures mensuelles et calcul d'évolution mois/mois.
- 🗂️ **Génération de données test** — Insertion automatique de relevés réalistes sur 6 mois pour tous les bâtiments enregistrés.

---

## Stack technique

| Composant | Technologie |
|---|---|
| Langage | Java 25 |
| Interface graphique | JavaFX 21 |
| Persistance | SQLite (via `sqlite-jdbc`) |
| Build | Maven |
| Tests | JUnit Jupiter 5.12 |

---

## Prérequis

- **Java 25** ou supérieur
- **Maven 3.8** ou supérieur

---

## Lancer l'application

```bash
# Cloner le dépôt
git clone https://github.com/GueuleDeL0up/smart-energy-manager.git
cd smart-energy-manager

# Lancer l'application
mvn javafx:run
```

La base de données `smart_energy.db` est créée automatiquement à la racine du projet au premier lancement.

---

## Lancer les tests

```bash
mvn test
```

24 tests automatiques répartis en 3 classes :
- `BatimentServiceTest` — 7 tests unitaires (repository en mémoire)
- `AnalyseServiceTest` — 8 tests unitaires
- `SQLiteBatimentRepositoryTest` — 9 tests d'intégration (base SQLite `:memory:`)

---

## Architecture

```
Vue (FXML) ←→ Contrôleur ←→ Service ←→ Repository ←→ SQLite
```

L'application respecte le pattern MVC strict : aucune logique métier dans les contrôleurs, aucun SQL dans les services. La persistance utilise le pattern Repository avec une implémentation SQLite et le pattern Single Table Inheritance pour la hiérarchie des bâtiments.

Pour plus de détails : [`docs/dev.md`](docs/dev.md)

---

## Auteurs

| Nom | GitHub |
| :--- | :--- |
| **Maxime BOGNON** | [@HighMax524](https://github.com/HighMax524/) |
| **Maximilien CANONNE** | [@Dprive](https://github.com/Dprive/) |
| **Nicolas CLEMENT** | [@GueuleDeL0up](https://github.com/GueuleDeL0up/) |
