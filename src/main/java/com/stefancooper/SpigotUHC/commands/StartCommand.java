package com.stefancooper.SpigotUHC.commands;

import java.util.List;
import java.util.Optional;

import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.types.BossBarBorder;
import com.stefancooper.SpigotUHC.types.RandomFinalLocation;
import com.stefancooper.SpigotUHC.types.UHCLoot;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.scheduler.BukkitTask;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.COUNTDOWN_TIMER_LENGTH;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.DIFFICULTY;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_FINAL_LOCATION;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.SPREAD_MIN_DISTANCE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_FINAL_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_Y_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.utils.Constants.MAXIMUM_FINAL_SIZE_FOR_Y_SHRINK;

public class StartCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "start";

    private static int shrinkYBorderBlock;
    private BukkitTask runner;

    public StartCommand(CommandSender sender, String cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        shrinkYBorderBlock = -64;

        // Worlds
        final World world = getConfig().getWorlds().getOverworld();
        final World nether = getConfig().getWorlds().getNether();
        final World end = getConfig().getWorlds().getEnd();

        // Config Values
        final int centerX = getConfig().getProperty(WORLD_BORDER_CENTER_X, Defaults.WORLD_BORDER_CENTER_X);
        final int centerZ = getConfig().getProperty(WORLD_BORDER_CENTER_Z, Defaults.WORLD_BORDER_CENTER_Z);
        final int minDistance = getConfig().getProperty(SPREAD_MIN_DISTANCE, Defaults.MIN_SPREAD_DISTANCE);
        final int maxDistance = getConfig().getProperty(WORLD_BORDER_INITIAL_SIZE, Defaults.WORLD_BORDER_INITIAL_SIZE) / 2;

        // World and Countdown timer are both configs that will always be set
        final int countdownTimer = getConfig().getProperty(COUNTDOWN_TIMER_LENGTH, Defaults.COUNTDOWN_TIMER_LENGTH);

        // Final Center Location
        final Location finalLocation;
        if (getConfig().getProperty(RANDOM_FINAL_LOCATION, Defaults.RANDOM_FINAL_LOCATION)) {
            final int initialWorldBorderSize = getConfig().getProperty(WORLD_BORDER_INITIAL_SIZE, Defaults.WORLD_BORDER_INITIAL_SIZE);
            final RandomFinalLocation location = new RandomFinalLocation(world, centerX, centerZ, initialWorldBorderSize);
            finalLocation = location.getLocation();
        } else {
            finalLocation = new Location(world, centerX, 64, centerZ);
        }

        // Wipe existing achievements
        getSender().getServer().dispatchCommand(getSender(), "advancement revoke @a everything");

        // Actions on the world
        Bukkit.setDefaultGameMode(GameMode.SURVIVAL);
        Utils.setWorldEffects(List.of(world, nether, end), (cbWorld) -> {
            world.getWorldBorder().setSize(getConfig().getProperty(WORLD_BORDER_INITIAL_SIZE, Defaults.WORLD_BORDER_INITIAL_SIZE));
            world.getWorldBorder().setCenter(finalLocation.getX(), finalLocation.getZ());
            world.setTime(1000);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).forEach(Entity::remove);
            world.setGameRule(GameRule.FALL_DAMAGE, true);
        });

        // Actions on the player
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.resetMaxHealth();
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue();
            player.setHealth(maxHealth);
            player.setSaturation(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setExp(0);
            player.setLevel(0);
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(PotionEffectType.MINING_FATIGUE.createEffect((int) Utils.secondsToTicks(countdownTimer), 3));
            player.addPotionEffect(PotionEffectType.REGENERATION.createEffect((int) Utils.secondsToTicks(countdownTimer) + (int) Utils.secondsToTicks(30), 3));
            if (getConfig().getProperty(RANDOM_FINAL_LOCATION, Defaults.RANDOM_FINAL_LOCATION)) {
                player.getInventory().addItem(RandomFinalLocation.generateWorldCenterCompass());
                player.setCompassTarget(world.getWorldBorder().getCenter());
            }
        });

        Bukkit.broadcastMessage("UHC: Countdown starting now. Don't forget to record your POV if you can. GLHF!");

        // Spread players
        // spreadplayers <x> <z> <spreadDistance> <maxRange> <teams> <targets>
        // See: https://minecraft.fandom.com/wiki/Commands/spreadplayers
        final String spreadCommand = String.format("spreadplayers %s %s %s %s true @a", finalLocation.getX(), finalLocation.getZ(), minDistance, maxDistance);
        getSender().getServer().dispatchCommand(getSender(), spreadCommand);

        // Timed actions
        int gracePeriod = getConfig().getProperty(GRACE_PERIOD_TIMER, Defaults.GRACE_PERIOD_TIMER);
        int worldBorderGracePeriod = getConfig().getProperty(WORLD_BORDER_GRACE_PERIOD, Defaults.WORLD_BORDER_GRACE_PERIOD);

        // World border grace period
        if (worldBorderGracePeriod > 0) getConfig().getManagedResources().runTaskLater(endWorldBorderGracePeriod(), worldBorderGracePeriod + countdownTimer);
        // PVP Grace period
        if (gracePeriod > 0) getConfig().getManagedResources().runTaskLater(endGracePeriod(), gracePeriod + countdownTimer);

        // Countdown timer
        for (int curr = 0; curr <= countdownTimer; curr++) {
            if (curr == 0) {
                getConfig().getPlugin().setCountingDown(true);
                getConfig().getManagedResources().runTaskLater(countdown(curr, world), 0);
            } else {
                getConfig().getManagedResources().runTaskLater(countdown(curr, world), curr);
            }
        }

        // World border boss bar
        if (getConfig().getProperty(ConfigKey.WORLD_BORDER_IN_BOSSBAR, Defaults.WORLD_BORDER_IN_BOSSBAR)) {
            BossBarBorder bossBarBorder = getConfig().getManagedResources().getBossBarBorder();
            Bukkit.getOnlinePlayers().forEach(player -> bossBarBorder.getBossBar().addPlayer(player));
            bossBarBorder.getBossBar().setVisible(true);
            getConfig().getManagedResources().runRepeatingTask(bossBarBorder.updateProgress(), 1);
        }

        // Timestamps
        if (getConfig().getProperty(ConfigKey.ENABLE_TIMESTAMPS, Defaults.ENABLE_TIMESTAMPS)) {
            getConfig().getManagedResources().addTimestamp("[Meta] UHC Started", false);
        }

        // UHC Loot
        if (UHCLoot.isConfigured(getConfig())) {
            getConfig().getManagedResources().runTaskLater(() -> new UHCLoot(getConfig()), countdownTimer);
        }

        getConfig().getPlugin().setStarted(true);
    }

    protected Runnable countdown(int remaining, World world) {
        int countdownTimer = getConfig().getProperty(COUNTDOWN_TIMER_LENGTH, Defaults.COUNTDOWN_TIMER_LENGTH);
        return () -> {
            int timeLeft = countdownTimer - remaining;
            if (timeLeft == 2) {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Ready", 10, 70, 20));
            } else if (timeLeft == 1) {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Set", 10, 70, 20));
            } else if (timeLeft == 0) {
                // Countdown over!
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Go!", 10, 70, 20));
                world.setDifficulty(getConfig().getProperty(DIFFICULTY, Defaults.DIFFICULTY));
                getConfig().getPlugin().setCountingDown(false);
            } else {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Starting soon...", 10, 70, 20));
            }
        };
    }

    protected Runnable endGracePeriod () {
        return () -> {
            Bukkit.broadcastMessage("UHC: PVP grace period is now over.");
            getConfig().getManagedResources().addTimestamp("[Meta] PVP grace period is now over.");
            Utils.setWorldEffects(List.of(getConfig().getWorlds().getOverworld(), getConfig().getWorlds().getNether(), getConfig().getWorlds().getEnd()), (cbWorld) -> cbWorld.setPVP(true));
            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("Grace period over", "\uD83D\uDC40 Watch your back \uD83D\uDC40", 10, 70, 20));
        };
    }

    protected Runnable endWorldBorderGracePeriod () {
        return () -> {

            int finalWorldBorderSize = getConfig().getProperty(WORLD_BORDER_FINAL_SIZE, Defaults.WORLD_BORDER_FINAL_SIZE);
            int shrinkingTime = getConfig().getProperty(WORLD_BORDER_SHRINKING_PERIOD, Defaults.WORLD_BORDER_SHRINKING_PERIOD);

            Utils.setWorldEffects(List.of(getConfig().getWorlds().getOverworld(), getConfig().getWorlds().getNether(), getConfig().getWorlds().getEnd()), (cbWorld) -> {
                WorldBorder wb = cbWorld.getWorldBorder();
                wb.setDamageBuffer(5);
                wb.setDamageAmount(0.2);
                wb.setSize(finalWorldBorderSize, shrinkingTime);
            });

            if (Optional.ofNullable(getConfig().getProperty(WORLD_BORDER_Y_SHRINKING_PERIOD)).isPresent() &&
                    Optional.ofNullable(getConfig().getProperty(WORLD_BORDER_FINAL_Y)).isPresent() &&
                        finalWorldBorderSize <= MAXIMUM_FINAL_SIZE_FOR_Y_SHRINK) {
                getConfig().getManagedResources().runTaskLater(shrinkYBorderOverTime(), shrinkingTime);
            }

            Bukkit.broadcastMessage("UHC: World Border shrink grace period is now over.");
            getConfig().getManagedResources().addTimestamp("[Meta] World Border shrink grace period is now over.");
            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("World border shrinking", "Don't get caught...", 10, 70, 20));
        };
    }

    protected Runnable shrinkYBorderOverTime() {
        return () -> {
            final int centerX = getConfig().getProperty(WORLD_BORDER_CENTER_X, Defaults.WORLD_BORDER_CENTER_X);
            final int centerZ = getConfig().getProperty(WORLD_BORDER_CENTER_Z, Defaults.WORLD_BORDER_CENTER_Z);
            final int finalSize = getConfig().getProperty(WORLD_BORDER_FINAL_SIZE, Defaults.WORLD_BORDER_FINAL_SIZE);
            final int finalY = getConfig().getProperty(WORLD_BORDER_FINAL_Y, Defaults.WORLD_BORDER_FINAL_Y);
            final int shrinkTime = getConfig().getProperty(WORLD_BORDER_Y_SHRINKING_PERIOD, Defaults.WORLD_BORDER_Y_SHRINKING_PERIOD);
            final int interval = shrinkTime / (finalY + 64);

            int eitherSide = finalSize / 2;
            int corner1X = centerX + eitherSide;
            int corner2X = centerX - eitherSide;
            int corner1Z = centerZ + eitherSide;
            int corner2Z = centerZ - eitherSide;

            if (shrinkTime > 0) {
                Bukkit.broadcastMessage("UHC: Y Border shrink grace period over.");
                getConfig().getManagedResources().addTimestamp("[Meta] Y Border shrink grace period over.");
                runner = getConfig().getManagedResources().runRepeatingTask(() -> {
                    shrinkYBorderBlock++;
                    final String fillCommand = String.format("fill %s %s %s %s %s %s minecraft:bedrock", corner1X, shrinkYBorderBlock, corner1Z, corner2X, shrinkYBorderBlock, corner2Z);
                    getSender().getServer().dispatchCommand(Bukkit.getConsoleSender(), fillCommand);
                    if (shrinkYBorderBlock >= finalY) {
                        runner.cancel();
                    }
                }, interval);
            }
        };
    }
}
