package dev.nxms.guardcore.utils;

import org.bukkit.command.CommandSender;

/**
 * Narzędzie do sprawdzania uprawnień.
 */
public class PermissionUtils {

    /**
     * Sprawdza czy gracz ma uprawnienie do komendy.
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission("guardcore.admin") || sender.hasPermission(permission);
    }

    /**
     * Sprawdza czy gracz ma dostęp do komend set.
     */
    public static boolean hasSetPermission(CommandSender sender) {
        return hasPermission(sender, "guardcore.set");
    }

    /**
     * Sprawdza czy gracz ma dostęp do komend add.
     */
    public static boolean hasAddPermission(CommandSender sender) {
        return hasPermission(sender, "guardcore.add");
    }

    /**
     * Sprawdza czy gracz ma dostęp do komend remove.
     */
    public static boolean hasRemovePermission(CommandSender sender) {
        return hasPermission(sender, "guardcore.remove");
    }

    /**
     * Sprawdza czy gracz ma dostęp do komend info.
     */
    public static boolean hasInfoPermission(CommandSender sender) {
        return hasPermission(sender, "guardcore.info");
    }

    /**
     * Sprawdza czy gracz ma dostęp do komendy help.
     */
    public static boolean hasHelpPermission(CommandSender sender) {
        return hasPermission(sender, "guardcore.help");
    }

    /**
     * Sprawdza czy gracz ma dostęp do komendy reload.
     */
    public static boolean hasReloadPermission(CommandSender sender) {
        return hasPermission(sender, "guardcore.reload");
    }

    /**
     * Sprawdza czy gracz ma dostęp do komendy bypass.
     */
    public static boolean hasBypassPermission(CommandSender sender) {
        return hasPermission(sender, "guardcore.bypass");
    }

    /**
     * Sprawdza czy gracz ma podstawowy dostęp do komend guardcore.
     */
    public static boolean hasCommandAccess(CommandSender sender) {
        return hasPermission(sender, "guardcore.command");
    }
}