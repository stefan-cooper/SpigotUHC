package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.types.Configurable;

import java.io.*;
import java.util.Properties;
import java.util.stream.Stream;


public class Config {

    private final Properties config;
    private final Properties defaultConfig;
    private final ConfigParser parser;

    public Config() {
        Properties props = new Properties();
        try {
            final FileInputStream in = new FileInputStream("./plugins/uhc_config.properties");
            props.load(in);
            in.close();
        } catch (IOException ignored) {
            try {
                // TODO - find a better way to do this, perhaps mock/mockito?
                File file = new File("./src/test/java/resources/uhc_config.properties");
                FileInputStream in = new FileInputStream(file);
                props.load(in);
                in.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } // noop
        this.config = props;
        this.defaultConfig = Defaults.createDefaultConfig();
        this.parser = new ConfigParser(this);
        this.setDefaults();
        parser.executeConfigurables(this.config.entrySet().stream().map(prop -> parser.propertyToConfigurable((String) prop.getKey(), (String) prop.getValue())).toList());
    }

    public String getProp(String key) {
        return (String) config.get(key);
    }

    public String getProps() {
        Stream<String> mapped = this.config.entrySet().stream().map(prop -> prop.getKey() + "=" + prop.getValue() + "\n");
        return mapped.reduce("", String::concat);
    }

    public void setProp(String key, String value) {
        Configurable<?> configurable = parser.propertyToConfigurable(key, value);
        if (configurable != null) {
            config.setProperty(key, value);
            parser.executeConfigurable(configurable);
            try {
                File file = new File("./plugins/uhc_config.properties");
                FileOutputStream fos = new FileOutputStream(file);
                config.store(fos, "saving new value");
                fos.close();
            } catch (Exception e) {
                try {
                    // TODO - find a better way to do this, perhaps mock/mockito?
                    File file = new File("./src/test/java/resources/uhc_config.properties");
                    FileOutputStream fos = new FileOutputStream(file);
                    config.store(fos, "saving new value");
                    fos.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
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
        defaultConfig.forEach((key, value) -> setProp((String) key, (String) value));
    }

}
