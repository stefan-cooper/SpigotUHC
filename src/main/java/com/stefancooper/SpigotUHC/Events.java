package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.resources.DeathAction;
import com.sun.tools.javac.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.KILLERS_IN_SCOREBOARD;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.ON_DEATH_ACTION;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.PLAYER_HEAD_GOLDEN_APPLE;
import static com.stefancooper.SpigotUHC.resources.Constants.PLAYER_HEAD;

public class Events implements Listener {

    private HashMap<UUID, Integer> playerKills = new HashMap<>();
    private final Config config;

    public Events (Config config) {
        this.config = config;
    }

    // View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

    @EventHandler
    public void onDeath (PlayerDeathEvent event) {
        switch (DeathAction.fromString(config.getProp(ON_DEATH_ACTION.configName))){
            case SPECTATE:
                event.getEntity().setGameMode(GameMode.SPECTATOR);
                break;
            case KICK:
                event.getEntity().kickPlayer("GG, you suck");
                break;
            case null:
            default:
                break;
        }

        if (Boolean.parseBoolean(config.getProp(PLAYER_HEAD_GOLDEN_APPLE.configName))){
            Player player = event.getEntity();
            ItemStack head = new ItemStack(Material.PLAYER_HEAD,1);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            assert headMeta != null;
            headMeta.setDisplayName(PLAYER_HEAD);
            headMeta.setLore(List.of("Put this item in a bench", "For a Golden Apple"));
            headMeta.setOwningPlayer(player);
            head.setItemMeta(headMeta);
            player.getWorld().dropItemNaturally(player.getLocation(), head);
        }

        if (Boolean.parseBoolean(config.getProp(KILLERS_IN_SCOREBOARD.configName))){
            Player victim = event.getEntity();
            Player killer = victim.getKiller();
            if(killer != null){
                UUID killerId = killer.getUniqueId();

                // Increment the kill count for the player
                int kills = playerKills.getOrDefault(killerId, 0) + 1;
                playerKills.put(killerId, kills);
                updateScoreboard(killer);
            }
        }
    }

    @EventHandler
    public void onRespawn (PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = player.getLastDeathLocation();
        if (deathLocation != null && player.getGameMode().equals(GameMode.SPECTATOR)) {
            event.setRespawnLocation(deathLocation);
        }
    }

    // Function to update the player's scoreboard when their kill count changes
    public void updateScoreboard(Player player) {
        if (Boolean.parseBoolean(config.getProp(KILLERS_IN_SCOREBOARD.configName))){
            // Get the player's current scoreboard
            Scoreboard board = player.getScoreboard();
            Objective objective = board.getObjective("Kills");
            Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(board));

            if (objective != null) {
                // Get the player's current kill count
                int kills = playerKills.getOrDefault(player.getUniqueId(), 0);

                // Update the kill count on the scoreboard
                Score killScore = objective.getScore(ChatColor.YELLOW + player.getName());
                killScore.setScore(kills);
            }
        }
    }
}
