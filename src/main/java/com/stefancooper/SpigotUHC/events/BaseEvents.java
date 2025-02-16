package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.enums.DeathAction;

import com.stefancooper.SpigotUHC.types.BossBarBorder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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

    public BaseEvents (Config config) {
        this.config = config;
    }

    // View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

    @Nullable
    private Location getWorldSpawn() {
        Optional<String> worldSpawnX = Optional.ofNullable(config.getProp(WORLD_SPAWN_X.configName));
        Optional<String> worldSpawnY = Optional.ofNullable(config.getProp(WORLD_SPAWN_Y.configName));
        Optional<String> worldSpawnZ = Optional.ofNullable(config.getProp(WORLD_SPAWN_Z.configName));
        if (worldSpawnX.isPresent() && worldSpawnY.isPresent() && worldSpawnZ.isPresent()) {
            int x = Integer.parseInt(worldSpawnX.get());
            int y = Integer.parseInt(worldSpawnY.get());
            int z = Integer.parseInt(worldSpawnZ.get());
            return new Location(config.getWorlds().getOverworld(), x, y, z);
        }
        return null;
    }

    // DamageSource API is experimental, so this may break in a spigot update
    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void onDeath (PlayerDeathEvent event) {
        switch (DeathAction.fromString(config.getProp(ON_DEATH_ACTION.configName))){
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

        if (Boolean.parseBoolean(config.getProp(PLAYER_HEAD_GOLDEN_APPLE.configName))){
            Player player = event.getEntity();
            ItemStack head = new ItemStack(Material.PLAYER_HEAD,1);
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

        if (Boolean.parseBoolean(config.getProp(ENABLE_TIMESTAMPS.configName))) {
            if (event.getEntity().getLastDamageCause() != null &&
                    event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity() != null &&
                    event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity().getType() == EntityType.PLAYER
            ) {
                Player player = (Player) event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity();
                config.getManagedResources().addTimestamp(String.format("%s kills %s", player.getDisplayName(), event.getEntity().getDisplayName()));
            } else {
                config.getManagedResources().addTimestamp(String.format("%s dies", event.getEntity().getDisplayName()));
            }
        }

        if (Boolean.TRUE.equals(config.getProperty(WHISPER_TEAMMATE_DEAD_LOCATION))) {
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
        if (config.getPlugin().getStarted() && Boolean.TRUE.equals(config.getProperty(WORLD_BORDER_IN_BOSSBAR))) {
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
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
            // noop
            return;
        }
        if (Boolean.TRUE.equals(config.getProperty(DISABLE_WITCHES)) && event.getEntity().getType().equals(EntityType.WITCH)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        final Player sender = event.getPlayer();
        final GameMode gameMode = sender.getGameMode();

        System.out.println(Arrays.toString(event.getRecipients().toArray()));

        if (Boolean.TRUE.equals(config.getProperty(ENABLE_DEATH_CHAT)) && gameMode.equals(GameMode.SPECTATOR)) {
            System.out.println("Removing alive players from recipients");
            final List<Player> alivePlayers = (List<Player>) Bukkit.getOnlinePlayers().stream().toList();
            alivePlayers.forEach(player -> event.getRecipients().remove(player));
            event.setMessage(String.format("(Death Chat) %s", event.getMessage()));
        }

        System.out.println(Arrays.toString(event.getRecipients().toArray()));
    }
}
