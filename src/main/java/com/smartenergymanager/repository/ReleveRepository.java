package com.smartenergymanager.repository;

import com.smartenergymanager.model.Releve;
import java.util.List;

/**
 * Interface définissant les opérations de stockage pour les relevés de consommation.
 */
public interface ReleveRepository {
    /** Sauvegarde ou met à jour un relevé. */
    void save(Releve releve);
    /** Récupère un relevé par son identifiant. */
    Releve findById(Long id);
    /** Liste tous les relevés d'un bâtiment donné. */
    List<Releve> findByBatiment(Long batimentId);
    /** Liste tous les relevés enregistrés. */
    List<Releve> findAll();
    /** Supprime un relevé par son identifiant. */
    void delete(Long id);
}
