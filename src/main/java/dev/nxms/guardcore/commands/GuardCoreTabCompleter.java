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

public class GuardCoreTabCompleter implements TabCompleter {

    private final GuardCore plugin;

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
            "entitySpawnPoint", "disallowedEntity", "disallowedBlock", "blockDestruction", "reload", "bypass"
    );

    private static final List<String> BYPASS_TYPES = Arrays.asList(
            "disallowedBlocks",
            "blockDespawn",
            "blockDestruction"
    );

    private static final List<String> BOOLEAN_VALUES = Arrays.asList("true", "false");

    private static final List<String> DURATION_EXAMPLES = Arrays.asList(
            "1t", "5t", "10t", "20t",
            "50ms", "100ms", "250ms", "500ms",
            "1s", "5s", "10s", "30s",
            "0.5s", "1.5s", "2.5s",
            "1m", "5m", "10m", "30m",
            "1h", "6h", "12h",
            "1d", "7d",
            "1s500ms", "1s10t", "1m30s", "1h30m", "1d12h"
    );

    private static final List<String> TIME_OF_DAY_EXAMPLES = Arrays.asList(
            "00:00", "00:30",
            "01:00", "01:30",
            "02:00", "02:30",
            "03:00", "03:30",
            "04:00", "04:30",
            "05:00", "05:30",
            "06:00", "06:30",
            "07:00", "07:30",
            "08:00", "08:30",
            "09:00", "09:30",
            "10:00", "10:30",
            "11:00", "11:30",
            "12:00", "12:30",
            "13:00", "13:30",
            "14:00", "14:30",
            "15:00", "15:30",
            "16:00", "16:30",
            "17:00", "17:30",
            "18:00", "18:30",
            "19:00", "19:30",
            "20:00", "20:30",
            "21:00", "21:30",
            "22:00", "22:30",
            "23:00", "23:30"
    );

    private static final List<String> LIMIT_EXAMPLES = Arrays.asList(
            "1", "5", "10", "25", "50", "100", "250", "500"
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

        switch (args.length) {
            case 1:
                completions = getMainSubCommands(sender);
                break;
            case 2:
                completions = getSecondArguments(sender, args[0]);
                break;
            case 3:
                completions = getThirdArguments(sender, args[0], args[1]);
                break;
            case 4:
                completions = getFourthArguments(sender, args[0], args[1], args[2]);
                break;
            case 5:
                completions = getFifthArguments(sender, args[0], args[1]);
                break;
            case 6:
                completions = getSixthArguments(sender, args[0], args[1]);
                break;
        }

        String currentArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg))
                .collect(Collectors.toList());
    }

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
        if (PermissionUtils.hasBypassPermission(sender)) {
            commands.add("bypass");
        }

        return commands;
    }

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
            case "bypass":
                if (PermissionUtils.hasBypassPermission(sender)) {
                    return new ArrayList<>(BYPASS_TYPES);
                }
                break;
        }
        return new ArrayList<>();
    }

    private List<String> getThirdArguments(CommandSender sender, String firstArg, String secondArg) {
        String action = firstArg.toLowerCase();

        if (action.equals("set") || action.equals("add") || action.equals("remove") || action.equals("info")) {
            return getWorldNames();
        }

        // bypass <type> <true/false>
        if (action.equals("bypass")) {
            String bypassType = secondArg.toLowerCase();
            if (BYPASS_TYPES.stream().anyMatch(type -> type.equalsIgnoreCase(bypassType))) {
                return new ArrayList<>(BOOLEAN_VALUES);
            }
        }

        return new ArrayList<>();
    }

    private List<String> getFourthArguments(CommandSender sender, String firstArg, String secondArg, String thirdArg) {
        String action = firstArg.toLowerCase();
        String setting = secondArg.toLowerCase();

        switch (action) {
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
                return new ArrayList<>(BOOLEAN_VALUES);
            case "blockdespawntime":
                return new ArrayList<>(DURATION_EXAMPLES);
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

    private List<String> getFifthArguments(CommandSender sender, String firstArg, String secondArg) {
        String action = firstArg.toLowerCase();
        String setting = secondArg.toLowerCase();

        if (action.equals("add") && setting.equals("entitylimit")) {
            return new ArrayList<>(LIMIT_EXAMPLES);
        }

        if (action.equals("set") && setting.equals("entityspawntime")) {
            return new ArrayList<>(TIME_OF_DAY_EXAMPLES);
        }

        return new ArrayList<>();
    }

    private List<String> getSixthArguments(CommandSender sender, String firstArg, String secondArg) {
        String action = firstArg.toLowerCase();
        String setting = secondArg.toLowerCase();

        if (action.equals("set") && setting.equals("entityspawntime")) {
            return new ArrayList<>(TIME_OF_DAY_EXAMPLES);
        }

        return new ArrayList<>();
    }

    private List<String> getWorldNames() {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList());
    }

    private List<String> getEntityNames() {
        return Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .map(EntityType::name)
                .collect(Collectors.toList());
    }

    private List<String> getBlockNames() {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::name)
                .collect(Collectors.toList());
    }

    private List<String> getSpawnPointNames(String worldName) {
        return new ArrayList<>(plugin.getConfigManager()
                .getAllEntitySpawnPoints(worldName).keySet());
    }
}