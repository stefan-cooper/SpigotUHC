package com.stefancooper.SpigotUHC;

import java.util.List;

import com.stefancooper.SpigotUHC.types.BossBarBorder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.COUNTDOWN_TIMER_LENGTH;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.DIFFICULTY;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.ENABLE_TIMESTAMPS;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.ON_DEATH_ACTION;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.PLAYER_HEAD_GOLDEN_APPLE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.RANDOM_TEAMS_ENABLED;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.RANDOM_TEAM_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_ENABLED;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_HEALTH;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_X;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_Y;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_Z;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_TIME;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.SPREAD_MIN_DISTANCE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.TEAM_BLUE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.TEAM_GREEN;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.TEAM_ORANGE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.TEAM_PINK;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.TEAM_RED;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.TEAM_YELLOW;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_IN_BOSSBAR;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_BORDER_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME_END;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.WORLD_NAME_NETHER;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.fromString;
import com.stefancooper.SpigotUHC.types.UHCTeam;
import com.stefancooper.SpigotUHC.types.Configurable;

public class ConfigParser {

    private final Config config;
    private BossBarBorder bossBarBorder;

    public ConfigParser(Config config) {
        this.config = config;
    }

    public Configurable<?> propertyToConfigurable(String key, String value) {
        return switch (fromString(key)) {
            case WORLD_BORDER_INITIAL_SIZE -> new Configurable<>(WORLD_BORDER_INITIAL_SIZE, Double.parseDouble(value));
            case WORLD_BORDER_FINAL_SIZE -> new Configurable<>(WORLD_BORDER_FINAL_SIZE, Double.parseDouble(value));
            case WORLD_BORDER_SHRINKING_PERIOD -> new Configurable<>(WORLD_BORDER_SHRINKING_PERIOD, Double.parseDouble(value));
            case WORLD_BORDER_GRACE_PERIOD -> new Configurable<>(WORLD_BORDER_GRACE_PERIOD, Double.parseDouble(value));
            case WORLD_BORDER_CENTER_X -> new Configurable<>(WORLD_BORDER_CENTER_X, Double.parseDouble(value));
            case WORLD_BORDER_CENTER_Z -> new Configurable<>(WORLD_BORDER_CENTER_Z, Double.parseDouble(value));
            case RANDOM_TEAMS_ENABLED -> new Configurable<>(RANDOM_TEAMS_ENABLED, Boolean.parseBoolean((value)));
            case RANDOM_TEAM_SIZE -> new Configurable<>(RANDOM_TEAM_SIZE, Double.parseDouble(value));
            case TEAM_RED -> new Configurable<>(TEAM_RED, value);
            case TEAM_YELLOW -> new Configurable<>(TEAM_YELLOW, value);
            case TEAM_GREEN -> new Configurable<>(TEAM_GREEN, value);
            case TEAM_BLUE -> new Configurable<>(TEAM_BLUE, value);
            case TEAM_ORANGE -> new Configurable<>(TEAM_ORANGE, value);
            case TEAM_PINK -> new Configurable<>(TEAM_PINK, value);
            case SPREAD_MIN_DISTANCE -> new Configurable<>(SPREAD_MIN_DISTANCE, Double.parseDouble(value));
            case GRACE_PERIOD_TIMER -> new Configurable<>(GRACE_PERIOD_TIMER, Double.parseDouble(value));
            case ON_DEATH_ACTION -> new Configurable<>(ON_DEATH_ACTION, value);
            case COUNTDOWN_TIMER_LENGTH -> new Configurable<>(COUNTDOWN_TIMER_LENGTH, Double.parseDouble(value));
            case PLAYER_HEAD_GOLDEN_APPLE -> new Configurable<>(PLAYER_HEAD_GOLDEN_APPLE, Boolean.parseBoolean((value)));
            case WORLD_NAME -> new Configurable<>(WORLD_NAME, value);
            case WORLD_NAME_NETHER -> new Configurable<>(WORLD_NAME_NETHER, value);
            case WORLD_NAME_END -> new Configurable<>(WORLD_NAME_END, value);
            case DIFFICULTY -> new Configurable<>(DIFFICULTY, Difficulty.valueOf(value));
            case WORLD_BORDER_IN_BOSSBAR -> new Configurable<>(WORLD_BORDER_IN_BOSSBAR, Boolean.parseBoolean(value));
            case ENABLE_TIMESTAMPS -> new Configurable<>(ENABLE_TIMESTAMPS, Boolean.parseBoolean(value));
            // Revive config
            case REVIVE_ENABLED -> new Configurable<>(REVIVE_ENABLED, Boolean.parseBoolean(value));
            case REVIVE_TIME -> new Configurable<>(REVIVE_TIME, Integer.parseInt(value));
            case REVIVE_HEALTH -> new Configurable<>(REVIVE_HEALTH, Integer.parseInt(value));
            case REVIVE_LOCATION_SIZE -> new Configurable<>(REVIVE_LOCATION_SIZE, Integer.parseInt(value));
            case REVIVE_LOCATION_X -> new Configurable<>(REVIVE_LOCATION_X, Integer.parseInt(value));
            case REVIVE_LOCATION_Y -> new Configurable<>(REVIVE_LOCATION_Y, Integer.parseInt(value));
            case REVIVE_LOCATION_Z -> new Configurable<>(REVIVE_LOCATION_Z, Integer.parseInt(value));
            case null -> null;
        };
    }

    private void createTeam(UHCTeam uhcTeam) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // if team already exists, redefine it
        if (scoreboard.getTeam(uhcTeam.getName()) != null) {
            scoreboard.getTeam(uhcTeam.getName()).unregister();
        }
        Team team = scoreboard.registerNewTeam(uhcTeam.getName());
        uhcTeam.getPlayers().forEach(player -> {
            // if player is already on another team, remove them from that team and put them on this team
            if (scoreboard.getEntryTeam(player) != null) {
                scoreboard.getEntryTeam(player).removeEntry(player);
            }
            team.addEntry(player);
        });
        team.setColor(uhcTeam.getColor());
        team.setAllowFriendlyFire(false);
        team.setPrefix(String.format("[%s] ", uhcTeam.getName()));
    }

    public void executeConfigurable(Configurable<?> configurable) {
        if (configurable == null) {
            System.out.println("Invalid config value attempted to be executed, ignoring...");
            return;
        }
        World overworld = config.getWorlds().getOverworld();
        World nether = config.getWorlds().getNether();
        World end = config.getWorlds().getEnd();
        switch (configurable.key()) {
            case WORLD_BORDER_INITIAL_SIZE:
                Double newWorldBorderSize = (Double) configurable.value();
                Utils.setWorldEffects(List.of(overworld, nether, end), (world) -> {
                    WorldBorder worldBorder = world.getWorldBorder();
                    worldBorder.setSize(newWorldBorderSize);
                    worldBorder.setDamageAmount(0);
                });
                break;
            case WORLD_BORDER_CENTER_X:
                Double newWorldCenterX = (Double) configurable.value();
                Utils.setWorldEffects(List.of(overworld, nether, end), (world) -> {
                    WorldBorder worldBorder = world.getWorldBorder();
                    double worldCenterZ = worldBorder.getCenter().getZ();
                    worldBorder.setCenter(newWorldCenterX, worldCenterZ);
                });
                break;
            case WORLD_BORDER_CENTER_Z:
                Double newWorldCenterZ = (Double) configurable.value();
                Utils.setWorldEffects(List.of(overworld, nether, end), (world) -> {
                    WorldBorder worldBorder = world.getWorldBorder();
                    double worldCenterX = worldBorder.getCenter().getX();
                    worldBorder.setCenter(worldCenterX, newWorldCenterZ);
                });
                break;
            case TEAM_RED:
                createTeam(new UHCTeam("Red", (String) configurable.value(), ChatColor.RED ));
                break;
            case TEAM_BLUE:
                createTeam(new UHCTeam("Blue", (String) configurable.value(), ChatColor.AQUA ));
                break;
            case TEAM_GREEN:
                createTeam(new UHCTeam("Green", (String) configurable.value(), ChatColor.GREEN ));
                break;
            case TEAM_YELLOW:
                createTeam(new UHCTeam("Yellow", (String) configurable.value(), ChatColor.YELLOW ));
                break;
            case TEAM_ORANGE:
                createTeam(new UHCTeam("Orange", (String) configurable.value(), ChatColor.GOLD ));
                break;
            case TEAM_PINK:
                createTeam(new UHCTeam("Pink", (String) configurable.value(), ChatColor.LIGHT_PURPLE ));
                break;
            case PLAYER_HEAD_GOLDEN_APPLE:
                NamespacedKey playerHeadKey = config.getManagedResources().getPlayerHeadKey();
                if (Boolean.parseBoolean(config.getProp(PLAYER_HEAD_GOLDEN_APPLE.configName))) {
                    if (Bukkit.getRecipe(playerHeadKey) == null) {
                        ItemStack apple = new ItemStack(Material.GOLDEN_APPLE, 1);
                        ItemMeta appleMeta = apple.getItemMeta();
                        apple.setItemMeta(appleMeta);
                        ShapedRecipe recipe = new ShapedRecipe(playerHeadKey, apple);
                        recipe.shape("   ", " X ", "   ");
                        recipe.setIngredient('X', Material.PLAYER_HEAD);
                        Bukkit.addRecipe(recipe);
                    }
                } else {
                    if (Bukkit.getRecipe(playerHeadKey) != null) {
                        Bukkit.removeRecipe(playerHeadKey);
                    }
                }
            case WORLD_NAME:
            case WORLD_NAME_NETHER:
            case WORLD_NAME_END:
                config.getWorlds().updateWorlds();

            default:
                break;
        }
    }

    public void executeConfigurables(List<? extends Configurable<?>> configurables) {
        configurables.forEach(this::executeConfigurable);
    }

}
