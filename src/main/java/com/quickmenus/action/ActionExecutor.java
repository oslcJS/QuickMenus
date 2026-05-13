package com.quickmenus.action;
import com.quickmenus.QuickMenus;
import com.quickmenus.compat.EcoHook;
import com.quickmenus.hotbar.HideManager;
import com.quickmenus.menu.OpenMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
public final class ActionExecutor {
    private final QuickMenus plugin;
    private final Map<UUID, Long> vanishCooldowns = new HashMap<>();
    public ActionExecutor(QuickMenus plugin) {
        this.plugin = plugin;
    }
    public void execute(Player player, List<MenuAction> actions, OpenMenu openMenu) {
        for (MenuAction action : actions) {
            if (!handleAction(player, action, openMenu)) break;
        }
    }
    private boolean handleAction(Player player, MenuAction action, OpenMenu openMenu) {
        switch (action.type()) {
            case CLOSE -> player.closeInventory();
            case MESSAGE -> player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', parsePlaceholders(player, action.argument()))
            );
            case PROXY -> connectToProxy(player, action.argument());
            case SOUND -> {
                try {
                    Sound sound = Sound.valueOf(action.argument().toUpperCase());
                    player.playSound(player.getLocation(), sound, 1f, 1f);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Unknown sound: " + action.argument());
                }
            }
            case COMMAND, PLAYER -> Bukkit.dispatchCommand(player,
                    parsePlaceholders(player, action.argument()));
            case CONSOLE -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    parsePlaceholders(player, action.argument()));
            case PAGE_NEXT -> {
                if (openMenu != null) openMenu.nextPage(player);
            }
            case PAGE_PREV -> {
                if (openMenu != null) openMenu.prevPage(player);
            }
            case PAGE_GO -> {
                if (openMenu != null) {
                    try {
                        int page = Integer.parseInt(action.argument().trim());
                        openMenu.goToPage(player, page);
                    } catch (NumberFormatException ignored) {}
                }
            }
            case PERMISSION_CHECK -> {
                if (!player.hasPermission(action.argument())) {
                    player.sendMessage(ChatColor.RED + "You don't have permission for that.");
                    return false;
                }
            }
            case TOGGLE_VANISH -> {
                if (plugin.getHotbarManager() == null) break;
                long now = System.currentTimeMillis();
                Long last = vanishCooldowns.get(player.getUniqueId());
                if (last != null && now - last < 1000) break;
                vanishCooldowns.put(player.getUniqueId(), now);
                HideManager hm = plugin.getHotbarManager().getHideManager();
                boolean nowHidden = hm.toggle(player);
                plugin.getHotbarManager().updateHideItem(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        nowHidden ? "&7All players &chidden&7." : "&7All players &avisible&7."));
                try {
                    player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1f, 1f);
                } catch (IllegalArgumentException ignored) {}
            }
            case ECO_WITHDRAW -> {
                EcoHook eco = plugin.getEcoHook();
                if (!eco.isHooked()) break;
                double amount;
                try {
                    amount = Double.parseDouble(action.argument().trim());
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid ECO_WITHDRAW amount: " + action.argument());
                    break;
                }
                if (eco.withdraw(player, amount)) {
                    player.sendMessage(ChatColor.GREEN + "Withdrew " + eco.format(amount) + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough money.");
                }
            }
            case ECO_DEPOSIT -> {
                EcoHook eco2 = plugin.getEcoHook();
                if (!eco2.isHooked()) break;
                double amount;
                try {
                    amount = Double.parseDouble(action.argument().trim());
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid ECO_DEPOSIT amount: " + action.argument());
                    break;
                }
                eco2.deposit(player, amount);
                player.sendMessage(ChatColor.GREEN + "Received " + eco2.format(amount) + ".");
            }
            case ECO_BALANCE_CHECK -> {
                EcoHook eco3 = plugin.getEcoHook();
                if (!eco3.isHooked()) break;
                double amount;
                try {
                    amount = Double.parseDouble(action.argument().trim());
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid ECO_BALANCE_CHECK amount: " + action.argument());
                    break;
                }
                if (!eco3.has(player, amount)) {
                    player.sendMessage(ChatColor.RED + "You need " + eco3.format(amount) + " for that.");
                    return false;
                }
            }
        }
        return true;
    }
    private void connectToProxy(Player player, String serverName) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bos);
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(plugin, "BungeeCord", bos.toByteArray());
            if (plugin.getConfig().getBoolean("settings.debug", false)) {
                plugin.getLogger().info("Sending " + player.getName() + " to proxy server: " + serverName);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send proxy connect for " + serverName + ": " + e.getMessage());
        }
    }
    private String parsePlaceholders(Player player, String text) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}