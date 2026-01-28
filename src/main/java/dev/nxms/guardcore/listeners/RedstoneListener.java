package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

import java.util.EnumSet;
import java.util.Set;

/**
 * Listener obsługujący mechanizmy redstone.
 * Blokuje działanie redstone gdy jest to skonfigurowane.
 */
public class RedstoneListener implements Listener {

    private final GuardCore plugin;
    private final ConfigManager config;

    // Materiały związane z redstone
    private static final Set<Material> REDSTONE_MATERIALS = EnumSet.of(
            Material.REDSTONE_WIRE,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WALL_TORCH,
            Material.REDSTONE_BLOCK,
            Material.REDSTONE_LAMP,
            Material.REPEATER,
            Material.COMPARATOR,
            Material.OBSERVER,
            Material.PISTON,
            Material.STICKY_PISTON,
            Material.PISTON_HEAD,
            Material.MOVING_PISTON,
            Material.DISPENSER,
            Material.DROPPER,
            Material.HOPPER,
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.CHERRY_BUTTON,
            Material.MANGROVE_BUTTON,
            Material.BAMBOO_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON,
            Material.POLISHED_BLACKSTONE_BUTTON,
            Material.STONE_PRESSURE_PLATE,
            Material.OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.ACACIA_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.CHERRY_PRESSURE_PLATE,
            Material.MANGROVE_PRESSURE_PLATE,
            Material.BAMBOO_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE,
            Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.TRIPWIRE_HOOK,
            Material.TRIPWIRE,
            Material.TRAPPED_CHEST,
            Material.DAYLIGHT_DETECTOR,
            Material.TARGET,
            Material.SCULK_SENSOR,
            Material.CALIBRATED_SCULK_SENSOR,
            Material.LIGHTNING_ROD
    );

    // Materiały interaktywne (przyciski, dźwignie)
    private static final Set<Material> INTERACTIVE_REDSTONE = EnumSet.of(
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.CHERRY_BUTTON,
            Material.MANGROVE_BUTTON,
            Material.BAMBOO_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON,
            Material.POLISHED_BLACKSTONE_BUTTON
    );

    public RedstoneListener(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    /**
     * Blokuje zmiany sygnału redstone.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRedstoneChange(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        String worldName = block.getWorld().getName();

        if (config.isRedstoneMechanismBlocked(worldName)) {
            // Zablokuj zmianę sygnału redstone
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    /**
     * Blokuje interakcję z elementami redstone (przyciski, dźwignie).
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        String worldName = block.getWorld().getName();

        if (config.isRedstoneMechanismBlocked(worldName)) {
            Material material = block.getType();

            if (INTERACTIVE_REDSTONE.contains(material)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Sprawdza czy materiał jest związany z redstone.
     */
    public static boolean isRedstoneMaterial(Material material) {
        return REDSTONE_MATERIALS.contains(material);
    }
}