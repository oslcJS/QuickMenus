package com.quickmenus.hotbar;
import com.quickmenus.QuickMenus;
import com.quickmenus.action.MenuAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
public final class HotbarListener implements Listener {
    private final QuickMenus plugin;
    private final HotbarManager hotbarManager;
    public HotbarListener(QuickMenus plugin, HotbarManager hotbarManager) {
        this.plugin = plugin;
        this.hotbarManager = hotbarManager;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        hotbarManager.apply(player);
        hotbarManager.updateHideItem(player);
        HideManager hm = hotbarManager.getHideManager();
        if (hm.isHidden(player)) {
            hm.hideAll(player);
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player)) continue;
            if (hm.isHidden(online)) {
                online.hidePlayer(plugin, player);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        hotbarManager.apply(player);
        hotbarManager.updateHideItem(player);
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!hotbarManager.isEnabled()) return;
        if (event.getClickedInventory() != null
                && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            int slot = event.getSlot();
            if (slot >= 0 && slot <= 8 && hotbarManager.isHotbarSlot(slot)) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.isShiftClick()) {
            if (event.getSlot() >= 0 && event.getSlot() <= 8 && hotbarManager.isHotbarSlot(event.getSlot())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarSlot = event.getHotbarButton();
            if (hotbarSlot >= 0 && hotbarSlot <= 8 && hotbarManager.isHotbarSlot(hotbarSlot)) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            if (event.getSlot() >= 0 && event.getSlot() <= 8 && hotbarManager.isHotbarSlot(event.getSlot())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getClickedInventory() != null
                && event.getClickedInventory().getType() != InventoryType.PLAYER
                && event.getClick().isShiftClick()) {
            int raw = event.getRawSlot();
            int topSize = event.getView().getTopInventory().getSize();
            if (raw >= topSize) {
                int bottomSlot = raw - topSize;
                if (bottomSlot >= 0 && bottomSlot <= 8 && hotbarManager.isHotbarSlot(bottomSlot)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!hotbarManager.isEnabled()) return;
        for (int slot : event.getNewItems().keySet()) {
            if (slot >= 0 && slot <= 8 && hotbarManager.isHotbarSlot(slot)) {
                event.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        if (!hotbarManager.isEnabled()) return;
        Player player = event.getPlayer();
        int heldSlot = player.getInventory().getHeldItemSlot();
        if (hotbarManager.isHotbarSlot(heldSlot)
                && event.getItemDrop().getItemStack().getType() != Material.AIR) {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!hotbarManager.isEnabled()) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;
        int heldSlot = player.getInventory().getHeldItemSlot();
        HotbarItemDef def = hotbarManager.getItemAt(heldSlot);
        if (def == null) return;
        if (item.getType() != def.material()) {
            boolean isToggleVanish = def.actions().stream()
                    .anyMatch(a -> a.type() == MenuAction.Type.TOGGLE_VANISH);
            if (!isToggleVanish
                    || (item.getType() != Material.LIME_DYE && item.getType() != Material.RED_DYE)) {
                return;
            }
        }
        event.setCancelled(true);
        String soundName = hotbarManager.getSound();
        if (soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, 1f, 1f);
            } catch (IllegalArgumentException ignored) {}
        }
        if (!def.actions().isEmpty()) {
            plugin.getMenuManager().getExecutor().execute(player, def.actions(), null);
        }
    }
}