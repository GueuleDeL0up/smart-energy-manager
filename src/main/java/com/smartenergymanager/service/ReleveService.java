package com.smartenergymanager.service;

import com.smartenergymanager.model.Releve;
import com.smartenergymanager.repository.ReleveRepository;

import java.util.List;

/**
 * Service gérant la logique métier liée aux relevés de consommation.
 */
public class ReleveService {
    private final ReleveRepository repository;

    public ReleveService(ReleveRepository repository) {
        this.repository = repository;
    }

    public void ajouterReleve(Releve releve) {
        repository.save(releve);
    }

    public void supprimerReleve(Long id) {
        repository.delete(id);
    }

    public List<Releve> findAll() {
        return repository.findAll();
    }

    public List<Releve> findByBatiment(Long batimentId) {
        return repository.findByBatiment(batimentId);
    }
}
