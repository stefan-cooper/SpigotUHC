package com.stefancooper.SpigotUHC.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.List;

public class UHCTeam {

    private final ChatColor color;
    private final String players;
    private final String name;

    public UHCTeam(String name, String players, ChatColor color) {
        this.name = name;
        this.players = players;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public List<String> getPlayers() {
        return Arrays.stream(players.split(",")).map(String::trim).toList();
    }

    public ChatColor getColor() {
        return color;
    }

    public static void createTeam(UHCTeam uhcTeam) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // if team already exists, redefine it
        if (scoreboard.getTeam(uhcTeam.getName()) != null) {
            scoreboard.getTeam(uhcTeam.getName()).unregister();
        }
        Team team = scoreboard.registerNewTeam(uhcTeam.getName());
        uhcTeam.getPlayers().forEach(player -> {
            // if player is already on another team, remove them from that team and put them on this team
            if (scoreboard.getEntryTeam(player) != null) {
                scoreboard.getEntryTeam(player).removeEntry(player);
            }
            team.addEntry(player);
        });
        team.setColor(uhcTeam.getColor());
        team.setAllowFriendlyFire(false);
        team.setPrefix(String.format("[%s] ", uhcTeam.getName()));
    }
}
