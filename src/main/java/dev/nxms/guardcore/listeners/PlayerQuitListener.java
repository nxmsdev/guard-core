package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener obsługujący wyjście gracza z serwera.
 */
public class PlayerQuitListener implements Listener {

    private final GuardCore plugin;

    public PlayerQuitListener(GuardCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Usuń bypass gracza przy wyjściu
        plugin.getBypassManager().removePlayer(event.getPlayer().getUniqueId());
    }
}