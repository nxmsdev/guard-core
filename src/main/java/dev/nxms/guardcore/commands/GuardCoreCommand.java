package dev.nxms.guardcore.commands;

import dev.nxms.guardcore.GuardCore;
import dev.nxms.guardcore.config.ConfigManager;
import dev.nxms.guardcore.config.MessageManager;
import dev.nxms.guardcore.utils.PermissionUtils;
import dev.nxms.guardcore.utils.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class GuardCoreCommand implements CommandExecutor {

    private final GuardCore plugin;
    private final ConfigManager config;
    private final MessageManager messages;

    public GuardCoreCommand(GuardCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.messages = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionUtils.hasCommandAccess(sender)) {
            messages.send(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            handleHelp(sender, null);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set":
                handleSet(sender, args);
                break;
            case "add":
                handleAdd(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            case "info":
                handleInfo(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "help":
                handleHelp(sender, args.length > 1 ? args[1] : null);
                break;
            case "bypass":
                handleBypass(sender, args);
                break;
            default:
                messages.send(sender, "unknown-command");
                break;
        }

        return true;
    }

    // ===== BYPASS COMMANDS =====

    // ===== BYPASS COMMANDS =====

    private void handleBypass(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messages.send(sender, "player-only-command");
            return;
        }

        if (!PermissionUtils.hasBypassPermission(sender)) {
            messages.send(sender, "no-permission");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            handleBypassHelp(player);
            return;
        }

        String bypassType = args[1].toLowerCase();

        switch (bypassType) {
            case "disallowedblocks":
                handleBypassDisallowedBlocks(player, args);
                break;
            case "blockdespawn":
                handleBypassBlockDespawn(player, args);
                break;
            case "blockdestruction":
                handleBypassBlockDestruction(player, args);
                break;
            case "waterflow":
                handleBypassWaterFlow(player, args);
                break;
            case "lavaflow":
                handleBypassLavaFlow(player, args);
                break;
            default:
                handleBypassHelp(player);
                break;
        }
    }

    private void handleBypassDisallowedBlocks(Player player, String[] args) {
        boolean newState;

        if (args.length < 3) {
            newState = plugin.getBypassManager().toggleDisallowedBlocksBypass(player.getUniqueId());
        } else {
            String value = args[2].toLowerCase();
            switch (value) {
                case "true":
                case "on":
                case "tak":
                case "1":
                    newState = true;
                    break;
                case "false":
                case "off":
                case "nie":
                case "0":
                    newState = false;
                    break;
                default:
                    messages.send(player, "invalid-boolean");
                    return;
            }
            plugin.getBypassManager().setDisallowedBlocksBypass(player.getUniqueId(), newState);
        }

        messages.send(player, "bypass-disallowedblocks-set",
                MessageManager.placeholders("status", messages.getBooleanDisplay(newState)));
    }

    private void handleBypassBlockDespawn(Player player, String[] args) {
        boolean newState;

        if (args.length < 3) {
            newState = plugin.getBypassManager().toggleBlockDespawnBypass(player.getUniqueId());
        } else {
            String value = args[2].toLowerCase();
            switch (value) {
                case "true":
                case "on":
                case "tak":
                case "1":
                    newState = true;
                    break;
                case "false":
                case "off":
                case "nie":
                case "0":
                    newState = false;
                    break;
                default:
                    messages.send(player, "invalid-boolean");
                    return;
            }
            plugin.getBypassManager().setBlockDespawnBypass(player.getUniqueId(), newState);
        }

        messages.send(player, "bypass-blockdespawn-set",
                MessageManager.placeholders("status", messages.getBooleanDisplay(newState)));
    }

    private void handleBypassBlockDestruction(Player player, String[] args) {
        boolean newState;

        if (args.length < 3) {
            newState = plugin.getBypassManager().toggleBlockDestructionBypass(player.getUniqueId());
        } else {
            String value = args[2].toLowerCase();
            switch (value) {
                case "true":
                case "on":
                case "tak":
                case "1":
                    newState = true;
                    break;
                case "false":
                case "off":
                case "nie":
                case "0":
                    newState = false;
                    break;
                default:
                    messages.send(player, "invalid-boolean");
                    return;
            }
            plugin.getBypassManager().setBlockDestructionBypass(player.getUniqueId(), newState);
        }

        messages.send(player, "bypass-blockdestruction-set",
                MessageManager.placeholders("status", messages.getBooleanDisplay(newState)));
    }

    private void handleBypassWaterFlow(Player player, String[] args) {
        boolean newState;

        if (args.length < 3) {
            newState = plugin.getBypassManager().toggleWaterFlowBypass(player.getUniqueId());
        } else {
            String value = args[2].toLowerCase();
            switch (value) {
                case "true":
                case "on":
                case "tak":
                case "1":
                    newState = true;
                    break;
                case "false":
                case "off":
                case "nie":
                case "0":
                    newState = false;
                    break;
                default:
                    messages.send(player, "invalid-boolean");
                    return;
            }
            plugin.getBypassManager().setWaterFlowBypass(player.getUniqueId(), newState);
        }

        messages.send(player, "bypass-waterflow-set",
                MessageManager.placeholders("status", messages.getBooleanDisplay(newState)));
    }

    private void handleBypassLavaFlow(Player player, String[] args) {
        boolean newState;

        if (args.length < 3) {
            newState = plugin.getBypassManager().toggleLavaFlowBypass(player.getUniqueId());
        } else {
            String value = args[2].toLowerCase();
            switch (value) {
                case "true":
                case "on":
                case "tak":
                case "1":
                    newState = true;
                    break;
                case "false":
                case "off":
                case "nie":
                case "0":
                    newState = false;
                    break;
                default:
                    messages.send(player, "invalid-boolean");
                    return;
            }
            plugin.getBypassManager().setLavaFlowBypass(player.getUniqueId(), newState);
        }

        messages.send(player, "bypass-lavaflow-set",
                MessageManager.placeholders("status", messages.getBooleanDisplay(newState)));
    }

    private void handleBypassHelp(Player player) {
        boolean disallowedBlocksStatus = plugin.getBypassManager().hasDisallowedBlocksBypass(player.getUniqueId());
        boolean blockDespawnStatus = plugin.getBypassManager().hasBlockDespawnBypass(player.getUniqueId());
        boolean blockDestructionStatus = plugin.getBypassManager().hasBlockDestructionBypass(player.getUniqueId());
        boolean waterFlowStatus = plugin.getBypassManager().hasWaterFlowBypass(player.getUniqueId());
        boolean lavaFlowStatus = plugin.getBypassManager().hasLavaFlowBypass(player.getUniqueId());

        messages.send(player, "bypass-info", MessageManager.placeholders(
                "disallowedblocks_status", messages.getBooleanDisplay(disallowedBlocksStatus),
                "blockdespawn_status", messages.getBooleanDisplay(blockDespawnStatus),
                "blockdestruction_status", messages.getBooleanDisplay(blockDestructionStatus),
                "waterflow_status", messages.getBooleanDisplay(waterFlowStatus),
                "lavaflow_status", messages.getBooleanDisplay(lavaFlowStatus)
        ));
    }

    // ===== SET COMMANDS =====

    private void handleSet(CommandSender sender, String[] args) {
        if (!PermissionUtils.hasSetPermission(sender)) {
            messages.send(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "set"));
            return;
        }

        String setting = args[1].toLowerCase();

        switch (setting) {
            case "blockdespawntime":
                handleSetBlockDespawnTime(sender, args);
                break;
            case "blockdespawn":
                handleSetBlockDespawn(sender, args);
                break;
            case "waterflow":
                handleSetWaterFlow(sender, args);
                break;
            case "lavaflow":
                handleSetLavaFlow(sender, args);
                break;
            case "blockredstonemechanism":
                handleSetRedstoneMechanism(sender, args);
                break;
            case "blockdestruction":
                handleSetBlockDestruction(sender, args);
                break;
            case "entityspawntime":
                handleSetEntitySpawnTime(sender, args);
                break;
            case "entityspawnpointtime":
                handleSetEntitySpawnPointTime(sender, args);
                break;
            default:
                messages.send(sender, "unknown-command");
                break;
        }
    }

    private void handleSetBlockDespawnTime(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "blockDespawnTime"));
            return;
        }

        String worldName = args[2];
        String time = args[3];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!TimeParser.isValidDuration(time)) {
            messages.send(sender, "invalid-time-format");
            return;
        }

        config.setBlockDespawnTime(worldName, time);
        messages.send(sender, "blockdespawn-set-time", MessageManager.placeholders(
                "world", worldName,
                "time", time
        ));
    }

    private void handleSetBlockDespawn(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "blockDespawn"));
            return;
        }

        String worldName = args[2];
        String value = args[3].toLowerCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!value.equals("true") && !value.equals("false")) {
            messages.send(sender, "invalid-boolean");
            return;
        }

        boolean enabled = Boolean.parseBoolean(value);
        config.setBlockDespawnEnabled(worldName, enabled);

        messages.send(sender, "blockdespawn-set-enabled", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(enabled)
        ));
    }

    private void handleSetWaterFlow(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "waterFlow"));
            return;
        }

        String worldName = args[2];
        String value = args[3].toLowerCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!value.equals("true") && !value.equals("false")) {
            messages.send(sender, "invalid-boolean");
            return;
        }

        boolean enabled = Boolean.parseBoolean(value);
        config.setWaterFlowEnabled(worldName, enabled);

        messages.send(sender, "waterflow-set", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(enabled)
        ));
    }

    private void handleSetLavaFlow(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "lavaFlow"));
            return;
        }

        String worldName = args[2];
        String value = args[3].toLowerCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!value.equals("true") && !value.equals("false")) {
            messages.send(sender, "invalid-boolean");
            return;
        }

        boolean enabled = Boolean.parseBoolean(value);
        config.setLavaFlowEnabled(worldName, enabled);

        messages.send(sender, "lavaflow-set", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(enabled)
        ));
    }

    private void handleSetRedstoneMechanism(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "blockRedstoneMechanism"));
            return;
        }

        String worldName = args[2];
        String value = args[3].toLowerCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!value.equals("true") && !value.equals("false")) {
            messages.send(sender, "invalid-boolean");
            return;
        }

        boolean blocked = Boolean.parseBoolean(value);
        config.setRedstoneMechanismBlocked(worldName, blocked);

        messages.send(sender, "redstonemechanism-set", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(blocked)
        ));
    }

    private void handleSetBlockDestruction(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "blockDestruction"));
            return;
        }

        String worldName = args[2];
        String value = args[3].toLowerCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!value.equals("true") && !value.equals("false")) {
            messages.send(sender, "invalid-boolean");
            return;
        }

        boolean allowed = Boolean.parseBoolean(value);
        config.setBlockDestructionAllowed(worldName, allowed);

        messages.send(sender, "blockdestruction-set", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(allowed)
        ));
    }

    private void handleSetEntitySpawnTime(CommandSender sender, String[] args) {
        if (args.length < 6) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entitySpawnTime"));
            return;
        }

        String worldName = args[2];
        String entityName = args[3].toUpperCase();
        String from = args[4];
        String to = args[5];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!isValidEntity(entityName)) {
            messages.send(sender, "invalid-entity", MessageManager.placeholders("entity", entityName));
            return;
        }

        if (!TimeParser.isValidTimeOfDay(from) || !TimeParser.isValidTimeOfDay(to)) {
            messages.send(sender, "invalid-time-range");
            return;
        }

        config.setEntitySpawnTime(worldName, entityName, from, to);

        messages.send(sender, "entityspawntime-set", MessageManager.placeholders(
                "entity", entityName,
                "from", from,
                "to", to,
                "world", worldName
        ));
    }

    // ===== ADD COMMANDS =====

    private void handleAdd(CommandSender sender, String[] args) {
        if (!PermissionUtils.hasAddPermission(sender)) {
            messages.send(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "add"));
            return;
        }

        String setting = args[1].toLowerCase();

        switch (setting) {
            case "entitylimit":
                handleAddEntityLimit(sender, args);
                break;
            case "entityspawnpoint":
                handleAddEntitySpawnPoint(sender, args);
                break;
            case "disallowedentity":
                handleAddDisallowedEntity(sender, args);
                break;
            case "disallowedblock":
                handleAddDisallowedBlock(sender, args);
                break;
            default:
                messages.send(sender, "unknown-command");
                break;
        }
    }

    private void handleAddEntityLimit(CommandSender sender, String[] args) {
        if (args.length < 5) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entityLimit"));
            return;
        }

        String worldName = args[2];
        String entityName = args[3].toUpperCase();
        String limitStr = args[4];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!isValidEntity(entityName)) {
            messages.send(sender, "invalid-entity", MessageManager.placeholders("entity", entityName));
            return;
        }

        int limit;
        try {
            limit = Integer.parseInt(limitStr);
            if (limit < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entityLimit"));
            return;
        }

        config.setEntityLimit(worldName, entityName, limit);

        messages.send(sender, "entitylimit-added", MessageManager.placeholders(
                "limit", String.valueOf(limit),
                "entity", entityName,
                "world", worldName
        ));
    }

    private void handleAddEntitySpawnPoint(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messages.send(sender, "player-only-command");
            return;
        }

        if (args.length < 6) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entitySpawnPoint"));
            return;
        }

        Player player = (Player) sender;
        String worldName = args[2];
        String entityName = args[3].toUpperCase();
        String pointName = args[4];
        String intervalStr = args[5].toLowerCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!isValidEntity(entityName)) {
            messages.send(sender, "invalid-entity", MessageManager.placeholders("entity", entityName));
            return;
        }

        // Parsuj interwał używając TimeParser
        if (!TimeParser.isValidDuration(intervalStr)) {
            messages.send(sender, "invalid-interval");
            return;
        }

        long intervalMs = TimeParser.parseDuration(intervalStr);
        long intervalTicks = intervalMs / 50; // 1 tick = 50ms

        if (intervalTicks <= 0) {
            messages.send(sender, "invalid-interval");
            return;
        }

        Location location = player.getLocation();
        config.addEntitySpawnPoint(worldName, pointName, entityName, location, intervalTicks);

        // Przeładuj EntitySpawnPointManager żeby uwzględnił nowy punkt
        plugin.getEntitySpawnPointManager().reload();

        String intervalDisplay = TimeParser.formatDuration(intervalMs);
        messages.send(sender, "entityspawnpoint-added", MessageManager.placeholders(
                "name", pointName,
                "entity", entityName,
                "world", worldName,
                "interval", intervalDisplay
        ));
    }

    private void handleInfoEntitySpawnPoint(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entitySpawnPoint"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (args.length >= 4) {
            String pointName = args[3];
            Map<String, Object> point = config.getEntitySpawnPoint(worldName, pointName);

            if (point == null) {
                messages.send(sender, "entityspawnpoint-not-found", MessageManager.placeholders(
                        "name", pointName,
                        "world", worldName
                ));
                return;
            }

            long intervalTicks = (Long) point.get("interval");
            long intervalMs = intervalTicks * 50;
            String intervalDisplay = TimeParser.formatDuration(intervalMs);

            messages.send(sender, "entityspawnpoint-info", MessageManager.placeholders(
                    "name", pointName,
                    "world", worldName,
                    "entity", (String) point.get("entity"),
                    "x", String.format("%.2f", (Double) point.get("x")),
                    "y", String.format("%.2f", (Double) point.get("y")),
                    "z", String.format("%.2f", (Double) point.get("z")),
                    "interval", intervalDisplay
            ));
        } else {
            Map<String, Map<String, Object>> points = config.getAllEntitySpawnPoints(worldName);

            if (points.isEmpty()) {
                messages.send(sender, "entityspawnpoint-list-empty", MessageManager.placeholders("world", worldName));
                return;
            }

            messages.send(sender, "entityspawnpoint-list-header", MessageManager.placeholders("world", worldName));

            for (Map.Entry<String, Map<String, Object>> entry : points.entrySet()) {
                Map<String, Object> point = entry.getValue();
                long intervalTicks = (Long) point.get("interval");
                long intervalMs = intervalTicks * 50;
                String intervalDisplay = TimeParser.formatDuration(intervalMs);

                messages.send(sender, "entityspawnpoint-list-item", MessageManager.placeholders(
                        "name", entry.getKey(),
                        "entity", (String) point.get("entity"),
                        "x", String.format("%.0f", (Double) point.get("x")),
                        "y", String.format("%.0f", (Double) point.get("y")),
                        "z", String.format("%.0f", (Double) point.get("z")),
                        "interval", intervalDisplay
                ));
            }

            messages.send(sender, "entityspawnpoint-list-footer", MessageManager.placeholders("world", worldName));
        }
    }

    // Dodaj tę metodę pomocniczą na końcu klasy (przed ostatnim }):
    private String formatInterval(long ticks) {
        if (ticks % 20 == 0) {
            long seconds = ticks / 20;
            if (seconds >= 60 && seconds % 60 == 0) {
                return (seconds / 60) + "m";
            }
            return seconds + "s";
        }
        return ticks + "t";
    }

    private void handleSetEntitySpawnPointTime(CommandSender sender, String[] args) {
        if (args.length < 5) {
            messages.send(sender, "invalid-arguments",
                    MessageManager.placeholders("command", "entitySpawnPointTime"));
            return;
        }

        String worldName = args[2];
        String pointName = args[3];
        String intervalStr = args[4].toLowerCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        // spawn point musi istnieć
        if (config.getEntitySpawnPoint(worldName, pointName) == null) {
            messages.send(sender, "entityspawnpoint-not-found", MessageManager.placeholders(
                    "name", pointName,
                    "world", worldName
            ));
            return;
        }

        // Parsuj interwał (tym samym formatem co blockDespawnTime)
        if (!TimeParser.isValidDuration(intervalStr)) {
            messages.send(sender, "invalid-interval");
            return;
        }

        long intervalMs = TimeParser.parseDuration(intervalStr);
        long intervalTicks = intervalMs / 50; // 1 tick = 50ms

        if (intervalTicks <= 0) {
            messages.send(sender, "invalid-interval");
            return;
        }

        boolean updated = config.setEntitySpawnPointInterval(worldName, pointName, intervalTicks);
        if (!updated) {
            // na wypadek gdyby config się zmienił między checkiem a setem
            messages.send(sender, "entityspawnpoint-not-found", MessageManager.placeholders(
                    "name", pointName,
                    "world", worldName
            ));
            return;
        }

        // przestaw task dla tego konkretnego punktu
        plugin.getEntitySpawnPointManager().updateSpawnPointInterval(worldName, pointName, intervalTicks);

        messages.send(sender, "entityspawnpointtime-set", MessageManager.placeholders(
                "name", pointName,
                "world", worldName,
                "interval", TimeParser.formatDuration(intervalMs)
        ));
    }

    private void handleAddDisallowedEntity(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "disallowedEntity"));
            return;
        }

        String worldName = args[2];
        String entityName = args[3].toUpperCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!isValidEntity(entityName)) {
            messages.send(sender, "invalid-entity", MessageManager.placeholders("entity", entityName));
            return;
        }

        config.addDisallowedEntity(worldName, entityName);

        messages.send(sender, "disallowedentity-added", MessageManager.placeholders(
                "entity", entityName,
                "world", worldName
        ));
    }

    private void handleAddDisallowedBlock(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "disallowedBlock"));
            return;
        }

        String worldName = args[2];
        String blockName = args[3].toUpperCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!isValidBlock(blockName)) {
            messages.send(sender, "invalid-block", MessageManager.placeholders("block", blockName));
            return;
        }

        config.addDisallowedBlock(worldName, blockName);

        messages.send(sender, "disallowedblock-added", MessageManager.placeholders(
                "block", blockName,
                "world", worldName
        ));
    }

    // ===== REMOVE COMMANDS =====

    private void handleRemove(CommandSender sender, String[] args) {
        if (!PermissionUtils.hasRemovePermission(sender)) {
            messages.send(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "remove"));
            return;
        }

        String setting = args[1].toLowerCase();

        switch (setting) {
            case "entitylimit":
                handleRemoveEntityLimit(sender, args);
                break;
            case "entityspawntime":
                handleRemoveEntitySpawnTime(sender, args);
                break;
            case "entityspawnpoint":
                handleRemoveEntitySpawnPoint(sender, args);
                break;
            case "disallowedentity":
                handleRemoveDisallowedEntity(sender, args);
                break;
            case "disallowedblock":
                handleRemoveDisallowedBlock(sender, args);
                break;
            default:
                messages.send(sender, "unknown-command");
                break;
        }
    }

    private void handleRemoveEntityLimit(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entityLimit"));
            return;
        }

        String worldName = args[2];
        String entityName = args[3].toUpperCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        config.removeEntityLimit(worldName, entityName);

        messages.send(sender, "entitylimit-removed", MessageManager.placeholders(
                "entity", entityName,
                "world", worldName
        ));
    }

    private void handleRemoveEntitySpawnTime(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entitySpawnTime"));
            return;
        }

        String worldName = args[2];
        String entityName = args[3].toUpperCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        config.removeEntitySpawnTime(worldName, entityName);

        messages.send(sender, "entityspawntime-removed", MessageManager.placeholders(
                "entity", entityName,
                "world", worldName
        ));
    }

    private void handleRemoveEntitySpawnPoint(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entitySpawnPoint"));
            return;
        }

        String worldName = args[2];
        String pointName = args[3];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (config.getEntitySpawnPoint(worldName, pointName) == null) {
            messages.send(sender, "entityspawnpoint-not-found", MessageManager.placeholders(
                    "name", pointName,
                    "world", worldName
            ));
            return;
        }

        config.removeEntitySpawnPoint(worldName, pointName);

        // WAŻNE: natychmiast zatrzymaj task dla tego punktu
        plugin.getEntitySpawnPointManager().removeSpawnPoint(worldName, pointName);

        messages.send(sender, "entityspawnpoint-removed", MessageManager.placeholders(
                "name", pointName,
                "world", worldName
        ));
    }

    private void handleRemoveDisallowedEntity(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "disallowedEntity"));
            return;
        }

        String worldName = args[2];
        String entityName = args[3].toUpperCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!config.isEntityDisallowed(worldName, entityName)) {
            messages.send(sender, "disallowedentity-not-found", MessageManager.placeholders(
                    "entity", entityName,
                    "world", worldName
            ));
            return;
        }

        config.removeDisallowedEntity(worldName, entityName);

        messages.send(sender, "disallowedentity-removed", MessageManager.placeholders(
                "entity", entityName,
                "world", worldName
        ));
    }

    private void handleRemoveDisallowedBlock(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "disallowedBlock"));
            return;
        }

        String worldName = args[2];
        String blockName = args[3].toUpperCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        if (!config.isBlockDisallowed(worldName, blockName)) {
            messages.send(sender, "disallowedblock-not-found", MessageManager.placeholders(
                    "block", blockName,
                    "world", worldName
            ));
            return;
        }

        config.removeDisallowedBlock(worldName, blockName);

        messages.send(sender, "disallowedblock-removed", MessageManager.placeholders(
                "block", blockName,
                "world", worldName
        ));
    }

    // ===== INFO COMMANDS =====

    private void handleInfo(CommandSender sender, String[] args) {
        if (!PermissionUtils.hasInfoPermission(sender)) {
            messages.send(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "info"));
            return;
        }

        String setting = args[1].toLowerCase();

        switch (setting) {
            case "blockdespawntime":
            case "blockdespawn":
                handleInfoBlockDespawn(sender, args);
                break;
            case "waterflow":
                handleInfoWaterFlow(sender, args);
                break;
            case "lavaflow":
                handleInfoLavaFlow(sender, args);
                break;
            case "entitylimit":
                handleInfoEntityLimit(sender, args);
                break;
            case "entityspawntime":
                handleInfoEntitySpawnTime(sender, args);
                break;
            case "blockredstonemechanism":
                handleInfoRedstoneMechanism(sender, args);
                break;
            case "entityspawnpoint":
                handleInfoEntitySpawnPoint(sender, args);
                break;
            case "disallowedentity":
                handleInfoDisallowedEntity(sender, args);
                break;
            case "disallowedblock":
                handleInfoDisallowedBlock(sender, args);
                break;
            case "blockdestruction":
                handleInfoBlockDestruction(sender, args);
                break;
            default:
                messages.send(sender, "unknown-command");
                break;
        }
    }

    private void handleInfoBlockDespawn(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "blockDespawn"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        boolean enabled = config.isBlockDespawnEnabled(worldName);
        String time = config.getBlockDespawnTime(worldName);

        messages.send(sender, "blockdespawn-info", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(enabled),
                "time", time
        ));
    }

    private void handleInfoWaterFlow(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "waterFlow"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        boolean enabled = config.isWaterFlowEnabled(worldName);

        messages.send(sender, "waterflow-info", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(enabled)
        ));
    }

    private void handleInfoLavaFlow(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "lavaFlow"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        boolean enabled = config.isLavaFlowEnabled(worldName);

        messages.send(sender, "lavaflow-info", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(enabled)
        ));
    }

    private void handleInfoEntityLimit(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entityLimit"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        // Jeśli podano entity - pokaż limit dla konkretnego entity
        if (args.length >= 4) {
            String entityName = args[3].toUpperCase();

            int limit = config.getEntityLimit(worldName, entityName);

            if (limit == -1) {
                messages.send(sender, "entitylimit-not-set", MessageManager.placeholders(
                        "entity", entityName,
                        "world", worldName
                ));
            } else {
                messages.send(sender, "entitylimit-info", MessageManager.placeholders(
                        "entity", entityName,
                        "world", worldName,
                        "limit", String.valueOf(limit)
                ));
            }
        } else {
            // Pokaż wszystkie limity dla świata
            Map<String, Integer> limits = config.getEntityLimits(worldName);

            if (limits.isEmpty()) {
                messages.send(sender, "entitylimit-list-empty", MessageManager.placeholders("world", worldName));
                return;
            }

            messages.send(sender, "entitylimit-list-header", MessageManager.placeholders("world", worldName));

            for (Map.Entry<String, Integer> entry : limits.entrySet()) {
                messages.send(sender, "entitylimit-list-item", MessageManager.placeholders(
                        "entity", entry.getKey(),
                        "limit", String.valueOf(entry.getValue())
                ));
            }

            messages.send(sender, "entitylimit-list-footer", MessageManager.placeholders("world", worldName));
        }
    }

    private void handleInfoEntitySpawnTime(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "entitySpawnTime"));
            return;
        }

        String worldName = args[2];
        String entityName = args[3].toUpperCase();

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        String[] times = config.getEntitySpawnTime(worldName, entityName);

        if (times == null) {
            messages.send(sender, "entityspawntime-not-set", MessageManager.placeholders(
                    "entity", entityName,
                    "world", worldName
            ));
        } else {
            messages.send(sender, "entityspawntime-info", MessageManager.placeholders(
                    "entity", entityName,
                    "world", worldName,
                    "from", times[0],
                    "to", times[1]
            ));
        }
    }

    private void handleInfoRedstoneMechanism(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "blockRedstoneMechanism"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        boolean blocked = config.isRedstoneMechanismBlocked(worldName);

        messages.send(sender, "redstonemechanism-info", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(blocked)
        ));
    }

    private void handleInfoDisallowedEntity(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "disallowedEntity"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        List<String> entities = config.getDisallowedEntities(worldName);

        if (entities.isEmpty()) {
            messages.send(sender, "disallowedentity-list-empty", MessageManager.placeholders("world", worldName));
            return;
        }

        messages.send(sender, "disallowedentity-list-header", MessageManager.placeholders("world", worldName));

        for (String entity : entities) {
            messages.send(sender, "disallowedentity-list-item", MessageManager.placeholders("entity", entity));
        }

        messages.send(sender, "disallowedentity-list-footer", MessageManager.placeholders("world", worldName));
    }

    private void handleInfoDisallowedBlock(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "disallowedBlock"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        List<String> blocks = config.getDisallowedBlocks(worldName);

        if (blocks.isEmpty()) {
            messages.send(sender, "disallowedblock-list-empty", MessageManager.placeholders("world", worldName));
            return;
        }

        messages.send(sender, "disallowedblock-list-header", MessageManager.placeholders("world", worldName));

        for (String block : blocks) {
            messages.send(sender, "disallowedblock-list-item", MessageManager.placeholders("block", block));
        }

        messages.send(sender, "disallowedblock-list-footer", MessageManager.placeholders("world", worldName));
    }

    private void handleInfoBlockDestruction(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "invalid-arguments", MessageManager.placeholders("command", "blockDestruction"));
            return;
        }

        String worldName = args[2];

        if (!isValidWorld(worldName)) {
            messages.send(sender, "world-not-found", MessageManager.placeholders("world", worldName));
            return;
        }

        boolean allowed = config.isBlockDestructionAllowed(worldName);

        messages.send(sender, "blockdestruction-info", MessageManager.placeholders(
                "world", worldName,
                "status", messages.getBooleanDisplay(allowed)
        ));
    }

    // ===== OTHER COMMANDS =====

    private void handleReload(CommandSender sender) {
        if (!PermissionUtils.hasReloadPermission(sender)) {
            messages.send(sender, "no-permission");
            return;
        }

        plugin.reload();
        messages.send(sender, "reload-success");
    }

    private void handleHelp(CommandSender sender, String command) {
        if (!PermissionUtils.hasHelpPermission(sender)) {
            messages.send(sender, "no-permission");
            return;
        }

        if (command == null) {
            messages.send(sender, "help-header");
            messages.send(sender, "help-list");
            messages.send(sender, "help-footer");
            return;
        }

        String helpKey = "help-" + command.toLowerCase();
        String helpMessage = messages.getRaw(helpKey);

        if (helpMessage.startsWith("§cMissing message:") || helpMessage.contains("Missing message")) {
            messages.send(sender, "help-command-not-found", MessageManager.placeholders("command", command));
            return;
        }

        messages.send(sender, helpKey);
    }

    // ===== UTILITY METHODS =====

    private boolean isValidWorld(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }

    private boolean isValidEntity(String entityName) {
        try {
            EntityType.valueOf(entityName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isValidBlock(String blockName) {
        try {
            Material material = Material.valueOf(blockName.toUpperCase());
            return material.isBlock();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}