package com.stefancooper.SpigotUHC.enums;

public enum ConfigKey {
    // World Border Enums
    WORLD_BORDER_INITIAL_SIZE("world.border.initial.size"), // Initial size world border at start of the UHC
    WORLD_BORDER_FINAL_SIZE("world.border.final.size"), // Final size of the world border at the end of the UHC
    WORLD_BORDER_SHRINKING_PERIOD("world.border.shrinking.period"), // Time (in seconds) to shrink from the initial size to the final size
    WORLD_BORDER_GRACE_PERIOD("world.border.grace.period"), // Grace period time (in seconds) before the border will begin to shrink
    WORLD_BORDER_CENTER_X("world.border.center.x"), // World border center X coord
    WORLD_BORDER_CENTER_Z("world.border.center.z"), // World border center Z coord
    WORLD_BORDER_IN_BOSSBAR("world.border.in.bossbar"),
    // Team Enums
    RANDOM_TEAMS_ENABLED("random.teams.enabled"), // (optional) Enable random teams
    RANDOM_TEAM_SIZE("random.team.size"), // (optional) Random team size
    TEAM_RED("team.red"), // Team red players
    TEAM_YELLOW("team.yellow"), // Team yellow players
    TEAM_GREEN("team.green"), // Team green players
    TEAM_BLUE("team.blue"), // Team blue players
    TEAM_ORANGE("team.orange"), // Team orange players
    TEAM_PINK("team.pink"), // Team pink players
    TEAM_PURPLE("team.purple"), // Team purple players
    // Spread Players Enums
    SPREAD_MIN_DISTANCE("spread.min.distance"),
    // Misc Enums
    GRACE_PERIOD_TIMER("grace.period.timer"), // Grace period time (in seconds) before PVP is enabled
    ON_DEATH_ACTION("on.death.action"), // (optional) Action to undertake when a player dies ("spectate" | "kick")
    COUNTDOWN_TIMER_LENGTH("countdown.timer.length"), // Countdown to start the game after UHC start command issued
    PLAYER_HEAD_GOLDEN_APPLE("player.head.golden.apple"), // (optional) drop player heads who are killed that can be crafted into golden apples
    WORLD_NAME("world.name"), // Name of the minecraft world
    WORLD_NAME_NETHER("nether.world.name"), // Name of the nether world
    WORLD_NAME_END("end.world.name"), // Name of the end world
    ENABLE_TIMESTAMPS("enable.timestamps"), // Get timestamps in txt file of notable events
    DIFFICULTY("difficulty"), // Difficulty during the UHC
    // (Optional) Revive config
    REVIVE_ENABLED("revive.enabled"),
    REVIVE_TIME("revive.time"),
    REVIVE_LOCATION_SIZE("revive.location.size"),
    REVIVE_LOCATION_X("revive.location.x"),
    REVIVE_LOCATION_Y("revive.location.y"),
    REVIVE_LOCATION_Z("revive.location.z"),
    REVIVE_HP("revive.hp"),
    REVIVE_LOSE_MAX_HEALTH("revive.lose.max.health"),
    // (Optional) World spawn - this is the spawn of all players before the UHC has started
    WORLD_SPAWN_X("world.spawn.x"),
    WORLD_SPAWN_Y("world.spawn.y"),
    WORLD_SPAWN_Z("world.spawn.z"),
    // (Optional) Random final location
    RANDOM_FINAL_LOCATION("random.final.location");

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
