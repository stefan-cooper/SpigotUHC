package com.stefancooper.SpigotUHC.utils;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_ENABLED;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_VIA_ARMOR_STAND;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_VIA_PLATFORMS;

public class Utils {

    public static World getWorld (String name) {
        final String worldName = name != null ? name : Defaults.WORLD_NAME;
        if (Bukkit.getWorld(worldName) == null) return Bukkit.createWorld(WorldCreator.name(worldName).environment(World.Environment.NORMAL));
        else return Bukkit.getWorld(worldName);
    }

    public interface WorldBorderCallback {
        void execute(World world);
    }

    public static void setWorldEffects(List<World> worlds, WorldBorderCallback callback) {
        worlds.forEach(callback::execute);
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

    public static boolean testMode() {
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

    public static void spawnDustParticle(final World world, final Location location, final int count, final Particle.DustOptions dustOptions) {
        try {
            world.spawnParticle(Particle.DUST, location, count, 0.25, 0.25, 0.25, 0.0, dustOptions, true);
        } catch (Exception e) {
            // noop
            // for some reason, this function is not implemented in MockBukkit but it is not worth us mocking
        }
    }

    public static void spawnParticle(final World world, final Particle particle, final Location location, final int count) {
        try {
            world.spawnParticle(particle, location, count);
        } catch (Exception e) {
            // noop
            // for some reason, this function is not implemented in MockBukkit but it is not worth us mocking
        }
    }

    public static boolean isReviveViaPlatformsEnabled(final Config config) {
        boolean reviveEnabled = config.getProperty(REVIVE_ENABLED, Defaults.REVIVE_ENABLED);
        boolean reviveViaPlatforms = config.getProperty(REVIVE_VIA_PLATFORMS, Defaults.REVIVE_VIA_PLATFORMS);
        return reviveEnabled && reviveViaPlatforms;
    }

    public static boolean isReviveViaArmorStandEnabled(final Config config) {
        boolean reviveEnabled = config.getProperty(REVIVE_ENABLED, Defaults.REVIVE_ENABLED);
        boolean reviveViaPlatforms = config.getProperty(REVIVE_VIA_ARMOR_STAND, Defaults.REVIVE_VIA_ARMOR_STAND);
        return reviveEnabled && reviveViaPlatforms;
    }

    public static boolean checkOddsOf(final int odds, final int outOf) {
        Random random = new Random();
        return random.nextInt(outOf) < odds;
    }
}
