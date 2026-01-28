package dev.nxms.guardcore.managers;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.utils.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BlockDespawnManager {

    private final GuardCore plugin;
    private final ConfigManager config;
    private BukkitTask despawnTask;

    // Jak często sprawdzać bloki (w sekundach)
    private static final int CHECK_INTERVAL_SECONDS = 1;

    // Opóźnienie między usuwaniem kolejnych bloków (w sekundach)
    private static final double DESPAWN_DELAY_SECONDS = 0.25;

    public BlockDespawnManager(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        startDespawnTask();
    }

    private void startDespawnTask() {
        long intervalTicks = CHECK_INTERVAL_SECONDS * 20L;
        despawnTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkBlocksForDespawn, intervalTicks, intervalTicks);
        plugin.getLogger().info("Block despawn task started (checking every " + CHECK_INTERVAL_SECONDS + " second(s))");
    }

    private void checkBlocksForDespawn() {
        Map<String, ConfigManager.PlacedBlockData> placedBlocks = config.getPlacedBlocksData();

        if (placedBlocks.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        List<BlockToDespawn> blocksToRemove = new ArrayList<>();

        for (Map.Entry<String, ConfigManager.PlacedBlockData> entry : placedBlocks.entrySet()) {
            String locationKey = entry.getKey();
            ConfigManager.PlacedBlockData blockData = entry.getValue();

            // Sprawdź czy blok ma bypass - jeśli tak, pomiń
            if (blockData.hasBypassDespawn()) {
                continue;
            }

            long placedTime = blockData.getPlacedTime();

            String[] parts = locationKey.split(":");
            if (parts.length != 4) {
                config.removePlacedBlockByKey(locationKey);
                continue;
            }

            String worldName = parts[0];
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                continue;
            }

            if (!config.isBlockDespawnEnabled(worldName)) {
                continue;
            }

            String despawnTimeStr = config.getBlockDespawnTime(worldName);
            long despawnTimeMs = TimeParser.parseDuration(despawnTimeStr);

            if (despawnTimeMs <= 0) {
                continue;
            }

            long timePassed = currentTime - placedTime;

            if (timePassed >= despawnTimeMs) {
                try {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);

                    blocksToRemove.add(new BlockToDespawn(locationKey, world, x, y, z, placedTime));

                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid block location format: " + locationKey);
                    config.removePlacedBlockByKey(locationKey);
                }
            }
        }

        if (!blocksToRemove.isEmpty()) {
            blocksToRemove.sort(Comparator.comparingLong(b -> b.placedTime));
            despawnBlocksSequentially(blocksToRemove, 0);
        }
    }

    private void despawnBlocksSequentially(List<BlockToDespawn> blocks, int index) {
        if (index >= blocks.size()) {
            return;
        }

        BlockToDespawn block = blocks.get(index);
        long delayTicks = (long) (DESPAWN_DELAY_SECONDS * 20);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location location = new Location(block.world, block.x, block.y, block.z);

            if (location.getBlock().getType() != Material.AIR) {
                location.getBlock().setType(Material.AIR);
            }

            config.removePlacedBlockByKey(block.key);
            despawnBlocksSequentially(blocks, index + 1);

        }, delayTicks);
    }

    public void shutdown() {
        if (despawnTask != null) {
            despawnTask.cancel();
            despawnTask = null;
        }
    }

    public void reload() {
        shutdown();
        startDespawnTask();
    }

    private static class BlockToDespawn {
        final String key;
        final World world;
        final int x, y, z;
        final long placedTime;

        BlockToDespawn(String key, World world, int x, int y, int z, long placedTime) {
            this.key = key;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.placedTime = placedTime;
        }
    }
}