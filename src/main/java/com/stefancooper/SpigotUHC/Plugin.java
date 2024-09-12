package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.commands.Config;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

import static com.stefancooper.SpigotUHC.commands.Config.CONFIG_ARG;

public class Plugin extends JavaPlugin implements Listener {

    public void onEnable() { // This is called when the plugin is loaded into the server.
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        System.out.println("UHC Plugin enabled");
    }

    public void onDisable() { // This is called when the plugin is unloaded from the server.

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equals("uhc")) {
            if (Arrays.asList(args).contains(CONFIG_ARG)) {
                Config config = new Config(sender, cmd, args);
                config.execute();
            }
        }
        return true;
    }
}

