package com.stefancooper.SpigotUHC.types;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomFinalLocation {
    final double centerX;
    final double centerZ;
    World world;

    public RandomFinalLocation(World world, int initialCenterX, int initialCenterZ, int initialWorldBorderSize) {
        Random random = new Random();
        int eitherSide = initialWorldBorderSize / 2;
        centerX = (random.nextInt((initialCenterX + eitherSide) - (initialCenterX - eitherSide)) + (initialCenterX - eitherSide));
        centerZ = (random.nextInt((initialCenterZ + eitherSide) - (initialCenterZ - eitherSide)) + (initialCenterZ - eitherSide));
        this.world = world;
    }

    public static ItemStack generateWorldCenterCompass() {
        ItemStack centerCompass = new ItemStack (Material.COMPASS);
        ItemMeta meta = centerCompass.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "Pointing at center");
        if (meta.hasLore()) {
            for (String l : meta.getLore()) {
                lore.add(l);
            }
        }
        meta.setLore(lore);
        centerCompass.setItemMeta(meta);
        return centerCompass;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public Location getLocation() {
        return new Location(world, centerX, 64, centerZ);
    }

}
