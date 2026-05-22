# 📘 Guide des Classes - Smart Energy Manager

Ce document explique le rôle de chaque classe créée et l'ordre recommandé pour poursuivre le développement du projet.

---

## 🏗️ 1. Couche Modèle (`com.smartenergymanager.model`)
*Le cœur de l'application, définit les données.*

- **`TypeEnergie` (Enum)** : Définit les types d'énergies gérés (ÉLECTRICITÉ, EAU, GAZ, etc.). Centralise les options pour éviter les erreurs de saisie.
- **`Batiment` (Abstract)** : Classe mère de tous les types de bâtiments. Elle contient les informations communes (nom, adresse, surface) et la logique partagée (calcul de consommation totale).
- **`Maison`, `Appartement`, `Bureau`, `BatimentUniversitaire`** : Extensions spécifiques de `Batiment`. Elles permettent d'ajouter des attributs propres (ex : `nbPieces` pour une maison, `entreprise` pour un bureau).
- **`Releve`** : Représente une donnée de consommation à un instant T pour un type d'énergie précis.
- **`Alerte`** : Stocke les messages d'anomalies détectées (ex : consommation excessive).
- **`Device` & `Consumption`** : Permettent de suivre la consommation au niveau d'un appareil spécifique (ex : un radiateur, une machine à laver).

---

## 🗄️ 2. Couche Accès aux Données (`com.smartenergymanager.repository` & `database`)
*Gère le stockage et la récupération des informations.*

- **`DatabaseConnection`** : Gère la connexion unique à la base de données SQLite (`smart_energy.db`).
- **`BatimentRepository` (Interface)** : Définit les méthodes standards pour sauvegarder, lire, lister ou supprimer des bâtiments.
- **`DeviceRepository` (Interface)** : Idem pour les équipements (Device).

---

## ⚙️ 3. Couche Service (`com.smartenergymanager.service`)
*Contient la "logique métier" (les calculs et règles).*

- **`BatimentService`** : Utilise le repository pour gérer le cycle de vie des bâtiments. Inclut la logique de **duplication** (via le pattern Prototype/Clone).
- **`AnalyseService`** : Le cerveau analytique. C'est ici que vous coderez les algorithmes pour détecter les fuites d'eau ou les pics de consommation électrique.
- **`DeviceService`** : Gère l'enregistrement et le suivi des appareils connectés.
- **`ImportExportService`** : Prévu pour charger des données depuis des fichiers CSV externes.
- **`ExternalApiService`** : Permet de simuler ou d'appeler des APIs réelles (Météo, Tarifs EDF).

---

## 🎮 4. Couche Interface (`com.smartenergymanager.controller` & `App`)
*Fait le lien avec l'utilisateur.*

- **`App`** : Point d'entrée principal qui charge l'interface graphique JavaFX.
- **`MainController`** : Gère les interactions sur la fenêtre principale (clics sur les boutons, affichage des listes).

---

## 🚀 Ordre de développement recommandé

Pour que votre projet avance de manière cohérente, suivez cet ordre :

1.  **Implémentation des Repositories (Persistance)** :
    *   Créer les classes d'implémentation (ex : `SQLiteBatimentRepository`) qui utilisent JDBC pour parler à la base de données SQLite.
    *   Utiliser le fichier `src/main/resources/schema.sql` pour créer les tables.

2.  **Logique Métier (Services)** :
    *   Remplir les méthodes de `AnalyseService` (ex : calcul réel des moyennes de consommation).
    *   Connecter les services aux repositories.

3.  **Tests Unitaires** :
    *   Créer des tests dans `src/test/java` pour vérifier que vos calculs de consommation dans `Batiment` et `AnalyseService` sont exacts.

4.  **Interface Graphique (Vue)** :
    *   Modifier `MainView.fxml` avec **Scene Builder** pour créer le design.
    *   Lier les éléments du FXML au `MainController`.

5.  **Import de données** :
    *   Finaliser `ImportExportService` pour pouvoir tester l'application avec de gros volumes de données réelles.
