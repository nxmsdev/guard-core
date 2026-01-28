package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.UUID;

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
        Block toBlock = event.getToBlock();
        String worldName = block.getWorld().getName();
        Material material = block.getType();
        Location sourceLocation = block.getLocation();

        // Sprawdź wodę
        if (isWater(material)) {
            if (!config.isWaterFlowEnabled(worldName)) {
                // Sprawdź czy źródło wody było postawione przez gracza z bypass
                UUID placerUUID = config.getFluidPlacedByPlayerUUID(sourceLocation);
                if (placerUUID != null && plugin.getBypassManager().hasWaterFlowBypass(placerUUID)) {
                    // Gracz ma bypass - pozwól na rozlewanie
                    // Rejestruj nową lokalizację płynu z tym samym UUID
                    config.addPlacedFluid(toBlock.getLocation(), placerUUID);
                    return;
                }
                event.setCancelled(true);
                return;
            }
        }

        // Sprawdź lawę
        if (isLava(material)) {
            if (!config.isLavaFlowEnabled(worldName)) {
                // Sprawdź czy źródło lawy było postawione przez gracza z bypass
                UUID placerUUID = config.getFluidPlacedByPlayerUUID(sourceLocation);
                if (placerUUID != null && plugin.getBypassManager().hasLavaFlowBypass(placerUUID)) {
                    // Gracz ma bypass - pozwól na rozlewanie
                    // Rejestruj nową lokalizację płynu z tym samym UUID
                    config.addPlacedFluid(toBlock.getLocation(), placerUUID);
                    return;
                }
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Sprawdza czy materiał to woda.
     */
    private boolean isWater(Material material) {
        return material == Material.WATER;
    }

    /**
     * Sprawdza czy materiał to lawa.
     */
    private boolean isLava(Material material) {
        return material == Material.LAVA;
    }
}