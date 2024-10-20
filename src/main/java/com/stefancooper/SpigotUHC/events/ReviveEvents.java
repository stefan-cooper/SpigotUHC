package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.types.Revive;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.Optional;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.*;

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
                int playerHeadIndex = event.getPlayer().getInventory().first(Material.PLAYER_HEAD);
                ItemStack playerHead = event.getPlayer().getInventory().getItem(playerHeadIndex);

                assert playerHead.getItemMeta() != null;
                SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                boolean isTeammate = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(meta.getOwningPlayer().getName()).hasEntry(event.getPlayer().getName());

                if (isTeammate && insideReviveZone) {
                    config.getManagedResources().startReviving(event.getPlayer(), meta.getOwningPlayer().getPlayer(), playerHead.clone());
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
}