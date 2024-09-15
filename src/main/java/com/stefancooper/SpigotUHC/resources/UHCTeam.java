package com.stefancooper.SpigotUHC.resources;

import org.bukkit.ChatColor;

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
}
