package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Properties;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.*;
import static com.stefancooper.SpigotUHC.enums.DeathAction.SPECTATE;
import static com.stefancooper.SpigotUHC.utils.Constants.MAXIMUM_FINAL_SIZE_FOR_Y_SHRINK;

public class Defaults {

    public static String HEALTH_OBJECTIVE = "health";
    public static String DEFAULT_WORLD_NAME = "world";
    public static String DEFAULT_NETHER_WORLD_NAME = "world_nether";
    public static String DEFAULT_END_WORLD_NAME = "world_the_end";
    public static String DEFAULT_WORLD_BORDER_INITIAL_SIZE = "2000";
    public static String DEFAULT_WORLD_BORDER_FINAL_SIZE = "150";
    public static String DEFAULT_WORLD_BORDER_SHRINKING_PERIOD = "7200";
    public static String DEFAULT_WORLD_BORDER_GRACE_PERIOD = "1800";
    public static String DEFAULT_WORLD_BORDER_CENTER_X = "0";
    public static String DEFAULT_WORLD_BORDER_CENTER_Z = "0";
    public static String DEFAULT_GRACE_PERIOD_TIMER = "600";
    public static String DEFAULT_ON_DEATH_ACTION = SPECTATE.name;
    public static String DEFAULT_COUNTDOWN_TIMER_LENGTH = "10";
    public static String DEFAULT_MIN_SPREAD_DISTANCE = "250";
    public static Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;

    public static Properties createDefaultConfig() {
        final Properties defaults = new Properties();
        defaults.setProperty(WORLD_NAME.configName, DEFAULT_WORLD_NAME);
        defaults.setProperty(WORLD_NAME_NETHER.configName, DEFAULT_NETHER_WORLD_NAME);
        defaults.setProperty(WORLD_NAME_END.configName, DEFAULT_END_WORLD_NAME);
        defaults.setProperty(WORLD_BORDER_INITIAL_SIZE.configName, DEFAULT_WORLD_BORDER_INITIAL_SIZE);
        defaults.setProperty(WORLD_BORDER_FINAL_SIZE.configName, DEFAULT_WORLD_BORDER_FINAL_SIZE);
        defaults.setProperty(WORLD_BORDER_SHRINKING_PERIOD.configName, DEFAULT_WORLD_BORDER_SHRINKING_PERIOD);
        defaults.setProperty(WORLD_BORDER_GRACE_PERIOD.configName, DEFAULT_WORLD_BORDER_GRACE_PERIOD);
        defaults.setProperty(WORLD_BORDER_CENTER_X.configName, DEFAULT_WORLD_BORDER_CENTER_X);
        defaults.setProperty(WORLD_BORDER_CENTER_Z.configName, DEFAULT_WORLD_BORDER_CENTER_Z);
        defaults.setProperty(GRACE_PERIOD_TIMER.configName, DEFAULT_GRACE_PERIOD_TIMER);
        defaults.setProperty(ON_DEATH_ACTION.configName, DEFAULT_ON_DEATH_ACTION);
        defaults.setProperty(COUNTDOWN_TIMER_LENGTH.configName, DEFAULT_COUNTDOWN_TIMER_LENGTH);
        defaults.setProperty(SPREAD_MIN_DISTANCE.configName, DEFAULT_MIN_SPREAD_DISTANCE);
        defaults.setProperty(DIFFICULTY.configName, DEFAULT_DIFFICULTY.name());
        return defaults;
    }

    public static void setDefaultGameRules(Config config) {
        Utils.setWorldEffects(List.of(config.getWorlds().getOverworld(), config.getWorlds().getNether(), config.getWorlds().getEnd()), (world) -> {
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT, MAXIMUM_FINAL_SIZE_FOR_Y_SHRINK * MAXIMUM_FINAL_SIZE_FOR_Y_SHRINK); // square the maximum final size to be used in y shrink
            // set pvp to false, will be enabled when /uhc start is ran
            world.setPVP(false);
        });
        config.getPlugin().setCountingDown(false);
        Bukkit.getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.ADVENTURE));
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective healthObjective;
        if (board.getObjective(HEALTH_OBJECTIVE) == null) {
            healthObjective = board.registerNewObjective(HEALTH_OBJECTIVE, Criteria.HEALTH, "Health");
        } else {
            healthObjective = board.getObjective(HEALTH_OBJECTIVE);
        }
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        healthObjective.setRenderType(RenderType.HEARTS);
    }
}
