package com.quickmenus.menu;
import com.quickmenus.QuickMenus;
import com.quickmenus.action.ActionExecutor;
import com.quickmenus.model.MenuDef;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;
public final class MenuManager {
    private final QuickMenus plugin;
    private final MenuLoader loader;
    private final ActionExecutor executor;
    private final Map<String, MenuDef> menus = new LinkedHashMap<>();
    private final Map<UUID, OpenMenu> sessions = new HashMap<>();
    private final Map<UUID, BukkitTask> refreshTasks = new HashMap<>();
    public MenuManager(QuickMenus plugin) {
        this.plugin   = plugin;
        this.loader   = new MenuLoader(plugin);
        this.executor = new ActionExecutor(plugin);
    }
    public void loadMenus() {
        menus.clear();
        menus.putAll(loader.loadAll());
    }
    public int menuCount() { return menus.size(); }
    public boolean open(Player player, String id) {
        MenuDef def = menus.get(id);
        if (def == null) return false;
        if (def.hasPermission() && !player.hasPermission(def.permission())) {
            return false;
        }
        OpenMenu session = new OpenMenu(def, 1);
        sessions.put(player.getUniqueId(), session);
        session.open(player);
        if (session.needsRefresh()) {
            startRefresh(player, session);
        }
        return true;
    }
    private void startRefresh(Player player, OpenMenu session) {
        BukkitTask old = refreshTasks.remove(player.getUniqueId());
        if (old != null) old.cancel();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || !sessions.containsKey(player.getUniqueId())) {
                stopRefresh(player.getUniqueId());
                return;
            }
            session.render(player);
        }, session.refreshRate(), session.refreshRate());
        refreshTasks.put(player.getUniqueId(), task);
    }
    public void close(Player player) {
        sessions.remove(player.getUniqueId());
        stopRefresh(player.getUniqueId());
    }
    private void stopRefresh(UUID uuid) {
        BukkitTask task = refreshTasks.remove(uuid);
        if (task != null) task.cancel();
    }
    public void closeAll() {
        new ArrayList<>(sessions.keySet()).forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.closeInventory();
            stopRefresh(uuid);
        });
        sessions.clear();
    }
    public OpenMenu getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }
    public boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }
    public ActionExecutor getExecutor() { return executor; }
    public Map<String, MenuDef> getMenus() { return Collections.unmodifiableMap(menus); }
    public Optional<MenuDef> getMenu(String id) { return Optional.ofNullable(menus.get(id)); }
}