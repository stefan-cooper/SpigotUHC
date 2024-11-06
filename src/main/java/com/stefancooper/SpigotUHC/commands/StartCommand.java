package com.stefancooper.SpigotUHC.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.types.BossBarBorder;
import com.stefancooper.SpigotUHC.types.RandomFinalLocation;
import com.stefancooper.SpigotUHC.types.UHCLoot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.utils.Utils;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.COUNTDOWN_TIMER_LENGTH;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.DIFFICULTY;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_ENABLED;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_FINAL_LOCATION;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.SPREAD_MIN_DISTANCE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_SHRINKING_PERIOD;

public class StartCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "start";

    public StartCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
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

        // Actions on the world
        Bukkit.setDefaultGameMode(GameMode.SURVIVAL);
        Utils.setWorldEffects(List.of(world, nether, end), (cbWorld) -> {
            world.getWorldBorder().setSize(Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName)));
            world.getWorldBorder().setCenter(finalLocation.getX(), finalLocation.getZ());
            world.setTime(1000);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).forEach(Entity::remove);
        });

        // Actions on the player
        Bukkit.getOnlinePlayers().forEach(player -> {
            final double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
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

        if (getConfig().getProperty(LOOT_CHEST_ENABLED)) {
            UHCLoot loot = new UHCLoot(getConfig());
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

            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("World border shrinking", "Don't get caught..."));
        };
    }
}
