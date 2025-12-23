package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.enums.PerformanceTrackingEvent;
import com.stefancooper.SpigotUHC.types.UHCLoot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.ENABLE_PERFORMANCE_TRACKING;
import static com.stefancooper.SpigotUHC.types.UHCLoot.getChestLocation;
import static com.stefancooper.SpigotUHC.types.UHCLoot.isSameLocation;


public class PerformanceEvents implements Listener {

    private final Config config;
    private final Set<Location> lootChestLocations = new HashSet<>();

    public PerformanceEvents (Config config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.DEATH, player.getName(), 1);
        if (event.getEntity().getLastDamageCause() != null &&
                event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity() != null &&
                event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity().getType() == EntityType.PLAYER
        ) {
            final Player killer = (Player) event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity();
            config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.KILL, killer.getName(), 1);
        }

    }

    @EventHandler
    public void onGoldOreBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.GOLD_ORE) || event.getBlock().getType().equals(Material.DEEPSLATE_GOLD_ORE)) {
            final Player player = event.getPlayer();
            config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.GOLD_ORE_MINED, player.getName(), 1);
        }
    }

    @EventHandler
    public void onLootChestOpenEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CHEST)) {
            if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && UHCLoot.isConfigured(config) && getChestLocation(config).isPresent()) {
                final Location chestLocation = event.getClickedBlock().getLocation();
                if (isSameLocation(getChestLocation(config).get(), chestLocation) && !lootChestLocations.contains(chestLocation)) {
                    lootChestLocations.add(chestLocation);
                    config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.LOOT_CHEST_CLAIMED, event.getPlayer().getName(), 1);
                }
            }
        }
    }

    @EventHandler
    public void onPVEDamage(EntityDamageEvent event) {
        // if damage is thorns, ignore
        if (event.getDamageSource().getDamageType() == DamageType.THORNS) {
            return;
        }
        if (event.getEntity() instanceof final Player damagee && !(event.getDamageSource().getCausingEntity() instanceof Player) && !(event.getDamageSource().getDirectEntity() instanceof Player)) {
            config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.PVE_DAMAGE, damagee.getName(), (int) event.getFinalDamage());
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // if damage is thorns, ignore
        if (event.getDamageSource().getDamageType() == DamageType.THORNS) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof final Player attacker) {
                // Melee
                config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.DAMAGE_DEALT, attacker.getName(), (int) event.getFinalDamage());
            } else if (event.getDamager() instanceof final Projectile projectile && projectile instanceof Arrow && projectile.getShooter() instanceof final Player attacker) {
                // Bow or Crossbow
                config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.DAMAGE_DEALT, attacker.getName(), (int) event.getFinalDamage());
            }
        }
    }

}
