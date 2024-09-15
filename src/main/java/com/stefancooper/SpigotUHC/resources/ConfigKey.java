package com.stefancooper.SpigotUHC.resources;

public enum ConfigKey {
    // World Border Enums
    WORLD_BORDER_INITIAL_SIZE("world.border.initial.size"),
    WORLD_BORDER_FINAL_SIZE("world.border.final.size"),
    WORLD_BORDER_SHRINKING_PERIOD("world.border.shrinking.period"),
    WORLD_BORDER_GRACE_PERIOD("world.border.grace.period"),
    WORLD_BORDER_CENTER_X("world.border.center.x"),
    WORLD_BORDER_CENTER_Z("world.border.center.z"),
    // Team Enums
    RANDOM_TEAMS_ENABLED("random.teams.enabled"),
    RANDOM_TEAM_SIZE("random.team.size"),
    TEAM_RED("team.red"),
    TEAM_YELLOW("team.yellow"),
    TEAM_GREEN("team.green"),
    TEAM_BLUE("team.blue"),
    TEAM_ORANGE("team.orange"),
    // Misc Enums
    GRACE_PERIOD_TIMER("grace.period.timer"),
    ON_DEATH_ACTION("on.death.action"),
    COUNTDOWN_TIMER_LENGTH("countdown.timer.length"),
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
