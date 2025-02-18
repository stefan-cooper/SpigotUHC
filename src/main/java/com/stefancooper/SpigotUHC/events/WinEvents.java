package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
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

        final boolean doNotEndGameAutomatically = (boolean) Optional.ofNullable(config.getProperty(DISABLE_END_GAME_AUTOMATICALLY)).orElse(false);

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

        Optional<String> worldSpawnX = Optional.ofNullable(config.getProp(WORLD_SPAWN_X.configName));
        Optional<String> worldSpawnY = Optional.ofNullable(config.getProp(WORLD_SPAWN_Y.configName));
        Optional<String> worldSpawnZ = Optional.ofNullable(config.getProp(WORLD_SPAWN_Z.configName));

        if (worldSpawnX.isPresent() && worldSpawnZ.isPresent()) {
            int x = Integer.parseInt(worldSpawnX.get());
            int y = Integer.parseInt(worldSpawnY.get());
            int z = Integer.parseInt(worldSpawnZ.get());

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
