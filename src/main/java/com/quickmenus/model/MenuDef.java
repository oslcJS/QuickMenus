package com.quickmenus.model;
import java.util.List;
public record MenuDef(
        String id,
        String title,
        int slots,
        int pages,
        boolean refreshEnabled,
        int refreshRate,
        String permission,
        List<MenuItemDef> items
) {
    public boolean hasPermission() { return permission != null && !permission.isBlank(); }
}