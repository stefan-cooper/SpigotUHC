package com.stefancooper.SpigotUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.stefancooper.SpigotUHC.Config;

public abstract class AbstractCommand {

    private final CommandSender sender;
    private final Command cmd;
    private final String[] args;
    private final Config config;

    public AbstractCommand (CommandSender sender, Command cmd, String[] args, Config config) {
        this.sender = sender;
        this.cmd = cmd;
        this.args = args;
        this.config = config;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Command getCommand() {
        return cmd;
    }

    public String[] getArgs() {
        return args;
    }

    public Config getConfig() { return config; }

    public abstract void execute();
}
