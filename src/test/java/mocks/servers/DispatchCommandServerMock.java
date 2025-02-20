package mocks.servers;

import be.seeseemelk.mockbukkit.AsyncCatcher;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DispatchCommandServerMock extends ServerMock {

    @Override
    public boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String commandLine)
    {
        AsyncCatcher.catchOp("command dispatch");
        String[] commands = commandLine.split(" ");
        String commandLabel = commands[0];
        String[] args = Arrays.copyOfRange(commands, 1, commands.length);
        Command command = getCommandMap().getCommand(commandLabel);

        Player admin = Bukkit.getPlayer("admin");

        if (command != null) {
            return command.execute(sender, commandLabel, args);
        } else if (commandLabel.equals("fill") && admin != null) {
            admin.sendMessage(commandLine);
            return true;
        } else if (commandLabel.equals("spreadplayers")) {
            sender.sendMessage(commandLine);
            return true;
        } else {
            return false;
        }
    }

}
