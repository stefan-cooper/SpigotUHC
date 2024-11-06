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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.List;
import java.util.Random;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_FREQUENCY;

public class UHCLoot {

    private final BukkitTask lootTask;

    private static final List<Material> lowTier = List.of(
            Material.APPLE
    );
    private static final List<Material> midTier = List.of(
            Material.SADDLE
    );
    private static final List<Material> highTier = List.of(
            Material.MACE
    );

    public UHCLoot(final Config config) {
        final World world = config.getWorlds().getOverworld();
        final int chestX = config.getProperty(LOOT_CHEST_X);
        final int chestY = config.getProperty(LOOT_CHEST_Y);
        final int chestZ = config.getProperty(LOOT_CHEST_Z);
        final int lootFrequency = config.getProperty(LOOT_FREQUENCY);

        final Block lootChestBlock = world.getBlockAt(chestX, chestY, chestZ);
        lootChestBlock.setType(Material.CHEST);
        final Chest lootChest = (Chest) lootChestBlock.getState();

        this.lootTask = config.getManagedResources().runRepeatingTask(() -> {
            world.spawnParticle(Particle.ENCHANT, new Location(world, chestX + 0.5, chestY + 1.5, chestZ + 0.5), 1000);
            lootChest.getBlockInventory().clear();
            final Random random = new Random();
            for (int i = 0; i < 3; i++) {
                final int spin = (int) (Math.random() * 101);
                final Material itemToAdd;
                if (spin < 5) {
                    itemToAdd = highTier.get(random.nextInt(highTier.size()));
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 2));
                } else if (spin < 20) {
                    itemToAdd = midTier.get(random.nextInt(midTier.size()));
                } else {
                    itemToAdd = lowTier.get(random.nextInt(midTier.size()));
                }
                final ItemStack item = new ItemStack(itemToAdd);
                lootChest.getBlockInventory().addItem(item);
            }

        }, lootFrequency);
    }

    public void cancelUHCLoot() {
        lootTask.cancel();
    }

}
