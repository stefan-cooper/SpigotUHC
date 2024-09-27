package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.resources.DeathAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.*;
import static com.stefancooper.SpigotUHC.resources.Constants.PLAYER_HEAD;

public class Events implements Listener {

    private final Config config;

    public Events (Config config) {
        this.config = config;
    }

    // View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

    // DamageSource API is experimental, so this may break in a spigot update
    @SuppressWarnings("UnstableApiUsage")
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

        if (Boolean.parseBoolean(config.getProp(ENABLE_TIMESTAMPS.configName))) {
            if (event.getEntity().getLastDamageCause() != null &&
                    event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity() != null &&
                        event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity().getType() == EntityType.PLAYER
            ) {
                Player player = (Player) event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity();
                config.getManagedResources().addTimestamp(String.format("%s kills %s", player.getDisplayName(), event.getEntity().getDisplayName()));
            } else {
                config.getManagedResources().addTimestamp(String.format("%s dies", event.getEntity().getDisplayName()));
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = player.getLastDeathLocation();
        if (deathLocation != null && player.getGameMode().equals(GameMode.SPECTATOR)) {
            event.setRespawnLocation(deathLocation);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        GameMode currentGamemode = event.getPlayer().getGameMode();
        if (currentGamemode == GameMode.SPECTATOR) {
            return;
        } else if (config.getPlugin().getStarted()) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        } else {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Location getTo = event.getTo();
        final Location getFrom = event.getFrom();
        if (config.getPlugin().isCountingDown() && getTo != null && (getTo.getY() > getFrom.getY() || getTo.getX() != getFrom.getX() || getTo.getZ() != getFrom.getZ())) {
            event.setCancelled(true);
        }
    }
}
