package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.stefancooper.SpigotUHC.Config;

import java.util.Arrays;

public abstract class AbstractCommand {

    private final CommandSender sender;
    private final String cmd;
    private final String[] args;
    private final Config config;

    public AbstractCommand (CommandSender sender, String cmd, String[] args, Config config) {
        this.sender = sender;
        this.cmd = cmd;
        this.args = args;
        this.config = config;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getCommand() {
        return cmd;
    }

    public String[] getArgs() {
        return args;
    }

    public Config getConfig() { return config; }

    public abstract void execute();
}
