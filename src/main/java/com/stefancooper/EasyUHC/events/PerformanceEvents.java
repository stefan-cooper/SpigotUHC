package com.stefancooper.EasyUHC.events;

import com.stefancooper.EasyUHC.Config;
import com.stefancooper.EasyUHC.Defaults;
import com.stefancooper.EasyUHC.base.PerformanceTrackingEvent;
import com.stefancooper.EasyUHC.evolvingshield.EvolvingShield;
import com.stefancooper.EasyUHC.uhcloot.UHCLoot;
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
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.stefancooper.EasyUHC.base.ConfigKey.ENABLE_EVOLVING_SHIELDS;
import static com.stefancooper.EasyUHC.uhcloot.UHCLoot.getChestLocation;
import static com.stefancooper.EasyUHC.uhcloot.UHCLoot.isSameLocation;

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
                    final Player player = event.getPlayer();
                    lootChestLocations.add(chestLocation);
                    config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.LOOT_CHEST_CLAIMED, player.getName(), 1);
                    config.getManagedResources().addTimestamp(String.format("[UHC Loot] Loot chest claimed by %s", player.getName()));
                    if (config.getProperty(ENABLE_EVOLVING_SHIELDS, Defaults.ENABLE_EVOLVING_SHIELDS)) {
                        final Optional<ItemStack> getShield = EvolvingShield.getEvolvingShieldFromPlayer(config, player);
                        if (getShield.isPresent()) {
                            final ItemStack shield = getShield.get();
                            EvolvingShield.updateXP(
                                    config,
                                    shield,
                                    player,
                                    EvolvingShield.EvolvingShieldXPType.LOOT_CHEST
                            );
                        }
                    }
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
