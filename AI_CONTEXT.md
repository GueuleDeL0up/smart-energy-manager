# Contexte du Projet : Smart Energy Manager

## 1. Informations Générales

- **Module** : II.1102 Projet Algorithmique et Programmation JAVA (S2 2025-2026).
- **Sujet** : Développement d'une application de maîtrise de la consommation énergétique ("Smart Energy Manager").
- **Objectif métier** : Permettre à un utilisateur de gérer des bâtiments, d'enregistrer des consommations énergétiques, de visualiser les évolutions et d'estimer les coûts pour optimiser l'usage énergétique.

## 2. Stack Technique

- **Langage** : Java JDK 25.
- **Interface Graphique** : JavaFX.
- **Gestionnaire de dépendances** : Maven.
- **Base de données** : SQL (SQLite, par exemple, pour respecter les consignes de persistance).
- **Architecture** : MVC (Modèle-Vue-Contrôleur).

## 3. Modèle de Données (Core Entities)

### Bâtiments

- **Types supportés** : Maison, Appartement, Bureau, Local commercial, Bâtiment universitaire, etc.
- **Opérations CRUD** : Créer, modifier, supprimer, et cloner un bâtiment existant.

### Consommations (Relevés)

- **Énergies** : Électricité, Eau, Gaz, Chauffage, Climatisation, Production solaire (facultatif).
- **Champs obligatoires** : Date, Heure, Type d'énergie, Quantité consommée, Coût estimé.
- **Méthodes d'ajout** : Saisie manuelle, Import de fichiers CSV, Génération automatique de données de test.

## 4. Fonctionnalités Principales (Vues et Contrôleurs attendus)

1. **Tableau de Bord (Dashboard)** :
   - Consommation totale (jour/mois/année) et coût estimé.
   - Identification du bâtiment le plus consommateur.
   - Alertes et indicateurs de performance.
2. **Visualisation Graphique (JavaFX Charts)** :
   - Courbes temporelles, histogrammes, diagrammes comparatifs (multi-bâtiments), répartition par énergie (PieChart).
3. **Analyse de données (Requêtes SQL complexes)** :
   - Détection des pics de consommation, de l'énergie dominante, et tendances d'augmentation.

## 5. Fonctionnalités Avancées / Bonus (À privilégier si la base est stable)

- **API Externes** : Intégration de données météo, de tarifs énergétiques ou de géolocalisation.
- **Analyse Intelligente** : Détection d'anomalies, prédiction des consommations futures.
- **Sécurité** : Authentification utilisateur et chiffrement local des données de la base SQL.

---

## 6. ⚠️ Instructions strictes pour l'Assistant IA

Tu es un assistant technique et un mentor pour un étudiant en informatique.

1. **Aide ciblée** : Fournis des snippets courts, des explications sur les concepts (ex: Binding JavaFX, structure MVC, requêtes SQL avec `PreparedStatement`) ou de l'aide au débogage.
2. **Bonnes pratiques** : Assure-toi que les propositions respectent le pattern MVC (pas de logique métier dans les fichiers de vue ou les contrôleurs JavaFX).
3. **Style de code** : Utilise les fonctionnalités modernes de Java (Records pour les DTO, Switch expressions, Streams) compatibles avec JDK 25.
