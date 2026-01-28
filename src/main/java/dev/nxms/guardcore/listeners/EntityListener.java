package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.config.MessageManager;
import dev.nxms.guardcore.managers.EntityLimitManager;
import dev.nxms.guardcore.managers.EntitySpawnTimeManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

public class EntityListener implements Listener {

    private final GuardCore plugin;
    private final ConfigManager config;
    private final MessageManager messages;
    private final EntityLimitManager limitManager;
    private final EntitySpawnTimeManager spawnTimeManager;

    public EntityListener(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.messages = plugin.getMessageManager();
        this.limitManager = plugin.getEntityLimitManager();
        this.spawnTimeManager = plugin.getEntitySpawnTimeManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (world == null) return;

        String worldName = world.getName();
        EntityType entityType = event.getEntityType();
        String entityName = entityType.name();

        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        // Nie blokuj spawnów wywołanych przez komendy lub pluginy
        if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM ||
                reason == CreatureSpawnEvent.SpawnReason.COMMAND) {
            return;
        }

        // Sprawdź czy entity jest na liście zakazanych
        if (config.isEntityDisallowed(worldName, entityName)) {
            event.setCancelled(true);
            return;
        }

        // Sprawdź limit entity
        if (!limitManager.canSpawnEntity(world, entityType)) {
            event.setCancelled(true);
            return;
        }

        // Sprawdź czas spawnu (nie dotyczy jajek spawn - to obsługujemy osobno)
        if (reason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            if (!spawnTimeManager.canSpawnAtCurrentTime(worldName, entityType)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (world == null) return;

        String worldName = world.getName();
        EntityType entityType = event.getEntityType();
        String entityName = entityType.name();

        // Sprawdź czy entity jest na liście zakazanych
        if (config.isEntityDisallowed(worldName, entityName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerUseSpawnEgg(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        // Sprawdź czy to jajko spawn
        Material material = item.getType();
        if (!material.name().endsWith("_SPAWN_EGG")) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        String worldName = block.getWorld().getName();
        EntityType entityType = getEntityTypeFromSpawnEgg(material);

        if (entityType == null) {
            return;
        }

        String entityName = entityType.name();

        // Sprawdź czy gracz ma uprawnienia admina
        if (player.hasPermission("guardcore.admin")) {
            return;
        }

        // Sprawdź czy entity jest na liście zakazanych
        if (config.isEntityDisallowed(worldName, entityName)) {
            event.setCancelled(true);
            messages.send(player, "entity-spawn-blocked", MessageManager.placeholders(
                    "entity", entityName
            ));
            return;
        }

        // Sprawdź limit entity
        if (!limitManager.canSpawnEntity(block.getWorld(), entityType)) {
            event.setCancelled(true);
            messages.send(player, "entity-limit-reached", MessageManager.placeholders(
                    "entity", entityName
            ));
            return;
        }

        // Sprawdź czas spawnu
        if (!spawnTimeManager.canSpawnAtCurrentTime(worldName, entityType)) {
            event.setCancelled(true);
            messages.send(player, "entity-spawn-time-blocked", MessageManager.placeholders(
                    "entity", entityName
            ));
            return;
        }
    }

    private EntityType getEntityTypeFromSpawnEgg(Material material) {
        String materialName = material.name();

        // Usuń "_SPAWN_EGG" z końca
        if (!materialName.endsWith("_SPAWN_EGG")) {
            return null;
        }

        String entityName = materialName.substring(0, materialName.length() - "_SPAWN_EGG".length());

        try {
            return EntityType.valueOf(entityName);
        } catch (IllegalArgumentException e) {
            // Niektóre nazwy mogą się różnić
            switch (entityName) {
                case "MOOSHROOM":
                    return EntityType.MOOSHROOM;
                case "SNOW_GOLEM":
                    return EntityType.SNOW_GOLEM;
                case "IRON_GOLEM":
                    return EntityType.IRON_GOLEM;
                default:
                    return null;
            }
        }
    }
}