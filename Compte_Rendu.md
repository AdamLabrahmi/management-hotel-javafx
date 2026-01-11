# Compte Rendu Global : Projet de Gestion Hôtelière

## 1. Introduction
Ce projet consiste en la mise en place d'une application JavaFX complète pour la gestion d'un établissement hôtelier. L'objectif était de structurer une application robuste permettant la gestion des clients, des chambres, des réservations, des factures et des plaintes, en appliquant les meilleures pratiques de programmation orientée objet (POO), le pattern MVC (Modèle-Vue-Contrôleur) et les fonctionnalités modernes de Java (Streams, Multithreading).

## 2. Architecture du Projet
Le projet suit une structure Maven standard, organisée en couches professionnelles (N-Tier) pour assurer la séparation des responsabilités.

### Arborescence des fichiers
```text
MangmentHotel/
├── pom.xml                           # Configuration Maven (Dépendances JavaFX, Lombok, MySQL, Hibernate)
├── Dockerfile                        # Configuration pour la conteneurisation de l'application
├── docker-compose.yml                # Orchestration de l'app et de la base de données MySQL
└── src/main/java/com/emsi/mh/mangmenthotel/
    ├── controller/                   # Contrôleurs JavaFX (Logique UI)
    ├── dao/                          # Couche d'accès aux données (JDBC/Hibernate)
    │   ├── IDao.java                 # Interface générique CRUD
    │   └── GenericDAOImpl.java       # Implémentation générique
    ├── model/                        # Modèles de données (Entities)
    ├── service/                      # Logique métier et Streams
    ├── util/                         # Utilitaires (Connexion DB, HibernateUtil)
    └── HelloApplication.java         # Point d'entrée de l'application
```

## 3. Analyse Détaillée des Composants

### A. Modèle de Données (`Chambre.java`, `Reservation.java`, etc.)
Les classes modèles utilisent l'encapsulation et sont optimisées avec **Lombok** (`@Data`, `@Builder`, `@AllArgsConstructor`) pour réduire le code boilerplate.

```java
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chambre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numChambre;
    @Enumerated(EnumType.STRING)
    private TypeChambre type;
    private Double prixParNuit;
    @Enumerated(EnumType.STRING)
    private StatutChambre statut;
}
```

### B. Abstraction et Généricité (`IDao.java`)
L'interface `IDao<T, ID>` définit un contrat générique pour toutes les opérations de persistance, permettant une réutilisation maximale du code.

```java
public interface IDao<T, ID> {
    void create(T entity);
    T findById(ID id);
    List<T> findAll();
    void update(T entity);
    void deleteById(ID id);
}
```

### C. Logique Métier et Streams (`ReservationService.java`)
Le service utilise l'API **Stream** de Java pour le traitement efficace des collections, notamment pour le filtrage des réservations par client.

```java
public List<Reservation> getReservationsByClient(Long clientId) {
    return dao.findAll().stream()
            .filter(r -> r.getClient().getId().equals(clientId))
            .collect(Collectors.toList());
}
```

### D. Multithreading et Réactivité (`ChambreController.java`)
Pour garantir une interface fluide, les chargements de données lourds sont effectués dans des **Threads** séparés via la classe `Task` de JavaFX.

```java
private void loadChambres() {
    Task<ObservableList<Chambre>> task = new Task<>() {
        @Override
        protected ObservableList<Chambre> call() {
            return FXCollections.observableArrayList(chambreService.getAllChambres());
        }
    };
    task.setOnSucceeded(e -> chambreTable.setItems(task.getValue()));
    new Thread(task).start();
}
```

## 4. Guide d'Exécution

### Compilation avec Maven
```bash
mvn clean package
```

### Exécution locale
```bash
mvn javafx:run
```

## 5. Couche de Persistance et Base de Données
Le projet supporte deux modes de persistance :
1.  **JDBC Classique** : Via `DatabaseConnection.java`, pour un contrôle précis des requêtes SQL.
2.  **Hibernate (ORM)** : Via `HibernateUtil.java`, permettant un mapping Objet-Relationnel fluide avec MySQL.

### Configuration Docker
L'utilisation de Docker permet de lancer l'application et sa base de données avec une seule commande :
```bash
docker compose up --build
```

## 6. Avantages de l'Architecture
| Caractéristique | Bénéfice |
| :--- | :--- |
| **Séparation des couches** | Facilité de maintenance (on peut changer la DB sans toucher à l'UI). |
| **Multithreading** | Interface utilisateur fluide et réactive (pas de freeze). |
| **Généricité (DAOs)** | Réduction drastique de la duplication de code. |
| **Conteneurisation** | Déploiement identique sur n'importe quel système via Docker. |

---
**Conclusion** : Ce projet démontre une maîtrise complète du cycle de développement Java moderne, allant de la conception orientée objet à la conteneurisation, en passant par l'optimisation des performances via les Streams et le Multithreading.
