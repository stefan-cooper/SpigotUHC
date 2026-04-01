package com.stefancooper.EasyUHC.events;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.stefancooper.EasyUHC.Config;
import com.stefancooper.EasyUHC.Defaults;
import com.stefancooper.EasyUHC.enums.PerformanceTrackingEvent;
import com.stefancooper.EasyUHC.types.EvolvingShield;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import static com.stefancooper.EasyUHC.enums.ConfigKey.ENABLE_EVOLVING_SHIELDS;

public class EvolvingShieldEvents implements Listener {

    private final Config config;

    public EvolvingShieldEvents(final Config config) {
        this.config = config;
    }

    // ----- Interacting with Shield Events -----

    @EventHandler
    // Cancel events that drop the evolving shield
    public void onDrop(final PlayerDropItemEvent event) {
        final ItemStack item = event.getItemDrop().getItemStack();

        if (EvolvingShield.isEvolvingShield(config, item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    // Cancel events that attempt to put the evolving shield in chests
    public void onInventoryClick(final InventoryClickEvent event) {
        final Inventory top = event.getView().getTopInventory();

        // Prevent placing into container with cursor ---
        if (event.getClickedInventory() == top) {
            final ItemStack cursor = event.getCursor();

            if (EvolvingShield.isEvolvingShield(config, cursor)) {
                event.setCancelled(true);
                return;
            }
        }

        // Prevent shift-click into container ---
        if (event.isShiftClick()) {
            final ItemStack current = event.getCurrentItem();

            // Shift-click from player inventory → goes into top inventory
            if (top.getType() != InventoryType.CRAFTING && event.getClickedInventory() == event.getView().getBottomInventory()) {
                if (EvolvingShield.isEvolvingShield(config, current)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Prevent number key swaps into container ---
        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked()
                    .getInventory()
                    .getItem(event.getHotbarButton());

            if (EvolvingShield.isEvolvingShield(config, hotbarItem)
                    && event.getClickedInventory() == top) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    // Delete evolving shields on death
    public void onDeath(final PlayerDeathEvent event) {
        event.getDrops().removeIf(drop -> EvolvingShield.isEvolvingShield(config, drop));
    }

    @EventHandler
    // Cancel events that attempt to put the shield in an item frame
    public void onInteract(final PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        if (EvolvingShield.isEvolvingShield(config, event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }
    }

    // ----- XP EVENTS -----
    @EventHandler
    public void onXPPickup(final PlayerPickupExperienceEvent event) {
        if (config.getProperty(ENABLE_EVOLVING_SHIELDS, Defaults.ENABLE_EVOLVING_SHIELDS)) {
            final Player player = event.getPlayer();
            final Optional<ItemStack> getShield = EvolvingShield.getEvolvingShieldFromPlayer(config, player);
            if (getShield.isPresent()) {
                final ItemStack shield = getShield.get();
                EvolvingShield.updateXP(
                        config,
                        shield,
                        EvolvingShield.calculateXP(EvolvingShield.EvolvingShieldXPType.EXPERIENCE, event.getExperienceOrb().getExperience()),
                        player
                );
            } else {
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        // if damage is thorns, ignore
        if (event.getDamageSource().getDamageType() == DamageType.THORNS) {
            return;
        }
        if (config.getProperty(ENABLE_EVOLVING_SHIELDS, Defaults.ENABLE_EVOLVING_SHIELDS) && event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof final Player attacker) {
                // Melee
                final Optional<ItemStack> getShield = EvolvingShield.getEvolvingShieldFromPlayer(config, attacker);
                if (getShield.isPresent()) {
                    final ItemStack shield = getShield.get();
                    EvolvingShield.updateXP(
                            config,
                            shield,
                            EvolvingShield.calculateXP(EvolvingShield.EvolvingShieldXPType.DAMAGE_HEARTS, (int) Math.round(event.getFinalDamage())),
                            attacker
                    );
                }
            } else if (event.getDamager() instanceof final Projectile projectile && projectile instanceof Arrow && projectile.getShooter() instanceof final Player attacker) {
                // Bow or Crossbow
                final Optional<ItemStack> getShield = EvolvingShield.getEvolvingShieldFromPlayer(config, attacker);
                if (getShield.isPresent()) {
                    final ItemStack shield = getShield.get();
                    EvolvingShield.updateXP(
                            config,
                            shield,
                            EvolvingShield.calculateXP(EvolvingShield.EvolvingShieldXPType.DAMAGE_HEARTS, (int) Math.round(event.getFinalDamage())),
                            attacker
                    );
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.getEntity().getLastDamageCause() != null &&
                event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity() != null &&
                event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity().getType() == EntityType.PLAYER
        ) {
            final Player killer = (Player) event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity();
            final Optional<ItemStack> getShield = EvolvingShield.getEvolvingShieldFromPlayer(config, killer);
            if (getShield.isPresent()) {
                final ItemStack shield = getShield.get();
                EvolvingShield.updateXP(
                        config,
                        shield,
                        EvolvingShield.calculateXP(EvolvingShield.EvolvingShieldXPType.KILL, 1),
                        killer
                );
            }
        }
    }



    /**
     * TODO
     * - Give XP on damage - needs manual testing
     * - Give XP on kills - needs manual testing
     * - Update enchantments for XP as it goes up
     * - Destroy item on death - done
     * - Prevent item from being dropped - done
     * - Prevent item from being put in chest
     * - New enchantments
     *
     */
}
