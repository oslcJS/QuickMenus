package com.quickmenus.compat;
import com.quickmenus.QuickMenus;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
public final class PapiHook extends PlaceholderExpansion {
    private final QuickMenus plugin;
    public PapiHook(QuickMenus plugin) {
        this.plugin = plugin;
    }
    @Override public @NotNull String getIdentifier() { return "quickmenus"; }
    @Override public @NotNull String getAuthor()     { return "QuickPlugins"; }
    @Override public @NotNull String getVersion()    { return plugin.getDescription().getVersion(); }
    @Override public boolean persist()               { return true; }
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        if (params.startsWith("has_menu_")) {
            String id = params.substring("has_menu_".length());
            return plugin.getMenuManager().getMenu(id).isPresent() ? "true" : "false";
        }
        if (params.equals("menu_count")) {
            return String.valueOf(plugin.getMenuManager().menuCount());
        }
        if (params.equals("balance")) {
            if (!plugin.getEcoHook().isHooked()) return "0";
            return String.valueOf(plugin.getEcoHook().getBalance(player));
        }
        if (params.equals("balance_formatted")) {
            if (!plugin.getEcoHook().isHooked()) return "0";
            return plugin.getEcoHook().format(plugin.getEcoHook().getBalance(player));
        }
        return null;
    }
}