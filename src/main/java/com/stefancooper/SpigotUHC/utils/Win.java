package com.stefancooper.SpigotUHC.utils;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import com.stefancooper.SpigotUHC.types.UHCTeam;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.*;

public class Win implements Listener {

    private final Config config;

    public Win(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        Bukkit.getLogger().info("Player Death Event triggered for: " + playerName);

        Team playerTeam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(playerName);
        if (playerTeam != null) {

            Bukkit.getLogger().info(ChatColor.RED + playerName + " has died!");
        }

        List<UHCTeam> teamsWithSurvivors = getTeamsWithSurvivors();

        if (teamsWithSurvivors.size() == 1) {
            UHCTeam winningTeam = teamsWithSurvivors.get(0);
            String winningTeamName = winningTeam.getName();
            List<String> winningTeamMembers = new ArrayList<>(Bukkit.getScoreboardManager().getMainScoreboard().getTeam(winningTeamName).getEntries());

            String formattedWinningMembers;
            if (winningTeamMembers.size() > 1) {
                formattedWinningMembers = String.join(", ", winningTeamMembers.subList(0, winningTeamMembers.size() - 1))
                        + " and " + winningTeamMembers.get(winningTeamMembers.size() - 1);
            } else {
                formattedWinningMembers = winningTeamMembers.get(0);
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendTitle(ChatColor.GOLD + "Congratulations to Team " + winningTeamName + "!", "GG " + formattedWinningMembers + "!", 10, 100, 10);
            }


            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.sendTitle(ChatColor.GOLD + "Thank you all for playing!", "Hope it was fun!", 10, 40, 10);
                    }
                    endGame();
                }
            }.runTaskLater(config.getPlugin(), 100L);

//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                        onlinePlayer.sendTitle(ChatColor.GOLD + "Flip " + winningTeamMemers, "", 2, 2, 2);
//                    }
//                }
//            }.runTaskLater(config.getPlugin(), 100L);
        }

        Bukkit.getLogger().info("Updated teams with survivors after " + playerName + "'s death.");
    }

    private void endGame() {
        config.getPlugin().setStarted(false);
        config.trigger();
        config.getManagedResources().cancelTimer();

        Optional<String> worldSpawnX = Optional.ofNullable(config.getProp(WORLD_SPAWN_X.configName));
        Optional<String> worldSpawnY = Optional.ofNullable(config.getProp(WORLD_SPAWN_Y.configName));
        Optional<String> worldSpawnZ = Optional.ofNullable(config.getProp(WORLD_SPAWN_Z.configName));

        if (worldSpawnX.isPresent() && worldSpawnZ.isPresent()) {

            World world = Utils.getWorld(config.getProp(WORLD_NAME.configName));

            int x = Integer.parseInt(worldSpawnX.get());
            int y = Integer.parseInt(worldSpawnY.get());
            int z = Integer.parseInt(worldSpawnZ.get());

//            int y = world.getHighestBlockYAt(x, z);
//
//            for(Player player : Bukkit.getOnlinePlayers()) {
//                Block blockAtHighestY = world.getBlockAt(x, y, z);
//                if (blockAtHighestY.getType().isSolid()) {
//                    player.teleport(new Location(player.getWorld(), x, y, z));
//                } else {
//                    while (!blockAtHighestY.getType().isSolid()) {
//                        blockAtHighestY = world.getBlockAt(x, y, z);
//                        y--;
//                    }
//                    y++;
//                    player.teleport(new Location(player.getWorld(), x, y, z));
//                }
//            }

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(new Location(player.getWorld(), x, y, z));
            }
        }
    }

    private List<UHCTeam> getTeamsWithSurvivors() {
        List<UHCTeam> teamsWithSurvivors = new ArrayList<>();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (Team team : scoreboard.getTeams()) {
            boolean hasSurvivor = false;
            List<String> playerNames = new ArrayList<>();

            for (String playerName : team.getEntries()) {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null && player.getGameMode() == GameMode.SURVIVAL) {
                    hasSurvivor = true;
                }
                playerNames.add(playerName);
            }

            if (hasSurvivor) {
                String playersAsString = String.join(", ", playerNames);
                UHCTeam uhcTeam = new UHCTeam(team.getName(), playersAsString, team.getColor());
                teamsWithSurvivors.add(uhcTeam);
            }
        }

        return teamsWithSurvivors;
    }


}