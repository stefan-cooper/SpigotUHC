package com.stefancooper.SpigotUHC.resources;

public enum ConfigKey {
    WORLD_BORDER_INITIAL_SIZE("world.border.initial.size"),
    WORLD_BORDER_FINAL_SIZE("world.border.final.size"),
    WORLD_BORDER_SHRINKING_PERIOD("world.border.shrinking.period"),
    WORLD_BORDER_GRACE_PERIOD("world.border.grace.period"),
    WORLD_BORDER_CENTER_X("world.border.center.x"),
    WORLD_BORDER_CENTER_Y("world.border.center.y"),
    WORLD_BORDER_CENTER_Z("world.border.center.z"),
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
