package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Arrays;

public class SetConfigCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "set";

    public SetConfigCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        Arrays.asList(getArgs()).forEach(arg -> {
            String[] splitConfig = arg.split("=");
            if (splitConfig.length == 2 && ConfigKey.fromString(splitConfig[0]) != null) {
                String key = splitConfig[0];
                String value = splitConfig[1];
                getConfig().setProp(key, value);
            } else {
                System.out.println("Invalid configuration: " + arg);
                getSender().sendMessage("Invalid configuration: " + arg);
            }
        });

    }
}
