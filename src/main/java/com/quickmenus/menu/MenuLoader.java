package com.quickmenus.menu;
import com.quickmenus.QuickMenus;
import com.quickmenus.action.MenuAction;
import com.quickmenus.model.MenuDef;
import com.quickmenus.model.MenuItemDef;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.*;
public final class MenuLoader {
    private final QuickMenus plugin;
    public MenuLoader(QuickMenus plugin) {
        this.plugin = plugin;
    }
    public Map<String, MenuDef> loadAll() {
        File menusDir = new File(plugin.getDataFolder(), "menus");
        if (!menusDir.exists()) {
            menusDir.mkdirs();
        }
        saveDefaultMenu("menus/serverselector.yml");
        saveDefaultMenu("menus/example_paged.yml");
        saveDefaultMenu("menus/main_menu.yml");
        Map<String, MenuDef> loaded = new LinkedHashMap<>();
        File[] files = menusDir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return loaded;
        for (File file : files) {
            try {
                MenuDef def = loadFile(file);
                if (def != null) {
                    loaded.put(def.id(), def);
                    plugin.getLogger().info("Loaded menu: " + def.id() + " (" + def.pages() + " page(s))");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load menu file " + file.getName() + ": " + e.getMessage());
            }
        }
        return loaded;
    }
    private void saveDefaultMenu(String resourcePath) {
        File target = new File(plugin.getDataFolder(), resourcePath);
        if (!target.exists()) {
            plugin.saveResource(resourcePath, false);
        }
    }
    private MenuDef loadFile(File file) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String id      = cfg.getString("id", file.getName().replace(".yml", ""));
        String title   = cfg.getString("title", "&8Menu");
        int slots      = cfg.getInt("slots", 54);
        int pages      = Math.max(1, cfg.getInt("pages", 1));
        String perm    = cfg.getString("permission", null);
        boolean refreshEnabled = cfg.getBoolean("refresh.enabled", false);
        int refreshRate        = cfg.getInt("refresh.rate", 40);
        ConfigurationSection itemsSec = cfg.getConfigurationSection("items");
        List<MenuItemDef> items = new ArrayList<>();
        if (itemsSec != null) {
            for (String key : itemsSec.getKeys(false)) {
                ConfigurationSection sec = itemsSec.getConfigurationSection(key);
                if (sec == null) continue;
                MenuItemDef def = parseItem(key, sec);
                if (def != null) items.add(def);
            }
        }
        return new MenuDef(id, title, slots, pages, refreshEnabled, refreshRate, perm, items);
    }
    private MenuItemDef parseItem(String key, ConfigurationSection sec) {
        String matName = sec.getString("material", "STONE").toUpperCase();
        Material mat;
        try {
            mat = Material.valueOf(matName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Unknown material '" + matName + "' in item '" + key + "' - defaulting to STONE.");
            mat = Material.STONE;
        }
        int slot       = sec.getInt("slot", -1);
        int page       = sec.getInt("page", 0);
        int amount     = sec.getInt("amount", 1);
        boolean glow   = sec.getBoolean("glow", false);
        boolean pageNav= sec.getBoolean("page_nav", false);
        String name    = sec.getString("display_name", "");
        List<String> lore      = sec.getStringList("lore");
        List<String> itemFlags = sec.getStringList("item_flags");
        List<MenuAction> actions = new ArrayList<>();
        for (String raw : sec.getStringList("actions")) {
            MenuAction action = MenuAction.parse(raw);
            if (action != null) actions.add(action);
        }
        return new MenuItemDef(key, mat, slot, page, amount, glow, pageNav, name, lore, itemFlags, actions);
    }
}