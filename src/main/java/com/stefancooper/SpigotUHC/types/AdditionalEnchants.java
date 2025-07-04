package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdditionalEnchants {

    private final Config config;
    private static final String NIGHT_VISION_LORE = ChatColor.GRAY + "" + ChatColor.ITALIC + "Night Vision Goggles";

    public AdditionalEnchants(Config config) {
        this.config = config;
    }

    public void apply(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;

        Material type = item.getType();

        switch (type) {
            case SHIELD -> {
                if (config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_SHIELD, false)) {
                    // Block re-enchanting if already enchanted
                    if (!item.getEnchantments().isEmpty()) return;

                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;

                    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

                    boolean hasCustomShieldEnchant = lore.stream().anyMatch(line ->
                            ChatColor.stripColor(line).toLowerCase().startsWith("knockback") ||
                                    ChatColor.stripColor(line).toLowerCase().startsWith("thorns"));

                    if (hasCustomShieldEnchant) return;

                    boolean applyKnockback = new Random().nextBoolean();

                    if (applyKnockback) {
                        int knockbackLevel = 1 + new Random().nextInt(2); // Knockback I–II
                        item.addUnsafeEnchantment(Enchantment.KNOCKBACK, knockbackLevel);
                        lore.add(ChatColor.GRAY + "Knockback " + romanNumeral(knockbackLevel));
                    } else {
                        int thornsLevel = 1 + new Random().nextInt(3); // Thorns I–III
                        item.addUnsafeEnchantment(Enchantment.THORNS, thornsLevel);
                        lore.add(ChatColor.GRAY + "Thorns " + romanNumeral(thornsLevel));
                    }

                    meta.setLore(lore);

                    // Ensure the enchantment glow is visible
                    meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);

                    item.setItemMeta(meta);
                }
            }

            case TRIDENT -> {
                if (config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_TRIDENT, false)) {
                    item.addUnsafeEnchantment(Enchantment.CHANNELING, 1);

                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                        lore.add(ChatColor.LIGHT_PURPLE + "Ender Trident I"); // Enchant-like naming
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                }
            }

            case DIAMOND_HELMET, IRON_HELMET, NETHERITE_HELMET, GOLDEN_HELMET -> {
                if (config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_HELMET, false)) {
                    item.addEnchantment(Enchantment.RESPIRATION, 3);

                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                        if (!lore.contains(NIGHT_VISION_LORE)) {
                            lore.add(NIGHT_VISION_LORE);
                        }
                        meta.setLore(lore);
                        meta.setCustomModelData(3001); // Optional: use for resource pack visuals
                        item.setItemMeta(meta);
                    }
                }
            }

            case ARROW, SPECTRAL_ARROW, TIPPED_ARROW -> {
                if (config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_ARROWS, false)) {
                    // Placeholder — implement later if needed
                }
            }

            case ENCHANTED_GOLDEN_APPLE, GOLDEN_APPLE -> {
                if (config.getProperty(ConfigKey.ADDITIONAL_ENCHANTS_APPLE, false)) {
                    // Placeholder — implement later if needed
                }
            }

            default -> {
                // No additional enchantments for this material
            }
        }
    }

    public static boolean isNightVisionGoggles(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasLore() && meta.getLore().contains(NIGHT_VISION_LORE);
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
