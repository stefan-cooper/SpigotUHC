package com.stefancooper.SpigotUHC;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Utils {

    public static World getWorld (String name) {
        if (Bukkit.getWorld(name) == null) return Bukkit.createWorld(WorldCreator.name(name).environment(World.Environment.NORMAL));
        else return Bukkit.getWorld(name);
    }

}
