package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.types.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;

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
            case WORLD_NAME -> new Configurable<>(WORLD_NAME, value);
            case null -> null;

        };
    }

    public void executeConfigurable(Configurable<?> configurable) {
        if (configurable == null) {
            System.out.println("Invalid config value attempted to be executed, ignoring...");
            return;
        }
        switch (configurable.key()) {
            case WORLD_BORDER_INITIAL_SIZE:
                Double newWorldBorderSize = (Double) configurable.value();
                WorldBorder worldBorder = Bukkit.getWorld(config.getProp(WORLD_NAME.configName)).getWorldBorder();
                worldBorder.setSize(newWorldBorderSize);
                break;
            default:
                break;
        }
    }

    public void executeConfigurables(List<? extends Configurable<?>> configurables) {
        configurables.forEach(this::executeConfigurable);
    }

}
