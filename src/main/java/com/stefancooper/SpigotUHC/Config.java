package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.utils.Configurable;
import com.stefancooper.SpigotUHC.types.Worlds;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.Properties;
import java.util.stream.Stream;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_NAME;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_NAME_END;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_NAME_NETHER;
import static com.stefancooper.SpigotUHC.utils.Constants.CONFIG_LOCATION;

public class Config {

    private Properties config;
    private final Properties defaultConfig;
    private final ConfigParser parser;
    private final Plugin plugin;
    private final ManagedResources managedResources;
    private final Worlds worlds;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        this.config = new Properties();
        this.parser = new ConfigParser(this);
        this.defaultConfig = Defaults.createDefaultConfig();
        this.config = loadInitialProperties();
        this.managedResources = new ManagedResources(this);
        this.worlds = new Worlds(this);
        this.setDefaults();
        parser.executeConfigurables(this.config.entrySet().stream().map(prop -> parser.propertyToConfigurable((String) prop.getKey(), (String) prop.getValue())).toList());
    }

    private Properties loadInitialProperties() {
        final Properties props = new Properties();
        try {
            boolean file = new File(CONFIG_LOCATION).createNewFile();
            // if file had to be created, go and set the defaults
            if (file) setDefaults();
            else {
                final FileInputStream in = new FileInputStream(CONFIG_LOCATION);
                props.load(in);
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return props;
    }

    public ManagedResources getManagedResources() {
        return managedResources;
    }

    public Worlds getWorlds() { return worlds; }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getProp(String key) {
        return (String) config.get(key);
    }

    /** improved version of getProp, this will use the parsing that we've already done */
    public <T> T getProperty(ConfigKey key) {
        return (T) parser.propertyToConfigurable(key.configName, getProp(key.configName)).value();
    }

    public String getProps() {
        Stream<String> mapped = this.config.entrySet().stream().sorted(Comparator.comparing(i1 -> (String) i1.getKey())).map(prop -> prop.getKey() + "=" + prop.getValue() + "\n");

        return mapped.reduce("", String::concat);
    }

    public void setProp(String key, String value) {
        Configurable<?> configurable = parser.propertyToConfigurable(key, value);
        if (configurable != null) {
            config.setProperty(key, value);
            parser.executeConfigurable(configurable);
            try {
                File file = new File(CONFIG_LOCATION);
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

    private void setDefaults() {
        defaultConfig.forEach((key, value) -> {
            if (getProp((String) key) == null) {
                setProp((String) key, (String) value);
            }
        });
    }

    public void resetToDefaults() {
        config.clear();
        setProp(WORLD_NAME.configName, (String) defaultConfig.get(WORLD_NAME.configName));
        setProp(WORLD_NAME_NETHER.configName, (String) defaultConfig.get(WORLD_NAME_NETHER.configName));
        setProp(WORLD_NAME_END.configName, (String) defaultConfig.get(WORLD_NAME_END.configName));
        defaultConfig.forEach((key, value) -> setProp((String) key, (String) value));
        Defaults.setDefaultGameRules(this);
    }

    public void trigger() {
        setProp(WORLD_NAME.configName, (String) defaultConfig.get(WORLD_NAME.configName));
        config.forEach((key, value) -> setProp((String) key, (String) value));
        Defaults.setDefaultGameRules(this);
    }

}
