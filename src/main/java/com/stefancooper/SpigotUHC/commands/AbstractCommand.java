package com.stefancooper.SpigotUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand {

    private final CommandSender sender;
    private final Command cmd;
    private final String[] args;

    public AbstractCommand (CommandSender sender, Command cmd, String[] args) {
        this.sender = sender;
        this.cmd = cmd;
        this.args = args;
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

    public abstract void execute();
}
