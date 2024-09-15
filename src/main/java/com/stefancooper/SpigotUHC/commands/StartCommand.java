package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME;

public class StartCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "start";

    public StartCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        getSender().getServer().getOnlinePlayers().forEach(player -> {
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
            player.setHealth(maxHealth);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setExp(0);
        });
        Utils.getWorld(getConfig().getProp(WORLD_NAME.configName)).setPVP(true);

        /**
         * TODO
         * - Spread players by team
         * - Start timer for grace period
         * - Start countdown timer to start uhc
         * - Start timer for world border grace period
         */
    }
}
