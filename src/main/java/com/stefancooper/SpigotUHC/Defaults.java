package com.stefancooper.SpigotUHC;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scoreboard.*;

import java.util.Properties;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME;

public class Defaults {

    private static String HEALTH_OBJECTIVE = "health";

    public static Properties createDefaultConfig() {
        final Properties defaults = new Properties();
        defaults.setProperty(WORLD_NAME.configName, "world");
        defaults.setProperty(WORLD_BORDER_INITIAL_SIZE.configName, "2000");
        return defaults;
    }

    public static void setDefaultGameRules(Config config) {
        final World world = Bukkit.getWorld(config.getProp(WORLD_NAME.configName));
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective healthObjective;
        if (board.getObjective(HEALTH_OBJECTIVE) == null) {
            healthObjective = board.getObjective(HEALTH_OBJECTIVE);
        } else {
            healthObjective = board.registerNewObjective(HEALTH_OBJECTIVE, Criteria.HEALTH, "Health");
        }
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        healthObjective.setRenderType(RenderType.HEARTS);
    }
}
