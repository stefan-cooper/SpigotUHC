package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class UnsetConfigCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "unset";

    public UnsetConfigCommand(CommandSender sender, String cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        Arrays.asList(getArgs()).forEach(arg -> {
            getConfig().unsetProp(arg);
        });

    }
}
