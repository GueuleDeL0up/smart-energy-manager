package com.smartenergymanager.repository;

import com.smartenergymanager.model.Device;
import java.util.List;

/**
 * Interface définissant les opérations de stockage pour les appareils (Devices).
 */
public interface DeviceRepository {
    /** Sauvegarde ou met à jour un appareil. */
    void save(Device device);
    /** Récupère un appareil par son identifiant. */
    Device findById(Long id);
    /** Liste tous les appareils. */
    List<Device> findAll();
    /** Supprime un appareil. */
    void delete(Long id);
}
