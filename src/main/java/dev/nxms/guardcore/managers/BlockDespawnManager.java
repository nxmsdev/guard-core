package dev.nxms.guardcore.managers;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.utils.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Map;

/**
 * Zarządza despawnem bloków postawionych przez graczy.
 * Cyklicznie sprawdza bloki i usuwa te, które przekroczyły czas życia.
 */
public class BlockDespawnManager {

    private final GuardCore plugin;
    private final ConfigManager config;
    private BukkitTask despawnTask;

    // Interwał sprawdzania bloków (w tickach - 20 ticków = 1 sekunda)
    private static final long CHECK_INTERVAL = 20 * 60; // Co minutę

    public BlockDespawnManager(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        startDespawnTask();
    }

    /**
     * Uruchamia zadanie cyklicznego sprawdzania bloków do despawnu.
     */
    private void startDespawnTask() {
        despawnTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkBlocksForDespawn, CHECK_INTERVAL, CHECK_INTERVAL);
    }

    /**
     * Sprawdza wszystkie postawione bloki i usuwa te, które powinny zniknąć.
     */
    private void checkBlocksForDespawn() {
        Map<String, Long> placedBlocks = config.getPlacedBlocks();
        long currentTime = System.currentTimeMillis();

        Iterator<Map.Entry<String, Long>> iterator = placedBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            String locationKey = entry.getKey();
            long placedTime = entry.getValue();

            // Parsuj lokalizację z klucza
            String[] parts = locationKey.split(":");
            if (parts.length != 4) {
                continue;
            }

            String worldName = parts[0];
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                continue;
            }

            // Sprawdź czy despawn jest włączony dla tego świata
            if (!config.isBlockDespawnEnabled(worldName)) {
                continue;
            }

            // Pobierz czas despawnu dla świata
            String despawnTimeStr = config.getBlockDespawnTime(worldName);
            long despawnTimeMs = TimeParser.parseDuration(despawnTimeStr);

            if (despawnTimeMs <= 0) {
                continue;
            }

            // Sprawdź czy blok powinien zniknąć
            if (currentTime - placedTime >= despawnTimeMs) {
                try {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);

                    Location location = new Location(world, x, y, z);

                    // Usuń blok (ustaw na powietrze)
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        location.getBlock().setType(org.bukkit.Material.AIR);
                    });

                    // Usuń z mapy
                    config.removePlacedBlock(location);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid block location format: " + locationKey);
                }
            }
        }
    }

    /**
     * Zatrzymuje zadanie despawnu.
     */
    public void shutdown() {
        if (despawnTask != null) {
            despawnTask.cancel();
        }
    }

    /**
     * Przeładowuje manager.
     */
    public void reload() {
        shutdown();
        startDespawnTask();
    }
}