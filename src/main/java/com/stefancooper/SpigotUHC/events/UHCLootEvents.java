package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.types.UHCLoot;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Optional;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.*;
import static com.stefancooper.SpigotUHC.types.UHCLoot.getChestLocation;

public class UHCLootEvents implements Listener {

    private final Config config;

    public UHCLootEvents (Config config) {
        this.config = config;
    }

    private boolean isSameLocation(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ();
    }

    // View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (UHCLoot.isConfigured(config) && getChestLocation(config).isPresent()) {
            if (isSameLocation(getChestLocation(config).get(), event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (UHCLoot.isConfigured(config) && getChestLocation(config).isPresent()) {
            if (isSameLocation(getChestLocation(config).get(), event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (UHCLoot.isConfigured(config) && getChestLocation(config).isPresent()) {
            if (event.blockList().contains(getChestLocation(config).get().getBlock())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (UHCLoot.isConfigured(config) && getChestLocation(config).isPresent()) {
            if (isSameLocation(getChestLocation(config).get(), event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
