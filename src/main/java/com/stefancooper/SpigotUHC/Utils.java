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
    public static int secondsToTicks (int seconds) {
        return seconds * 20;
    }

}
