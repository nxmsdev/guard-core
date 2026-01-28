package dev.nxms.guardcore;

import dev.nxms.guardcore.commands.GuardCoreCommand;
import dev.nxms.guardcore.commands.GuardCoreTabCompleter;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.config.MessageManager;
import dev.nxms.guardcore.listeners.BlockListener;
import dev.nxms.guardcore.listeners.EntityListener;
import dev.nxms.guardcore.listeners.FluidListener;
import dev.nxms.guardcore.listeners.RedstoneListener;
import dev.nxms.guardcore.managers.BlockDespawnManager;
import dev.nxms.guardcore.managers.EntityLimitManager;
import dev.nxms.guardcore.managers.EntitySpawnPointManager;
import dev.nxms.guardcore.managers.EntitySpawnTimeManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Główna klasa pluginu GuardCore.
 * Zarządza inicjalizacją i wyłączaniem wszystkich komponentów pluginu.
 */
public class GuardCore extends JavaPlugin {

    private static GuardCore instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private BlockDespawnManager blockDespawnManager;
    private EntityLimitManager entityLimitManager;
    private EntitySpawnTimeManager entitySpawnTimeManager;
    private EntitySpawnPointManager entitySpawnPointManager;

    @Override
    public void onEnable() {
        instance = this;

        // Inicjalizacja managerów konfiguracji
        initializeManagers();

        // Rejestracja komend
        registerCommands();

        // Rejestracja listenerów
        registerListeners();

        getLogger().info("GuardCore has been enabled!");
        getLogger().info("Author: nxmsdev | Website: www.nxms.dev");
    }

    @Override
    public void onDisable() {
        // Zapisanie konfiguracji przed wyłączeniem
        if (configManager != null) {
            configManager.saveConfig();
        }

        // Zatrzymanie zadań despawnu bloków
        if (blockDespawnManager != null) {
            blockDespawnManager.shutdown();
        }

        getLogger().info("GuardCore has been disabled!");
    }

    /**
     * Inicjalizuje wszystkie managery pluginu.
     */
    private void initializeManagers() {
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        blockDespawnManager = new BlockDespawnManager(this);
        entityLimitManager = new EntityLimitManager(this);
        entitySpawnTimeManager = new EntitySpawnTimeManager(this);
        entitySpawnPointManager = new EntitySpawnPointManager(this);
    }

    /**
     * Rejestruje komendy pluginu.
     */
    private void registerCommands() {
        GuardCoreCommand commandExecutor = new GuardCoreCommand(this);
        GuardCoreTabCompleter tabCompleter = new GuardCoreTabCompleter(this);

        getCommand("guardcore").setExecutor(commandExecutor);
        getCommand("guardcore").setTabCompleter(tabCompleter);
    }

    /**
     * Rejestruje listenery zdarzeń.
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new FluidListener(this), this);
        getServer().getPluginManager().registerEvents(new RedstoneListener(this), this);
    }

    /**
     * Przeładowuje plugin i wszystkie jego komponenty.
     */
    public void reload() {
        configManager.reloadConfig();
        messageManager.reload();
        blockDespawnManager.reload();
        entitySpawnPointManager.reload();
    }

    /**
     * Zwraca instancję pluginu.
     */
    public static GuardCore getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public BlockDespawnManager getBlockDespawnManager() {
        return blockDespawnManager;
    }

    public EntityLimitManager getEntityLimitManager() {
        return entityLimitManager;
    }

    public EntitySpawnTimeManager getEntitySpawnTimeManager() {
        return entitySpawnTimeManager;
    }

    public EntitySpawnPointManager getEntitySpawnPointManager() {
        return entitySpawnPointManager;
    }
}