package dev.nxms.guardcore.managers;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

/**
 * Zarządza punktami spawnu entity.
 * Cyklicznie spawnuje entity w zdefiniowanych punktach.
 */
public class EntitySpawnPointManager {

    private final GuardCore plugin;
    private final ConfigManager config;
    private BukkitTask spawnTask;

    // Interwał sprawdzania spawn pointów (w tickach)
    private static final long SPAWN_INTERVAL = 20 * 60 * 5; // Co 5 minut

    public EntitySpawnPointManager(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        startSpawnTask();
    }

    /**
     * Uruchamia zadanie cyklicznego spawnu entity.
     */
    private void startSpawnTask() {
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, this::spawnEntitiesAtPoints, SPAWN_INTERVAL, SPAWN_INTERVAL);
    }

    /**
     * Spawnuje entity we wszystkich zdefiniowanych punktach spawnu.
     */
    private void spawnEntitiesAtPoints() {
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            Map<String, Map<String, Object>> spawnPoints = config.getAllEntitySpawnPoints(worldName);

            for (Map.Entry<String, Map<String, Object>> entry : spawnPoints.entrySet()) {
                Map<String, Object> pointData = entry.getValue();

                String entityName = (String) pointData.get("entity");
                double x = (Double) pointData.get("x");
                double y = (Double) pointData.get("y");
                double z = (Double) pointData.get("z");

                try {
                    EntityType entityType = EntityType.valueOf(entityName);
                    Location location = new Location(world, x, y, z);

                    // Sprawdź czy entity może się zespawnować (czas, limit, disallowed)
                    if (canSpawnEntity(worldName, entityType)) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            world.spawnEntity(location, entityType);
                        });
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid entity type in spawn point: " + entityName);
                }
            }
        }
    }

    /**
     * Sprawdza czy entity może być zespawnowane.
     */
    private boolean canSpawnEntity(String worldName, EntityType entityType) {
        // Sprawdź czy entity nie jest na liście zakazanych
        if (config.isEntityDisallowed(worldName, entityType.name())) {
            return false;
        }

        // Sprawdź limit entity
        if (!plugin.getEntityLimitManager().canSpawnEntity(Bukkit.getWorld(worldName), entityType)) {
            return false;
        }

        // Sprawdź czas spawnu
        if (!plugin.getEntitySpawnTimeManager().canSpawnAtCurrentTime(worldName, entityType)) {
            return false;
        }

        return true;
    }

    /**
     * Wymusza spawn entity w danym punkcie.
     */
    public void forceSpawn(String worldName, String spawnPointName) {
        Map<String, Object> pointData = config.getEntitySpawnPoint(worldName, spawnPointName);

        if (pointData == null) {
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }

        String entityName = (String) pointData.get("entity");
        double x = (Double) pointData.get("x");
        double y = (Double) pointData.get("y");
        double z = (Double) pointData.get("z");

        try {
            EntityType entityType = EntityType.valueOf(entityName);
            Location location = new Location(world, x, y, z);
            world.spawnEntity(location, entityType);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid entity type: " + entityName);
        }
    }

    /**
     * Zatrzymuje zadanie spawnu.
     */
    public void shutdown() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }
    }

    /**
     * Przeładowuje manager.
     */
    public void reload() {
        shutdown();
        startSpawnTask();
    }
}