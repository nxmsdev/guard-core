package dev.nxms.guardcore.managers;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 * Zarządza limitami entity na świecie.
 * Sprawdza czy można zespawnować nowe entity na podstawie ustawionych limitów.
 */
public class EntityLimitManager {

    private final GuardCore plugin;
    private final ConfigManager config;

    public EntityLimitManager(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    /**
     * Sprawdza czy można zespawnować entity danego typu na świecie.
     *
     * @param world Świat
     * @param entityType Typ entity
     * @return true jeśli można zespawnować, false jeśli limit został osiągnięty
     */
    public boolean canSpawnEntity(World world, EntityType entityType) {
        String worldName = world.getName();
        String entityName = entityType.name();

        int limit = config.getEntityLimit(worldName, entityName);

        // Jeśli nie ma limitu (-1), pozwól na spawn
        if (limit == -1) {
            return true;
        }

        // Policz aktualne entity tego typu na świecie
        long currentCount = world.getEntities().stream()
                .filter(e -> e.getType() == entityType)
                .count();

        return currentCount < limit;
    }

    /**
     * Zwraca aktualną liczbę entity danego typu na świecie.
     */
    public int getEntityCount(World world, EntityType entityType) {
        return (int) world.getEntities().stream()
                .filter(e -> e.getType() == entityType)
                .count();
    }

    /**
     * Zwraca limit dla danego entity na świecie.
     *
     * @return Limit lub -1 jeśli brak limitu
     */
    public int getLimit(String worldName, String entityType) {
        return config.getEntityLimit(worldName, entityType);
    }
}