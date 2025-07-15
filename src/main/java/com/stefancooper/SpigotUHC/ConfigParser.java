package com.stefancooper.SpigotUHC;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.stefancooper.SpigotUHC.types.BossBarBorder;
import com.stefancooper.SpigotUHC.utils.Utils;
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

import static com.stefancooper.SpigotUHC.enums.ConfigKey.ALL_TREES_SPAWN_APPLES;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.COUNTDOWN_TIMER_LENGTH;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.CRAFTABLE_NOTCH_APPLE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.CRAFTABLE_PLAYER_HEAD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.DIFFICULTY;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.DISABLE_END_GAME_AUTOMATICALLY;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.DISABLE_WITCHES;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.ENABLE_DEATH_CHAT;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.ENABLE_TIMESTAMPS;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_ENABLED;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_HIGH_LOOT_ODDS;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_MID_LOOT_ODDS;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_SPINS_PER_GEN;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_X_RANGE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_FREQUENCY;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.LOOT_CHEST_Z_RANGE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.ON_DEATH_ACTION;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.PLAYER_HEAD_GOLDEN_APPLE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_FINAL_LOCATION;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_TEAMS_POT_ONE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_TEAMS_POT_THREE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_TEAMS_POT_TWO;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_ANY_HEAD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_ENABLED;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_HP;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOSE_MAX_HEALTH;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_TIME;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_VIA_ARMOR_STAND;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_VIA_PLATFORMS;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.SPREAD_MIN_DISTANCE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.TEAM_BLUE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.TEAM_GREEN;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.TEAM_ORANGE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.TEAM_PINK;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.TEAM_PURPLE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.TEAM_RED;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.TEAM_YELLOW;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WHISPER_TEAMMATE_DEAD_LOCATION;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_FINAL_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_IN_BOSSBAR;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_Y_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_NAME;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_NAME_END;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_NAME_NETHER;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_SPAWN_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_SPAWN_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_SPAWN_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.ADDITIONAL_ENCHANTS_SHIELD;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.fromString;
import static com.stefancooper.SpigotUHC.types.UHCTeam.createTeam;

import com.stefancooper.SpigotUHC.types.UHCTeam;
import com.stefancooper.SpigotUHC.utils.Configurable;

public class ConfigParser {

    private final Config config;
    private BossBarBorder bossBarBorder;

    public ConfigParser(Config config) {
        this.config = config;
    }

    public Configurable<?> propertyToConfigurable(String key, String value) {
        return switch (fromString(key)) {
            case WORLD_BORDER_INITIAL_SIZE -> new Configurable<>(WORLD_BORDER_INITIAL_SIZE, Integer.parseInt(value));
            case WORLD_BORDER_FINAL_SIZE -> new Configurable<>(WORLD_BORDER_FINAL_SIZE, Integer.parseInt(value));
            case WORLD_BORDER_SHRINKING_PERIOD -> new Configurable<>(WORLD_BORDER_SHRINKING_PERIOD, Integer.parseInt(value));
            case WORLD_BORDER_GRACE_PERIOD -> new Configurable<>(WORLD_BORDER_GRACE_PERIOD, Integer.parseInt(value));
            case WORLD_BORDER_CENTER_X -> new Configurable<>(WORLD_BORDER_CENTER_X, Integer.parseInt(value));
            case WORLD_BORDER_CENTER_Z -> new Configurable<>(WORLD_BORDER_CENTER_Z, Integer.parseInt(value));
            case WORLD_BORDER_FINAL_Y -> new Configurable<>(WORLD_BORDER_FINAL_Y, Integer.parseInt(value));
            case WORLD_BORDER_Y_SHRINKING_PERIOD -> new Configurable<>(WORLD_BORDER_Y_SHRINKING_PERIOD, Integer.parseInt(value));
            case TEAM_RED -> new Configurable<>(TEAM_RED, value);
            case TEAM_YELLOW -> new Configurable<>(TEAM_YELLOW, value);
            case TEAM_GREEN -> new Configurable<>(TEAM_GREEN, value);
            case TEAM_BLUE -> new Configurable<>(TEAM_BLUE, value);
            case TEAM_ORANGE -> new Configurable<>(TEAM_ORANGE, value);
            case TEAM_PINK -> new Configurable<>(TEAM_PINK, value);
            case TEAM_PURPLE -> new Configurable<>(TEAM_PURPLE, value);
            case SPREAD_MIN_DISTANCE -> new Configurable<>(SPREAD_MIN_DISTANCE, Integer.parseInt(value));
            case GRACE_PERIOD_TIMER -> new Configurable<>(GRACE_PERIOD_TIMER, Integer.parseInt(value));
            case ON_DEATH_ACTION -> new Configurable<>(ON_DEATH_ACTION, value);
            case COUNTDOWN_TIMER_LENGTH -> new Configurable<>(COUNTDOWN_TIMER_LENGTH, Integer.parseInt(value));
            case PLAYER_HEAD_GOLDEN_APPLE -> new Configurable<>(PLAYER_HEAD_GOLDEN_APPLE, Boolean.parseBoolean((value)));
            case WORLD_NAME -> new Configurable<>(WORLD_NAME, value);
            case WORLD_NAME_NETHER -> new Configurable<>(WORLD_NAME_NETHER, value);
            case WORLD_NAME_END -> new Configurable<>(WORLD_NAME_END, value);
            case DIFFICULTY -> new Configurable<>(DIFFICULTY, Difficulty.valueOf(value));
            case WORLD_BORDER_IN_BOSSBAR -> new Configurable<>(WORLD_BORDER_IN_BOSSBAR, Boolean.parseBoolean(value));
            case ENABLE_TIMESTAMPS -> new Configurable<>(ENABLE_TIMESTAMPS, Boolean.parseBoolean(value));
            case ENABLE_DEATH_CHAT -> new Configurable<>(ENABLE_DEATH_CHAT, Boolean.parseBoolean(value));
            case DISABLE_END_GAME_AUTOMATICALLY -> new Configurable<>(DISABLE_END_GAME_AUTOMATICALLY, Boolean.parseBoolean(value));
            case RANDOM_FINAL_LOCATION -> new Configurable<>(RANDOM_FINAL_LOCATION, Boolean.parseBoolean(value));
            case DISABLE_WITCHES -> new Configurable<>(DISABLE_WITCHES, Boolean.parseBoolean(value));
            case CRAFTABLE_NOTCH_APPLE -> new Configurable<>(CRAFTABLE_NOTCH_APPLE, Boolean.parseBoolean(value));
            case CRAFTABLE_PLAYER_HEAD -> new Configurable<>(CRAFTABLE_PLAYER_HEAD, Boolean.parseBoolean(value));
            case WHISPER_TEAMMATE_DEAD_LOCATION -> new Configurable<>(WHISPER_TEAMMATE_DEAD_LOCATION, Boolean.parseBoolean(value));
            case ALL_TREES_SPAWN_APPLES -> new Configurable<>(ALL_TREES_SPAWN_APPLES, Boolean.parseBoolean(value));
            // Revive config
            case REVIVE_ENABLED -> new Configurable<>(REVIVE_ENABLED, Boolean.parseBoolean(value));
            case REVIVE_TIME -> new Configurable<>(REVIVE_TIME, Integer.parseInt(value));
            case REVIVE_HP -> new Configurable<>(REVIVE_HP, Integer.parseInt(value));
            case REVIVE_LOCATION_SIZE -> new Configurable<>(REVIVE_LOCATION_SIZE, Integer.parseInt(value));
            case REVIVE_LOCATION_X -> new Configurable<>(REVIVE_LOCATION_X, value);
            case REVIVE_LOCATION_Y -> new Configurable<>(REVIVE_LOCATION_Y, value);
            case REVIVE_LOCATION_Z -> new Configurable<>(REVIVE_LOCATION_Z, value);
            case REVIVE_LOSE_MAX_HEALTH -> new Configurable<>(REVIVE_LOSE_MAX_HEALTH, Integer.parseInt(value));
            case REVIVE_ANY_HEAD -> new Configurable<>(REVIVE_ANY_HEAD, Boolean.parseBoolean(value));
            case REVIVE_VIA_ARMOR_STAND -> new Configurable<>(REVIVE_VIA_ARMOR_STAND, Boolean.parseBoolean(value));
            case REVIVE_VIA_PLATFORMS -> new Configurable<>(REVIVE_VIA_PLATFORMS, Boolean.parseBoolean(value));
            // Random teams
            case RANDOM_TEAMS_POT_ONE -> new Configurable<>(RANDOM_TEAMS_POT_ONE, new HashSet<>(Arrays.asList(value.split(","))));
            case RANDOM_TEAMS_POT_TWO -> new Configurable<>(RANDOM_TEAMS_POT_TWO, new HashSet<>(Arrays.asList(value.split(","))));
            case RANDOM_TEAMS_POT_THREE -> new Configurable<>(RANDOM_TEAMS_POT_THREE, new HashSet<>(Arrays.asList(value.split(","))));
            // World spawn
            case WORLD_SPAWN_X -> new Configurable<>(WORLD_SPAWN_X, Integer.parseInt(value));
            case WORLD_SPAWN_Y -> new Configurable<>(WORLD_SPAWN_Y, Integer.parseInt(value));
            case WORLD_SPAWN_Z -> new Configurable<>(WORLD_SPAWN_Z, Integer.parseInt(value));
            // UHC Loot
            case LOOT_CHEST_ENABLED -> new Configurable<>(LOOT_CHEST_ENABLED, Boolean.parseBoolean(value));
            case LOOT_CHEST_X -> new Configurable<>(LOOT_CHEST_X, Integer.valueOf(value));
            case LOOT_CHEST_Y -> new Configurable<>(LOOT_CHEST_Y, Integer.valueOf(value));
            case LOOT_CHEST_Z -> new Configurable<>(LOOT_CHEST_Z, Integer.valueOf(value));
            case LOOT_CHEST_X_RANGE -> new Configurable<>(LOOT_CHEST_X_RANGE, value);
            case LOOT_CHEST_Z_RANGE -> new Configurable<>(LOOT_CHEST_Z_RANGE, value);
            case LOOT_CHEST_FREQUENCY -> new Configurable<>(LOOT_CHEST_FREQUENCY, Integer.valueOf(value));
            case LOOT_CHEST_HIGH_LOOT_ODDS -> new Configurable<>(LOOT_CHEST_HIGH_LOOT_ODDS, Integer.valueOf(value));
            case LOOT_CHEST_SPINS_PER_GEN -> new Configurable<>(LOOT_CHEST_SPINS_PER_GEN, Integer.valueOf(value));
            case LOOT_CHEST_MID_LOOT_ODDS -> new Configurable<>(LOOT_CHEST_MID_LOOT_ODDS, Integer.valueOf(value));
            // Additional Enchants
            case ADDITIONAL_ENCHANTS_SHIELD -> new Configurable<>(ADDITIONAL_ENCHANTS_SHIELD, Boolean.parseBoolean(value));
            case null -> null;
        };
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
                int newWorldBorderSize = (int) configurable.value();
                Utils.setWorldEffects(List.of(overworld, nether, end), (world) -> {
                    WorldBorder worldBorder = world.getWorldBorder();
                    worldBorder.setSize(newWorldBorderSize);
                    worldBorder.setDamageAmount(0);
                });
                break;
            case WORLD_BORDER_CENTER_X:
                int newWorldCenterX = (int) configurable.value();
                Utils.setWorldEffects(List.of(overworld, nether, end), (world) -> {
                    WorldBorder worldBorder = world.getWorldBorder();
                    double worldCenterZ = worldBorder.getCenter().getZ();
                    worldBorder.setCenter(newWorldCenterX, worldCenterZ);
                });
                break;
            case WORLD_BORDER_CENTER_Z:
                int newWorldCenterZ = (int) configurable.value();
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
            case TEAM_PURPLE:
                createTeam(new UHCTeam("Purple", (String) configurable.value(), ChatColor.DARK_PURPLE ));
                break;
            case PLAYER_HEAD_GOLDEN_APPLE:
                NamespacedKey playerHeadKey = config.getManagedResources().getPlayerHeadKey();
                if (config.getProperty(PLAYER_HEAD_GOLDEN_APPLE, Defaults.PLAYER_HEAD_GOLDEN_APPLE)) {
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
                break;
            case CRAFTABLE_NOTCH_APPLE:
                NamespacedKey notchAppleKey = config.getManagedResources().getNotchAppleKey();
                if (config.getProperty(CRAFTABLE_NOTCH_APPLE, Defaults.CRAFTABLE_NOTCH_APPLE)) {
                    if (Bukkit.getRecipe(notchAppleKey) == null) {
                        ItemStack apple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
                        ShapedRecipe recipe = new ShapedRecipe(notchAppleKey, apple);
                        recipe.shape("GGG", "GAG", "GGG");
                        recipe.setIngredient('G', Material.GOLD_BLOCK);
                        recipe.setIngredient('A', Material.APPLE);
                        Bukkit.addRecipe(recipe);
                    }
                } else {
                    if (Bukkit.getRecipe(notchAppleKey) != null) {
                        Bukkit.removeRecipe(notchAppleKey);
                    }
                }
                break;
            case CRAFTABLE_PLAYER_HEAD:
                final NamespacedKey craftablePlayerHeadKey = config.getManagedResources().getCraftablePlayerHeadKey();
                if (config.getProperty(CRAFTABLE_PLAYER_HEAD, Defaults.CRAFTABLE_PLAYER_HEAD)) {
                    if (Bukkit.getRecipe(craftablePlayerHeadKey) == null) {
                        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                        ShapedRecipe recipe = new ShapedRecipe(craftablePlayerHeadKey, playerHead);
                        recipe.shape("DDD", "DGD", "DDD");
                        recipe.setIngredient('D', Material.DIAMOND);
                        recipe.setIngredient('G', Material.GOLDEN_APPLE);
                        Bukkit.addRecipe(recipe);
                    }
                } else {
                    if (Bukkit.getRecipe(craftablePlayerHeadKey) != null) {
                        Bukkit.removeRecipe(craftablePlayerHeadKey);
                    }
                }
                break;
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
