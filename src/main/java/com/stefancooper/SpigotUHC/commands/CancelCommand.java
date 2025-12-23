package com.stefancooper.SpigotUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.stefancooper.SpigotUHC.Config;

public class CancelCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "cancel";

    public CancelCommand(CommandSender sender, String cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        getConfig().getPlugin().setUHCLive(false);
        getConfig().trigger();
        getConfig().getManagedResources().cancelTimer();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("MATCH HAS ENDED","", 10, 70, 20);
        }
    }
}
