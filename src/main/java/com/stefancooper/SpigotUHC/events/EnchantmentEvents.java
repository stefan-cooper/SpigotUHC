package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.types.AdditionalEnchants;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.stefancooper.SpigotUHC.utils.Utils.romanNumeral;

public class EnchantmentEvents implements Listener {

    private final Config config;
    private final Random random = new Random();

    public EnchantmentEvents(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();

        // Helmet logic
        if (item.getType().name().endsWith("_HELMET") &&
                event.getExpLevelCost() >= 5 &&
                random.nextInt(10) < 2 &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_HELMET, Defaults.ADDITIONAL_ENCHANTS_HELMET)) {

            new AdditionalEnchants(config).apply(item);
            return;
        }

        // Shield logic
        if (item.getType() == Material.SHIELD &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_SHIELD, Defaults.ADDITIONAL_ENCHANTS_SHIELD)) {

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            boolean alreadyEnchanted = !item.getEnchantments().isEmpty();
            boolean hasLoreEnchant = meta.hasLore() && meta.getLore().stream().anyMatch(line -> {
                String stripped = ChatColor.stripColor(line).toLowerCase();
                return stripped.startsWith("knockback") || stripped.startsWith("thorns");
            });

            if (alreadyEnchanted || hasLoreEnchant) return;

            event.getEnchantsToAdd().clear();

            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

            item.addUnsafeEnchantment(event.getEnchantmentHint(), event.getLevelHint());
            if (event.getEnchantmentHint().equals(Enchantment.THORNS)) {
                lore.add(ChatColor.GRAY + "Thorns " + romanNumeral(event.getLevelHint()));
            } else if (event.getEnchantmentHint().equals(Enchantment.KNOCKBACK)) {
                lore.add(ChatColor.GRAY + "Knockback " + romanNumeral(event.getLevelHint()));
            }

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setEnchantmentGlintOverride(true);
            item.setItemMeta(meta);
            return;
        }

        // Trident logic
        if (item.getType() == Material.TRIDENT &&
                event.getExpLevelCost() >= 5 &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_TRIDENT, Defaults.ADDITIONAL_ENCHANTS_TRIDENT)) {

            new AdditionalEnchants(config).apply(item);
        }
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() == Material.SHIELD &&
                config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_SHIELD, Defaults.ADDITIONAL_ENCHANTS_SHIELD)) {

            ItemMeta meta = item.getItemMeta();
            boolean alreadyEnchanted = !item.getEnchantments().isEmpty();
            boolean hasLoreEnchant = meta != null && meta.hasLore() && meta.getLore().stream().anyMatch(line -> {
                String stripped = ChatColor.stripColor(line).toLowerCase();
                return stripped.startsWith("knockback") || stripped.startsWith("thorns");
            });

            if (alreadyEnchanted || hasLoreEnchant) {
                for (int i = 0; i < 3; i++) {
                    event.getOffers()[i] = null;
                }
                return;
            }

            final List<EnchantmentAndLevel> LOW_LEVEL_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentAndLevel(Enchantment.KNOCKBACK, 1),
                    new EnchantmentAndLevel(Enchantment.THORNS, 1)
            );

            final List<EnchantmentAndLevel> MID_LEVEL_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentAndLevel(Enchantment.KNOCKBACK, 1),
                    new EnchantmentAndLevel(Enchantment.KNOCKBACK, 2),
                    new EnchantmentAndLevel(Enchantment.THORNS, 1),
                    new EnchantmentAndLevel(Enchantment.THORNS, 2)
            );

            final List<EnchantmentAndLevel> HIGH_LEVEL_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentAndLevel(Enchantment.KNOCKBACK, 2),
                    new EnchantmentAndLevel(Enchantment.THORNS, 2),
                    new EnchantmentAndLevel(Enchantment.THORNS, 3)
            );

            final EnchantmentAndLevel lowOddsChoice = LOW_LEVEL_POSSIBLE_ENCHANTS.get(new Random().nextInt(0, LOW_LEVEL_POSSIBLE_ENCHANTS.size()));
            final EnchantmentAndLevel midOddsChoice = MID_LEVEL_POSSIBLE_ENCHANTS.get(new Random().nextInt(0, MID_LEVEL_POSSIBLE_ENCHANTS.size()));
            final EnchantmentAndLevel highOddsChoice = HIGH_LEVEL_POSSIBLE_ENCHANTS.get(new Random().nextInt(0, HIGH_LEVEL_POSSIBLE_ENCHANTS.size()));

            event.getOffers()[0] = new EnchantmentOffer(lowOddsChoice.enchantment, lowOddsChoice.level, 3);
            event.getOffers()[1] = new EnchantmentOffer(midOddsChoice.enchantment, midOddsChoice.level, 7);
            event.getOffers()[2] = new EnchantmentOffer(highOddsChoice.enchantment, highOddsChoice.level, 10);

            event.setCancelled(false); // Allow GUI to appear for shields
        }
    }

    private class EnchantmentAndLevel {
        public Enchantment enchantment;
        public int level;

        public EnchantmentAndLevel(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }
    }
}
