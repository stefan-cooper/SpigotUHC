package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.commands.CancelCommand;
import com.stefancooper.SpigotUHC.commands.LateStartCommand;
import com.stefancooper.SpigotUHC.commands.PvpCommand;
import com.stefancooper.SpigotUHC.commands.RandomiseTeamsCommand;
import com.stefancooper.SpigotUHC.commands.ResumeCommand;
import com.stefancooper.SpigotUHC.commands.SetConfigCommand;
import com.stefancooper.SpigotUHC.commands.StartCommand;
import com.stefancooper.SpigotUHC.commands.UnsetConfigCommand;
import com.stefancooper.SpigotUHC.commands.ViewConfigCommand;
import com.stefancooper.SpigotUHC.events.BaseEvents;
import com.stefancooper.SpigotUHC.events.ReviveEvents;
import com.stefancooper.SpigotUHC.events.TimestampEvents;
import com.stefancooper.SpigotUHC.events.UHCLootEvents;
import com.stefancooper.SpigotUHC.utils.UHCCommandTabCompleter;
import com.stefancooper.SpigotUHC.events.WinEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;

public class Plugin extends JavaPlugin implements Listener {

    private Config config;
    private boolean started;
    private boolean countingDown;

    // This is called when the plugin is loaded into the server.
    public void onEnable() {
        config = new Config(this);
        Defaults.setDefaultGameRules(this.config);
        Bukkit.getPluginManager().registerEvents(new BaseEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new ReviveEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new TimestampEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new WinEvents(config), this);
        Bukkit.getPluginManager().registerEvents(new UHCLootEvents(config), this);
        started = false;
        System.out.println("UHC Plugin enabled");
        getCommand("uhc").setTabCompleter(new UHCCommandTabCompleter());
    }

    public Config getUHCConfig() {
        return config;
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
                case SetConfigCommand.COMMAND_KEY:
                    new SetConfigCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case UnsetConfigCommand.COMMAND_KEY:
                    new UnsetConfigCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case ViewConfigCommand.COMMAND_KEY:
                    new ViewConfigCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case StartCommand.COMMAND_KEY:
                    new StartCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case CancelCommand.COMMAND_KEY:
                    new CancelCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case ResumeCommand.COMMAND_KEY:
                    new ResumeCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case LateStartCommand.COMMAND_KEY:
                    new LateStartCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case PvpCommand.COMMAND_KEY:
                    new PvpCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                case RandomiseTeamsCommand.COMMAND_KEY:
                    new RandomiseTeamsCommand(sender, cmd, getCommandArgs(args), config).execute();
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    // Has the UHC started?
    public boolean getStarted() { return started; }

    public boolean isCountingDown() { return countingDown; }

    public void setCountingDown(boolean countingDown) { this.countingDown = countingDown; }
}

