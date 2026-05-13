package com.quickmenus.command;
import com.quickmenus.QuickMenus;
import com.quickmenus.menu.MenuManager;
import com.quickmenus.model.MenuDef;
import com.quickmenus.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.List;
public final class QuickMenusCommand implements CommandExecutor, TabCompleter {
    private final QuickMenus plugin;
    private final MenuManager manager;
    public QuickMenusCommand(QuickMenus plugin, MenuManager manager) {
        this.plugin  = plugin;
        this.manager = manager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            showHelp(sender, label);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("quickmenus.admin")) {
                    Msg.send(sender, "no-permission"); return true;
                }
                plugin.reloadConfig();
                manager.closeAll();
                manager.loadMenus();
                plugin.getHotbarManager().load();
                Msg.send(sender, "reloaded", "{count}", String.valueOf(manager.menuCount()));
            }
            case "list" -> {
                if (!sender.hasPermission("quickmenus.admin")) {
                    Msg.send(sender, "no-permission"); return true;
                }
                Msg.raw(sender, "&5&lQuickMenus &7— &eLoaded Menus:");
                for (MenuDef def : manager.getMenus().values()) {
                    Msg.raw(sender, "  &7• &e" + def.id()
                            + " &8(&7" + def.slots() + " slots, " + def.pages() + " page(s)&8)");
                }
            }
            case "info" -> {
                if (!sender.hasPermission("quickmenus.admin")) {
                    Msg.send(sender, "no-permission"); return true;
                }
                if (args.length < 2) {
                    Msg.raw(sender, "&cUsage: /" + label + " info <id>"); return true;
                }
                manager.getMenu(args[1]).ifPresentOrElse(def -> {
                    Msg.raw(sender, "&5&lQuickMenus &7— &e" + def.id());
                    Msg.raw(sender, "  &7Title:   &f" + def.title());
                    Msg.raw(sender, "  &7Slots:   &f" + def.slots());
                    Msg.raw(sender, "  &7Pages:   &f" + def.pages());
                    Msg.raw(sender, "  &7Items:   &f" + def.items().size());
                    Msg.raw(sender, "  &7Refresh: &f" + (def.refreshEnabled() ? def.refreshRate() + "t" : "off"));
                    Msg.raw(sender, "  &7Perm:    &f" + (def.hasPermission() ? def.permission() : "none"));
                }, () -> Msg.send(sender, "menu-not-found", "{id}", args[1]));
            }
            default -> showHelp(sender, label);
        }
        return true;
    }
    private void showHelp(CommandSender sender, String label) {
        Msg.raw(sender, "&5&lQuickMenus &7v" + plugin.getDescription().getVersion() + " &8— &eCommands:");
        Msg.raw(sender, "  &e/" + label + " reload     &7— reload all menus");
        Msg.raw(sender, "  &e/" + label + " list       &7— list loaded menus");
        Msg.raw(sender, "  &e/" + label + " info <id>  &7— menu details");
        Msg.raw(sender, "  &e/menu <id> [player]       &7— open a menu");
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "list", "info", "help")
                    .stream().filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            return manager.getMenus().keySet().stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase())).toList();
        }
        return List.of();
    }
}