package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.resources.DeathAction;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.*;
import static com.stefancooper.SpigotUHC.resources.Constants.PLAYER_HEAD;

public class Events implements Listener {

    private final Config config;

    public Events(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        switch (DeathAction.fromString(config.getProp(ON_DEATH_ACTION.configName))){
            case SPECTATE:
                event.getEntity().setGameMode(GameMode.SPECTATOR);
                break;
            case KICK:
                event.getEntity().kickPlayer("GG, you suck");
                break;
            default:
                break;
        }

        if(Boolean.parseBoolean(config.getProp(PLAYER_HEAD_GOLDEN_APPLE.configName))){
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
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = player.getLastDeathLocation();
        if(deathLocation != null && player.getGameMode().equals(GameMode.SPECTATOR)){
            event.setRespawnLocation(deathLocation);
        } else {
            Location fallbackLocation = new Location(player.getWorld(), 0, 64, 0);
            event.setRespawnLocation(fallbackLocation);
        }
    }
}

// View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html