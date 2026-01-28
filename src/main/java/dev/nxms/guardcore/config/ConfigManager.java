package dev.nxms.guardcore.config;

import dev.nxms.guardcore.GuardCore;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private final GuardCore plugin;
    private FileConfiguration config;
    private File configFile;

    // Zmieniona struktura: przechowuje też UUID gracza który postawił blok
    private Map<String, PlacedBlockData> placedBlocks;

    public ConfigManager(GuardCore plugin) {
        this.plugin = plugin;
        this.placedBlocks = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        loadPlacedBlocks();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadPlacedBlocks();
    }

    public void saveConfig() {
        try {
            savePlacedBlocks();
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }

    public String getLanguage() {
        return config.getString("language", "pl");
    }

    private void ensureWorldSection(String worldName) {
        if (!config.contains("worlds." + worldName)) {
            config.createSection("worlds." + worldName);
            config.set("worlds." + worldName + ".blockDespawn.enabled", false);
            config.set("worlds." + worldName + ".blockDespawn.time", "1d0h0m0s");
            config.set("worlds." + worldName + ".waterFlow", true);
            config.set("worlds." + worldName + ".lavaFlow", true);
            config.set("worlds." + worldName + ".blockRedstoneMechanism", false);
            config.set("worlds." + worldName + ".blockDestruction", true);
            saveConfig();
        }
    }

    // Block Despawn methods

    public boolean isBlockDespawnEnabled(String worldName) {
        ensureWorldSection(worldName);
        return config.getBoolean("worlds." + worldName + ".blockDespawn.enabled", false);
    }

    public void setBlockDespawnEnabled(String worldName, boolean enabled) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".blockDespawn.enabled", enabled);
        saveConfig();
    }

    public String getBlockDespawnTime(String worldName) {
        ensureWorldSection(worldName);
        return config.getString("worlds." + worldName + ".blockDespawn.time", "1d0h0m0s");
    }

    public void setBlockDespawnTime(String worldName, String time) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".blockDespawn.time", time);
        saveConfig();
    }

    // Water/Lava Flow methods

    public boolean isWaterFlowEnabled(String worldName) {
        ensureWorldSection(worldName);
        return config.getBoolean("worlds." + worldName + ".waterFlow", true);
    }

    public void setWaterFlowEnabled(String worldName, boolean enabled) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".waterFlow", enabled);
        saveConfig();
    }

    public boolean isLavaFlowEnabled(String worldName) {
        ensureWorldSection(worldName);
        return config.getBoolean("worlds." + worldName + ".lavaFlow", true);
    }

    public void setLavaFlowEnabled(String worldName, boolean enabled) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".lavaFlow", enabled);
        saveConfig();
    }

    // Redstone Mechanism methods

    public boolean isRedstoneMechanismBlocked(String worldName) {
        ensureWorldSection(worldName);
        return config.getBoolean("worlds." + worldName + ".blockRedstoneMechanism", false);
    }

    public void setRedstoneMechanismBlocked(String worldName, boolean blocked) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".blockRedstoneMechanism", blocked);
        saveConfig();
    }

    // Block Destruction methods

    public boolean isBlockDestructionAllowed(String worldName) {
        ensureWorldSection(worldName);
        return config.getBoolean("worlds." + worldName + ".blockDestruction", true);
    }

    public void setBlockDestructionAllowed(String worldName, boolean allowed) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".blockDestruction", allowed);
        saveConfig();
    }

    // Entity Limit methods

    public void setEntityLimit(String worldName, String entityType, int limit) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".entityLimits." + entityType, limit);
        saveConfig();
    }

    public void removeEntityLimit(String worldName, String entityType) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".entityLimits." + entityType, null);
        saveConfig();
    }

    public int getEntityLimit(String worldName, String entityType) {
        ensureWorldSection(worldName);
        return config.getInt("worlds." + worldName + ".entityLimits." + entityType, -1);
    }

    public Map<String, Integer> getEntityLimits(String worldName) {
        ensureWorldSection(worldName);
        Map<String, Integer> limits = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("worlds." + worldName + ".entityLimits");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                limits.put(key, section.getInt(key));
            }
        }
        return limits;
    }

    // Entity Spawn Time methods

    public void setEntitySpawnTime(String worldName, String entityType, String from, String to) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".entitySpawnTimes." + entityType + ".from", from);
        config.set("worlds." + worldName + ".entitySpawnTimes." + entityType + ".to", to);
        saveConfig();
    }

    public void removeEntitySpawnTime(String worldName, String entityType) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".entitySpawnTimes." + entityType, null);
        saveConfig();
    }

    public String[] getEntitySpawnTime(String worldName, String entityType) {
        ensureWorldSection(worldName);
        String from = config.getString("worlds." + worldName + ".entitySpawnTimes." + entityType + ".from");
        String to = config.getString("worlds." + worldName + ".entitySpawnTimes." + entityType + ".to");
        if (from == null || to == null) {
            return null;
        }
        return new String[]{from, to};
    }

    public Map<String, String[]> getAllEntitySpawnTimes(String worldName) {
        ensureWorldSection(worldName);
        Map<String, String[]> times = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("worlds." + worldName + ".entitySpawnTimes");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String from = section.getString(key + ".from");
                String to = section.getString(key + ".to");
                if (from != null && to != null) {
                    times.put(key, new String[]{from, to});
                }
            }
        }
        return times;
    }

    // Entity Spawn Point methods

    public void addEntitySpawnPoint(String worldName, String name, String entityType, Location location) {
        ensureWorldSection(worldName);
        String path = "worlds." + worldName + ".entitySpawnPoints." + name;
        config.set(path + ".entity", entityType);
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        saveConfig();
    }

    public void removeEntitySpawnPoint(String worldName, String name) {
        ensureWorldSection(worldName);
        config.set("worlds." + worldName + ".entitySpawnPoints." + name, null);
        saveConfig();
    }

    public Map<String, Object> getEntitySpawnPoint(String worldName, String name) {
        ensureWorldSection(worldName);
        String path = "worlds." + worldName + ".entitySpawnPoints." + name;
        if (!config.contains(path)) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("entity", config.getString(path + ".entity"));
        data.put("x", config.getDouble(path + ".x"));
        data.put("y", config.getDouble(path + ".y"));
        data.put("z", config.getDouble(path + ".z"));
        return data;
    }

    public Map<String, Map<String, Object>> getAllEntitySpawnPoints(String worldName) {
        ensureWorldSection(worldName);
        Map<String, Map<String, Object>> points = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("worlds." + worldName + ".entitySpawnPoints");
        if (section != null) {
            for (String name : section.getKeys(false)) {
                points.put(name, getEntitySpawnPoint(worldName, name));
            }
        }
        return points;
    }

    // Disallowed Entity methods

    public void addDisallowedEntity(String worldName, String entityType) {
        ensureWorldSection(worldName);
        List<String> entities = getDisallowedEntities(worldName);
        if (!entities.contains(entityType)) {
            entities.add(entityType);
            config.set("worlds." + worldName + ".disallowedEntities", entities);
            saveConfig();
        }
    }

    public void removeDisallowedEntity(String worldName, String entityType) {
        ensureWorldSection(worldName);
        List<String> entities = getDisallowedEntities(worldName);
        entities.remove(entityType);
        config.set("worlds." + worldName + ".disallowedEntities", entities);
        saveConfig();
    }

    public List<String> getDisallowedEntities(String worldName) {
        ensureWorldSection(worldName);
        return new ArrayList<>(config.getStringList("worlds." + worldName + ".disallowedEntities"));
    }

    public boolean isEntityDisallowed(String worldName, String entityType) {
        return getDisallowedEntities(worldName).contains(entityType);
    }

    // Disallowed Block methods

    public void addDisallowedBlock(String worldName, String blockType) {
        ensureWorldSection(worldName);
        List<String> blocks = getDisallowedBlocks(worldName);
        if (!blocks.contains(blockType)) {
            blocks.add(blockType);
            config.set("worlds." + worldName + ".disallowedBlocks", blocks);
            saveConfig();
        }
    }

    public void removeDisallowedBlock(String worldName, String blockType) {
        ensureWorldSection(worldName);
        List<String> blocks = getDisallowedBlocks(worldName);
        blocks.remove(blockType);
        config.set("worlds." + worldName + ".disallowedBlocks", blocks);
        saveConfig();
    }

    public List<String> getDisallowedBlocks(String worldName) {
        ensureWorldSection(worldName);
        return new ArrayList<>(config.getStringList("worlds." + worldName + ".disallowedBlocks"));
    }

    public boolean isBlockDisallowed(String worldName, String blockType) {
        return getDisallowedBlocks(worldName).contains(blockType);
    }

    // ===== PLACED BLOCKS TRACKING =====

    /**
     * Klasa przechowująca dane o postawionym bloku.
     */
    public static class PlacedBlockData {
        private final long placedTime;
        private final UUID playerUUID;
        private final boolean bypassDespawn;

        public PlacedBlockData(long placedTime, UUID playerUUID, boolean bypassDespawn) {
            this.placedTime = placedTime;
            this.playerUUID = playerUUID;
            this.bypassDespawn = bypassDespawn;
        }

        // Konstruktor dla kompatybilności wstecznej
        public PlacedBlockData(long placedTime, UUID playerUUID) {
            this(placedTime, playerUUID, false);
        }

        public long getPlacedTime() {
            return placedTime;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public boolean hasBypassDespawn() {
            return bypassDespawn;
        }
    }

    /**
     * Dodaje blok do rejestru (z UUID gracza i flagą bypass).
     */
    public void addPlacedBlock(Location location, UUID playerUUID, boolean bypassDespawn) {
        String key = locationToKey(location);
        placedBlocks.put(key, new PlacedBlockData(System.currentTimeMillis(), playerUUID, bypassDespawn));
    }

    /**
     * Dodaje blok do rejestru (z UUID gracza, bez bypass).
     */
    public void addPlacedBlock(Location location, UUID playerUUID) {
        addPlacedBlock(location, playerUUID, false);
    }

    /**
     * Dodaje blok do rejestru (bez UUID - kompatybilność wsteczna).
     */
    public void addPlacedBlock(Location location) {
        addPlacedBlock(location, null, false);
    }

    public void removePlacedBlock(Location location) {
        String key = locationToKey(location);
        placedBlocks.remove(key);
    }

    public void removePlacedBlockByKey(String key) {
        placedBlocks.remove(key);
    }

    public boolean isBlockPlacedByPlayer(Location location) {
        String key = locationToKey(location);
        return placedBlocks.containsKey(key);
    }

    public long getBlockPlacedTime(Location location) {
        String key = locationToKey(location);
        PlacedBlockData data = placedBlocks.get(key);
        return data != null ? data.getPlacedTime() : -1L;
    }

    /**
     * Pobiera UUID gracza który postawił blok.
     */
    public UUID getBlockPlacedByPlayerUUID(Location location) {
        String key = locationToKey(location);
        PlacedBlockData data = placedBlocks.get(key);
        return data != null ? data.getPlayerUUID() : null;
    }

    /**
     * Pobiera UUID gracza który postawił blok (po kluczu).
     */
    public UUID getBlockPlacedByPlayerUUID(String key) {
        PlacedBlockData data = placedBlocks.get(key);
        return data != null ? data.getPlayerUUID() : null;
    }

    /**
     * Sprawdza czy blok ma bypass na despawn.
     */
    public boolean hasBlockBypassDespawn(String key) {
        PlacedBlockData data = placedBlocks.get(key);
        return data != null && data.hasBypassDespawn();
    }

    /**
     * Pobiera dane o postawionym bloku.
     */
    public PlacedBlockData getPlacedBlockData(String key) {
        return placedBlocks.get(key);
    }

    /**
     * Zwraca mapę kluczy lokalizacji do czasów postawienia (kompatybilność).
     */
    public Map<String, Long> getPlacedBlocks() {
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<String, PlacedBlockData> entry : placedBlocks.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getPlacedTime());
        }
        return result;
    }

    /**
     * Zwraca pełną mapę danych o postawionych blokach.
     */
    public Map<String, PlacedBlockData> getPlacedBlocksData() {
        return new HashMap<>(placedBlocks);
    }

    private String locationToKey(Location location) {
        return location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" +
                location.getBlockZ();
    }

    private void loadPlacedBlocks() {
        placedBlocks.clear();
        ConfigurationSection section = config.getConfigurationSection("placedBlocks");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String actualKey = key.replace(".", ":");

                // Sprawdź czy to nowy format (z UUID) czy stary
                if (section.isConfigurationSection(key)) {
                    // Nowy format
                    long time = section.getLong(key + ".time");
                    String uuidStr = section.getString(key + ".player");
                    boolean bypassDespawn = section.getBoolean(key + ".bypassDespawn", false);
                    UUID uuid = null;
                    if (uuidStr != null && !uuidStr.isEmpty()) {
                        try {
                            uuid = UUID.fromString(uuidStr);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    placedBlocks.put(actualKey, new PlacedBlockData(time, uuid, bypassDespawn));
                } else {
                    // Stary format (tylko czas)
                    long time = section.getLong(key);
                    placedBlocks.put(actualKey, new PlacedBlockData(time, null, false));
                }
            }
        }
        plugin.getLogger().info("Loaded " + placedBlocks.size() + " placed blocks from config");
    }

    private void savePlacedBlocks() {
        config.set("placedBlocks", null);
        for (Map.Entry<String, PlacedBlockData> entry : placedBlocks.entrySet()) {
            String safeKey = entry.getKey().replace(":", ".");
            PlacedBlockData data = entry.getValue();

            config.set("placedBlocks." + safeKey + ".time", data.getPlacedTime());
            config.set("placedBlocks." + safeKey + ".bypassDespawn", data.hasBypassDespawn());
            if (data.getPlayerUUID() != null) {
                config.set("placedBlocks." + safeKey + ".player", data.getPlayerUUID().toString());
            }
        }
    }
}