package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.enums.DeathAction;
import com.stefancooper.SpigotUHC.types.BossBarBorder;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.*;

public class BaseEvents implements Listener {

    private final Config config;

    public BaseEvents(Config config) {
        this.config = config;
    }

    // View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

    @Nullable
    private Location getWorldSpawn() {
        Optional<Integer> worldSpawnX = Optional.ofNullable(config.getProperty(WORLD_SPAWN_X));
        Optional<Integer> worldSpawnY = Optional.ofNullable(config.getProperty(WORLD_SPAWN_Y));
        Optional<Integer> worldSpawnZ = Optional.ofNullable(config.getProperty(WORLD_SPAWN_Z));
        if (worldSpawnX.isPresent() && worldSpawnY.isPresent() && worldSpawnZ.isPresent()) {
            int x = worldSpawnX.get();
            int y = worldSpawnY.get();
            int z = worldSpawnZ.get();
            return new Location(config.getWorlds().getOverworld(), x, y, z);
        }
        return null;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        switch (DeathAction.fromString(config.getProperty(ON_DEATH_ACTION, Defaults.ON_DEATH_ACTION))) {
            case SPECTATE:
                event.getEntity().setGameMode(GameMode.SPECTATOR);
                break;
            case KICK:
                event.getEntity().kickPlayer("GG, you suck");
                break;
            case null:
            default:
                break;
        }

        if (config.getProperty(PLAYER_HEAD_GOLDEN_APPLE, Defaults.PLAYER_HEAD_GOLDEN_APPLE)) {
            Player player = event.getEntity();
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            assert headMeta != null;
            headMeta.setDisplayName(String.format("%s's head", player.getDisplayName()));
            headMeta.setLore(List.of("Put this item in a bench", "For a Golden Apple"));
            headMeta.setOwningPlayer(player);
            headMeta.setUnbreakable(true);
            headMeta.setFireResistant(true);
            try {
                headMeta.setRarity(ItemRarity.EPIC);
            } catch (Exception e) {
                // noop
                // for some reason, this function is not implemented in MockBukkit but it is not worth us mocking
            }
            head.setItemMeta(headMeta);
            player.getWorld().dropItemNaturally(player.getLocation(), head);
        }


        if (config.getProperty(WHISPER_TEAMMATE_DEAD_LOCATION, Defaults.WHISPER_TEAMMATE_DEAD_LOCATION)) {
            Player player = event.getEntity();
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Optional<Team> playerTeam = scoreboard.getTeams().stream().filter(team -> team.hasEntry(player.getDisplayName())).findFirst();
            playerTeam.ifPresent(team -> {
                team.getEntries().forEach(teammatePlayer -> {
                    Player teammate = Bukkit.getPlayer(teammatePlayer);
                    if (teammate != null) {
                        teammate.sendMessage(String.format("(Only visible to your team) %s death location: %s, %s, %s",
                                player.getDisplayName(),
                                (int) player.getLocation().getX(),
                                (int) player.getLocation().getY(),
                                (int) player.getLocation().getZ()
                        ));
                    }
                });
            });
        }

        // Play death cannon
        if (config.getPlugin().getStarted()) {
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = player.getLastDeathLocation();
        if (deathLocation != null && player.getGameMode().equals(GameMode.SPECTATOR)) {
            event.setRespawnLocation(deathLocation);
        }
        if (!config.getPlugin().getStarted()) {
            final Location worldSpawn = getWorldSpawn();
            if (worldSpawn != null) {
                event.setRespawnLocation(worldSpawn);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        /* -- Setting Game mode -- */
        GameMode currentGamemode = event.getPlayer().getGameMode();
        if (currentGamemode == GameMode.SPECTATOR) {
            return;
        } else if (config.getPlugin().getStarted()) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        } else {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            final Location worldSpawn = getWorldSpawn();
            final int inventorySize = Arrays.stream(event.getPlayer().getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList().size();
            // don't teleport them if their inventory has something inside it (this suggests that the uhc has started and maybe the server crashed)
            if (inventorySize == 0 && worldSpawn != null) {
                event.getPlayer().teleport(worldSpawn);
            }
        }

        /* -- Setting boss bar -- */
        if (config.getPlugin().getStarted() && config.getProperty(WORLD_BORDER_IN_BOSSBAR, Defaults.WORLD_BORDER_IN_BOSSBAR)) {
            BossBarBorder bossBarBorder = config.getManagedResources().getBossBarBorder();
            bossBarBorder.getBossBar().addPlayer(event.getPlayer());
            bossBarBorder.getBossBar().setVisible(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Location getTo = event.getTo();
        final Location getFrom = event.getFrom();
        if (config.getPlugin().isCountingDown() && getTo != null && (getTo.getY() > getFrom.getY() || getTo.getX() != getFrom.getX() || getTo.getZ() != getFrom.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {

        // Set Happy Ghasts to 60 hearts
        if (event.getEntity().getType().equals(EntityType.HAPPY_GHAST)) {
            final HappyGhast ghast = (HappyGhast) event.getEntity();
            final AttributeInstance maxHealth = ghast.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(120);
            }
            ghast.setHealth(120);
        }

        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
            // noop
            return;
        }
        if (config.getProperty(DISABLE_WITCHES, Defaults.DISABLE_WITCHES) && event.getEntity().getType().equals(EntityType.WITCH)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        final Player sender = event.getPlayer();
        final GameMode gameMode = sender.getGameMode();

        if (config.getProperty(ENABLE_DEATH_CHAT, Defaults.ENABLE_DEATHCHAT) && gameMode.equals(GameMode.SPECTATOR)) {
            final List<Player> alivePlayers = (List<Player>) Bukkit.getOnlinePlayers().stream().filter(player -> player.getGameMode().equals(GameMode.SURVIVAL)).toList();
            alivePlayers.forEach(player -> event.getRecipients().remove(player));
            event.setMessage(String.format("(Death Chat) %s", event.getMessage()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (config.getProperty(ALL_TREES_SPAWN_APPLES, Defaults.ALL_TREES_SPAWN_APPLES)) {
            // 1/200 chance when breaking leaves to spawn an apple
            if (isBlockLeaves(event.getBlock()) && Utils.checkOddsOf(2, 200)) {
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (config.getProperty(ALL_TREES_SPAWN_APPLES, Defaults.ALL_TREES_SPAWN_APPLES)) {
            // 1/200 chance when leaves decay to spawn an apple
            if (isBlockLeaves(event.getBlock()) && Utils.checkOddsOf(2, 200)) {
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        }
    }

    @EventHandler
    public void onPearlTeleport(EntityDamageEvent event) {
        if (config.getProperty(DISABLE_ENDER_PEARL_DAMAGE, Defaults.DISABLE_ENDER_PEARL_DAMAGE) && event.getDamageSource().getDamageType().equals(DamageType.ENDER_PEARL)) {
            event.setCancelled(true);
        }
    }

    private boolean isBlockLeaves(Block block) {
        return switch (block.getType()) {
            case Material.ACACIA_LEAVES, Material.AZALEA_LEAVES, Material.BIRCH_LEAVES, Material.CHERRY_LEAVES,
                 Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.SPRUCE_LEAVES,
                 Material.FLOWERING_AZALEA_LEAVES, Material.PALE_OAK_LEAVES -> true;
            default -> false;
        };
    }
}