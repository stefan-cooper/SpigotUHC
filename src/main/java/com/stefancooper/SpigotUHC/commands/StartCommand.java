package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.SPREAD_MAX_DISTANCE;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.COUNTDOWN_TIMER_LENGTH;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.SPREAD_MIN_DISTANCE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME;

public class StartCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "start";

    public StartCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {

        World world = Utils.getWorld(getConfig().getProp(WORLD_NAME.configName));

        // Actions on the player
        Bukkit.getOnlinePlayers().forEach(player -> {
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
            player.setHealth(maxHealth);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setExp(0);
            player.setWalkSpeed(0);
            player.setGameMode(GameMode.SURVIVAL);
        });

        // Actions on the world
        world.getWorldBorder().setSize(Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName)));
        world.setTime(1000);

        // Spread Players
        double centerX = Double.parseDouble(getConfig().getProp(WORLD_BORDER_CENTER_X.configName));
        double centerZ =  Double.parseDouble(getConfig().getProp(WORLD_BORDER_CENTER_Z.configName));
        double minDistance =  Double.parseDouble(getConfig().getProp(SPREAD_MIN_DISTANCE.configName));
        double maxDistance = Double.parseDouble(getConfig().getProp(SPREAD_MAX_DISTANCE.configName));
        String spreadCommand = String.format("spreadplayers %f %f %f %f true @a", centerX, centerZ, minDistance, maxDistance);
        getSender().getServer().dispatchCommand(getSender(), spreadCommand);

        // Timed actions
        Timer timer = new Timer();
        Optional<String> gracePeriod = Optional.ofNullable(getConfig().getProp(GRACE_PERIOD_TIMER.configName));
        Optional<String> worldBorderGracePeriod = Optional.ofNullable(getConfig().getProp(WORLD_BORDER_GRACE_PERIOD.configName));
        Optional<String> countdownTimer = Optional.ofNullable(getConfig().getProp(COUNTDOWN_TIMER_LENGTH.configName));

        countdownTimer.ifPresent(s -> {
            int remaining = Integer.parseInt(s);
            for (int i = 0; i <= remaining; i++) {
                if (i == 0) {
                    timer.schedule(countdown(i), 0L);
                } else {
                    timer.schedule(countdown(i), i * 1000L);
                }
            }
        });
        worldBorderGracePeriod.ifPresent(s -> timer.schedule(endWorldBorderGracePeriod(), Long.parseLong(s) * 1000));
        gracePeriod.ifPresent(s -> timer.schedule(endGracePeriod(), Long.parseLong(s) * 1000));
    }

    private TimerTask countdown(int remaining) {
        String countdownTimer = getConfig().getProp(COUNTDOWN_TIMER_LENGTH.configName);
        return new TimerTask() {
            @Override
            public void run() {
                int timeLeft = Integer.parseInt(countdownTimer) - remaining;
                if (timeLeft == 2) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Ready"));
                } else if (timeLeft == 1) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Set"));
                } else if (timeLeft == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Go!"));
                    Bukkit.getOnlinePlayers().forEach(player -> player.setWalkSpeed(0.2F));
                } else {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(Integer.toString(timeLeft), "Starting soon..."));
                }
            }
        };
    }

    private TimerTask endGracePeriod () {
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println("PVP GRACE PERIOD OVER");
                Utils.getWorld(getConfig().getProp(WORLD_NAME.configName)).setPVP(true);
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("Grace period over", "\uD83D\uDC40 Watch your back \uD83D\uDC40"));
            }
        };
    }

    private TimerTask endWorldBorderGracePeriod () {
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println("BORDER GRACE PERIOD OVER");
                String finalWorldBorderSize = getConfig().getProp(WORLD_BORDER_FINAL_SIZE.configName);
                String shrinkingTime = getConfig().getProp(WORLD_BORDER_SHRINKING_PERIOD.configName);
                Utils.getWorld(getConfig().getProp(WORLD_NAME.configName)).getWorldBorder().setSize(Double.parseDouble(finalWorldBorderSize), Long.parseLong(shrinkingTime));
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("World border shrinking", "Don't get caught..."));
            }
        };
    }
}
