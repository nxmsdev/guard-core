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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Zarządza wiadomościami pluginu.
 * Obsługuje wielojęzyczność i formatowanie wiadomości.
 */
public class MessageManager {

    private final GuardCore plugin;
    private FileConfiguration messagesConfig;

    // Pattern do wykrywania {placeholder}
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    public MessageManager(GuardCore plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /**
     * Ładuje plik wiadomości na podstawie ustawionego języka.
     */
    private void loadMessages() {
        String currentLanguage = plugin.getConfigManager().getLanguage();
        String fileName = "messages_" + currentLanguage + ".yml";

        File messagesFile = new File(plugin.getDataFolder(), fileName);

        if (!messagesFile.exists()) {
            plugin.saveResource("messages_en.yml", false);
            plugin.getLogger().warning("Couldn't find " + fileName + "! Loading default messages file (messages_en.yml).");
            fileName = "messages_en.yml";
            messagesFile = new File(plugin.getDataFolder(), fileName);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultConfig);
        }

        plugin.getLogger().info("Messages has been loaded.");
    }

    /**
     * Przeładowuje wiadomości.
     */
    public void reload() {
        plugin.getLogger().info("Reloading messages.");
        loadMessages();
        plugin.getLogger().info("Messages has been reloaded.");
    }

    /**
     * Pobiera surową wiadomość z pliku konfiguracji.
     */
    public String getRaw(String key) {
        return messagesConfig.getString(key, "");
    }

    /**
     * Zamienia wszystkie {klucz} na wartości z pliku messages.
     * Pomija placeholdery przekazane w mapie (te będą zamienione później).
     */
    private String replaceConfigPlaceholders(String message, Map<String, String> customPlaceholders) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);

            // Sprawdź czy to jest placeholder z kodu (customPlaceholders)
            if (customPlaceholders != null && customPlaceholders.containsKey(placeholder)) {
                continue; // Pomiń - zostanie zamieniony później
            }

            // Sprawdź czy istnieje w pliku messages
            String value = getRaw(placeholder);
            if (!value.isEmpty()) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Zamienia placeholdery przekazane z kodu.
     */
    private String replaceCustomPlaceholders(String message, Map<String, String> placeholders) {
        if (placeholders == null) return message;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return message;
    }

    /**
     * Pobiera wiadomość z zamienionymi placeholderami z pliku messages i z kodu.
     */
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getRaw(key);

        if (message.isEmpty()) {
            return "&cMissing message: " + key;
        }

        // Najpierw zamień placeholdery z pliku messages (np. {prefix})
        message = replaceConfigPlaceholders(message, placeholders);

        // Potem zamień placeholdery z kodu (np. {player}, {reason})
        message = replaceCustomPlaceholders(message, placeholders);

        return message;
    }

    /**
     * Pobiera wiadomość bez placeholderów z kodu.
     */
    public String getMessage(String key) {
        return getMessage(key, null);
    }

    /**
     * Konwertuje wiadomość na Component.
     */
    public Component toComponent(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    /**
     * Wysyła wiadomość do gracza/konsoli.
     */
    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        String message = getMessage(key, placeholders);
        sender.sendMessage(toComponent(message));
    }

    /**
     * Wysyła wiadomość bez placeholderów.
     */
    public void send(CommandSender sender, String key) {
        send(sender, key, null);
    }

    /**
     * Wysyła surową wiadomość (bez zamiany placeholderów z pliku) z placeholderami z kodu.
     */
    public void sendRaw(CommandSender sender, String key, Map<String, String> placeholders) {
        String message = getRaw(key);

        if (message.isEmpty()) {
            message = "&cMissing message: " + key;
        }

        message = replaceCustomPlaceholders(message, placeholders);
        sender.sendMessage(toComponent(message));
    }

    /**
     * Wysyła surową wiadomość bez placeholderów.
     */
    public void sendRaw(CommandSender sender, String key) {
        sendRaw(sender, key, null);
    }

    /**
     * Konwertuje boolean na czytelny tekst.
     */
    public String getBooleanDisplay(boolean value) {
        return value ? getRaw("boolean-true") : getRaw("boolean-false");
    }

    /**
     * Tworzy mapę placeholderów z podanych par klucz-wartość.
     */
    public static Map<String, String> placeholders(String... keyValues) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValues.length - 1; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
}