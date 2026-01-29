package dev.nxms.guardcore.managers;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class EntitySpawnPointManager {

    private final GuardCore plugin;
    private final ConfigManager config;

    // Klucz: "worldName:pointName"
    private final Map<String, BukkitTask> spawnTasks;

    public EntitySpawnPointManager(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.spawnTasks = new HashMap<>();
        startAllSpawnTasks();
    }

    private void startAllSpawnTasks() {
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            Map<String, Map<String, Object>> spawnPoints = config.getAllEntitySpawnPoints(worldName);

            for (String pointName : spawnPoints.keySet()) {
                Map<String, Object> pointData = spawnPoints.get(pointName);
                long intervalTicks = (Long) pointData.get("interval");
                startSpawnTask(worldName, pointName, intervalTicks);
            }
        }

        plugin.getLogger().info("Started " + spawnTasks.size() + " entity spawn point tasks.");
    }

    private void startSpawnTask(String worldName, String pointName, long intervalTicks) {
        String taskKey = worldName + ":" + pointName;

        // anuluj istniejący
        BukkitTask old = spawnTasks.remove(taskKey);
        if (old != null) old.cancel();

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                // Pobierz aktualne dane punktu (jeśli usunięty -> stop)
                Map<String, Object> pointData = config.getEntitySpawnPoint(worldName, pointName);
                if (pointData == null) {
                    this.cancel();
                    spawnTasks.remove(taskKey);
                    return;
                }

                spawnEntityAtPoint(worldName, pointData);
            }
        };

        BukkitTask task = runnable.runTaskTimer(plugin, intervalTicks, intervalTicks);
        spawnTasks.put(taskKey, task);
    }

    private void spawnEntityAtPoint(String worldName, Map<String, Object> pointData) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        String entityName = (String) pointData.get("entity");
        double x = (Double) pointData.get("x");
        double y = (Double) pointData.get("y");
        double z = (Double) pointData.get("z");

        try {
            EntityType entityType = EntityType.valueOf(entityName);
            Location location = new Location(world, x, y, z);

            if (canSpawnEntity(worldName, entityType)) {
                world.spawnEntity(location, entityType);
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid entity type in spawn point: " + entityName);
        }
    }

    private boolean canSpawnEntity(String worldName, EntityType entityType) {
        if (config.isEntityDisallowed(worldName, entityType.name())) return false;
        if (!plugin.getEntityLimitManager().canSpawnEntity(Bukkit.getWorld(worldName), entityType)) return false;
        if (!plugin.getEntitySpawnTimeManager().canSpawnAtCurrentTime(worldName, entityType)) return false;
        return true;
    }

    public void forceSpawn(String worldName, String spawnPointName) {
        Map<String, Object> pointData = config.getEntitySpawnPoint(worldName, spawnPointName);
        if (pointData == null) return;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

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
     * Natychmiast usuwa task dla spawn pointa (bez reload()).
     */
    public void removeSpawnPoint(String worldName, String pointName) {
        String taskKey = worldName + ":" + pointName;
        BukkitTask task = spawnTasks.remove(taskKey);
        if (task != null) {
            task.cancel();
        }
    }

    public void shutdown() {
        for (BukkitTask task : spawnTasks.values()) {
            task.cancel();
        }
        spawnTasks.clear();
        plugin.getLogger().info("Entity Spawn Point Manager has shut down.");
    }

    public void reload() {
        shutdown();
        startAllSpawnTasks();
        plugin.getLogger().info("Entity Spawn Point Manager has been reloaded.");
    }
}