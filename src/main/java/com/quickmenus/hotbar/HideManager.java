package com.quickmenus.hotbar;
import com.quickmenus.QuickMenus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
public final class HideManager {
    private final QuickMenus plugin;
    private final Set<UUID> hidden = new HashSet<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 1000;
    public HideManager(QuickMenus plugin) {
        this.plugin = plugin;
    }
    public boolean toggle(Player player) {
        long now = System.currentTimeMillis();
        Long last = cooldowns.get(player.getUniqueId());
        if (last != null && now - last < COOLDOWN_MS) {
            return false;
        }
        cooldowns.put(player.getUniqueId(), now);
        if (hidden.contains(player.getUniqueId())) {
            showAll(player);
            return false;
        } else {
            hideAll(player);
            return true;
        }
    }
    public boolean isHidden(Player player) {
        return hidden.contains(player.getUniqueId());
    }
    public Set<UUID> getHiddenPlayers() {
        return Collections.unmodifiableSet(hidden);
    }
    public void hideAll(Player player) {
        hidden.add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.equals(player)) {
                player.hidePlayer(plugin, p);
            }
        }
    }
    public void showAll(Player player) {
        hidden.remove(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.equals(player)) {
                player.showPlayer(plugin, p);
            }
        }
    }
}