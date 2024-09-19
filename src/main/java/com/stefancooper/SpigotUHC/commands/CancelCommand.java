package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CancelCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "cancel";

    public CancelCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        getConfig().getPlugin().setStarted(false);
        getConfig().trigger();
    }
}
