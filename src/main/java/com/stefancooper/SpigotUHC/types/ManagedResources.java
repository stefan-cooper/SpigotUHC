package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitScheduler;

import static com.stefancooper.SpigotUHC.resources.Constants.PLAYER_HEAD;

public class ManagedResources {

    final Config config;
    final BossBarBorder bossBarBorder;
    final BukkitScheduler scheduler;
    final NamespacedKey playerHead;

    public ManagedResources(Config config) {
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

}
