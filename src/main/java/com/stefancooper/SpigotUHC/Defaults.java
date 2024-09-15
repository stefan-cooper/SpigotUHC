package com.stefancooper.SpigotUHC;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scoreboard.*;

import java.util.Properties;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.*;

public class Defaults {

    public static String HEALTH_OBJECTIVE = "health";
    public static String DEFAULT_WORLD_NAME = "world";
    public static String DEFAULT_WORLD_BORDER_INITIAL_SIZE = "2000";
    public static String DEFAULT_WORLD_BORDER_CENTER_X = "0";
    public static String DEFAULT_WORLD_BORDER_CENTER_Z = "0";

    public static Properties createDefaultConfig() {
        final Properties defaults = new Properties();
        defaults.setProperty(WORLD_NAME.configName, DEFAULT_WORLD_NAME);
        defaults.setProperty(WORLD_BORDER_INITIAL_SIZE.configName, DEFAULT_WORLD_BORDER_INITIAL_SIZE);
        defaults.setProperty(WORLD_BORDER_CENTER_X.configName, DEFAULT_WORLD_BORDER_CENTER_X);
        defaults.setProperty(WORLD_BORDER_CENTER_Z.configName, DEFAULT_WORLD_BORDER_CENTER_Z);
        return defaults;
    }

    public static void setDefaultGameRules(Config config) {
        final World world = Utils.getWorld(config.getProp(WORLD_NAME.configName));
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
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
