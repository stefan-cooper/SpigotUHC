package com.stefancooper.SpigotUHC.commands;

import java.util.List;
import java.util.Optional;

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

    public StartCommand(CommandSender sender, Command cmd, String[] args, Config config) {
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
        final int centerX = Integer.parseInt(getConfig().getProp(WORLD_BORDER_CENTER_X.configName));
        final int centerZ = Integer.parseInt(getConfig().getProp(WORLD_BORDER_CENTER_Z.configName));
        final double minDistance =  Double.parseDouble(getConfig().getProp(SPREAD_MIN_DISTANCE.configName));
        final double maxDistance =  Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName)) / 2;

        // World and Countdown timer are both configs that will always be set
        final int countdownTimer = Integer.parseInt(getConfig().getProp(COUNTDOWN_TIMER_LENGTH.configName));

        // Final Center Location
        final Location finalLocation;
        if (Boolean.parseBoolean(getConfig().getProp(RANDOM_FINAL_LOCATION.configName))) {
            final int initialWorldBorderSize = Integer.parseInt(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName));
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
            world.getWorldBorder().setSize(Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName)));
            world.getWorldBorder().setCenter(finalLocation.getX(), finalLocation.getZ());
            world.setTime(1000);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).forEach(Entity::remove);
            world.setGameRule(GameRule.FALL_DAMAGE, true);
        });

        // Actions on the player
        Bukkit.getOnlinePlayers().forEach(player -> {
            final double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue();
            player.resetMaxHealth();
            player.setHealth(maxHealth);
            player.setSaturation(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setExp(0);
            player.setLevel(0);
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(PotionEffectType.MINING_FATIGUE.createEffect((int) Utils.secondsToTicks(countdownTimer), 3));
            if (Boolean.parseBoolean(getConfig().getProp(RANDOM_FINAL_LOCATION.configName))) {
                player.getInventory().addItem(RandomFinalLocation.generateWorldCenterCompass());
                player.setCompassTarget(world.getWorldBorder().getCenter());
            }
        });

        // Spread players
        // spreadplayers <x> <z> <spreadDistance> <maxRange> <teams> <targets>
        // See: https://minecraft.fandom.com/wiki/Commands/spreadplayers
        final String spreadCommand = String.format("spreadplayers %f %f %f %f true @a", finalLocation.getX(), finalLocation.getZ(), minDistance, maxDistance);
        getSender().getServer().dispatchCommand(getSender(), spreadCommand);

        // Timed actions
        Optional<String> gracePeriod = Optional.ofNullable(getConfig().getProp(GRACE_PERIOD_TIMER.configName));
        Optional<String> worldBorderGracePeriod = Optional.ofNullable(getConfig().getProp(WORLD_BORDER_GRACE_PERIOD.configName));

        // World border grace period
        worldBorderGracePeriod.ifPresent(s -> getConfig().getManagedResources().runTaskLater(endWorldBorderGracePeriod(), Integer.parseInt(s)));
        // PVP Grace period
        gracePeriod.ifPresent(s -> getConfig().getManagedResources().runTaskLater(endGracePeriod(), Integer.parseInt(s)));

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
        if (Boolean.parseBoolean(getConfig().getProp(ConfigKey.WORLD_BORDER_IN_BOSSBAR.configName))) {
            BossBarBorder bossBarBorder = getConfig().getManagedResources().getBossBarBorder();
            Bukkit.getOnlinePlayers().forEach(player -> bossBarBorder.getBossBar().addPlayer(player));
            bossBarBorder.getBossBar().setVisible(true);
            getConfig().getManagedResources().runRepeatingTask(bossBarBorder.updateProgress(), 1);
        }

        // Timestamps
        if (Boolean.parseBoolean(getConfig().getProp(ConfigKey.ENABLE_TIMESTAMPS.configName))) {
            getConfig().getManagedResources().addTimestamp("UHC Started", false);
        }

        if (UHCLoot.isConfigured(getConfig())) {
            new UHCLoot(getConfig());
        }

        getConfig().getPlugin().setStarted(true);
    }

    protected Runnable countdown(int remaining, World world) {
        String countdownTimer = getConfig().getProp(COUNTDOWN_TIMER_LENGTH.configName);
        return () -> {
            int timeLeft = Integer.parseInt(countdownTimer) - remaining;
            if (timeLeft == 2) {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Ready"));
            } else if (timeLeft == 1) {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Set"));
            } else if (timeLeft == 0) {
                // Countdown over!
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Go!"));
                world.setDifficulty(Difficulty.valueOf(getConfig().getProp(DIFFICULTY.configName)));
                getConfig().getPlugin().setCountingDown(false);
            } else {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Starting soon..."));
            }
        };
    }

    protected Runnable endGracePeriod () {
        return () -> {
            System.out.println("PVP GRACE PERIOD OVER");
            Utils.setWorldEffects(List.of(getConfig().getWorlds().getOverworld(), getConfig().getWorlds().getNether(), getConfig().getWorlds().getEnd()), (cbWorld) -> cbWorld.setPVP(true));
            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("Grace period over", "\uD83D\uDC40 Watch your back \uD83D\uDC40"));
        };
    }

    protected Runnable endWorldBorderGracePeriod () {
        return () -> {
            System.out.println("BORDER GRACE PERIOD OVER");

            String finalWorldBorderSize = getConfig().getProp(WORLD_BORDER_FINAL_SIZE.configName);
            String shrinkingTime = getConfig().getProp(WORLD_BORDER_SHRINKING_PERIOD.configName);

            Utils.setWorldEffects(List.of(getConfig().getWorlds().getOverworld(), getConfig().getWorlds().getNether(), getConfig().getWorlds().getEnd()), (cbWorld) -> {
                WorldBorder wb = cbWorld.getWorldBorder();
                wb.setDamageBuffer(5);
                wb.setDamageAmount(0.2);
                wb.setSize(Double.parseDouble(finalWorldBorderSize), Long.parseLong(shrinkingTime));
            });

            if (Optional.ofNullable(getConfig().getProperty(WORLD_BORDER_Y_SHRINKING_PERIOD)).isPresent() &&
                    Optional.ofNullable(getConfig().getProperty(WORLD_BORDER_FINAL_Y)).isPresent() &&
                        Integer.parseInt(finalWorldBorderSize) <= MAXIMUM_FINAL_SIZE_FOR_Y_SHRINK) {
                getConfig().getManagedResources().runTaskLater(shrinkYBorderOverTime(), Integer.parseInt(shrinkingTime));
            }

            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("World border shrinking", "Don't get caught..."));
        };
    }

    protected Runnable shrinkYBorderOverTime() {
        return () -> {
            final int centerX = Integer.parseInt(getConfig().getProp(WORLD_BORDER_CENTER_X.configName));
            final int centerZ = Integer.parseInt(getConfig().getProp(WORLD_BORDER_CENTER_Z.configName));
            final int finalSize = Integer.parseInt(getConfig().getProp(WORLD_BORDER_FINAL_SIZE.configName));
            final int finalY = Integer.parseInt(getConfig().getProp(WORLD_BORDER_FINAL_Y.configName));
            final int shrinkTime = Integer.parseInt(getConfig().getProp(WORLD_BORDER_Y_SHRINKING_PERIOD.configName));
            final int interval = shrinkTime / (finalY + 64);

            int eitherSide = finalSize / 2;
            int corner1X = centerX + eitherSide;
            int corner2X = centerX - eitherSide;
            int corner1Z = centerZ + eitherSide;
            int corner2Z = centerZ - eitherSide;

            runner = getConfig().getManagedResources().runRepeatingTask(() -> {
                shrinkYBorderBlock++;
                final String fillCommand = String.format("fill %s %s %s %s %s %s minecraft:bedrock", corner1X, shrinkYBorderBlock, corner1Z, corner2X, shrinkYBorderBlock, corner2Z);
                getSender().getServer().dispatchCommand(Bukkit.getConsoleSender(), fillCommand);
                if (shrinkYBorderBlock >= finalY) {
                    runner.cancel();
                }
            }, interval);
        };
    }
}
