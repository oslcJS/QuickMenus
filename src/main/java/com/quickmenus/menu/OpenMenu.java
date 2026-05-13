package com.quickmenus.menu;
import com.quickmenus.model.MenuDef;
import com.quickmenus.model.MenuItemDef;
import com.quickmenus.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;
import java.util.List;
public final class OpenMenu {
    private final MenuDef def;
    private int currentPage;
    private Inventory inventory;
    public OpenMenu(MenuDef def, int startPage) {
        this.def = def;
        this.currentPage = Math.max(1, Math.min(startPage, def.pages()));
    }
    public MenuDef getDef() { return def; }
    public int getCurrentPage() { return currentPage; }
    public void nextPage(Player player) {
        if (currentPage < def.pages()) {
            currentPage++;
            open(player);
        }
    }
    public void prevPage(Player player) {
        if (currentPage > 1) {
            currentPage--;
            open(player);
        }
    }
    public void goToPage(Player player, int page) {
        if (page >= 1 && page <= def.pages()) {
            currentPage = page;
            open(player);
        }
    }
    public void open(Player player) {
        inventory = Bukkit.createInventory(null, def.slots(), colorTitle());
        populate();
        player.openInventory(inventory);
    }
    public void render(Player player) {
        if (inventory == null) { open(player); return; }
        inventory.clear();
        populate();
    }
    private String colorTitle() {
        String t = def.title()
                .replace("{page}", String.valueOf(currentPage))
                .replace("{max_page}", String.valueOf(def.pages()));
        return ChatColor.translateAlternateColorCodes('&', t);
    }
    private void populate() {
        MenuItemDef filler = findFiller();
        if (filler != null) {
            for (int i = 0; i < def.slots(); i++) {
                inventory.setItem(i, Items.build(filler.material(), 1, filler.displayName(),
                        filler.lore(), filler.glow(), filler.itemFlags()));
            }
        }
        List<MenuItemDef> visible = visibleItems();
        for (MenuItemDef item : visible) {
            if (item.slot() < 0 || item.slot() >= def.slots()) continue;
            inventory.setItem(item.slot(), Items.build(item.material(), item.amount(),
                    applyPagePlaceholders(item.displayName()),
                    applyPagePlaceholders(item.lore()),
                    item.glow(), item.itemFlags()));
        }
    }
    private MenuItemDef findFiller() {
        return def.items().stream()
                .filter(i -> i.slot() == -1)
                .findFirst().orElse(null);
    }
    private List<MenuItemDef> visibleItems() {
        List<MenuItemDef> result = new ArrayList<>();
        for (MenuItemDef item : def.items()) {
            if (item.slot() == -1) continue;
            boolean onThisPage = item.page() == 0
                    || item.page() == currentPage
                    || item.pageNav();
            if (onThisPage) result.add(item);
        }
        return result;
    }
    public MenuItemDef getItemAt(int slot) {
        MenuItemDef found = null;
        for (MenuItemDef item : def.items()) {
            if (item.slot() != slot) continue;
            if (item.page() == currentPage) return item;
            if (item.page() == 0 || item.pageNav()) found = item;
        }
        return found;
    }
    private String applyPagePlaceholders(String s) {
        if (s == null) return "";
        return s.replace("{page}", String.valueOf(currentPage))
                .replace("{max_page}", String.valueOf(def.pages()));
    }
    private List<String> applyPagePlaceholders(List<String> lore) {
        if (lore == null) return List.of();
        List<String> result = new ArrayList<>();
        for (String line : lore) result.add(applyPagePlaceholders(line));
        return result;
    }
    public boolean needsRefresh() { return def.refreshEnabled(); }
    public int refreshRate()      { return def.refreshRate(); }
    public Inventory getInventory() { return inventory; }
}