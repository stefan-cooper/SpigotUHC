package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.commands.UHCCommand;
import com.stefancooper.SpigotUHC.events.BaseEvents;
import com.stefancooper.SpigotUHC.events.EnchantmentEvents;
import com.stefancooper.SpigotUHC.events.ReviveEvents;
import com.stefancooper.SpigotUHC.events.TimestampEvents;
import com.stefancooper.SpigotUHC.events.UHCLootEvents;
import com.stefancooper.SpigotUHC.utils.Constants;
import com.stefancooper.SpigotUHC.events.WinEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Plugin extends JavaPlugin implements Listener {

    private Config config;
    private boolean started;
    private boolean countingDown;

    @Override
    // This is called when the plugin is loaded into the server.
    public void onEnable() {
        config = new Config(this);
        Defaults.setDefaultGameRules(this.config);
        Bukkit.getPluginManager().registerEvents(new BaseEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new ReviveEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new TimestampEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new WinEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new UHCLootEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new EnchantmentEvents(config), this);
        started = false;

        this.getLogger().log(Level.INFO, "UHC Plugin enabled");
        getServer().getCommandMap().register("uhc", new UHCCommand(config));

        assertCustomEnchantmentsExist();
    }

    private void assertCustomEnchantmentsExist() {
        final Registry<Enchantment> enchantRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        final Enchantment quickBoom = enchantRegistry.get(EnchantmentKeys.create(Key.key(String.format("%s:%s", Constants.SPIGOT_NAMESPACE, Constants.QUICKBOOM_ENCHANTMENT))));

        if (quickBoom == null) getLogger().warning("QuickBoom enchantment not found in registry!");
        else getLogger().fine("QuickBoom enchantment loaded successfully.");
    }

    public Config getUHCConfig() {
        return config;
    }

    // This is called when the plugin is unloaded from the server.
    public void onDisable() {}

    public void setStarted(boolean started) {
        this.started = started;
    }

    // Has the UHC started?
    public boolean getStarted() { return started; }

    public boolean isCountingDown() { return countingDown; }

    public void setCountingDown(boolean countingDown) { this.countingDown = countingDown; }
}

