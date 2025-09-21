package com.stefancooper.SpigotUHC.events;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.enchants.EnchantShield;
import com.stefancooper.SpigotUHC.enchants.EnchantTNT;
import com.stefancooper.SpigotUHC.enchants.PrepareShieldEnchant;
import com.stefancooper.SpigotUHC.enchants.PrepareTNTEnchant;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Level;

public class EnchantmentEvents implements Listener {

    private final Config config;

    public EnchantmentEvents(final Config config) {
        this.config = config;
    }

    @EventHandler
    public void onEnchantItem(final EnchantItemEvent event) throws Exception {
        final ItemStack item = event.getItem();

        // Shield enchants
        if (item.getType() == Material.SHIELD &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_SHIELD, Defaults.ADDITIONAL_ENCHANTS_SHIELD)) {
            final EnchantShield shieldEnchants = new EnchantShield(item, Map.of(event.getEnchantmentHint(), event.getLevelHint()));
            // Note - In the future, if base minecraft adds their own enchants for shields, we may want to remove this
            event.getEnchantsToAdd().clear();
            event.getEnchantsToAdd().putAll(shieldEnchants.getEnchantsToAdd());
        }

        // TNT enchants
        if (item.getType() == Material.TNT &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_TNT, Defaults.ADDITIONAL_ENCHANTS_TNT)) {
            final EnchantTNT shieldEnchants = new EnchantTNT(item, Map.of(event.getEnchantmentHint(), event.getLevelHint()));
            // Note - In the future, if base minecraft adds their own enchants for tnts, we may want to remove this
            event.getEnchantsToAdd().clear();
            event.getEnchantsToAdd().putAll(shieldEnchants.getEnchantsToAdd());
        }
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() == Material.SHIELD &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_SHIELD, Defaults.ADDITIONAL_ENCHANTS_SHIELD)) {

            final PrepareShieldEnchant prepareShieldEnchant = new PrepareShieldEnchant(item, event.getView().getEnchantmentSeed(), event.getEnchantmentBonus());

            if (prepareShieldEnchant.getOffers().length != 3 || prepareShieldEnchant.getOffers()[0] == null) return;

            event.getOffers()[0] = prepareShieldEnchant.getOffers()[0];
            event.getOffers()[1] = prepareShieldEnchant.getOffers()[1];
            event.getOffers()[2] = prepareShieldEnchant.getOffers()[2];

            event.setCancelled(false); // Allow GUI to appear for shields

            // force client to refresh inventory to show new offers
            config.getManagedResources().runTaskLater(() -> {
                event.getEnchanter().updateInventory();
            }, 1L);
            return;
        }

        if (item.getType() == Material.TNT &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_TNT, Defaults.ADDITIONAL_ENCHANTS_TNT)) {

            final PrepareTNTEnchant prepareTNTEnchant = new PrepareTNTEnchant(config, item, event.getView().getEnchantmentSeed(), event.getEnchantmentBonus());

            if (prepareTNTEnchant.getOffers().length != 3 || prepareTNTEnchant.getOffers()[0] == null) return;

            event.getOffers()[0] = prepareTNTEnchant.getOffers()[0];
            event.getOffers()[1] = prepareTNTEnchant.getOffers()[1];
            event.getOffers()[2] = prepareTNTEnchant.getOffers()[2];

            event.setCancelled(false); // Allow GUI to appear for TNT

            // force client to refresh inventory to show new offers
            config.getManagedResources().runTaskLater(() -> {
                event.getEnchanter().updateInventory();
            }, 1L);
        }
    }

    /* ----- Shield events ----- */

    @EventHandler
    public void onShieldBlock(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof final Player defender)) return;
        if (!(event.getDamager() instanceof final LivingEntity attacker)) return;
        if (!defender.isBlocking()) return;

        // Check both hands for a shield
        final ItemStack shield;
        final ItemStack offHand = defender.getInventory().getItemInOffHand();
        final ItemStack mainHand = defender.getInventory().getItemInMainHand();

        // If shield is in both off hand + main hand, use the off hand one
        if ((offHand.getType() == Material.SHIELD && mainHand.getType() == Material.SHIELD) || mainHand.getType() == Material.SHIELD) {
            shield = mainHand;
        } else if (offHand.getType() == Material.SHIELD) {
            shield = offHand;
        } else {
            // if not using a shield, get outta here
            return;
        }

        final int knockbackLevel = shield.getEnchantmentLevel(Enchantment.KNOCKBACK);
        final int thornsLevel = shield.getEnchantmentLevel(Enchantment.THORNS);

        // Apply Thorns damage
        if (thornsLevel > 0) {
            double damage = 1.0 + 0.5 * (thornsLevel - 1);
            attacker.damage(damage, defender);
            attacker.getWorld().spawnParticle(
                    Particle.DAMAGE_INDICATOR,
                    attacker.getLocation().add(0, 1, 0),
                    5 + 2 * thornsLevel
            );
        }

        // Apply Knockback with delay (to avoid Bukkit override)
        if (knockbackLevel > 0) {

            // Check if attacker and defender are facing each other
            final Vector attackerDir = attacker.getLocation().getDirection().normalize();
            final Vector defenderDir = defender.getLocation().getDirection().normalize();
            final Vector attackerToDefender = defender.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
            final Vector defenderToAttacker = attacker.getLocation().toVector().subtract(defender.getLocation().toVector()).normalize();
            final double attackerFacingVictim = attackerDir.dot(attackerToDefender);
            final double defenderFacingAttacker = defenderDir.dot(defenderToAttacker);

            final double threshold = 0.7; // adjust how strict the facing requirement is (1 = perfectly aligned)

            if (attackerFacingVictim > threshold && defenderFacingAttacker > threshold) {
                config.getManagedResources().runTaskLater(() -> {
                    Vector direction = attacker.getLocation().toVector()
                            .subtract(defender.getLocation().toVector())
                            .normalize()
                            .multiply(1);

                    attacker.setVelocity(direction.multiply(0.6 + 0.4 * knockbackLevel));
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        // if damage is not thorns, ignore
        if (event.getDamageSource().getDamageType() != DamageType.THORNS) return;
        // if damage is not from a player, ignore
        if (!(event.getDamageSource().getCausingEntity() instanceof final Player damager)) return;
        // if damager has thorns armor equipped, fair enough, ignore
        if (Arrays.stream(damager.getInventory().getArmorContents()).anyMatch(armor -> armor != null && armor.getEnchantments().containsKey(Enchantment.THORNS))) return;
        // cancel event because minecraft is trying to apply thorns damage even though the damager is not blocking
        if (!damager.isBlocking()) event.setCancelled(true);
    }

    // any tool/item with Knockback enchant will cause knockback when hitting
    // turn that off for the shield
    @EventHandler
    public void onEntityKnockbackEvent(EntityKnockbackByEntityEvent event) {
        // check that it is a player attacking
        if (!(event.getHitBy() instanceof final Player attacker)) return;
        final Entity defender = event.getEntity();

        // if the damage was caused by entity attack and by a shield, cancel the event
        if (event.getCause() == EntityKnockbackEvent.Cause.ENTITY_ATTACK &&
                attacker.getInventory().getItemInMainHand().getType() == Material.SHIELD &&
                attacker.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.KNOCKBACK)) {

            event.setKnockback(new Vector(0, 0, 0));
            config.getManagedResources().runTaskLater(() -> {
                final float yaw = attacker.getLocation().getYaw();
                final double x = -Math.sin(Math.toRadians(yaw)) * 0.2; // default knockback
                final double z =  Math.cos(Math.toRadians(yaw)) * 0.2;
                defender.setVelocity(new Vector(x, 0.3, z));
            }, 1L);

        }
    }

    /* ----- End of shield events ----- */

    /* ----- TNT Events ----- */

    private final Map<String, UUID> tntOwners = new HashMap<>();

    // check if item has quickboom enchant
    private boolean hasQuickboomEnchantment(final ItemStack item) {
        return item.containsEnchantment(config.getManagedResources().getQuickboomEnchantment());
    }

    private boolean hasBlastwaveEnchantment(final ItemStack item) {
        return item.containsEnchantment(config.getManagedResources().getBlastwaveEnchantment());
    }

    // get uuid from block
    private static String keyFromBlock(final Block b) {
        return b.getWorld().getUID() + ":" + b.getX() + "," + b.getY() + "," + b.getZ();
    }

    // apply correct fuse time to primed TNT
    private void handleQuickboom(final TNTPrimed tnt, final List<MetadataValue> metadata) {
        final int level = metadata.getFirst().asInt();
        switch (level) {
            case 4:
                tnt.setFuseTicks(10);
                break;
            case 3:
                tnt.setFuseTicks(20);
                break;
            case 2:
                tnt.setFuseTicks(30);
                break;
            case 1:
            default:
                tnt.setFuseTicks(40);
                break;
        }
    }

    // apply correct fuse time to primed TNT
    private void handleBlastwave(final TNTPrimed tnt, final List<MetadataValue> metadata) {
        final int level = metadata.getFirst().asInt();
        switch (level) {
            case 4:
                tnt.setYield(50f);
                break;
            case 3:
                tnt.setYield(20f);
                break;
            case 2:
                tnt.setYield(12f);
                break;
            case 1:
            default:
                tnt.setYield(8f);
                break;
        }
    }

    // save owners who place quickboom enchantment to memory so that we can make sure we dont damage them later
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.TNT) return;
        final ItemStack item = event.getItemInHand();
        if (hasQuickboomEnchantment(item) || hasBlastwaveEnchantment(item)) {
            final Player placer = event.getPlayer();
            final String key = keyFromBlock(event.getBlockPlaced());
            tntOwners.put(key, placer.getUniqueId());
            if (hasQuickboomEnchantment(item)) {
                event.getBlockPlaced().setMetadata(Constants.QUICKBOOM_ENCHANTMENT,
                        new FixedMetadataValue(config.getPlugin(),
                                item.getEnchantmentLevel(config.getManagedResources().getQuickboomEnchantment())
                        )
                );
            }
            if (hasBlastwaveEnchantment(item)) {
                event.getBlockPlaced().setMetadata(Constants.BLASTWAVE_ENCHANTMENT,
                        new FixedMetadataValue(config.getPlugin(),
                                item.getEnchantmentLevel(config.getManagedResources().getBlastwaveEnchantment())
                        )
                );
            }
        }

    }

    // when a tnt is primed, check our owners and set the source if it is in memory so that we can remove any damage
    @EventHandler
    public void onTNTAddToWorld(final EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed tnt)) return;

        final Location l = tnt.getLocation();
        final String key = l.getWorld().getUID() + ":" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
        final UUID ownerId = tntOwners.remove(key);

        if (ownerId != null) {
            final Player owner = Bukkit.getPlayer(ownerId);
            if (Bukkit.getPlayer(ownerId) != null) tnt.setSource(owner);
        }
    }

    // cancel damage if you placed an enchanted tnt
    @EventHandler
    public void onTNTDamage(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed tnt
            && event.getEntity() instanceof Player player
            && tnt.getSource() != null && tnt.getSource().equals(player)) {
                event.setCancelled(true);
        }
    }

    // when a tnt is primed, check if it has any enchantments we care about and apply any effects
    @EventHandler
    public void onTNTPrimeEvent(final TNTPrimeEvent event) {
        final Block block = event.getBlock();
        final List<MetadataValue> quickboomMetadata = block.getMetadata(Constants.QUICKBOOM_ENCHANTMENT);
        final List<MetadataValue> blastwaveMetadata = block.getMetadata(Constants.BLASTWAVE_ENCHANTMENT);
        if (!quickboomMetadata.isEmpty() || !blastwaveMetadata.isEmpty()) {
            Bukkit.getScheduler().runTask(config.getPlugin(), () -> {
                block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 1, 1, 1)
                    .stream()
                    .filter(ent -> ent instanceof TNTPrimed)
                    .map(ent -> (TNTPrimed) ent)
                    .forEach(tnt -> {
                        if (!quickboomMetadata.isEmpty()) handleQuickboom(tnt, quickboomMetadata);
                        if (!blastwaveMetadata.isEmpty()) handleBlastwave(tnt, blastwaveMetadata);
                    });
            });
        }
    }

    /* ----- End of TNT events ----- */
}
