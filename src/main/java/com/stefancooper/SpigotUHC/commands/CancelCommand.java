package com.stefancooper.SpigotUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.stefancooper.SpigotUHC.Config;
import org.jetbrains.annotations.NotNull;


public class CancelCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "cancel";

    public CancelCommand(CommandSender sender, String cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        getConfig().getPlugin().setStarted(false);
        getConfig().trigger();
        getConfig().getManagedResources().cancelTimer();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("MATCH HAS ENDED","", 10, 70, 20);
        }
    }

//    public static final String COMMAND_KEY = "uhc cancel";
//    private final Config config;
//
//    public CancelCommand(final Config config) {
//        super(COMMAND_KEY);
//        this.config = config;
//    }
//
//    @Override
//    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
//        config.getPlugin().setStarted(false);
//        config.trigger();
//        config.getManagedResources().cancelTimer();
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            player.sendTitle("MATCH HAS ENDED","", 10, 70, 20);
//        }
//        return true;
//    }
}
