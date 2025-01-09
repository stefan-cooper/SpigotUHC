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
    DISABLE_WITCHES("disable.witches"), // disable witch spawns
    CRAFTABLE_NOTCH_APPLE("craftable.notch.apple"), // re-add craftable notch apple
    CRAFTABLE_PLAYER_HEAD("craftable.player.head"), // add craftable player head

    // Revive config
    REVIVE_ENABLED("revive.enabled"), // Enable revive
    REVIVE_LOCATION_X("revive.location.x"), // Revive x center locations (comma seperated)
    REVIVE_LOCATION_Y("revive.location.y"), // Revive y center locations (comma seperated)
    REVIVE_LOCATION_Z("revive.location.z"), // Revive z center location (comma seperated)
    REVIVE_TIME("revive.time"), // How many seconds it takes to revive
    REVIVE_LOCATION_SIZE("revive.location.size"), // Diameter of revive location
    REVIVE_HP("revive.hp"), // Revivee starting hp
    REVIVE_LOSE_MAX_HEALTH("revive.lose.max.health"), // Revivee max hp loss
    REVIVE_ANY_HEAD("revive.any.head"), // Revive-able with any player head

    // Random teams
    RANDOM_TEAMS_POT_ONE("random.teams.pot.1"),
    RANDOM_TEAMS_POT_TWO("random.teams.pot.2"),
    RANDOM_TEAMS_POT_THREE("random.teams.pot.3"),

    // UHC Loot
    LOOT_CHEST_ENABLED("loot.chest.enabled"), // Enable loot chest
    LOOT_CHEST_X("loot.chest.x"), // Chest x
    LOOT_CHEST_Y("loot.chest.y"), // Chest y
    LOOT_CHEST_Z("loot.chest.z"), // Chest z
    LOOT_CHEST_FREQUENCY("loot.chest.frequency"), // Frequency of loot gen (in seconds)
    LOOT_CHEST_HIGH_LOOT_ODDS("loot.chest.high.loot.odds"), // % odds of a high loot item spawning (per spin)
    LOOT_CHEST_MID_LOOT_ODDS("loot.chest.mid.loot.odds"), // % odds of a mid loot item spawning (per spin)
    LOOT_CHEST_SPINS_PER_GEN("loot.chest.spins.per.gen") // how many items are spawned per gen
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
