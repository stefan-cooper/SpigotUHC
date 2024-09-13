package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.types.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.fromString;

public class Config {

    private final Properties config;

    public Config() {
        Properties props = new Properties();
        try {
            final FileInputStream in = new FileInputStream( "./plugins/uhc_config.properties" );
            props.load(in);
        } catch (IOException ignored) {} // noop
        this.config = props;
        executeConfigurables(this.config.entrySet().stream().map(prop -> propertyToConfigurable((String) prop.getKey(), (String) prop.getValue())).toList());
    }

    public String getProp(String key) {
        return (String) config.get(key);
    }

    public String getProps() {
        Stream<String> mapped = this.config.entrySet().stream().map(prop -> prop.getKey() + "=" + prop.getValue() + "\n");
        return mapped.reduce("", String::concat);
    }

    private Configurable<?> propertyToConfigurable(String key, String value) {
        return switch (fromString(key)) {
            case WORLD_BORDER_INITIAL_SIZE -> new Configurable<>(WORLD_BORDER_INITIAL_SIZE, Double.parseDouble(value));
            case null -> null;
        };
    }

    public void setProp(String key, String value) {
        Configurable<?> configurable = propertyToConfigurable(key, value);
        if (configurable != null) {
            config.setProperty(key, value);
            executeConfigurable(configurable);
            try {
                File file = new File("./plugins/uhc_config.properties");
                FileOutputStream fos = new FileOutputStream(file);
                config.store(fos, "saving new value");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Invalid config value attempted to be set: " + key + "=" + value);
        }

    }

    public void executeConfigurable(Configurable<?> configurable) {
        if (configurable == null) {
            System.out.println("Invalid config value attempted to be executed, ignoring...");
            return;
        }
        switch (configurable.key()) {
            case WORLD_BORDER_INITIAL_SIZE:
                Double newWorldBorderSize = (Double) configurable.value();
                WorldBorder worldBorder = Bukkit.getWorld("world").getWorldBorder();
                worldBorder.setSize(newWorldBorderSize);
                break;
        }
    }

    public void executeConfigurables(List<? extends Configurable<?>> configurables) {
        configurables.forEach(this::executeConfigurable);
    }

}
