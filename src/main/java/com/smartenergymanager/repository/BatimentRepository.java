package com.smartenergymanager.repository;

import com.smartenergymanager.model.Batiment;
import java.util.List;

/**
 * Interface définissant les opérations de stockage pour les bâtiments.
 */
public interface BatimentRepository {
    /** Sauvegarde ou met à jour un bâtiment. */
    void save(Batiment b);
    /** Récupère un bâtiment par son identifiant unique. */
    Batiment findById(Long id);
    /** Liste tous les bâtiments enregistrés. */
    List<Batiment> findAll();
    /** Supprime un bâtiment du système. */
    void delete(Long id);
}
