package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_ENABLED;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_FREQUENCY;

public class UHCLoot {

    private static final List<Material> lowTier = List.of(
            Material.APPLE,
            Material.IRON_INGOT,
            Material.STRING,
            Material.COAL,
            Material.BOOK,
            Material.OBSIDIAN,
            Material.COOKED_BEEF,
            Material.NETHERITE_HOE,
            Material.BUCKET,
            Material.GUNPOWDER,
            Material.GOAT_HORN
    );
    private static final List<Material> midTier = List.of(
            Material.SADDLE,
            Material.GOLD_INGOT,
            Material.TNT,
            Material.SPYGLASS,
            Material.DIAMOND,
            Material.IRON_CHESTPLATE,
            Material.IRON_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_LEGGINGS,
            Material.ARROW,
            Material.BOOKSHELF,
            Material.EXPERIENCE_BOTTLE,
            Material.SPECTRAL_ARROW,
            Material.DIAMOND_HORSE_ARMOR,
            Material.ENDER_PEARL
    );
    private static final List<Material> highTier = List.of(
            Material.MACE,
            Material.BOW,
            Material.DIAMOND_AXE,
            Material.GOLDEN_APPLE,
//            Material.PLAYER_HEAD, // revivable
            Material.GOLD_BLOCK,
            Material.DIAMOND_BLOCK,
            Material.TRIDENT,
            Material.ELYTRA,
            Material.POTION,
            Material.SPLASH_POTION
    );

    public UHCLoot(final Config config) {
        final World world = config.getWorlds().getOverworld();
        final Integer chestX = config.getProperty(LOOT_CHEST_X);
        final Integer chestY = config.getProperty(LOOT_CHEST_Y);
        final Integer chestZ = config.getProperty(LOOT_CHEST_Z);
        final Integer lootFrequency = config.getProperty(LOOT_CHEST_FREQUENCY);

        final Block lootChestBlock = world.getBlockAt(chestX, chestY, chestZ);
        lootChestBlock.setType(Material.CHEST);
        final Chest lootChest = (Chest) lootChestBlock.getState();

        config.getManagedResources().runRepeatingTask(() -> {
            try {
                world.spawnParticle(Particle.ENCHANT, new Location(world, chestX + 0.5, chestY + 1.5, chestZ + 0.5), 1000);
            } catch (Exception e) {
                // noop
                // for some reason, this function is not implemented in MockBukkit but it is not worth us mocking
            }
            lootChest.getBlockInventory().clear();
            final Random random = new Random();
            for (int i = 0; i < 5; i++) {
                final int spin = random.nextInt(100) + 1;
                final Material itemToAdd;

                // 1% over 5 attempts == 5% of getting at least one high-tier
                if (spin == 1) {
                    itemToAdd = highTier.get(random.nextInt(highTier.size()));
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_7, 2, 1));
                // 10% over 5 attempts == 40% of getting at least one mid-tier
                } else if (spin < 10) {
                    itemToAdd = midTier.get(random.nextInt(midTier.size()));
                // 90% over 5 attempts == 99.99999% of getting at least one low-tier
                } else {
                    itemToAdd = lowTier.get(random.nextInt(lowTier.size()));
                }
                final ItemStack item = new ItemStack(itemToAdd);
                addEnchantments(item);
                multiplyItems(item);
                addPotionEffects(item);
                lootChest.getBlockInventory().addItem(item);
            }

        }, lootFrequency);
    }

    private void addEnchantments(ItemStack item) {
        switch (item.getType()) {
            case Material.DIAMOND_AXE -> item.addEnchantment(Enchantment.SHARPNESS, 1);
            case Material.BOW -> item.addEnchantment(Enchantment.INFINITY, 1);
            case Material.IRON_CHESTPLATE -> {
                item.addEnchantment(Enchantment.THORNS, 1);
                item.addEnchantment(Enchantment.PROTECTION, 2);
            }
            case Material.IRON_LEGGINGS -> {
                item.addEnchantment(Enchantment.PROTECTION, 2);
                item.addEnchantment(Enchantment.SWIFT_SNEAK, 1);
            }
            case Material.IRON_BOOTS -> {
                item.addEnchantment(Enchantment.FROST_WALKER, 1);
                item.addEnchantment(Enchantment.PROTECTION, 2);
            }
            case Material.IRON_HELMET -> {
                item.addEnchantment(Enchantment.RESPIRATION, 3);
                item.addEnchantment(Enchantment.PROTECTION, 1);
            }
            case Material.TRIDENT -> {
                item.addEnchantment(Enchantment.LOYALTY, 1);
            }
        }
    }

    private void multiplyItems(ItemStack item) {
        switch (item.getType()) {
            case Material.EXPERIENCE_BOTTLE -> item.setAmount(8);
            case Material.GOLD_INGOT -> item.setAmount(2);
            case Material.ARROW, Material.SPECTRAL_ARROW -> item.setAmount(32);
            case Material.TNT -> item.setAmount(4);
            case Material.GUNPOWDER -> item.setAmount(5);
            case Material.IRON_INGOT -> item.setAmount(4);
            case Material.OBSIDIAN -> item.setAmount(3);
            case Material.COAL -> item.setAmount(8);
            case Material.COOKED_BEEF -> item.setAmount(5);
            case Material.STRING -> item.setAmount(3);
        }
    }

    public void addPotionEffects(ItemStack item) {
        final Random random = new Random();
        if (item.getType() == Material.POTION) {
            final List<PotionType> effects = List.of(
                    PotionType.STRENGTH,
                    PotionType.FIRE_RESISTANCE,
                    PotionType.INVISIBILITY,
                    PotionType.SWIFTNESS
            );
            PotionType effect = effects.get(random.nextInt(effects.size()));
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionMeta.setBasePotionType(effect);
            item.setItemMeta(potionMeta);
        } else if (item.getType() == Material.SPLASH_POTION) {
            final List<PotionType> effects = List.of(
                    PotionType.HARMING,
                    PotionType.SLOWNESS,
                    PotionType.WEAKNESS
            );
            PotionType effect = effects.get(random.nextInt(effects.size()));
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionMeta.setBasePotionType(effect);
            item.setItemMeta(potionMeta);
        }
    }

    public static boolean isConfigured(Config config) {
        final boolean enabled = Boolean.TRUE.equals(config.getProperty(LOOT_CHEST_ENABLED));
        return enabled && config.getProperty(LOOT_CHEST_X) != null &&
                config.getProperty(LOOT_CHEST_X) != null &&
                config.getProperty(LOOT_CHEST_Y) != null &&
                config.getProperty(LOOT_CHEST_FREQUENCY) != null;
    }

    public static Optional<Location> getChestLocation(Config config) {
        final Integer chestX = config.getProperty(LOOT_CHEST_X);
        final Integer chestY = config.getProperty(LOOT_CHEST_Y);
        final Integer chestZ = config.getProperty(LOOT_CHEST_Z);
        if (chestX != null && chestY != null && chestZ != null) {
            return Optional.of(new Location(config.getWorlds().getOverworld(), chestX, chestY, chestZ));
        }
        return Optional.empty();
    }
}
