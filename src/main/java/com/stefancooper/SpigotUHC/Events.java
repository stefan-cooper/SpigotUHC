package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.resources.DeathAction;
import com.stefancooper.SpigotUHC.types.Revive;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Optional;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.*;

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
            headMeta.setDisplayName(String.format("%s's head", player.getDisplayName()));
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
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            Optional<Revive> revive = config.getManagedResources().getRevive();
            boolean insideReviveZone = Revive.isInsideReviveZone(config, event.getTo());
            if (revive.isEmpty() && event.getPlayer().getInventory().contains(Material.PLAYER_HEAD)) {
                int playerHeadIndex = event.getPlayer().getInventory().first(Material.PLAYER_HEAD);
                ItemStack playerHead = event.getPlayer().getInventory().getItem(playerHeadIndex);

                assert playerHead.getItemMeta() != null;
                SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                boolean isTeammate = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(meta.getOwningPlayer().getName()).hasEntry(event.getPlayer().getName());

                if (isTeammate && insideReviveZone) {
                    config.getManagedResources().startReviving(event.getPlayer(), meta.getOwningPlayer().getPlayer(), playerHead);
                }
            } else if (revive.isPresent() && revive.get().reviver.getEntityId() == event.getPlayer().getEntityId()) {
                if (!insideReviveZone || !event.getPlayer().getInventory().contains(revive.get().playerHead)) {
                    System.out.println("Revive cancelled");
                    config.getManagedResources().cancelRevive();
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            if (Revive.isInsideReviveZone(config, event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            if (Revive.isInsideReviveZone(config, event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            if (Revive.isInsideReviveZone(config, event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            if (Revive.isInsideReviveZone(config, event.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            if (Revive.isInsideReviveZone(config, event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            Optional<Revive> revive = config.getManagedResources().getRevive();
            if (revive.isPresent() && revive.get().reviver.getEntityId() == event.getPlayer().getEntityId() && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(revive.get().playerHead.getItemMeta().getDisplayName())) {
                config.getManagedResources().cancelRevive();
            }
        }
    }
}
