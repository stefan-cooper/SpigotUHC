package com.stefancooper.SpigotUHC;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Utils {

    public static World getWorld (String name) {
        final String worldName = name != null ? name : Defaults.DEFAULT_WORLD_NAME;
        if (Bukkit.getWorld(worldName) == null) return Bukkit.createWorld(WorldCreator.name(worldName).environment(World.Environment.NORMAL));
        else return Bukkit.getWorld(worldName);
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

    private static boolean testMode() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }

    public static String getResourceLocation(final String fileName) {
        if (testMode()) {
            return String.format("./src/test/java/resources/%s", fileName);
        } else {
            return String.format("./plugins/%s", fileName);
        }
    }

}
