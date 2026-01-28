package dev.nxms.guardcore.commands;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completer dla komend GuardCore.
 * Ukrywa komendy przed graczami bez odpowiednich uprawnień.
 */
public class GuardCoreTabCompleter implements TabCompleter {

    private final GuardCore plugin;

    // Komendy dla każdego typu akcji
    private static final List<String> SET_COMMANDS = Arrays.asList(
            "blockDespawnTime", "blockDespawn", "waterFlow", "lavaFlow",
            "blockRedstoneMechanism", "blockDestruction", "entitySpawnTime"
    );

    private static final List<String> ADD_COMMANDS = Arrays.asList(
            "entityLimit", "entitySpawnPoint", "disallowedEntity", "disallowedBlock"
    );

    private static final List<String> REMOVE_COMMANDS = Arrays.asList(
            "entityLimit", "entitySpawnTime", "entitySpawnPoint", "disallowedEntity", "disallowedBlock"
    );

    private static final List<String> INFO_COMMANDS = Arrays.asList(
            "blockDespawnTime", "blockDespawn", "waterFlow", "lavaFlow",
            "entityLimit", "entitySpawnTime", "blockRedstoneMechanism",
            "entitySpawnPoint", "disallowedEntity", "disallowedBlock", "blockDestruction"
    );

    private static final List<String> HELP_COMMANDS = Arrays.asList(
            "blockDespawnTime", "blockDespawn", "waterFlow", "lavaFlow",
            "entityLimit", "entitySpawnTime", "blockRedstoneMechanism",
            "entitySpawnPoint", "disallowedEntity", "disallowedBlock", "blockDestruction", "reload"
    );

    public GuardCoreTabCompleter(GuardCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!PermissionUtils.hasCommandAccess(sender)) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Główne podkomendy
            completions = getMainSubCommands(sender);
        } else if (args.length == 2) {
            // Drugie argumenty zależne od pierwszego
            completions = getSecondArguments(sender, args[0]);
        } else if (args.length == 3) {
            // Trzecie argumenty - zwykle światy
            completions = getThirdArguments(sender, args[0], args[1]);
        } else if (args.length == 4) {
            // Czwarte argumenty
            completions = getFourthArguments(sender, args[0], args[1], args[2]);
        } else if (args.length == 5) {
            // Piąte argumenty
            completions = getFifthArguments(sender, args[0], args[1]);
        } else if (args.length == 6) {
            // Szóste argumenty (dla entitySpawnTime - godzina "to")
            completions = getSixthArguments(sender, args[0], args[1]);
        }

        // Filtruj wyniki na podstawie tego co gracz już wpisał
        String currentArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg))
                .collect(Collectors.toList());
    }

    /**
     * Zwraca główne podkomendy dostępne dla gracza.
     */
    private List<String> getMainSubCommands(CommandSender sender) {
        List<String> commands = new ArrayList<>();

        if (PermissionUtils.hasSetPermission(sender)) {
            commands.add("set");
        }
        if (PermissionUtils.hasAddPermission(sender)) {
            commands.add("add");
        }
        if (PermissionUtils.hasRemovePermission(sender)) {
            commands.add("remove");
        }
        if (PermissionUtils.hasInfoPermission(sender)) {
            commands.add("info");
        }
        if (PermissionUtils.hasReloadPermission(sender)) {
            commands.add("reload");
        }
        if (PermissionUtils.hasHelpPermission(sender)) {
            commands.add("help");
        }

        return commands;
    }

    /**
     * Zwraca dostępne drugie argumenty.
     */
    private List<String> getSecondArguments(CommandSender sender, String firstArg) {
        switch (firstArg.toLowerCase()) {
            case "set":
                if (PermissionUtils.hasSetPermission(sender)) {
                    return new ArrayList<>(SET_COMMANDS);
                }
                break;
            case "add":
                if (PermissionUtils.hasAddPermission(sender)) {
                    return new ArrayList<>(ADD_COMMANDS);
                }
                break;
            case "remove":
                if (PermissionUtils.hasRemovePermission(sender)) {
                    return new ArrayList<>(REMOVE_COMMANDS);
                }
                break;
            case "info":
                if (PermissionUtils.hasInfoPermission(sender)) {
                    return new ArrayList<>(INFO_COMMANDS);
                }
                break;
            case "help":
                if (PermissionUtils.hasHelpPermission(sender)) {
                    return new ArrayList<>(HELP_COMMANDS);
                }
                break;
        }
        return new ArrayList<>();
    }

    /**
     * Zwraca trzecie argumenty - głównie nazwy światów.
     */
    private List<String> getThirdArguments(CommandSender sender, String firstArg, String secondArg) {
        // Dla większości komend trzeci argument to nazwa świata
        if (firstArg.equalsIgnoreCase("set") ||
                firstArg.equalsIgnoreCase("add") ||
                firstArg.equalsIgnoreCase("remove") ||
                firstArg.equalsIgnoreCase("info")) {
            return getWorldNames();
        }
        return new ArrayList<>();
    }

    /**
     * Zwraca czwarte argumenty.
     */
    private List<String> getFourthArguments(CommandSender sender, String firstArg, String secondArg, String thirdArg) {
        String setting = secondArg.toLowerCase();

        switch (firstArg.toLowerCase()) {
            case "set":
                return getSetFourthArguments(setting);
            case "add":
                return getAddFourthArguments(setting);
            case "remove":
                return getRemoveFourthArguments(setting, thirdArg);
            case "info":
                return getInfoFourthArguments(setting, thirdArg);
        }

        return new ArrayList<>();
    }

    private List<String> getSetFourthArguments(String setting) {
        switch (setting) {
            case "blockdespawn":
            case "waterflow":
            case "lavaflow":
            case "blockredstonemechanism":
            case "blockdestruction":
                return Arrays.asList("true", "false");
            case "blockdespawntime":
                return Arrays.asList("1d0h0m0s", "12h0m0s", "1h0m0s", "30m0s");
            case "entityspawntime":
                return getEntityNames();
        }
        return new ArrayList<>();
    }

    private List<String> getAddFourthArguments(String setting) {
        switch (setting) {
            case "entitylimit":
            case "entityspawnpoint":
            case "disallowedentity":
                return getEntityNames();
            case "disallowedblock":
                return getBlockNames();
        }
        return new ArrayList<>();
    }

    private List<String> getRemoveFourthArguments(String setting, String worldName) {
        switch (setting) {
            case "entitylimit":
            case "entityspawntime":
            case "disallowedentity":
                return getEntityNames();
            case "entityspawnpoint":
                return getSpawnPointNames(worldName);
            case "disallowedblock":
                return getBlockNames();
        }
        return new ArrayList<>();
    }

    private List<String> getInfoFourthArguments(String setting, String worldName) {
        switch (setting) {
            case "entitylimit":
            case "entityspawntime":
                return getEntityNames();
            case "entityspawnpoint":
                return getSpawnPointNames(worldName);
        }
        return new ArrayList<>();
    }

    /**
     * Zwraca piąte argumenty.
     */
    private List<String> getFifthArguments(CommandSender sender, String firstArg, String secondArg) {
        String setting = secondArg.toLowerCase();

        if (firstArg.equalsIgnoreCase("add")) {
            if (setting.equals("entitylimit")) {
                return Arrays.asList("1", "5", "10", "25", "50", "100");
            }
        }

        if (firstArg.equalsIgnoreCase("set")) {
            if (setting.equals("entityspawntime")) {
                return getTimeExamples();
            }
        }

        return new ArrayList<>();
    }

    /**
     * Zwraca szóste argumenty.
     */
    private List<String> getSixthArguments(CommandSender sender, String firstArg, String secondArg) {
        if (firstArg.equalsIgnoreCase("set") && secondArg.equalsIgnoreCase("entityspawntime")) {
            return getTimeExamples();
        }
        return new ArrayList<>();
    }

    /**
     * Zwraca listę nazw wszystkich światów.
     */
    private List<String> getWorldNames() {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca listę nazw wszystkich entity.
     */
    private List<String> getEntityNames() {
        return Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .map(EntityType::name)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca listę nazw wszystkich bloków.
     */
    private List<String> getBlockNames() {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::name)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca listę nazw spawn pointów dla danego świata.
     */
    private List<String> getSpawnPointNames(String worldName) {
        return new ArrayList<>(plugin.getConfigManager()
                .getAllEntitySpawnPoints(worldName).keySet());
    }

    /**
     * Zwraca przykładowe godziny dla tab completion.
     */
    private List<String> getTimeExamples() {
        return Arrays.asList("00:00", "06:00", "12:00", "18:00", "20:00", "22:00");
    }
}