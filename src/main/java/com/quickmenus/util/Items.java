package com.quickmenus.util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
public final class Items {
    private Items() {}
    public static ItemStack build(Material material, int amount, String displayName, List<String> lore,
                                   boolean glow, List<String> itemFlags) {
        ItemStack item = new ItemStack(material, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (displayName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
        if (lore != null && !lore.isEmpty()) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
        }
        if (glow) {
            Enchantment glowEnchant = Enchantment.getByKey(NamespacedKey.minecraft("lure"));
            if (glowEnchant == null) glowEnchant = Enchantment.getByKey(NamespacedKey.minecraft("luck_of_the_sea"));
            if (glowEnchant != null) meta.addEnchant(glowEnchant, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (itemFlags != null) {
            for (String flagName : itemFlags) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
                    meta.addItemFlags(flag);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack placeholder() {
        return build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", null, false, null);
    }
}