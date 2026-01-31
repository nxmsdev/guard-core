package dev.nxms.guardcore.config;

import dev.nxms.guardcore.GuardCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages plugin messages and translations.
 * Supports multiple languages, modular prefixes, and color formatting.
 */
public class MessageManager {

    private final GuardCore plugin;
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();

    private FileConfiguration messagesConfig;
    private String language;

    // Pattern to match {placeholder} format
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    public MessageManager(GuardCore plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /**
     * Loads messages from the appropriate language file.
     */
    private void loadMessages() {
        // Save default message file if exists in JAR
        saveDefaultMessages("messages_en.yml");

        // Load configured language
        language = plugin.getConfigManager().getLanguage().toLowerCase();
        String fileName = "messages_" + language + ".yml";
        File file = new File(plugin.getDataFolder(), fileName);

        // Fallback to English if selected language doesn't exist
        if (!file.exists()) {
            plugin.getLogger().warning("Messages file " + fileName + " not found! Using messages_en.yml.");
            fileName = "messages_en.yml";
            file = new File(plugin.getDataFolder(), fileName);

            // Create English file if it doesn't exist
            if (!file.exists() && plugin.getResource("messages_en.yml") != null) {
                plugin.saveResource("messages_en.yml", false);
            }
        }

        messagesConfig = YamlConfiguration.loadConfiguration(file);

        // Load defaults from JAR
        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
            );
            messagesConfig.setDefaults(defaultConfig);
        }

        plugin.getLogger().info("Messages file has been loaded (" + fileName + ").");
    }

    /**
     * Saves a default message file if it doesn't exist.
     * Only saves if the resource exists in the JAR.
     */
    private void saveDefaultMessages(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists() && plugin.getResource(fileName) != null) {
            plugin.saveResource(fileName, false);
            plugin.getLogger().info("Created default " + fileName + " file.");
        }
    }

    /**
     * Reloads messages from the language file.
     */
    public void reload() {
        loadMessages();
    }

    /**
     * Gets a raw message from config without any processing.
     */
    public String getRaw(String key) {
        return messagesConfig.getString(key, "");
    }

    /**
     * Checks if a key exists in the messages config.
     */
    public boolean hasKey(String key) {
        return messagesConfig.contains(key) && !getRaw(key).isEmpty();
    }

    /**
     * Replaces {key} placeholders with values from messages file.
     * Only replaces placeholders that exist as keys in the config.
     * Prevents infinite recursion by tracking already processed keys.
     */
    private String replaceConfigPlaceholders(String message, Set<String> processedKeys) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String fullMatch = matcher.group(0);  // {placeholder}
            String placeholder = matcher.group(1); // placeholder

            // Skip if already processed (prevents infinite recursion)
            if (processedKeys.contains(placeholder)) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullMatch));
                continue;
            }

            // Check if this placeholder exists in config
            if (!hasKey(placeholder)) {
                // Keep original placeholder for custom placeholders like {player}, {reason}
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullMatch));
                continue;
            }

            // Mark as processed
            Set<String> newProcessedKeys = new HashSet<>(processedKeys);
            newProcessedKeys.add(placeholder);

            // Get and recursively process the value
            String value = getRaw(placeholder);
            value = replaceConfigPlaceholders(value, newProcessedKeys);

            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Gets a formatted message with config placeholders replaced.
     */
    public String get(String key) {
        String message = getRaw(key);
        if (message.isEmpty()) {
            return "&cMissing message: " + key;
        }

        // Replace config placeholders (like {prefix-error}, {prefix-success}, etc.)
        Set<String> processedKeys = new HashSet<>();
        processedKeys.add(key); // Prevent self-reference
        message = replaceConfigPlaceholders(message, processedKeys);

        return message;
    }

    /**
     * Gets a formatted message with custom placeholders replaced.
     */
    public String get(String key, Map<String, String> placeholders) {
        String message = get(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return message;
    }

    /**
     * Converts legacy text with color codes to Adventure Component.
     */
    public Component toComponent(String message) {
        return legacy.deserialize(message);
    }

    // ==================== SEND METHODS ====================

    /**
     * Sends a message to the sender.
     */
    public void send(CommandSender sender, String key) {
        sender.sendMessage(toComponent(get(key)));
    }

    /**
     * Sends a message with placeholder replacements.
     */
    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(toComponent(get(key, placeholders)));
    }

    /**
     * Sends raw text (not from config) to sender.
     */
    public void sendText(CommandSender sender, String text) {
        sender.sendMessage(toComponent(text));
    }

    /**
     * Sends raw text with placeholder replacements.
     */
    public void sendText(CommandSender sender, String text, Map<String, String> placeholders) {
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                text = text.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        sender.sendMessage(toComponent(text));
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Gets the current language code.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Converts boolean to readable text from config.
     */
    public String getBooleanDisplay(boolean value) {
        return value ? get("boolean-true") : get("boolean-false");
    }

    /**
     * Helper method to create placeholder maps easily.
     * Usage: placeholders("player", "Steve", "reason", "Hacking")
     */
    public static Map<String, String> placeholders(String... keyValues) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValues.length - 1; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
}