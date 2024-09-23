package com.stefancooper.SpigotUHC.commands;

import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import com.stefancooper.SpigotUHC.resources.ConfigKey;
import com.stefancooper.SpigotUHC.types.BossBarBorder;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.COUNTDOWN_TIMER_LENGTH;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.DIFFICULTY;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.SPREAD_MIN_DISTANCE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME;

public class StartCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "start";

    public StartCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {

        // World and Countdown timer are both configs that will always be set
        World world = Utils.getWorld(getConfig().getProp(WORLD_NAME.configName));
        int countdownTimer = Integer.parseInt(getConfig().getProp(COUNTDOWN_TIMER_LENGTH.configName));

        // Actions on the player
        Bukkit.getOnlinePlayers().forEach(player -> {
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
            player.setHealth(maxHealth);
            player.setSaturation(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setExp(0);
            player.setLevel(0);
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(PotionEffectType.MINING_FATIGUE.createEffect((int) Utils.secondsToTicks(countdownTimer), 3));
        });

        Bukkit.setDefaultGameMode(GameMode.SURVIVAL);

        // Actions on the world
        world.getWorldBorder().setSize(Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName)));
        world.setTime(1000);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).forEach(Entity::remove);

        // Spread Players
        double centerX = Double.parseDouble(getConfig().getProp(WORLD_BORDER_CENTER_X.configName));
        double centerZ =  Double.parseDouble(getConfig().getProp(WORLD_BORDER_CENTER_Z.configName));
        double minDistance =  Double.parseDouble(getConfig().getProp(SPREAD_MIN_DISTANCE.configName));
        double maxDistance =  Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName)) / 2;
        // spreadplayers <x> <z> <spreadDistance> <maxRange> <teams> <targets>
        // See: https://minecraft.fandom.com/wiki/Commands/spreadplayers
        String spreadCommand = String.format("spreadplayers %f %f %f %f true @a", centerX, centerZ, minDistance, maxDistance);
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

        if (Boolean.parseBoolean(getConfig().getProp(ConfigKey.WORLD_BORDER_IN_BOSSBAR.configName))) {
            BossBarBorder bossBarBorder = getConfig().getManagedResources().getBossBarBorder();
            Bukkit.getOnlinePlayers().forEach(player -> bossBarBorder.getBossBar().addPlayer(player));
            bossBarBorder.getBossBar().setVisible(true);
            getConfig().getManagedResources().runRepeatingTask(bossBarBorder.updateProgress(), 1);
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
            Utils.getWorld(getConfig().getProp(WORLD_NAME.configName)).setPVP(true);
            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("Grace period over", "\uD83D\uDC40 Watch your back \uD83D\uDC40"));
        };
    }

    protected Runnable endWorldBorderGracePeriod () {
        return () -> {
            System.out.println("BORDER GRACE PERIOD OVER");
            String finalWorldBorderSize = getConfig().getProp(WORLD_BORDER_FINAL_SIZE.configName);
            String shrinkingTime = getConfig().getProp(WORLD_BORDER_SHRINKING_PERIOD.configName);
            Utils.getWorld(getConfig().getProp(WORLD_NAME.configName)).getWorldBorder().setSize(Double.parseDouble(finalWorldBorderSize), Long.parseLong(shrinkingTime));
            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("World border shrinking", "Don't get caught..."));
        };
    }

    protected void endWorldBorderGracePeriod (int progressedSeconds) {
        System.out.println("BORDER GRACE PERIOD OVER");
        String finalWorldBorderSize = getConfig().getProp(WORLD_BORDER_FINAL_SIZE.configName);
        String shrinkingTime = getConfig().getProp(WORLD_BORDER_SHRINKING_PERIOD.configName);
        Utils.getWorld(getConfig().getProp(WORLD_NAME.configName)).getWorldBorder().setSize(Double.parseDouble(finalWorldBorderSize), Long.parseLong(shrinkingTime) - progressedSeconds);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("World border shrinking", "Don't get caught..."));
    }
}
