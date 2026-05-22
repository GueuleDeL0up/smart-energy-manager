package com.smartenergymanager.service;

import com.smartenergymanager.model.TypeEnergie;
import java.util.HashMap;
import java.util.Map;

/**
 * Service pour communiquer avec des sources de données externes (APIs).
 */
public class ExternalApiService {
    
    /** Récupère les prévisions météo pour ajuster les besoins en chauffage/clim. */
    public String recupererMeteo(String ville) {
        return "Ensoleillé";
    }

    /** Récupère les derniers tarifs en vigueur pour chaque type d'énergie. */
    public Map<TypeEnergie, Double> recupererTarifsActuels() {
        return new HashMap<>();
    }
}
