package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.config.MessageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Listener obsługujący zdarzenia związane z blokami.
 * Zarządza stawianiem, niszczeniem bloków oraz ochroną bloków.
 */
public class BlockListener implements Listener {

    private final GuardCore plugin;
    private final ConfigManager config;
    private final MessageManager messages;

    public BlockListener(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.messages = plugin.getMessageManager();
    }

    /**
     * Obsługuje stawianie bloków.
     * - Sprawdza czy blok jest dozwolony
     * - Rejestruje blok dla systemu despawnu
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String worldName = block.getWorld().getName();
        String blockType = block.getType().name();

        // Sprawdź czy blok jest na liście zakazanych
        if (config.isBlockDisallowed(worldName, blockType)) {
            event.setCancelled(true);
            messages.send(player, "disallowedblock-prevented",
                    MessageManager.placeholders("block", blockType));
            return;
        }

        // Zarejestruj blok dla systemu despawnu
        config.addPlacedBlock(block.getLocation());
    }

    /**
     * Obsługuje niszczenie bloków.
     * - Sprawdza ochronę bloków (tylko postawione przez graczy)
     * - Usuwa blok z rejestru
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        String worldName = block.getWorld().getName();

        // Sprawdź czy ochrona bloków jest włączona
        if (config.isBlockDestructionProtected(worldName)) {
            // Pozwól tylko na niszczenie bloków postawionych przez graczy
            if (!config.isBlockPlacedByPlayer(location)) {
                // Sprawdź czy gracz ma uprawnienia admina
                if (!player.hasPermission("guardcore.admin")) {
                    event.setCancelled(true);
                    messages.send(player, "blockdestruction-prevented");
                    return;
                }
            }
        }

        // Usuń blok z rejestru postawionych bloków
        config.removePlacedBlock(location);
    }
}