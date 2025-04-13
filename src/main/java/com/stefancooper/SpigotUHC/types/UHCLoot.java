package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_ENABLED;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_HIGH_LOOT_ODDS;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_MID_LOOT_ODDS;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_SPINS_PER_GEN;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_X_RANGE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_FREQUENCY;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Z_RANGE;

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
            Material.GOAT_HORN,
            Material.EXPERIENCE_BOTTLE
    );
    private static final List<Material> midTier = List.of(
            Material.SADDLE,
            Material.TNT,
            Material.SPYGLASS,
            Material.DIAMOND,
            Material.IRON_CHESTPLATE,
            Material.IRON_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_LEGGINGS,
            Material.ARROW,
            Material.BOOKSHELF,
            Material.SPECTRAL_ARROW,
            Material.DIAMOND_HORSE_ARMOR,
            Material.ENDER_PEARL,
            Material.GOLD_BLOCK,
            Material.ANVIL,
            Material.POTION,
            Material.SPLASH_POTION
    );
    private static final List<Material> highTier = List.of(
            Material.MACE,
            Material.BOW,
            Material.DIAMOND_AXE,
            Material.PLAYER_HEAD, // revivable
            Material.DIAMOND_BLOCK,
            Material.TRIDENT,
            Material.ELYTRA,
            Material.POTION,
            Material.SPLASH_POTION
    );

    public UHCLoot(final Config config) {
        if (!UHCLoot.isConfigured(config)) return;
        final Integer lootFrequency = config.getProperty(LOOT_CHEST_FREQUENCY);
        final Integer chestX = config.getProperty(LOOT_CHEST_X);
        final Integer chestY = config.getProperty(LOOT_CHEST_Y);
        final Integer chestZ = config.getProperty(LOOT_CHEST_Z);
        final String chestXRange = config.getProperty(LOOT_CHEST_X_RANGE);
        final String chestZRange = config.getProperty(LOOT_CHEST_Z_RANGE);
        final Random random = new Random();

        config.getManagedResources().runRepeatingTask(() -> {
            boolean usingStaticLootChestLocation;
            if (chestX != null && chestY != null && chestZ != null) {
                usingStaticLootChestLocation = true;
                Bukkit.getLogger().log(Level.FINE, "SpigotUHC: Using static loot chest location");
            } else if (chestXRange != null && chestZRange != null) {
                usingStaticLootChestLocation = false;
                Bukkit.getLogger().log(Level.FINE, "SpigotUHC: Using dynamic loot chest location");
            } else {
                return;
            }
            final World world = config.getWorlds().getOverworld();

            final Block lootChestBlock;
            if (usingStaticLootChestLocation) {
                lootChestBlock = world.getBlockAt(chestX, chestY, chestZ);
            } else {
                final String[] chestXRangeList = chestXRange.split(",");
                final String[] chestZRangeList = chestZRange.split(",");
                final Block surfaceBlock = world.getHighestBlockAt(
                        random.nextInt(Integer.parseInt(chestXRangeList[0]), Integer.parseInt(chestXRangeList[1])),
                        random.nextInt(Integer.parseInt(chestZRangeList[0]), Integer.parseInt(chestZRangeList[1]))
                );
                lootChestBlock = world.getBlockAt(surfaceBlock.getX(), surfaceBlock.getY() + 1, surfaceBlock.getZ());
                BukkitTask beam = config.getManagedResources().runRepeatingTask(() -> {
                    for (int y = lootChestBlock.getY() + 3; y < 256; y += 3) {
                        Utils.spawnDustParticle(world, new Location(world, lootChestBlock.getLocation().getX() + 0.5, y, lootChestBlock.getZ() + 0.5), 10, new Particle.DustOptions(Color.FUCHSIA, 100));
                    }
                }, 2);
                config.getManagedResources().setDynamicLootChestLocation(lootChestBlock);
                config.getManagedResources().runTaskLater(() -> {
                    lootChestBlock.setType(Material.AIR);
                    config.getManagedResources().cancelRepeatingTask(beam.getTaskId());
                }, lootFrequency);
            }

            lootChestBlock.setType(Material.CHEST);
            final Chest lootChest = (Chest) lootChestBlock.getState();

            Integer spawnRate = config.getProperty(LOOT_CHEST_SPINS_PER_GEN);
            Integer highLootOdds = config.getProperty(LOOT_CHEST_HIGH_LOOT_ODDS);
            Integer midLootOdds = config.getProperty(LOOT_CHEST_MID_LOOT_ODDS);

            if (highLootOdds == null) highLootOdds = 5;
            if (midLootOdds == null) midLootOdds = 40;
            if (spawnRate == null) spawnRate = 5;

            final int finalSpawnRate = spawnRate;
            final int finalHighLootOdds = highLootOdds;
            final int finalMidLootOdds = midLootOdds + finalHighLootOdds;

            Utils.spawnParticle(world, Particle.ENCHANT, new Location(world, lootChest.getX() + 0.5, lootChest.getY() + 1.5, lootChest.getZ() + 0.5), 1000);
            lootChest.getBlockInventory().clear();
            for (int i = 0; i < finalSpawnRate; i++) {
                final int spin = random.nextInt(100) + 1;
                final Material itemToAdd;
                Tier tier;

                if (spin < finalHighLootOdds) {
                    itemToAdd = highTier.get(random.nextInt(highTier.size()));
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_7, 2, 1));
                    tier = Tier.HIGH;
                } else if (spin < finalMidLootOdds) {
                    itemToAdd = midTier.get(random.nextInt(midTier.size()));
                    tier = Tier.MID;
                } else {
                    itemToAdd = lowTier.get(random.nextInt(lowTier.size()));
                    tier = Tier.LOW;
                }
                final ItemStack item = new ItemStack(itemToAdd);
                addEnchantments(item);
                multiplyItems(item);
                addPotionEffects(item, tier);
                lootChest.getBlockInventory().addItem(item);
            }

        }, lootFrequency);
    }

    private void addEnchantments(ItemStack item) {
        switch (item.getType()) {
            case Material.DIAMOND_AXE -> item.addEnchantment(Enchantment.SHARPNESS, 1);
            case Material.BOW -> item.addEnchantment(Enchantment.INFINITY, 1);
            case Material.IRON_CHESTPLATE -> {
                item.addEnchantment(Enchantment.THORNS, 2);
                item.addEnchantment(Enchantment.PROTECTION, 3);
            }
            case Material.IRON_LEGGINGS -> {
                item.addEnchantment(Enchantment.PROTECTION, 2);
                item.addEnchantment(Enchantment.SWIFT_SNEAK, 3);
            }
            case Material.IRON_BOOTS -> {
                item.addEnchantment(Enchantment.FEATHER_FALLING, 3);
                item.addEnchantment(Enchantment.PROTECTION, 2);
            }
            case Material.IRON_HELMET -> {
                item.addEnchantment(Enchantment.RESPIRATION, 3);
                item.addEnchantment(Enchantment.PROTECTION, 2);
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
            case Material.DIAMOND -> item.setAmount(3);
        }
    }

    public void addPotionEffects(ItemStack item, Tier tier) {
        final Random random = new Random();
        final List<PotionType> effects;
        if (item.getType() == Material.POTION) {
            if (tier == Tier.HIGH) {
                effects = List.of(
                        PotionType.STRENGTH,
                        PotionType.STRONG_HEALING
                );
            } else {
                effects = List.of(
                        PotionType.FIRE_RESISTANCE,
                        PotionType.INVISIBILITY,
                        PotionType.SWIFTNESS
                );
            }
            PotionType effect = effects.get(random.nextInt(effects.size()));
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionMeta.setBasePotionType(effect);
            item.setItemMeta(potionMeta);
        } else if (item.getType() == Material.SPLASH_POTION) {
            if (tier == Tier.HIGH) {
                effects = List.of(
                        PotionType.STRONG_HARMING,
                        PotionType.STRONG_HEALING
                );
            } else {
                effects = List.of(
                        PotionType.STRONG_SLOWNESS,
                        PotionType.LONG_WEAKNESS
                );
            }
            PotionType effect = effects.get(random.nextInt(effects.size()));
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionMeta.setBasePotionType(effect);
            item.setItemMeta(potionMeta);
        }
    }

    public static boolean isConfigured(Config config) {
        final boolean enabled = Boolean.TRUE.equals(config.getProperty(LOOT_CHEST_ENABLED));
        return enabled && (config.getProperty(LOOT_CHEST_X) != null &&
                config.getProperty(LOOT_CHEST_Y) != null &&
                config.getProperty(LOOT_CHEST_Z) != null) ||
                (config.getProperty(LOOT_CHEST_X_RANGE) != null &&
                config.getProperty(LOOT_CHEST_Z_RANGE) != null) &&
                config.getProperty(LOOT_CHEST_FREQUENCY) != null;
    }

    public static Optional<Location> getChestLocation(Config config) {
        final Integer chestX = config.getProperty(LOOT_CHEST_X);
        final Integer chestY = config.getProperty(LOOT_CHEST_Y);
        final Integer chestZ = config.getProperty(LOOT_CHEST_Z);
        if (config.getManagedResources().getDynamicLootChestLocation() != null) {
            return Optional.of(config.getManagedResources().getDynamicLootChestLocation().getLocation());
        } else if (chestX != null && chestY != null && chestZ != null) {
            return Optional.of(new Location(config.getWorlds().getOverworld(), chestX, chestY, chestZ));
        }
        return Optional.empty();
    }

    public enum Tier {
        HIGH,
        MID,
        LOW
    }
}
