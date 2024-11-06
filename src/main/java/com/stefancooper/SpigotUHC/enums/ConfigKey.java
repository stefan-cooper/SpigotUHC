package com.stefancooper.SpigotUHC.enums;

public enum ConfigKey {

    // ----- Required config -----
    WORLD_NAME("world.name"), // Name of the minecraft world
    WORLD_NAME_NETHER("nether.world.name"), // Name of the nether world
    WORLD_NAME_END("end.world.name"), // Name of the end world
    GRACE_PERIOD_TIMER("grace.period.timer"), // Grace period time (in seconds) before PVP is enabled
    COUNTDOWN_TIMER_LENGTH("countdown.timer.length"), // Countdown to start the game after UHC start command issued
    DIFFICULTY("difficulty"), // Difficulty during the UHC
    WORLD_BORDER_INITIAL_SIZE("world.border.initial.size"), // Initial size world border at start of the UHC
    WORLD_BORDER_FINAL_SIZE("world.border.final.size"), // Final size of the world border at the end of the UHC
    WORLD_BORDER_SHRINKING_PERIOD("world.border.shrinking.period"), // Time (in seconds) to shrink from the initial size to the final size
    WORLD_BORDER_GRACE_PERIOD("world.border.grace.period"), // Grace period time (in seconds) before the border will begin to shrink
    WORLD_BORDER_CENTER_X("world.border.center.x"), // World border center X coord
    WORLD_BORDER_CENTER_Z("world.border.center.z"), // World border center Z coord
    SPREAD_MIN_DISTANCE("spread.min.distance"), // minimum distance that players will be spread across the world
    ON_DEATH_ACTION("on.death.action"), // Action to undertake when a player dies ("spectate" | "kick")
    // ----- End required config -----

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

    // Misc Enums
    PLAYER_HEAD_GOLDEN_APPLE("player.head.golden.apple"), // (optional) drop player heads who are killed that can be crafted into golden apples
    ENABLE_TIMESTAMPS("enable.timestamps"), // Get timestamps in txt file of notable events
    RANDOM_FINAL_LOCATION("random.final.location"), // Use a random final location
    WORLD_BORDER_IN_BOSSBAR("world.border.in.bossbar"), // Add the world border into the bossbar
    WORLD_SPAWN_X("world.spawn.x"), // X coordinate for world spawn when a UHC is not active
    WORLD_SPAWN_Y("world.spawn.y"), // Y coordinate for world spawn when a UHC is not active
    WORLD_SPAWN_Z("world.spawn.z"), // Z coordinate for world spawn when a UHC is not active

    // Revive config
    REVIVE_ENABLED("revive.enabled"), // Enable revive
    REVIVE_LOCATION_X("revive.location.x"), // Revive x center location
    REVIVE_LOCATION_Y("revive.location.y"), // Revive y center location
    REVIVE_LOCATION_Z("revive.location.z"), // Revive z center location
    REVIVE_TIME("revive.time"), // How many seconds it takes to revive
    REVIVE_LOCATION_SIZE("revive.location.size"), // Diameter of revive location
    REVIVE_HP("revive.hp"), // Revivee starting hp
    REVIVE_LOSE_MAX_HEALTH("revive.lose.max.health"), // Revivee max hp loss

    // UHC Loot
    LOOT_CHEST_ENABLED("loot.chest.enabled"),
    LOOT_CHEST_X("loot.chest.x"),
    LOOT_CHEST_Y("loot.chest.y"),
    LOOT_CHEST_Z("loot.chest.z"),
    LOOT_FREQUENCY("loot.frequency"),
    ;

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
