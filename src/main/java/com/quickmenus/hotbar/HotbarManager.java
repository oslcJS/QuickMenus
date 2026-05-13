package com.quickmenus.hotbar;
import com.quickmenus.QuickMenus;
import com.quickmenus.action.MenuAction;
import com.quickmenus.util.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
public final class HotbarManager {
    private final QuickMenus plugin;
    private final HideManager hideManager;
    private List<HotbarItemDef> items = List.of();
    private boolean enabled = false;
    private String sound = null;
    public HotbarManager(QuickMenus plugin) {
        this.plugin = plugin;
        this.hideManager = new HideManager(plugin);
    }
    public void load() {
        enabled = plugin.getConfig().getBoolean("hotbar-items.enabled", true);
        sound = plugin.getConfig().getString("hotbar-items.sound", "UI_BUTTON_CLICK");
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("hotbar-items.items");
        if (sec == null) {
            items = List.of();
            return;
        }
        List<HotbarItemDef> loaded = new ArrayList<>();
        for (String key : sec.getKeys(false)) {
            ConfigurationSection itemSec = sec.getConfigurationSection(key);
            if (itemSec == null) continue;
            String matName = itemSec.getString("material", "STONE").toUpperCase();
            Material mat;
            try {
                mat = Material.valueOf(matName);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown material '" + matName + "' in hotbar item '" + key + "'");
                continue;
            }
            int slot = itemSec.getInt("slot", -1);
            if (slot < 0 || slot > 8) {
                plugin.getLogger().warning("Hotbar item '" + key + "' has invalid slot " + slot + " (must be 0-8)");
                continue;
            }
            int amount = itemSec.getInt("amount", 1);
            boolean glow = itemSec.getBoolean("glow", false);
            String name = itemSec.getString("display_name", "");
            List<String> lore = itemSec.getStringList("lore");
            List<String> itemFlags = itemSec.getStringList("item_flags");
            List<MenuAction> actions = new ArrayList<>();
            for (String raw : itemSec.getStringList("actions")) {
                MenuAction action = MenuAction.parse(raw);
                if (action != null) actions.add(action);
            }
            loaded.add(new HotbarItemDef(key, mat, slot, amount, glow, name, lore, itemFlags, actions));
        }
        this.items = loaded;
    }
    public boolean isEnabled() { return enabled; }
    public String getSound() { return sound; }
    public HideManager getHideManager() { return hideManager; }
    public List<HotbarItemDef> getItems() { return items; }
    public void apply(Player player) {
        if (!enabled) return;
        for (HotbarItemDef def : items) {
            ItemStack item = Items.build(def.material(), def.amount(), def.displayName(), def.lore(), def.glow(), def.itemFlags());
            player.getInventory().setItem(def.slot(), item);
        }
    }
    public HotbarItemDef getItemAt(int slot) {
        if (slot < 0 || slot > 8) return null;
        for (HotbarItemDef def : items) {
            if (def.slot() == slot) return def;
        }
        return null;
    }
    public boolean isHotbarSlot(int slot) {
        return getItemAt(slot) != null;
    }
    public void updateHideItem(Player player) {
        HotbarItemDef hideDef = getHideDef();
        if (hideDef == null) return;
        boolean hidden = hideManager.isHidden(player);
        Material mat = hidden ? Material.RED_DYE : Material.LIME_DYE;
        String name = hidden ? "&c&lShow Players" : "&a&lHide Players";
        List<String> lore = List.of(
                hidden ? "&7Players are currently &chidden&7." : "&7Players are currently &avisible&7.",
                "&eClick to toggle"
        );
        ItemStack item = Items.build(mat, 1, name, lore, hideDef.glow(), hideDef.itemFlags());
        player.getInventory().setItem(hideDef.slot(), item);
    }
    private HotbarItemDef getHideDef() {
        for (HotbarItemDef def : items) {
            for (MenuAction action : def.actions()) {
                if (action.type() == MenuAction.Type.TOGGLE_VANISH) return def;
            }
        }
        return null;
    }
}