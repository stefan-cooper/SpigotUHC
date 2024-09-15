package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.types.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;

import java.util.List;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.*;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME;

public class ConfigParser {

    private final Config config;

    public ConfigParser(Config config) {
        this.config = config;
    }

    public Configurable<?> propertyToConfigurable(String key, String value) {
        return switch (fromString(key)) {
            case WORLD_BORDER_INITIAL_SIZE -> new Configurable<>(WORLD_BORDER_INITIAL_SIZE, Double.parseDouble(value));
            case WORLD_BORDER_FINAL_SIZE -> new Configurable<>(WORLD_BORDER_FINAL_SIZE, Double.parseDouble(value));
            case WORLD_BORDER_SHRINKING_PERIOD -> new Configurable<>(WORLD_BORDER_SHRINKING_PERIOD, Double.parseDouble(value));
            case WORLD_BORDER_GRACE_PERIOD -> new Configurable<>(WORLD_BORDER_GRACE_PERIOD, Double.parseDouble(value));
            case WORLD_BORDER_CENTER_X -> new Configurable<>(WORLD_BORDER_CENTER_X, Double.parseDouble(value));
            case WORLD_BORDER_CENTER_Z -> new Configurable<>(WORLD_BORDER_CENTER_Z, Double.parseDouble(value));
            case RANDOM_TEAMS_ENABLED -> new Configurable<>(RANDOM_TEAMS_ENABLED, Boolean.parseBoolean((value)));
            case RANDOM_TEAM_SIZE -> new Configurable<>(RANDOM_TEAM_SIZE, Double.parseDouble(value));
            case TEAM_RED -> new Configurable<>(TEAM_RED, value);
            case TEAM_YELLOW -> new Configurable<>(TEAM_YELLOW, value);
            case TEAM_GREEN -> new Configurable<>(TEAM_GREEN, value);
            case TEAM_BLUE -> new Configurable<>(TEAM_BLUE, value);
            case TEAM_ORANGE -> new Configurable<>(TEAM_ORANGE, value);
            case GRACE_PERIOD_TIMER -> new Configurable<>(GRACE_PERIOD_TIMER, Double.parseDouble(value));
            case ON_DEATH_ACTION -> new Configurable<>(ON_DEATH_ACTION, value);
            case COUNTDOWN_TIMER_LENGTH -> new Configurable<>(COUNTDOWN_TIMER_LENGTH, Double.parseDouble(value));
            case WORLD_NAME -> new Configurable<>(WORLD_NAME, value);
            case null -> null;

        };
    }



    public void executeConfigurable(Configurable<?> configurable) {
        if (configurable == null) {
            System.out.println("Invalid config value attempted to be executed, ignoring...");
            return;
        }
        WorldBorder worldBorder = Utils.getWorld(config.getProp(WORLD_NAME.configName)).getWorldBorder();
        switch (configurable.key()) {
            case WORLD_BORDER_INITIAL_SIZE:
                Double newWorldBorderSize = (Double) configurable.value();
                worldBorder.setSize(newWorldBorderSize);
                break;
            case WORLD_BORDER_CENTER_X:
                Double newWorldCenterX = (Double) configurable.value();
                double worldCenterZ = worldBorder.getCenter().getZ();
                worldBorder.setCenter(newWorldCenterX, worldCenterZ);
                break;
            case WORLD_BORDER_CENTER_Z:
                Double newWorldCenterZ = (Double) configurable.value();
                double worldCenterX = worldBorder.getCenter().getX();
                worldBorder.setCenter(worldCenterX, newWorldCenterZ);
                break;
            default:
                break;
        }
    }

    public void executeConfigurables(List<? extends Configurable<?>> configurables) {
        configurables.forEach(this::executeConfigurable);
    }

}