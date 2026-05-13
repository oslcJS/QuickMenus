package com.quickmenus.hotbar;
import com.quickmenus.action.MenuAction;
import org.bukkit.Material;
import java.util.List;
public record HotbarItemDef(
        String key,
        Material material,
        int slot,
        int amount,
        boolean glow,
        String displayName,
        List<String> lore,
        List<String> itemFlags,
        List<MenuAction> actions
) {}