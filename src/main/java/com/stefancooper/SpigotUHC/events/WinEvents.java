package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import com.stefancooper.SpigotUHC.types.UHCTeam;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.*;

public class WinEvents implements Listener {

    private final Config config;

    public WinEvents(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        Bukkit.getLogger().log(Level.FINE, "Player Death Event triggered for: " + playerName);

        List<UHCTeam> teamsWithSurvivors = getTeamsWithSurvivors();

        final boolean doNotEndGameAutomatically = config.getProperty(DISABLE_END_GAME_AUTOMATICALLY, Defaults.DISABLE_END_GAME_AUTOMATICALLY);

        if (!doNotEndGameAutomatically && teamsWithSurvivors.size() == 1) {
            UHCTeam winningTeam = teamsWithSurvivors.getFirst();
            String winningTeamName = winningTeam.getName();
            List<String> winningTeamMembers = new ArrayList<>(Bukkit.getScoreboardManager().getMainScoreboard().getTeam(winningTeamName).getEntries());

            String formattedWinningMembers;
            if (winningTeamMembers.size() > 1) {
                formattedWinningMembers = String.join(", ", winningTeamMembers.subList(0, winningTeamMembers.size() - 1))
                        + " and " + winningTeamMembers.getLast();
            } else {
                formattedWinningMembers = winningTeamMembers.getFirst();
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendTitle(ChatColor.GOLD + "Congratulations to Team " + winningTeamName + "!", "GG " + formattedWinningMembers + "!", 10, 100, 10);
            }

            config.getManagedResources().runTaskLater(() -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendTitle(ChatColor.GOLD + "Thank you all for playing!", "Hope it was fun!", 10, 40, 10);
                }
                endGame();
            }, 5);
        }

        Bukkit.getLogger().log(Level.FINE,"Updated teams with survivors after " + playerName + "'s death.");
    }

    private void endGame() {
        config.getPlugin().setStarted(false);
        config.trigger();
        config.getManagedResources().cancelTimer();

        Optional<Integer> worldSpawnX = Optional.ofNullable(config.getProperty(WORLD_SPAWN_X));
        Optional<Integer> worldSpawnY = Optional.ofNullable(config.getProperty(WORLD_SPAWN_Y));
        Optional<Integer> worldSpawnZ = Optional.ofNullable(config.getProperty(WORLD_SPAWN_Z));

        if (worldSpawnX.isPresent() && worldSpawnZ.isPresent() && worldSpawnY.isPresent()) {
            int x = worldSpawnX.get();
            int y = worldSpawnY.get();
            int z = worldSpawnZ.get();

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(new Location(config.getWorlds().getOverworld(), x, y, z));
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
