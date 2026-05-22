package com.smartenergymanager.service;

import com.smartenergymanager.model.Device;
import com.smartenergymanager.repository.DeviceRepository;
import java.util.List;

/**
 * Service gérant la flotte d'appareils connectés.
 */
public class DeviceService {
    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    /** Enregistre un nouvel appareil. */
    public void registerDevice(Device device) {
        repository.save(device);
    }

    /** Récupère tous les appareils enregistrés. */
    public List<Device> getAllDevices() {
        return repository.findAll();
    }
}
