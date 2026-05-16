package com.quickmenus.util;

import com.quickmenus.QuickMenus;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Msg {
    private static QuickMenus plugin;
    private static String prefix = "&5[QuickMenus] &r";

    private Msg() {}

    public static void init(QuickMenus p) {
        plugin = p;
        prefix = p.getConfig().getString("messages.prefix", "&5[QuickMenus] &r");
    }

    public static String color(String s) {
        return s == null ? "" : ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String prefix() {
        return color(prefix);
    }

    public static void send(CommandSender sender, String key) {
        String raw = plugin.getConfig().getString("messages." + key, key);
        sender.sendMessage(color(prefix + raw));
    }

    public static void send(CommandSender sender, String key, String... replacements) {
        String raw = plugin.getConfig().getString("messages." + key, key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            raw = raw.replace(replacements[i], replacements[i + 1]);
        }
        sender.sendMessage(color(prefix + raw));
    }

    public static void raw(CommandSender sender, String text) {
        sender.sendMessage(color(text));
    }

    public static boolean debugEnabled() {
        return plugin != null && plugin.getConfig().getBoolean("settings.debug", false);
    }

    public static void debug(CommandSender sender, String key, String... replacements) {
        if (!debugEnabled()) return;
        send(sender, key, replacements);
    }
}
