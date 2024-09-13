package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.commands.SetConfigCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;

public class Plugin extends JavaPlugin implements Listener {

    private Config config;

    public void onEnable() { // This is called when the plugin is loaded into the server.
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        config = new Config();
        System.out.println("UHC Plugin enabled");
    }

    public void onDisable() { // This is called when the plugin is unloaded from the server.

    }

    /** Used to pass to child commands so that we don't pass the command key to them */
    private String[] getCommandArgs (String[] allArgs) {
        return Arrays.copyOfRange(allArgs, 1, allArgs.length);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equals("uhc") && args.length > 0) {
            switch (args[0]) {
                case SetConfigCommand.COMMAND_KEY:
                    SetConfigCommand setConfigCommand = new SetConfigCommand(sender, cmd, getCommandArgs(args), config);
                    setConfigCommand.execute();
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}

