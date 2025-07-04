package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.enums.DeathAction;

import com.stefancooper.SpigotUHC.types.AdditionalEnchants;
import com.stefancooper.SpigotUHC.types.BossBarBorder;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

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

    // DamageSource API is experimental, so this may break in a spigot update
    @SuppressWarnings("UnstableApiUsage")
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

        // Set Happy Ghasts to 100 hearts
        if (event.getEntity().getType().equals(EntityType.HAPPY_GHAST)) {
            final HappyGhast ghast = (HappyGhast) event.getEntity();
            final AttributeInstance maxHealth = ghast.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(200);
            }
            ghast.setHealth(200);
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

    private boolean isBlockLeaves(Block block) {
        return switch (block.getType()) {
            case Material.ACACIA_LEAVES, Material.AZALEA_LEAVES, Material.BIRCH_LEAVES, Material.CHERRY_LEAVES,
                 Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.SPRUCE_LEAVES,
                 Material.FLOWERING_AZALEA_LEAVES, Material.PALE_OAK_LEAVES -> true;
            default -> false;
        };
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Handle all inventory click types (shift-click, drag-drop, etc.)
        Bukkit.getScheduler().runTaskLater(config.getPlugin(), () -> {
            ItemStack helmet = player.getInventory().getHelmet();

            if (AdditionalEnchants.isNightVisionGoggles(helmet)) {
                if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.NIGHT_VISION,
                            Integer.MAX_VALUE,
                            0,
                            true,
                            false
                    ));
                }
            } else {
                if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                }
            }
        }, 1L); // Delay by 1 tick so inventory updates first
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        applyNightVisionIfWearingHelmet(event.getPlayer());
    }

    private void applyNightVisionIfWearingHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (AdditionalEnchants.isNightVisionGoggles(helmet)) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    Integer.MAX_VALUE,
                    0,
                    true,
                    false
            ));
        }
    }

    @EventHandler
    public void onShieldBlock(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        if (!defender.isBlocking()) return;

        ItemStack shield = defender.getInventory().getItemInOffHand();
        if (shield == null || shield.getType() != Material.SHIELD || !shield.hasItemMeta()) return;

        ItemMeta meta = shield.getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        int knockbackLevel = getEnchantLevelFromLore(meta, "Knockback");
        int thornsLevel = getEnchantLevelFromLore(meta, "Thorns");

        // Thorns damage scaling
        if (thornsLevel > 0) {
            double damage = 1.0 + 0.5 * (thornsLevel - 1); // Start with 1.0, increase by 0.5 per level
            attacker.damage(damage, defender);
            attacker.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, attacker.getLocation().add(0, 1, 0), 5 + 2 * thornsLevel);
        }

        // Knockback scaling — DELAY to avoid override by Bukkit
        if (knockbackLevel > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Vector direction = attacker.getLocation().toVector().subtract(defender.getLocation().toVector()).normalize().multiply(1);
                    attacker.setVelocity(direction.multiply(0.6 + 0.4 * knockbackLevel));
                }
            }.runTaskLater(config.getPlugin(), 1L); // Delay 1 tick to avoid Bukkit override
        }
    }


    @EventHandler
    public void onTridentLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player player)) return;

        // Check if this trident has "Ender Trident" in its lore
        //ItemStack item = player.getInventory().getItemInMainHand();
        //if (item == null || item.getType() != Material.TRIDENT || !item.hasItemMeta()) return;

        ItemMeta meta = trident.getItem().getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        boolean hasEnderTrident = meta.getLore().stream()
                .map(ChatColor::stripColor)
                .anyMatch(line -> line.toLowerCase().startsWith("ender trident"));

        if (!hasEnderTrident) return;

        Location destination = trident.getLocation().clone();

        // Delay teleport slightly to avoid server collision weirdness
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(destination);
                player.damage(1.0);
                player.getWorld().playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.PORTAL, destination.clone().add(0, 1, 0), 40, 0.5, 0.5, 0.5);
            }
        }.runTaskLater(config.getPlugin(), 1L);
        event.setCancelled(true);
    }

    private int getEnchantLevelFromLore(ItemMeta meta, String enchantName) {
        for (String line : meta.getLore()) {
            String stripped = ChatColor.stripColor(line).toLowerCase();
            if (stripped.startsWith(enchantName.toLowerCase())) {
                String[] parts = stripped.split(" ");
                if (parts.length > 1) {
                    try {
                        return romanToInt(parts[1]); // Expecting e.g., "Knockback II"
                    } catch (Exception e) {
                        return 1;
                    }
                }
                return 1;
            }
        }
        return 0;
    }

    private int romanToInt(String roman) {
        return switch (roman.toUpperCase()) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            default -> 1;
        };
    }
}