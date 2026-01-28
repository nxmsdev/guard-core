package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.config.MessageManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final GuardCore plugin;
    private final ConfigManager config;
    private final MessageManager messages;

    public BlockListener(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.messages = plugin.getMessageManager();
    }

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

        // Rejestruj blok TYLKO jeśli blockDespawn jest włączony
        if (config.isBlockDespawnEnabled(worldName)) {
            config.addPlacedBlock(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        String worldName = block.getWorld().getName();

        // Sprawdź czy ochrona bloków jest włączona
        if (config.isBlockDestructionProtected(worldName)) {
            if (!config.isBlockPlacedByPlayer(location)) {
                if (!player.hasPermission("guardcore.admin")) {
                    event.setCancelled(true);
                    messages.send(player, "blockdestruction-prevented");
                    return;
                }
            }
        }

        // Usuń blok z rejestru
        config.removePlacedBlock(location);
    }
}