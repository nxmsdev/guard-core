package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * Listener obsługujący rozlewanie się płynów.
 * Kontroluje przepływ wody i lawy na podstawie konfiguracji świata.
 */
public class FluidListener implements Listener {

    private final GuardCore plugin;
    private final ConfigManager config;

    public FluidListener(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    /**
     * Obsługuje rozlewanie się płynów (woda, lawa).
     * Anuluje zdarzenie jeśli przepływ danego płynu jest wyłączony.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();
        String worldName = block.getWorld().getName();
        Material material = block.getType();

        // Sprawdź wodę
        if (isWater(material)) {
            if (!config.isWaterFlowEnabled(worldName)) {
                event.setCancelled(true);
                return;
            }
        }

        // Sprawdź lawę
        if (isLava(material)) {
            if (!config.isLavaFlowEnabled(worldName)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Sprawdza czy materiał to woda.
     */
    private boolean isWater(Material material) {
        return material == Material.WATER ||
                material == Material.WATER_CAULDRON;
    }

    /**
     * Sprawdza czy materiał to lawa.
     */
    private boolean isLava(Material material) {
        return material == Material.LAVA ||
                material == Material.LAVA_CAULDRON;
    }
}