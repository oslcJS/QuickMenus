package com.quickmenus.command;

import com.quickmenus.QuickMenus;
import com.quickmenus.demo.DemoMenus;
import com.quickmenus.menu.MenuManager;
import com.quickmenus.model.MenuDef;
import com.quickmenus.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class QuickMenusCommand implements CommandExecutor, TabCompleter {

    private final QuickMenus plugin;
    private final MenuManager manager;

    public QuickMenusCommand(QuickMenus plugin, MenuManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            help(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reload(sender);
            case "list" -> list(sender);
            case "info" -> info(sender, args, label);
            case "preview" -> preview(sender, args);
            case "settings" -> settings(sender, args);
            case "demo" -> demo(sender, args);
            default -> help(sender, label);
        }
        return true;
    }

    private void help(CommandSender s, String label) {
        Msg.raw(s, "&5&lQuickMenus &7v" + plugin.getDescription().getVersion() + " &8- &eCommands:");
        Msg.raw(s, "  &e/" + label + " reload &7- reload all menus");
        Msg.raw(s, "  &e/" + label + " list &7- list loaded menus");
        Msg.raw(s, "  &e/" + label + " info <id> &7- menu details");
        Msg.raw(s, "  &e/" + label + " preview <id> &7- open menu as admin (bypass perms)");
        Msg.raw(s, "  &e/" + label + " settings [key] [value]");
        Msg.raw(s, "  &e/" + label + " demo list &7- list demo menus");
        Msg.raw(s, "  &e/" + label + " demo install <id|all> &7- install demo menu(s)");
        Msg.raw(s, "  &e/menu <id> [player] &7- open a menu");
    }

    private void reload(CommandSender s) {
        if (!hasAny(s, "quickmenus.admin", "qm.reload")) { Msg.send(s, "no-permission"); return; }
        plugin.reloadConfig();
        manager.closeAll();
        manager.loadMenus();
        plugin.getHotbarManager().load();
        Msg.send(s, "reloaded", "{count}", String.valueOf(manager.menuCount()));
    }

    private void list(CommandSender s) {
        if (!hasAny(s, "quickmenus.admin")) { Msg.send(s, "no-permission"); return; }
        Msg.raw(s, "&5&lQuickMenus &7- &eLoaded Menus:");
        for (MenuDef def : manager.getMenus().values()) {
            Msg.raw(s, "  &7- &e" + def.id() + " &8(&7" + def.slots() + " slots, "
                    + def.pages() + " page(s)&8)");
        }
    }

    private void info(CommandSender s, String[] args, String label) {
        if (!hasAny(s, "quickmenus.admin")) { Msg.send(s, "no-permission"); return; }
        if (args.length < 2) { Msg.raw(s, "&cUsage: /" + label + " info <id>"); return; }
        manager.getMenu(args[1]).ifPresentOrElse(def -> {
            Msg.raw(s, "&5&lQuickMenus &7- &e" + def.id());
            Msg.raw(s, "  &7Title:   &f" + def.title());
            Msg.raw(s, "  &7Slots:   &f" + def.slots());
            Msg.raw(s, "  &7Pages:   &f" + def.pages());
            Msg.raw(s, "  &7Items:   &f" + def.items().size());
            Msg.raw(s, "  &7Refresh: &f" + (def.refreshEnabled() ? def.refreshRate() + "t" : "off"));
            Msg.raw(s, "  &7Perm:    &f" + (def.hasPermission() ? def.permission() : "none"));
        }, () -> Msg.send(s, "menu-not-found", "{id}", args[1]));
    }

    private void preview(CommandSender s, String[] args) {
        if (!(s instanceof Player p)) { Msg.raw(s, "&cPlayer only."); return; }
        if (!hasAny(p, "quickmenus.admin")) { Msg.send(p, "no-permission"); return; }
        if (args.length < 2) { Msg.raw(p, "&cUsage: /qm preview <id>"); return; }
        if (manager.getMenu(args[1]).isEmpty()) {
            Msg.send(p, "menu-not-found", "{id}", args[1]);
            return;
        }
        if (!manager.open(p, args[1])) {
            MenuDef def = manager.getMenu(args[1]).orElse(null);
            if (def != null && def.hasPermission()) {
                p.addAttachment(plugin, def.permission(), true);
                manager.open(p, args[1]);
            }
        }
    }

    private void settings(CommandSender s, String[] args) {
        if (!hasAny(s, "quickmenus.admin", "qm.settings")) { Msg.send(s, "no-permission"); return; }
        if (args.length < 2) {
            Msg.raw(s, "&5&lQuickMenus &8- &eSettings");
            Msg.raw(s, "  &7debug: &e" + plugin.getConfig().getBoolean("settings.debug", false));
            Msg.raw(s, "  &7hotbar-items.enabled: &e"
                    + plugin.getConfig().getBoolean("hotbar-items.enabled", true));
            Msg.raw(s, "  &7open-sound: &e"
                    + plugin.getConfig().getString("settings.open-sound", "BLOCK_CHEST_OPEN"));
            Msg.raw(s, "  &7close-sound: &e"
                    + plugin.getConfig().getString("settings.close-sound", "UI_BUTTON_CLICK"));
            return;
        }
        if (args.length < 3) { Msg.raw(s, "&cUsage: /qm settings <key> <value>"); return; }
        String key = args[1].toLowerCase();
        String val = args[2];
        switch (key) {
            case "debug" -> setBool(s, "settings.debug", val);
            case "hotbar-items.enabled", "hotbar" -> setBool(s, "hotbar-items.enabled", val);
            case "open-sound" -> setString(s, "settings.open-sound", val);
            case "close-sound" -> setString(s, "settings.close-sound", val);
            default -> Msg.send(s, "settings-unknown", "{key}", key);
        }
    }

    private void demo(CommandSender s, String[] args) {
        if (!hasAny(s, "quickmenus.admin", "qm.demo")) { Msg.send(s, "no-permission"); return; }
        if (args.length < 2) { Msg.raw(s, "&cUsage: /qm demo <list|install> [id|all]"); return; }
        switch (args[1].toLowerCase()) {
            case "list" -> demoList(s);
            case "install" -> demoInstall(s, args);
            default -> Msg.raw(s, "&cUsage: /qm demo <list|install> [id|all]");
        }
    }

    private void demoList(CommandSender s) {
        Msg.raw(s, "&5&lQuickMenus &8- &eDemo Menus");
        for (DemoMenus.Demo d : DemoMenus.all()) {
            Msg.raw(s, "  &7- &e" + d.id() + " &8: &7" + d.description());
        }
        Msg.raw(s, "&7Install with &e/qm demo install <id> &7or &e/qm demo install all");
    }

    private void demoInstall(CommandSender s, String[] args) {
        if (args.length < 3) { Msg.raw(s, "&cUsage: /qm demo install <id|all>"); return; }
        String id = args[2].toLowerCase();
        if (id.equals("all")) {
            int n = DemoMenus.installAll(plugin);
            manager.closeAll();
            manager.loadMenus();
            Msg.send(s, "demo-installed-all", "{count}", String.valueOf(n));
            return;
        }
        if (!DemoMenus.exists(id)) {
            Msg.send(s, "demo-unknown", "{id}", id);
            return;
        }
        if (DemoMenus.install(plugin, id)) {
            manager.closeAll();
            manager.loadMenus();
            Msg.send(s, "demo-installed", "{id}", id);
        } else {
            Msg.send(s, "demo-install-failed", "{id}", id);
        }
    }

    private void setBool(CommandSender s, String path, String raw) {
        boolean b = raw.equalsIgnoreCase("true") || raw.equals("1")
                || raw.equalsIgnoreCase("on") || raw.equalsIgnoreCase("yes");
        plugin.getConfig().set(path, b);
        plugin.saveConfig();
        Msg.send(s, "settings-changed",
                "{key}", path.substring(path.lastIndexOf('.') + 1),
                "{value}", String.valueOf(b));
    }

    private void setString(CommandSender s, String path, String raw) {
        plugin.getConfig().set(path, raw);
        plugin.saveConfig();
        Msg.send(s, "settings-changed",
                "{key}", path.substring(path.lastIndexOf('.') + 1),
                "{value}", raw);
    }

    private boolean hasAny(CommandSender s, String... nodes) {
        for (String n : nodes) if (s.hasPermission(n)) return true;
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("reload", "list", "info", "preview", "settings", "demo", "help"), args[0]);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("preview")) {
                return filter(new ArrayList<>(manager.getMenus().keySet()), args[1]);
            }
            if (args[0].equalsIgnoreCase("settings")) {
                return filter(List.of("debug", "hotbar-items.enabled", "open-sound", "close-sound"), args[1]);
            }
            if (args[0].equalsIgnoreCase("demo")) {
                return filter(List.of("list", "install"), args[1]);
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("settings")) {
                if (args[1].equalsIgnoreCase("debug") || args[1].equalsIgnoreCase("hotbar-items.enabled")) {
                    return filter(List.of("true", "false"), args[2]);
                }
            }
            if (args[0].equalsIgnoreCase("demo") && args[1].equalsIgnoreCase("install")) {
                List<String> ids = new ArrayList<>();
                ids.add("all");
                for (DemoMenus.Demo d : DemoMenus.all()) ids.add(d.id());
                return filter(ids, args[2]);
            }
        }
        return List.of();
    }

    private List<String> filter(List<String> options, String prefix) {
        String p = prefix.toLowerCase();
        return options.stream().filter(s -> s.toLowerCase().startsWith(p)).collect(Collectors.toList());
    }
}
