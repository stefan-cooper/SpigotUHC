package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.types.Revive;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.*;

public class ReviveEvents implements Listener {

    private final Config config;

    public ReviveEvents (Config config) {
        this.config = config;
    }

    // View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            Optional<Revive> revive = config.getManagedResources().getRevive();
            boolean insideReviveZone = Revive.isInsideReviveZone(config, event.getTo());
            if (revive.isEmpty() && event.getPlayer().getInventory().contains(Material.PLAYER_HEAD)) {
                List<ItemStack> playerHeads = Arrays.stream(event.getPlayer().getInventory().getStorageContents()).filter(itemStack -> itemStack != null && itemStack.getType().equals(Material.PLAYER_HEAD)).toList();
                for (ItemStack playerHead : playerHeads) {
                    if (Boolean.TRUE.equals(config.getProperty(REVIVE_ANY_HEAD))) {
                        // Any head revive mode
                        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(event.getPlayer().getDisplayName());
                        if (team != null) {
                            List<Player> teammates = team.getEntries().stream().map(Bukkit::getPlayer).toList();
                            List<Player> deadTeammates  = teammates.stream().filter(player -> player.isDead() || player.getGameMode() == GameMode.SPECTATOR).toList();
                            if (!deadTeammates.isEmpty() && insideReviveZone) {
                                config.getManagedResources().startReviving(event.getPlayer(), deadTeammates.getFirst().getName(), playerHead.clone());
                                break;
                            }
                        }
                    } else {
                        // Only teammates head revive mode
                        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                        if (meta != null && meta.getOwningPlayer() != null && meta.getOwningPlayer() != null && meta.getOwningPlayer().getName() != null) {
                            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(meta.getOwningPlayer().getName());
                            if (team != null && team.hasEntry(event.getPlayer().getName()) && insideReviveZone) {
                                config.getManagedResources().startReviving(event.getPlayer(), meta.getOwningPlayer().getName(), playerHead.clone());
                                break;
                            }
                        }
                    }

                }
            } else if (revive.isPresent() && revive.get().reviver.getEntityId() == event.getPlayer().getEntityId()) {
                if (!insideReviveZone || !event.getPlayer().getInventory().contains(revive.get().playerHead)) {
                    config.getManagedResources().cancelRevive();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName)) && config.getManagedResources().getRevive().isPresent()) {
            Revive revive = config.getManagedResources().getRevive().get();
            if (revive.revivee.getEntityId() == event.getPlayer().getEntityId() || revive.reviver.getEntityId() == event.getPlayer().getEntityId()) {
                config.getManagedResources().cancelRevive();
            }
        }
    }

    @EventHandler
    public void onReviverDeath (PlayerDeathEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName)) && config.getManagedResources().getRevive().isPresent()) {
            Revive revive = config.getManagedResources().getRevive().get();
            if (revive.reviver.getEntityId() == event.getEntity().getEntityId()) {
                config.getManagedResources().cancelRevive();
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
            if (Revive.isInsideReviveZone(config, event.getBlockPlaced().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketPlace(PlayerBucketEmptyEvent event) {
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
            if (Revive.isNearReviveZone(config, event.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLiquidMove(BlockFromToEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            if (Revive.isInsideReviveZone(config, event.getBlock().getLocation()) || Revive.isInsideReviveZone(config, event.getToBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            if (event.getEntity() instanceof FallingBlock) {
                if (Revive.isInsideReviveZone(config, event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (Boolean.parseBoolean(config.getProp(REVIVE_ENABLED.configName))) {
            Optional<Revive> revive = config.getManagedResources().getRevive();
            Optional<ItemMeta> itemDropped = Optional.ofNullable(event.getItemDrop().getItemStack().getItemMeta());
            if (revive.isPresent() && itemDropped.isPresent() &&
                    revive.get().reviver.getEntityId() == event.getPlayer().getEntityId() &&
                    itemDropped.get().getDisplayName().equals(revive.get().playerHead.getItemMeta().getDisplayName())
            ) {
                config.getManagedResources().cancelRevive();
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.ITEM) {
            Item item = (Item) event.getEntity();
            if (item.getItemStack().getType() == Material.PLAYER_HEAD) {
                event.setCancelled(true);
            }
        }
    }

}
