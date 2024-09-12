package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.types.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.fromString;

public class Config extends AbstractCommand {

    public static final String CONFIG_ARG = "--config";

    public Config(CommandSender sender, Command cmd, String[] args) {
        super(sender, cmd, args);
    }

    private Configurable<?> argumentToConfigurable(String argument) {
        String key = argument.split("=")[0];
        String value = argument.split("=")[1];

        return switch (fromString(key)) {
            case WORLD_BORDER -> new Configurable<>(WORLD_BORDER, Double.parseDouble(value));
            case null -> null;
        };
    }

    private List<Configurable<?>> parseArgs() {
        List<Configurable<?>> configurables = new ArrayList<>();
        final int[] index = {0};
        String[] args = getArgs();

        Arrays.stream(args).toList().forEach((argument) -> {
            if (argument.equals(CONFIG_ARG)) {
                String valueSetter = args[index[0] + 1];
                configurables.add(argumentToConfigurable(valueSetter));
            }
            index[0]++;
        });

        return configurables;
    }

    @Override
    public void execute() {
        List<Configurable<?>> configurables = parseArgs();
        configurables.forEach(config -> {
            switch (config.key()) {
                case WORLD_BORDER:
                    Double newWorldBorderSize = (Double) config.value();
                    WorldBorder worldBorder = Bukkit.getWorld("world").getWorldBorder();
                    worldBorder.setSize(newWorldBorderSize);
                    break;
            }
        });
    }
}
