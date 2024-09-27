package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import static com.stefancooper.SpigotUHC.resources.Constants.PLAYER_HEAD;
import static com.stefancooper.SpigotUHC.resources.Constants.TIMESTAMPS_LOCATION;

public class ManagedResources {

    final Config config;
    final BossBarBorder bossBarBorder;
    final BukkitScheduler scheduler;
    final NamespacedKey playerHead;

    public ManagedResources(final Config config) {
        this.config = config;
        this.bossBarBorder = new BossBarBorder(config);
        this.scheduler = Bukkit.getScheduler();
        this.playerHead = new NamespacedKey(config.getPlugin(), PLAYER_HEAD);
    }

    public BossBarBorder getBossBarBorder() {
        return bossBarBorder;
    }

    public void runTaskLater(Runnable runnable, int time) {
        scheduler.runTaskLater(config.getPlugin(), runnable, Utils.secondsToTicks(time));
    }

    public void runRepeatingTask(Runnable runnable, int interval ) {
        scheduler.scheduleSyncRepeatingTask(config.getPlugin(), runnable, 0, Utils.secondsToTicks(interval));
    }

    public NamespacedKey getPlayerHeadKey() {
        return playerHead;
    }

    public void cancelTimer() {
        scheduler.cancelTasks(config.getPlugin());
    }

    public void addTimestamp(String event) {
        addTimestamp(event, true);
    }

    public void addTimestamp(String event, boolean append) {
        try {
            new File(TIMESTAMPS_LOCATION).createNewFile();
            FileWriter writer = new FileWriter(TIMESTAMPS_LOCATION, append);
            writer.write(String.format("%s : %s\n", new Date(), event));
            writer.close();
        } catch (Exception ignored) {}
    }

}
