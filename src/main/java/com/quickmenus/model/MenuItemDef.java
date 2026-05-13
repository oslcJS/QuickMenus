package com.quickmenus.model;
import com.quickmenus.action.MenuAction;
import org.bukkit.Material;
import java.util.List;
public record MenuItemDef(
        String key,
        Material material,
        int slot,
        int page,
        int amount,
        boolean glow,
        boolean pageNav,
        String displayName,
        List<String> lore,
        List<String> itemFlags,
        List<MenuAction> actions
) {}