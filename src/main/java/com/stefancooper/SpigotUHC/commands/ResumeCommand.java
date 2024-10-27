package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.utils.Utils;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.types.BossBarBorder;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Optional;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.DIFFICULTY;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_SHRINKING_PERIOD;

public class ResumeCommand extends StartCommand {

    public static final String COMMAND_KEY = "resume";

    public ResumeCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        final World world = getConfig().getWorlds().getOverworld();
        final World nether = getConfig().getWorlds().getNether();
        final World end = getConfig().getWorlds().getEnd();
        getConfig().getManagedResources().cancelTimer();

        int minutesProgressed;
        if (getArgs().length > 0) {
            minutesProgressed = Integer.parseInt(getArgs()[0]);
        } else {
            minutesProgressed = 0;
        }

        // Actions on the player
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
        });

        Bukkit.setDefaultGameMode(GameMode.SURVIVAL);

        // Actions on the world
        Utils.setWorldEffects(List.of(world, nether, end), (cbWorld) -> world.getWorldBorder().setSize(Double.parseDouble(getConfig().getProp(WORLD_BORDER_INITIAL_SIZE.configName))) );
        Utils.setWorldEffects(List.of(getConfig().getWorlds().getOverworld(), getConfig().getWorlds().getNether(), getConfig().getWorlds().getEnd()), (cbWorld) -> {
            cbWorld.setDifficulty(Difficulty.valueOf(getConfig().getProp(DIFFICULTY.configName)));
        });

        if (Boolean.parseBoolean(getConfig().getProp(ConfigKey.WORLD_BORDER_IN_BOSSBAR.configName))) {
            BossBarBorder bossBarBorder = getConfig().getManagedResources().getBossBarBorder();
            Bukkit.getOnlinePlayers().forEach(player -> bossBarBorder.getBossBar().addPlayer(player));
            bossBarBorder.getBossBar().setVisible(true);
            getConfig().getManagedResources().runRepeatingTask(bossBarBorder.updateProgress(), 1);
        }

        Optional<String> gracePeriod = Optional.ofNullable(getConfig().getProp(GRACE_PERIOD_TIMER.configName));
        Optional<String> worldBorderGracePeriod = Optional.ofNullable(getConfig().getProp(WORLD_BORDER_GRACE_PERIOD.configName));

        worldBorderGracePeriod.ifPresent(s -> {
            int secondsProgressed = minutesProgressed * 60;
            int worldBorderGracePeriodTime = Integer.parseInt(s);
            if (secondsProgressed > worldBorderGracePeriodTime) {
                endWorldBorderGracePeriod(secondsProgressed - worldBorderGracePeriodTime);
            } else {
                getConfig().getManagedResources().runTaskLater(endWorldBorderGracePeriod(), Integer.parseInt(s) - (minutesProgressed * 60));
            }
        });
        gracePeriod.ifPresent(s -> {
            int secondsProgressed = minutesProgressed * 60;
            int gracePeriodTimer = Integer.parseInt(s);
            if (secondsProgressed > gracePeriodTimer) {
                endGracePeriod().run();
            } else {
                getConfig().getManagedResources().runTaskLater(endGracePeriod(), Integer.parseInt(s) - secondsProgressed);

            }
        });

        getConfig().getPlugin().setStarted(true);
    }

    protected void endWorldBorderGracePeriod (int progressedSeconds) {
        System.out.println("BORDER GRACE PERIOD OVER");
        String finalWorldBorderSize = getConfig().getProp(WORLD_BORDER_FINAL_SIZE.configName);
        String shrinkingTime = getConfig().getProp(WORLD_BORDER_SHRINKING_PERIOD.configName);
        Utils.setWorldEffects(List.of(getConfig().getWorlds().getOverworld(), getConfig().getWorlds().getNether(), getConfig().getWorlds().getEnd()), (cbWorld) -> {
            WorldBorder wb = cbWorld.getWorldBorder();
            wb.setDamageBuffer(5);
            wb.setDamageAmount(0.2);
            wb.setSize(Double.parseDouble(finalWorldBorderSize), Long.parseLong(shrinkingTime) - progressedSeconds);
        });
        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("World border shrinking", "Don't get caught..."));
    }
}
