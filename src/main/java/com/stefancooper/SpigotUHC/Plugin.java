package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.commands.CancelCommand;
import com.stefancooper.SpigotUHC.commands.LateStartCommand;
import com.stefancooper.SpigotUHC.commands.PvpCommand;
import com.stefancooper.SpigotUHC.commands.RandomiseTeamsCommand;
import com.stefancooper.SpigotUHC.commands.ResumeCommand;
import com.stefancooper.SpigotUHC.commands.SetConfigCommand;
import com.stefancooper.SpigotUHC.commands.StartCommand;
import com.stefancooper.SpigotUHC.commands.UHCCommand;
import com.stefancooper.SpigotUHC.commands.UnsetConfigCommand;
import com.stefancooper.SpigotUHC.commands.ViewConfigCommand;
import com.stefancooper.SpigotUHC.events.BaseEvents;
import com.stefancooper.SpigotUHC.events.EnchantmentEvents;
import com.stefancooper.SpigotUHC.events.ReviveEvents;
import com.stefancooper.SpigotUHC.events.TimestampEvents;
import com.stefancooper.SpigotUHC.events.UHCLootEvents;
import com.stefancooper.SpigotUHC.utils.UHCCommandTabCompleter;
import com.stefancooper.SpigotUHC.events.WinEvents;
import com.stefancooper.SpigotUHC.utils.Utils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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

//        ---
        Enchantment quickBoom;
        Registry<Enchantment> enchantRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        // Lookup your custom enchant
        quickBoom = enchantRegistry.get(EnchantmentKeys.create(Key.key("spigotuhc:quickboom")));

        if (quickBoom == null) {
            getLogger().warning("QuickBoom enchantment not found in registry!");
        } else {
            getLogger().info("QuickBoom enchantment loaded successfully.");
        }
//        ---

        this.getLogger().log(Level.INFO, "UHC Plugin enabled");
        getServer().getCommandMap().register("uhc", new UHCCommand(config));

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

    /** Used to pass to child commands so that we don't pass the command key to them */
    private String[] getCommandArgs (String[] allArgs) {
        return Arrays.copyOfRange(allArgs, 1, allArgs.length);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.equals("uhc") && args.length > 0 && Utils.testMode()) {
            switch (args[0]) {
                case SetConfigCommand.COMMAND_KEY:
                    new SetConfigCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case UnsetConfigCommand.COMMAND_KEY:
                    new UnsetConfigCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case ViewConfigCommand.COMMAND_KEY:
                    new ViewConfigCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case StartCommand.COMMAND_KEY:
                    new StartCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case CancelCommand.COMMAND_KEY:
                    new CancelCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case ResumeCommand.COMMAND_KEY:
                    new ResumeCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case LateStartCommand.COMMAND_KEY:
                    new LateStartCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case PvpCommand.COMMAND_KEY:
                    new PvpCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                case RandomiseTeamsCommand.COMMAND_KEY:
                    new RandomiseTeamsCommand(sender, cmd.getLabel(), getCommandArgs(args), config).execute();
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}

