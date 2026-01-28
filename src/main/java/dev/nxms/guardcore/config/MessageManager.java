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

/**
 * Zarządza wiadomościami pluginu.
 * Obsługuje wielojęzyczność i formatowanie wiadomości.
 */
public class MessageManager {

    private final GuardCore plugin;
    private FileConfiguration messages;
    private String prefix;

    public MessageManager(GuardCore plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /**
     * Ładuje plik wiadomości na podstawie ustawionego języka.
     */
    public void loadMessages() {
        String language = plugin.getConfigManager().getLanguage();
        String fileName = "messages_" + language + ".yml";

        File messagesFile = new File(plugin.getDataFolder(), fileName);

        // Zapisz domyślne pliki wiadomości jeśli nie istnieją
        if (!new File(plugin.getDataFolder(), "messages_pl.yml").exists()) {
            plugin.saveResource("messages_pl.yml", false);
        }
        if (!new File(plugin.getDataFolder(), "messages_en.yml").exists()) {
            plugin.saveResource("messages_en.yml", false);
        }

        if (!messagesFile.exists()) {
            plugin.getLogger().warning("Messages file " + fileName + " not found, using messages_pl.yml");
            messagesFile = new File(plugin.getDataFolder(), "messages_pl.yml");
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = messages.getString("prefix", "&8[&6GuardCore&8] ");
    }

    /**
     * Przeładowuje wiadomości.
     */
    public void reload() {
        loadMessages();
    }

    /**
     * Pobiera surową wiadomość z pliku konfiguracji.
     */
    public String getRaw(String key) {
        return messages.getString(key, "&cMissing message: " + key);
    }

    /**
     * Pobiera wiadomość z prefixem i zamienionymi placeholderami.
     */
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = prefix + getRaw(key);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return message;
    }

    /**
     * Pobiera wiadomość z prefixem bez placeholderów.
     */
    public String getMessage(String key) {
        return getMessage(key, null);
    }

    /**
     * Wysyła wiadomość do gracza/konsoli.
     */
    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        String message = getMessage(key, placeholders);
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        sender.sendMessage(component);
    }

    /**
     * Wysyła wiadomość bez placeholderów.
     */
    public void send(CommandSender sender, String key) {
        send(sender, key, null);
    }

    /**
     * Wysyła surową wiadomość (bez prefixu) z placeholderami.
     */
    public void sendRaw(CommandSender sender, String key, Map<String, String> placeholders) {
        String message = getRaw(key);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        sender.sendMessage(component);
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