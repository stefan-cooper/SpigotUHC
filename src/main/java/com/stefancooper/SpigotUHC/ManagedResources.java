package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.types.BossBarBorder;
import com.stefancooper.SpigotUHC.types.Revive;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Optional;

import static com.stefancooper.SpigotUHC.utils.Constants.PLAYER_HEAD;
import static com.stefancooper.SpigotUHC.utils.Constants.TIMESTAMPS_LOCATION;

public class ManagedResources {

    final Config config;
    final BossBarBorder bossBarBorder;
    final BukkitScheduler scheduler;
    final NamespacedKey playerHead;
    Revive currentRevive = null;
    BukkitTask reviveDebounce = null;


    public ManagedResources(final Config config) {
        this.config = config;
        this.bossBarBorder = new BossBarBorder(config);
        this.scheduler = Bukkit.getScheduler();
        this.playerHead = new NamespacedKey(config.getPlugin(), PLAYER_HEAD);
    }

    public Optional<Revive> getRevive() {
        return Optional.ofNullable(currentRevive);
    }

    public void startReviving(Player reviver, String revivee, ItemStack playerHead) {
        currentRevive = new Revive(config, reviver, revivee, playerHead, () -> currentRevive = null, reviveDebounce == null || reviveDebounce.isCancelled() );
        if (currentRevive.revivee == null) {
            currentRevive.cancelRevive();
            currentRevive = null;
        }
        if (reviveDebounce == null || reviveDebounce.isCancelled()) {
            reviveDebounce = runTaskLater(() -> reviveDebounce.cancel(), 30);
        }
    }

    public void cancelRevive() {
        currentRevive.cancelRevive();
        currentRevive = null;
    }

    public BossBarBorder getBossBarBorder() {
        return bossBarBorder;
    }

    public BukkitTask runTaskLater(Runnable runnable, int time) {
        return scheduler.runTaskLater(config.getPlugin(), runnable, Utils.secondsToTicks(time));
    }

    public BukkitTask runRepeatingTask(Runnable runnable, int interval) {
        return scheduler.runTaskTimer(config.getPlugin(), runnable, 0, Utils.secondsToTicks(interval));
    }

    public void cancelRepeatingTask(int id) {
        scheduler.cancelTask(id);
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
