package com.stefancooper.SpigotUHC;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Utils {

    public static World getWorld (String name) {
        if (Bukkit.getWorld(name) == null) return Bukkit.createWorld(WorldCreator.name(name).environment(World.Environment.NORMAL));
        else return Bukkit.getWorld(name);
    }

    // Some things are managed in Minecraft ticks. Use this to convert from seconds to ticks
    public static long secondsToTicks (int seconds) {
        return (long) seconds * 20;
    }

    /**
     * Calculation:
     *  Required fields: Initial Size, Final Size, Current Size
     *  -
     *  Shrink Total Distance = Initial - Final
     *  Shrink Progress = Initial - Current
     *  -
     *  Percentage = ( Total - Progress ) / Total
     */
    public static double calculateWorldBorderProgress (int initialSize, int finalSize, int currentSize) {
        int distanceToShrink = initialSize - finalSize;
        int progress = initialSize - currentSize;
        return (double) (distanceToShrink - progress) / distanceToShrink;
    }

}
