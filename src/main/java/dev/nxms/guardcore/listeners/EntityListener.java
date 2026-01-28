package dev.nxms.guardcore.listeners;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.managers.EntityLimitManager;
import dev.nxms.guardcore.managers.EntitySpawnTimeManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * Listener obsługujący zdarzenia związane z entity.
 * Zarządza spawnem entity, limitami i restrykcjami czasowymi.
 */
public class EntityListener implements Listener {

    private final GuardCore plugin;
    private final ConfigManager config;
    private final EntityLimitManager limitManager;
    private final EntitySpawnTimeManager spawnTimeManager;

    public EntityListener(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.limitManager = plugin.getEntityLimitManager();
        this.spawnTimeManager = plugin.getEntitySpawnTimeManager();
    }

    /**
     * Obsługuje spawn stworzeń.
     * Sprawdza:
     * - Czy entity nie jest na liście zakazanych
     * - Czy nie został przekroczony limit entity
     * - Czy aktualny czas pozwala na spawn
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (world == null) return;

        String worldName = world.getName();
        EntityType entityType = event.getEntityType();
        String entityName = entityType.name();

        // Nie blokuj spawnów wywołanych przez pluginy lub komendy
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM ||
                reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG ||
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

        // Sprawdź czas spawnu
        if (!spawnTimeManager.canSpawnAtCurrentTime(worldName, entityType)) {
            event.setCancelled(true);
            return;
        }
    }

    /**
     * Obsługuje spawn wszystkich entity (nie tylko stworzeń).
     * Używane jako dodatkowe zabezpieczenie dla niektórych typów entity.
     */
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
}