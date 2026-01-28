package dev.nxms.guardcore.managers;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.utils.TimeParser;
import org.bukkit.entity.EntityType;

/**
 * Zarządza czasami spawnu entity.
 * Sprawdza czy aktualny czas mieści się w dozwolonym zakresie spawnu.
 */
public class EntitySpawnTimeManager {

    private final GuardCore plugin;
    private final ConfigManager config;

    public EntitySpawnTimeManager(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    /**
     * Sprawdza czy entity może się zespawnować o aktualnej godzinie.
     *
     * @param worldName Nazwa świata
     * @param entityType Typ entity
     * @return true jeśli entity może się zespawnować, false w przeciwnym przypadku
     */
    public boolean canSpawnAtCurrentTime(String worldName, EntityType entityType) {
        String entityName = entityType.name();
        String[] times = config.getEntitySpawnTime(worldName, entityName);

        // Jeśli nie ma ustawionego czasu, pozwól na spawn
        if (times == null) {
            return true;
        }

        String from = times[0];
        String to = times[1];

        return TimeParser.isCurrentTimeInRange(from, to);
    }

    /**
     * Sprawdza czy entity ma ustawiony czas spawnu.
     */
    public boolean hasSpawnTime(String worldName, String entityType) {
        return config.getEntitySpawnTime(worldName, entityType) != null;
    }

    /**
     * Zwraca zakres czasu spawnu dla entity.
     *
     * @return Tablica [from, to] lub null jeśli brak ustawienia
     */
    public String[] getSpawnTimeRange(String worldName, String entityType) {
        return config.getEntitySpawnTime(worldName, entityType);
    }
}