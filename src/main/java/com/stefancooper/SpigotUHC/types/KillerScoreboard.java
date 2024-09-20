package com.stefancooper.SpigotUHC.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import static com.stefancooper.SpigotUHC.resources.Constants.KILLER_SCOREBOARD_OBJECTIVE;

public class KillerScoreboard {

    final Objective killerObjective;

    public KillerScoreboard() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        if (board.getObjective(KILLER_SCOREBOARD_OBJECTIVE) == null) {
            killerObjective = board.registerNewObjective(KILLER_SCOREBOARD_OBJECTIVE, Criteria.PLAYER_KILL_COUNT, "Kills");
        } else {
            killerObjective = board.getObjective(KILLER_SCOREBOARD_OBJECTIVE);
        }
        killerObjective.setDisplayName(ChatColor.GREEN + "Player Kills");
    }

    public Objective getObjective() {
        return killerObjective;
    }

    public void updateScoreboard(Player player) {
        Score currentScore = killerObjective.getScore(player.getName());
        currentScore.setScore(currentScore.getScore() + 1);
    }
}
