package com.stefancooper.EasyUHC;

import com.stefancooper.EasyUHC.enums.ConfigKey;
import com.stefancooper.EasyUHC.enums.PerformanceTrackingEvent;
import com.stefancooper.EasyUHC.types.BossBarBorder;
import com.stefancooper.EasyUHC.types.InstantRevive;
import com.stefancooper.EasyUHC.types.UHCTeam;
import com.stefancooper.EasyUHC.utils.Utils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static com.stefancooper.EasyUHC.utils.Constants.EVOLVING_SHIELD_USER_KEY;
import static com.stefancooper.EasyUHC.utils.Constants.EVOLVING_SHIELD_XP_KEY;
import static com.stefancooper.EasyUHC.utils.Constants.BLASTWAVE_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.CRAFTABLE_PLAYER_HEAD;
import static com.stefancooper.EasyUHC.utils.Constants.NOTCH_APPLE;
import static com.stefancooper.EasyUHC.utils.Constants.PERFORMANCE_TRACKING_LOCATION;
import static com.stefancooper.EasyUHC.utils.Constants.PLAYER_HEAD;
import static com.stefancooper.EasyUHC.utils.Constants.QUICKBOOM_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SPIGOT_NAMESPACE;
import static com.stefancooper.EasyUHC.utils.Constants.TIMESTAMPS_LOCATION;

public class ManagedResources {

    final Config config;
    final BossBarBorder bossBarBorder;
    final BukkitScheduler scheduler;
    final NamespacedKey playerHead;
    final NamespacedKey craftablePlayerHead;
    final NamespacedKey notchApple;
    final NamespacedKey quickboomEnchantment;
    final NamespacedKey blastwaveEnchantment;
    final NamespacedKey evolvingShieldUserKey;
    final NamespacedKey evolvingShieldXPKey;
    final JSONObject statistics;
    final List<UHCTeam> teams;
    Block dynamicLootChestLocation = null;
    Block dynamicNetherLootChestLocation = null;
    long startTime = System.currentTimeMillis();

    public ManagedResources(final Config config) {
        this.config = config;
        this.bossBarBorder = new BossBarBorder(config);
        this.scheduler = Bukkit.getScheduler();
        this.playerHead = new NamespacedKey(config.getPlugin(), PLAYER_HEAD);
        this.craftablePlayerHead = new NamespacedKey(config.getPlugin(), CRAFTABLE_PLAYER_HEAD);
        this.notchApple = new NamespacedKey(config.getPlugin(), NOTCH_APPLE);
        this.quickboomEnchantment = new NamespacedKey(SPIGOT_NAMESPACE, QUICKBOOM_ENCHANTMENT);
        this.blastwaveEnchantment = new NamespacedKey(SPIGOT_NAMESPACE, BLASTWAVE_ENCHANTMENT);
        this.evolvingShieldUserKey = new NamespacedKey(SPIGOT_NAMESPACE, EVOLVING_SHIELD_USER_KEY);
        this.evolvingShieldXPKey = new NamespacedKey(SPIGOT_NAMESPACE, EVOLVING_SHIELD_XP_KEY);
        this.teams = new ArrayList<>();
        final boolean fileAlreadyExists;
        JSONObject json = null;
        try {
            fileAlreadyExists = new File(PERFORMANCE_TRACKING_LOCATION).createNewFile();
            if (!fileAlreadyExists) {
                final String content = new String(Files.readAllBytes(Paths.get(PERFORMANCE_TRACKING_LOCATION)));
                json = new JSONObject(content);
            } else {
                json = new JSONObject();
                final FileWriter writer = new FileWriter(PERFORMANCE_TRACKING_LOCATION, false);
                writer.write("{}");
                writer.close();
            }
        } catch (final IOException e) {
            if (json == null) {
                json = new JSONObject();
            }
            config.getPlugin().getLogger().log(Level.WARNING, "Failed to create performance tracking file.");
        }
        statistics = json;

    }

    public void instantRevive(Player reviver, String revivee, ArmorStand armorStand) {
        new InstantRevive(config, reviver, revivee, true, armorStand);
        config.getManagedResources().addPerformanceTrackingEvent(PerformanceTrackingEvent.REVIVE, reviver.getName(), 1);
    }

    public BossBarBorder getBossBarBorder() {
        return bossBarBorder;
    }

    public BukkitTask runTaskLater(Runnable runnable, int time) {
        return scheduler.runTaskLater(config.getPlugin(), runnable, Utils.secondsToTicks(time));
    }

    public BukkitTask runTaskLater(Runnable runnable, long time) {
        return scheduler.runTaskLater(config.getPlugin(), runnable, time);
    }

    public BukkitTask runRepeatingTask(Runnable runnable, int interval) {
        return scheduler.runTaskTimer(config.getPlugin(), runnable, 0, Utils.secondsToTicks(interval));
    }

    public BukkitTask runRepeatingTask(Runnable runnable, long interval) {
        return scheduler.runTaskTimer(config.getPlugin(), runnable, 0, interval);
    }

    public void cancelRepeatingTask(int id) {
        scheduler.cancelTask(id);
    }

    public NamespacedKey getPlayerHeadKey() {
        return playerHead;
    }

    public NamespacedKey getNotchAppleKey() {
        return notchApple;
    }

    public NamespacedKey getEvolvingShieldUserKey() {
        return evolvingShieldUserKey;
    }

    public NamespacedKey getEvolvingShieldXPKey() {
        return evolvingShieldXPKey;
    }

    public Enchantment getQuickboomEnchantment() {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(quickboomEnchantment);
    }

    public Enchantment getBlastwaveEnchantment() {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(blastwaveEnchantment);
    }

    public NamespacedKey getCraftablePlayerHeadKey() {
        return craftablePlayerHead;
    }

    public void cancelTimer() {
        scheduler.cancelTasks(config.getPlugin());
    }

    public void addTimestamp(String event) {
        addTimestamp(event, true);
    }

    public void addTimestamp(String event, boolean append) {
        if (config.getProperty(ConfigKey.ENABLE_TIMESTAMPS, Defaults.ENABLE_TIMESTAMPS)) {
            try {
                new File(TIMESTAMPS_LOCATION).createNewFile();
                final FileWriter writer = new FileWriter(TIMESTAMPS_LOCATION, append);
                if (!append) {
                    startTime = System.currentTimeMillis();
                }
                long now = System.currentTimeMillis();
                long elapsed = now - startTime;
                long seconds = elapsed / 1000;
                long hours = seconds / 3600;
                long minutes = (seconds % 3600) / 60;
                long secs = seconds % 60;
                String formattedTimestamp = String.format("%02d:%02d:%02d", hours, minutes, secs);
                writer.write(String.format("%s : %s\n", formattedTimestamp, event));
                writer.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static final JSONObject DEFAULT_PERFORMANCE_VALUE = new JSONObject()
            .put(PerformanceTrackingEvent.PVE_DAMAGE.name, 0)
            .put(PerformanceTrackingEvent.DEATH.name, 0)
            .put(PerformanceTrackingEvent.DAMAGE_DEALT.name, 0)
            .put(PerformanceTrackingEvent.REVIVE.name, 0)
            .put(PerformanceTrackingEvent.GOLD_ORE_MINED.name, 0)
            .put(PerformanceTrackingEvent.KILL.name, 0)
            .put(PerformanceTrackingEvent.LOOT_CHEST_CLAIMED.name, 0)
            .put(PerformanceTrackingEvent.RANKING.name, 0);

    public void addPerformanceTrackingEvent(final PerformanceTrackingEvent event, final String player, final int value) {
        if (config.getProperty(ConfigKey.ENABLE_PERFORMANCE_TRACKING, Defaults.ENABLE_PERFORMANCE_TRACKING) && config.getPlugin().isUHCLive()) {
            try {
                final JSONObject playerStats = statistics.optJSONObject(player, new JSONObject(DEFAULT_PERFORMANCE_VALUE.toString()));
                final int currentValue = playerStats.getInt(event.name);
                playerStats.put(event.name, currentValue + value);
                statistics.put(player, playerStats);
            } catch (Exception ignored) {
                config.getPlugin().getLogger().log(Level.FINE, String.format("Error occurred updating performance event for %s - (%s: %s)", player, event.name, value));
            }
        } else {
            config.getPlugin().getLogger().log(Level.FINE, "Performance event triggered but the UHC has not started");
        }

    }

    public Runnable updatePerformanceStatistics() {
        return () -> {
            final FileWriter writer;
            try {
                writer = new FileWriter(PERFORMANCE_TRACKING_LOCATION, false);
                writer.write(statistics.toString(2));
                writer.close();
            } catch (Exception e) {
                config.getPlugin().getLogger().log(Level.WARNING, "Error occurred updating performance file");
            }
        };
    }

    public Block getDynamicLootChestLocation() { return dynamicLootChestLocation; }

    public void setDynamicLootChestLocation(final Block block) { dynamicLootChestLocation = block; }

    public Block getDynamicNetherLootChestLocation() { return dynamicNetherLootChestLocation; }

    public void setDynamicNetherLootChestLocation(final Block block) { dynamicNetherLootChestLocation = block; }

    public List<UHCTeam> getTeams() { return teams; }

    public void addTeam(final UHCTeam team) {
        teams.removeIf((existingTeam) -> existingTeam.getName().equals(team.getName()));
        teams.add(team);
    }

}
