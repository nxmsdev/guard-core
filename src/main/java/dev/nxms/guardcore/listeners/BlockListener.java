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
import org.bukkit.event.player.PlayerBucketEmptyEvent;

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
            // Sprawdź czy gracz ma bypass
            if (player.hasPermission("guardcore.bypass")
                    && plugin.getBypassManager().hasDisallowedBlocksBypass(player.getUniqueId())) {
                // Gracz ma bypass - pozwól na postawienie
            } else {
                // Brak bypass - zablokuj
                event.setCancelled(true);
                messages.send(player, "disallowedblock-prevented",
                        MessageManager.placeholders("block", blockType));
                return;
            }
        }

        // Sprawdź czy gracz ma bypass na znikanie bloków
        boolean hasDespawnBypass = player.hasPermission("guardcore.bypass")
                && plugin.getBypassManager().hasBlockDespawnBypass(player.getUniqueId());

        // Sprawdź czy trzeba rejestrować blok
        boolean despawnEnabled = config.isBlockDespawnEnabled(worldName);
        boolean destructionDisabled = !config.isBlockDestructionAllowed(worldName);

        // Rejestruj blok jeśli potrzebne (despawn lub ochrona przed niszczeniem)
        if (despawnEnabled || destructionDisabled) {
            // trackForDespawn = true tylko jeśli despawn jest włączony I gracz nie ma bypass
            boolean trackForDespawn = despawnEnabled && !hasDespawnBypass;
            config.addPlacedBlock(block.getLocation(), player.getUniqueId(), hasDespawnBypass, trackForDespawn);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material bucket = event.getBucket();

        // Sprawdź czy to woda lub lawa
        if (bucket == Material.WATER_BUCKET || bucket == Material.LAVA_BUCKET) {
            // Rejestruj płyn z UUID gracza
            config.addPlacedFluid(block.getLocation(), player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        String worldName = block.getWorld().getName();

        // Sprawdź czy niszczenie bloków jest dozwolone
        // false = tylko bloki postawione przez graczy mogą być niszczone
        if (!config.isBlockDestructionAllowed(worldName)) {
            // Sprawdź czy gracz ma bypass na niszczenie bloków
            boolean hasDestructionBypass = player.hasPermission("guardcore.bypass")
                    && plugin.getBypassManager().hasBlockDestructionBypass(player.getUniqueId());

            // Jeśli gracz ma bypass - pozwól niszczyć
            if (hasDestructionBypass) {
                // Pozwól na zniszczenie
            } else {
                // Sprawdź czy blok był postawiony przez gracza
                if (!config.isBlockPlacedByPlayer(location)) {
                    event.setCancelled(true);
                    messages.send(player, "blockdestruction-prevented");
                    return;
                }
            }
        }

        // Usuń blok z rejestru
        config.removePlacedBlock(location);

        // Usuń też z rejestru płynów (jeśli to był płyn)
        config.removePlacedFluid(location);
    }
}