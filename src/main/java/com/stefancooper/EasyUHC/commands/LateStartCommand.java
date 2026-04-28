package com.stefancooper.EasyUHC.commands;

import com.stefancooper.EasyUHC.Config;
import com.stefancooper.EasyUHC.Defaults;
import com.stefancooper.EasyUHC.base.ConfigKey;
import com.stefancooper.EasyUHC.base.BossBarBorder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class LateStartCommand extends StartCommand {

    public static final String COMMAND_KEY = "latestart";

    public LateStartCommand(CommandSender sender, String cmd, String[] args, Config config) {
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
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue();
        player.setHealth(maxHealth);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);

        // set bossbar
        if (getConfig().getProperty(ConfigKey.WORLD_BORDER_IN_BOSSBAR, Defaults.WORLD_BORDER_IN_BOSSBAR)) {
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
    }
}
