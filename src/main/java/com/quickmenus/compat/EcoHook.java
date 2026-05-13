package com.quickmenus.compat;
import com.quickmenus.QuickMenus;
import com.quickmenus.quicklink.QuickLink;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import java.lang.reflect.Method;
public final class EcoHook {
    private final QuickMenus plugin;
    private Object api;
    private Method getBalance;
    private Method format;
    private Method has;
    private Method withdrawPlayer;
    private Method depositPlayer;
    private boolean hooked;
    public EcoHook(QuickMenus plugin) {
        this.plugin = plugin;
    }
    public void hook() {
        try {
            Class<?> apiClass = Class.forName("com.quickeco.api.QuickEcoAPI");
            RegisteredServiceProvider<?> provider =
                    Bukkit.getServicesManager().getRegistration(apiClass);
            if (provider != null) {
                api = provider.getProvider();
                resolveMethods(apiClass);
                hooked = true;
                plugin.getLogger().info("Hooked into QuickEco economy via ServicesManager.");
                return;
            }
            if (QuickLink.isLinked("QuickEco")) {
                Plugin eco = Bukkit.getPluginManager().getPlugin("QuickEco");
                if (eco != null && apiClass.isInstance(eco)) {
                    api = eco;
                    resolveMethods(apiClass);
                    hooked = true;
                    plugin.getLogger().info("Hooked into QuickEco economy via QuickLink.");
                }
            }
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("QuickEco not found, economy features disabled.");
        }
    }
    private void resolveMethods(Class<?> apiClass) throws ClassNotFoundException {
        try {
            Class<?> ecoProvider = Class.forName("com.quickeco.api.EconomyProvider");
            Method getEconomyProvider = apiClass.getMethod("getEconomyProvider");
            Object provider = getEconomyProvider.invoke(api);
            getBalance = ecoProvider.getMethod("getBalance", OfflinePlayer.class);
            format = ecoProvider.getMethod("format", double.class);
            has = ecoProvider.getMethod("has", OfflinePlayer.class, double.class);
            withdrawPlayer = ecoProvider.getMethod("withdrawPlayer", OfflinePlayer.class, double.class);
            depositPlayer = ecoProvider.getMethod("depositPlayer", OfflinePlayer.class, double.class);
        } catch (ReflectiveOperationException e) {
            throw new ClassNotFoundException("Failed to resolve EconomyProvider methods", e);
        }
    }
    public boolean isHooked() { return hooked; }
    private Object getEconomyProvider() {
        if (!hooked) return null;
        try {
            Method getEconomyProvider = api.getClass().getMethod("getEconomyProvider");
            return getEconomyProvider.invoke(api);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
    private Object invoke(Method method, Object... args) {
        if (!hooked) return null;
        try {
            Object provider = getEconomyProvider();
            if (provider == null) return null;
            return method.invoke(provider, args);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
    public double getBalance(OfflinePlayer player) {
        Object result = invoke(getBalance, player);
        return result instanceof Number n ? n.doubleValue() : 0;
    }
    public String format(double amount) {
        Object result = invoke(format, amount);
        return result instanceof String s ? s : String.valueOf(amount);
    }
    public boolean has(OfflinePlayer player, double amount) {
        Object result = invoke(has, player, amount);
        return result instanceof Boolean b && b;
    }
    public boolean withdraw(OfflinePlayer player, double amount) {
        Object result = invoke(withdrawPlayer, player, amount);
        return result instanceof Boolean b && b;
    }
    public boolean deposit(OfflinePlayer player, double amount) {
        Object result = invoke(depositPlayer, player, amount);
        return result instanceof Boolean b && b;
    }
}