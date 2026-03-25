package com.stefancooper.SpigotUHC.utils;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.types.Coordinate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.SPLIT_WITHIN_TEAMS_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_INITIAL_SIZE;

public class SpreadPlayers {

    final Config config;

    public SpreadPlayers(final Config config) {
        this.config = config;
    }


    public void trigger() {
        final int centerX = config.getProperty(WORLD_BORDER_CENTER_X, Defaults.WORLD_BORDER_CENTER_X);
        final int centerZ = config.getProperty(WORLD_BORDER_CENTER_Z, Defaults.WORLD_BORDER_CENTER_Z);
        final int diameter = config.getProperty(WORLD_BORDER_INITIAL_SIZE, Defaults.WORLD_BORDER_INITIAL_SIZE);
        final int splitSize = config.getProperty(SPLIT_WITHIN_TEAMS_SIZE, Defaults.SPLIT_WITHIN_TEAMS_SIZE);
        final World overworld = config.getWorlds().getOverworld();
        List<List<Player>> groups = getAllTeams();

        if (splitSize > 0) {
            groups = splitIntoClusters(groups, splitSize);
        }

        final List<Coordinate> coordinatesToTeleportTo = splitEvenly(centerX, centerZ, diameter, groups.size());
        Collections.shuffle(coordinatesToTeleportTo);

        if (coordinatesToTeleportTo.size() != groups.size()) {
            throw new RuntimeException("something went wrong!");
        }

        for (int i = 0; i < coordinatesToTeleportTo.size(); i++) {
            final List<Player> team = groups.get(i);
            final Coordinate startingLocation = coordinatesToTeleportTo.get(i);
            for (final Player player : team) {
                if (player != null) {
                    final Block startingBlock = overworld.getHighestBlockAt((int) startingLocation.x(), (int) startingLocation.z());
                    startingBlock.setType(Material.BEDROCK);
                    player.teleport(startingBlock.getLocation().add(0, 1,0));
                }
            }
        }

    }

    private List<List<Player>> getAllTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        List<List<Player>> teams = new ArrayList<>();
        for (Team team : scoreboard.getTeams()) {
            List<Player> teamPlayers = new ArrayList<>();
            for (String playerName : team.getEntries()) {
                teamPlayers.add(Bukkit.getPlayer(playerName));
            }
            teams.add(teamPlayers);
        }
        return teams;
    }

    private List<List<Player>> splitIntoClusters(List<List<Player>> allTeams, int splitSize) {
        List<List<Player>> dividedTeams = new ArrayList<>();
        for (List<Player> team : allTeams) {
            for (int i = 0; i < team.size(); i += splitSize) {
                int end = Math.min(i + splitSize, team.size());
                dividedTeams.add(new ArrayList<>(team.subList(i, end)));
            }
        }

        return dividedTeams;
    }

    public static List<Coordinate> splitEvenly(final double centerX, final double centerZ, final double diameter, final int totalGroups) {
        if (diameter < 0) {
            return List.of();
        }
        if (totalGroups < 1) {
            return List.of();
        }
        final List<Coordinate> coordinates = new ArrayList<>(totalGroups);
        if (totalGroups == 1) {
            return List.of(new Coordinate(centerX, centerZ));
        }

        final int cols = (int) Math.ceil(Math.sqrt(totalGroups));
        final int rows = (int) Math.ceil((double) totalGroups / cols);

        final double padding = 1.0;
        final double usableDiameter = diameter - (2 * padding);

        final double colSpacing = (cols > 1) ? usableDiameter / (cols - 1) : 0;
        final double rowSpacing = (rows > 1) ? usableDiameter / (rows - 1) : 0;
        final double radius = diameter / 2.0;
        final double startX = centerX - radius + padding;
        final double startZ = centerZ - radius + padding;

        int coordinatesAdded = 0;
        for (int row = 0; row < rows && coordinatesAdded < totalGroups; row++) {
            for (int col = 0; col < cols && coordinatesAdded < totalGroups; col++) {
                final double x = startX + (col * colSpacing);
                final double z = startZ + (row * rowSpacing);
                coordinates.add(new Coordinate(x,z));
                coordinatesAdded++;
            }
        }

        return coordinates;
    }
}
