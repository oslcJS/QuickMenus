package com.quickmenus.listener;
import com.quickmenus.QuickMenus;
import com.quickmenus.menu.MenuManager;
import com.quickmenus.menu.OpenMenu;
import com.quickmenus.model.MenuItemDef;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
public final class MenuListener implements Listener {
    private final QuickMenus plugin;
    private final MenuManager manager;
    public MenuListener(QuickMenus plugin, MenuManager manager) {
        this.plugin  = plugin;
        this.manager = manager;
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        OpenMenu session = manager.getSession(player);
        if (session == null) return;
        if (!event.getInventory().equals(session.getInventory())) return;
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= session.getDef().slots()) return;
        MenuItemDef item = session.getItemAt(slot);
        if (item == null || item.actions().isEmpty()) return;
        manager.getExecutor().execute(player, item.actions(), session);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        OpenMenu session = manager.getSession(player);
        if (session == null) return;
        if (event.getInventory().equals(session.getInventory())) {
            manager.close(player);
        }
    }
}