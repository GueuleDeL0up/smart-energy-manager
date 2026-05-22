package com.smartenergymanager.service;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.repository.BatimentRepository;

/**
 * Service gérant la logique métier liée aux bâtiments.
 */
public class BatimentService {
    private final BatimentRepository repository;

    public BatimentService(BatimentRepository repository) {
        this.repository = repository;
    }

    /** Crée un nouveau bâtiment dans le système. */
    public void creerBatiment(Batiment b) {
        repository.save(b);
    }

    /** Supprime un bâtiment via son ID. */
    public void supprimerBatiment(Long id) {
        repository.delete(id);
    }

    /** Modifie les informations d'un bâtiment existant. */
    public void modifierBatiment(Batiment b) {
        repository.save(b);
    }

    /**
     * Duplique un bâtiment existant (Pattern Prototype).
     * Utile pour créer des modèles de bâtiments similaires.
     */
    public Batiment dupliquerBatiment(Long id) {
        Batiment b = repository.findById(id);
        if (b != null) {
            Batiment clone = b.cloner();
            clone.setId(null); // Nouveau bâtiment, donc pas encore d'ID en base
            repository.save(clone);
            return clone;
        }
        return null;
    }
}
