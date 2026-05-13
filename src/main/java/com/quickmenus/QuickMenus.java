package com.quickmenus;
import com.quickmenus.command.MenuCommand;
import com.quickmenus.command.QuickMenusCommand;
import com.quickmenus.compat.EcoHook;
import com.quickmenus.compat.PapiHook;
import com.quickmenus.hotbar.HotbarListener;
import com.quickmenus.hotbar.HotbarManager;
import com.quickmenus.listener.MenuListener;
import com.quickmenus.menu.MenuManager;
import com.quickmenus.quicklink.QuickLink;
import com.quickmenus.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
public final class QuickMenus extends JavaPlugin {
    private static QuickMenus instance;
    private MenuManager menuManager;
    private HotbarManager hotbarManager;
    private EcoHook ecoHook;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Msg.init(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.menuManager = new MenuManager(this);
        menuManager.loadMenus();
        this.hotbarManager = new HotbarManager(this);
        hotbarManager.load();
        Bukkit.getPluginManager().registerEvents(new MenuListener(this, menuManager), this);
        Bukkit.getPluginManager().registerEvents(new HotbarListener(this, hotbarManager), this);
        registerCommand("quickmenus", new QuickMenusCommand(this, menuManager));
        registerCommand("menu",       new MenuCommand(this, menuManager));
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiHook(this).register();
            getLogger().info("Hooked into PlaceholderAPI.");
        }
        this.ecoHook = new EcoHook(this);
        ecoHook.hook();
        QuickLink.register(this);
        getLogger().info("QuickMenus enabled. Loaded " + menuManager.menuCount() + " menu(s).");
    }
    @Override
    public void onDisable() {
        menuManager.closeAll();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        QuickLink.unregister();
        getLogger().info("QuickMenus disabled.");
    }
    private void registerCommand(String name, Object executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) return;
        if (executor instanceof org.bukkit.command.CommandExecutor ce) cmd.setExecutor(ce);
        if (executor instanceof org.bukkit.command.TabCompleter tc) cmd.setTabCompleter(tc);
    }
    public static QuickMenus get() { return instance; }
    public MenuManager getMenuManager() { return menuManager; }
    public HotbarManager getHotbarManager() { return hotbarManager; }
    public EcoHook getEcoHook() { return ecoHook; }
}