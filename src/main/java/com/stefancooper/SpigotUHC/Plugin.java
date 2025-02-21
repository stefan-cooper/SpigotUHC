package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.commands.StartCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;

public class Plugin extends JavaPlugin implements Listener {

    private Config config;

    // This is called when the plugin is loaded into the server.
    public void onEnable() {
        config = new Config(this);
        System.out.println("UHC Plugin enabled");
    }


    // This is called when the plugin is unloaded from the server.
    public void onDisable() {}

    /** Used to pass to child commands so that we don't pass the command key to them */
    private String[] getCommandArgs (String[] allArgs) {
        return Arrays.copyOfRange(allArgs, 1, allArgs.length);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equals("uhc") && args.length > 0) {
            switch (args[0]) {
                case StartCommand.COMMAND_KEY:
                    new StartCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}

