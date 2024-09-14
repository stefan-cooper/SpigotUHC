package com.stefancooper.SpigotUHC.resources;

public enum ConfigKey {
    WORLD_BORDER_INITIAL_SIZE("world.border.initial.size"),
    WORLD_NAME("world.name");

    public final String configName;

    ConfigKey(String name) {
        this.configName = name;
    }

    public static ConfigKey fromString(String configName) {
        for (ConfigKey key : ConfigKey.values()) {
            if (key.configName.equalsIgnoreCase(configName)) {
                return key;
            }
        }
        return null;
    }
}
