package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.enchants.EnchantShield;
import com.stefancooper.SpigotUHC.enchants.PrepareShieldEnchant;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class EnchantmentEvents implements Listener {

    private final Config config;
    private final Random random = new Random();

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
        }
    }

    // Shield events

    @EventHandler
    public void onShieldBlock(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
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
