package com.quickmenus.command;
import com.quickmenus.QuickMenus;
import com.quickmenus.menu.MenuManager;
import com.quickmenus.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
public final class MenuCommand implements CommandExecutor, TabCompleter {
    private final QuickMenus plugin;
    private final MenuManager manager;
    public MenuCommand(QuickMenus plugin, MenuManager manager) {
        this.plugin  = plugin;
        this.manager = manager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Msg.raw(sender, "&5&lQuickMenus &7— &e/menu <id> [player]");
            return true;
        }
        String id = args[0];
        Player target;
        if (args.length >= 2) {
            if (!sender.hasPermission("quickmenus.open.others")) {
                Msg.send(sender, "no-permission");
                return true;
            }
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                Msg.send(sender, "player-not-found");
                return true;
            }
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("Console must specify a target player: /menu <id> <player>");
                return true;
            }
            target = p;
        }
        if (!target.hasPermission("quickmenus.use")) {
            Msg.send(sender, "no-permission");
            return true;
        }
        boolean opened = manager.open(target, id);
        if (!opened) {
            if (manager.getMenu(id).isEmpty()) {
                Msg.send(sender, "menu-not-found", "{id}", id);
            } else {
                Msg.send(sender, "no-permission");
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> ids = new ArrayList<>(manager.getMenus().keySet());
            ids.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
            return ids;
        }
        if (args.length == 2 && sender.hasPermission("quickmenus.open.others")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return List.of();
    }
}