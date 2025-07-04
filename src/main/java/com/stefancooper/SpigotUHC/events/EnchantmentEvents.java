package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                Boolean.TRUE.equals(config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_HELMET))) {

            AdditionalEnchants enchants = new AdditionalEnchants(config);
            enchants.apply(item);
        }

        // Shield logic
        if (item.getType() == Material.SHIELD &&
                event.getExpLevelCost() >= 5 &&
                Boolean.TRUE.equals(config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_SHIELD))) {

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            boolean alreadyEnchanted = !item.getEnchantments().isEmpty();
            boolean hasLoreEnchant = meta.hasLore() && meta.getLore().stream().anyMatch(line -> {
                String stripped = ChatColor.stripColor(line).toLowerCase();
                return stripped.startsWith("knockback") || stripped.startsWith("thorns");
            });

            if (alreadyEnchanted || hasLoreEnchant) return;

            // Clear default enchantments to apply our own
            event.getEnchantsToAdd().clear();

            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

            boolean applyKnockback = new Random().nextBoolean();
            if (applyKnockback) {
                int level = 1 + new Random().nextInt(2);
                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, level);
                lore.add(ChatColor.GRAY + "Knockback " + romanNumeral(level));
            } else {
                int level = 1 + new Random().nextInt(3);
                item.addUnsafeEnchantment(Enchantment.THORNS, level);
                lore.add(ChatColor.GRAY + "Thorns " + romanNumeral(level));
            }

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Ensures glow without cluttering tooltip
            item.setItemMeta(meta);
        }

        // Trident logic
        if (item.getType() == Material.TRIDENT &&
                event.getExpLevelCost() >= 5 &&
                Boolean.TRUE.equals(config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_TRIDENT))) {

            AdditionalEnchants enchants = new AdditionalEnchants(config);
            enchants.apply(item);
        }
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() == Material.SHIELD &&
                Boolean.TRUE.equals(config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_SHIELD))) {

            ItemMeta meta = item.getItemMeta();
            boolean alreadyEnchanted = !item.getEnchantments().isEmpty();
            boolean hasLoreEnchant = meta != null && meta.hasLore() && meta.getLore().stream().anyMatch(line -> {
                String stripped = ChatColor.stripColor(line).toLowerCase();
                return stripped.startsWith("knockback") || stripped.startsWith("thorns");
            });

            // Block re-enchanting
            if (alreadyEnchanted || hasLoreEnchant) {
                for (int i = 0; i < event.getOffers().length; i++) {
                    event.getOffers()[i] = null;
                }
                return;
            }

            // Show dummy enchantment options to activate GUI
            event.getOffers()[0] = new EnchantmentOffer(Enchantment.UNBREAKING, 1, 5);
            event.getOffers()[1] = new EnchantmentOffer(Enchantment.THORNS, 1, 10);
            event.getOffers()[2] = new EnchantmentOffer(Enchantment.KNOCKBACK, 2, 20);

            event.setCancelled(false); // Ensure not blocked by other plugins
        }
    }

    private String romanNumeral(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(level);
        };
    }
}

