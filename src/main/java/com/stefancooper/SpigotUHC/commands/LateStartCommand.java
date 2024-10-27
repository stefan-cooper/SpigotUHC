package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.types.BossBarBorder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.SPREAD_MIN_DISTANCE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_INITIAL_SIZE;

public class LateStartCommand extends StartCommand {

    public static final String COMMAND_KEY = "latestart";

    public LateStartCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }


    @Override
    public void execute() {
        getConfig().getManagedResources().cancelTimer();

        Player player;
        if (getArgs().length == 0) {
            return;
        } else {
            player = Bukkit.getPlayer(getArgs()[0]);
        }

        if (player == null) {
            return;
        }

        // Set starting stats
        player.setGameMode(GameMode.SURVIVAL);
        player.resetMaxHealth();
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        player.setHealth(maxHealth);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);

        // set bossbar
        if (Boolean.parseBoolean(getConfig().getProp(ConfigKey.WORLD_BORDER_IN_BOSSBAR.configName))) {
            BossBarBorder bossBarBorder = getConfig().getManagedResources().getBossBarBorder();
            bossBarBorder.getBossBar().addPlayer(player);
            bossBarBorder.getBossBar().setVisible(true);
        }

        AtomicBoolean teleported = new AtomicBoolean(false);

        // teleport to teammate
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Optional<Team> playerTeam = scoreboard.getTeams().stream().filter(team -> team.hasEntry(player.getDisplayName())).findFirst();
        playerTeam.ifPresent(team -> {
            Optional<String> teammateName = team.getEntries().stream().filter(teamPlayer -> !teamPlayer.equals(player.getDisplayName())).findFirst();
            teammateName.ifPresent(teammate -> {
                Player teammatePlayer = Bukkit.getPlayer(teammate);
                if (teammatePlayer == null) {
                    return;
                }
                player.teleport(teammatePlayer);
                teleported.set(true);
            });
        });

        // if could not be teleported to teammate, spread them as usual
        if (!teleported.get()) {
            // Spread Players
            double centerX = Double.parseDouble(getConfig().getProp(WORLD_BORDER_CENTER_X.configName));
            double centerZ =  Double.parseDouble(getConfig().getProp(WORLD_BORDER_CENTER_Z.configName));
            double minDistance =  Double.parseDouble(getConfig().getProp(SPREAD_MIN_DISTANCE.configName));
            double maxDistance =  Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName)) / 2;
            // spreadplayers <x> <z> <spreadDistance> <maxRange> <teams> <targets>
            // See: https://minecraft.fandom.com/wiki/Commands/spreadplayers
            String spreadCommand = String.format("spreadplayers %f %f %f %f true %s", centerX, centerZ, minDistance, maxDistance, player.getDisplayName());
            getSender().getServer().dispatchCommand(getSender(), spreadCommand);
        }
    }
}
