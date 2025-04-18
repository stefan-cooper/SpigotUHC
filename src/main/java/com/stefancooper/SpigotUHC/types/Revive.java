package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_HP;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_SIZE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_X;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_Y;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOCATION_Z;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOSE_MAX_HEALTH;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_TIME;


public class Revive {

    public interface ReviveCallback {
        void callback();
    }
    public final Config config;
    public final Player reviver;
    public final Player revivee;
    public final ItemStack playerHead;
    private final BukkitTask reviveTask;
    private final BukkitTask playParticles;
    private final ReviveCallback reviveCallback;

    private final int reviveHealth;
    private final int reviveLoseMaxHealth;
    private final List<Integer> reviveXs;
    private final List<Integer> reviveYs;
    private final List<Integer> reviveZs;
    private final int reviveSize;
    private final World world;

    public Revive(Config config, Player reviver, String revivee, ItemStack playerHead, ReviveCallback reviveCallback, boolean playSound) {
        this.config = config;
        this.reviver = reviver;
        this.revivee = Bukkit.getPlayer(revivee);
        this.world = config.getWorlds().getOverworld();
        this.reviveHealth = config.getProperty(REVIVE_HP, Defaults.REVIVE_HP);

        this.reviveXs = getReviveXs(config);
        this.reviveYs = getReviveYs(config);
        this.reviveZs = getReviveZs(config);
        this.reviveSize = config.getProperty(REVIVE_LOCATION_SIZE, Defaults.REVIVE_LOCATION_SIZE);
        this.reviveLoseMaxHealth = config.getProperty(REVIVE_LOSE_MAX_HEALTH, Defaults.REVIVE_LOSE_MAX_HEALTH);
        this.playerHead = playerHead;
        int reviveTime = config.getProperty(REVIVE_TIME, Defaults.REVIVE_TIME);

        this.reviveTask = config.getManagedResources().runTaskLater(revivePlayer(), reviveTime);
        this.playParticles = config.getManagedResources().runRepeatingTask(() -> {
            for (int reviveNumber = 0; reviveNumber < reviveXs.size(); reviveNumber++) {
                for (int x = reviveXs.get(reviveNumber) - reviveSize; x < reviveXs.get(reviveNumber) + reviveSize; x++ ) {
                    for (int z = reviveZs.get(reviveNumber) - reviveSize; z < reviveZs.get(reviveNumber) + reviveSize; z++) {
                        Location loc = new Location (world, x, reviveYs.get(reviveNumber), z);
                        if (isInsideReviveZone(config, loc)) {
                            try {
                                world.spawnParticle(Particle.GLOW, loc, 5);
                            } catch (Exception e) {
                                // noop
                                // for some reason, this function is not implemented in MockBukkit but it is not worth us mocking
                            }

                        }
                    }
                }
            }
        }, 1);
        this.reviveCallback = reviveCallback;

        for (int reviveNumber = 0; reviveNumber < reviveXs.size(); reviveNumber++) {
            for (int x = reviveXs.get(reviveNumber) - reviveSize; x < reviveXs.get(reviveNumber) + reviveSize; x++ ) {
                for (int z = reviveZs.get(reviveNumber) - reviveSize; z < reviveZs.get(reviveNumber) + reviveSize; z++) {
                    Location loc = new Location (world, x, reviveYs.get(reviveNumber), z);
                    if (isInsideReviveZone(config, loc)) {
                        world.getBlockAt(x, reviveYs.get(reviveNumber) - 1, z).setType(Material.BEDROCK);
                    }
                }
            }
        }

        if (this.revivee == null) {
            reviver.sendMessage(String.format("%s is offline, so cannot be revived", revivee));
            return;
        } else {
            Bukkit.broadcastMessage(String.format("%s is being revived!", this.revivee.getDisplayName()));
        }

        if (playSound) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
            });
        }
    }

    Runnable revivePlayer () {
        return () -> {
            playParticles.cancel();
            // double check that the reviver still has the player head
            if (reviver.getInventory().contains(playerHead)) {
                Bukkit.broadcastMessage(String.format("%s has been revived!", revivee.getDisplayName()));
                reviveCallback.callback();

                // Revivee effects
                revivee.getInventory().clear();
                revivee.spigot().respawn();
                revivee.teleport(reviver.getLocation());
                revivee.setGameMode(GameMode.SURVIVAL);
                if (revivee.getMaxHealth() < reviveHealth) {
                    revivee.setHealth(revivee.getMaxHealth());
                } else {
                    revivee.setHealth(reviveHealth);
                }
                revivee.setFoodLevel(20);
                revivee.setExp(0);
                revivee.setLevel(0);

                if (revivee.getMaxHealth() > reviveLoseMaxHealth) {
                    revivee.setMaxHealth(revivee.getMaxHealth() - reviveLoseMaxHealth);
                } else {
                    revivee.setMaxHealth(1);
                }

                revivee.spawnParticle(Particle.POOF, revivee.getLocation(), 1000);

                reviver.getInventory().remove(playerHead);

                // reviver effects
                if (playerHead.getAmount() > 1) {
                    playerHead.setAmount(playerHead.getAmount() - 1);
                    reviver.getInventory().addItem(playerHead);
                }

            }
        };
    }

    public void cancelRevive() {
        reviver.sendMessage(String.format("Revive of %s has been cancelled", revivee.getDisplayName()));
        reviveTask.cancel();
        playParticles.cancel();
    }

    private static List<Integer> getReviveXs(Config config) {
        String reviveX = config.getProperty(REVIVE_LOCATION_X);
        if (reviveX == null) reviveX = "0";
        try {
            return Arrays.stream(reviveX.split(",")).map(Integer::parseInt).toList();
        } catch (Exception e) {
            System.out.println("Exception caught setting the revive config, check your revive location config. Setting default value");
            return List.of(0);
        }
    }

    private static List<Integer> getReviveYs(Config config) {
        String reviveY = config.getProperty(REVIVE_LOCATION_Y);
        if (reviveY == null) reviveY = "0";
        try {
            return Arrays.stream(reviveY.split(",")).map(Integer::parseInt).toList();
        } catch (Exception e) {
            System.out.println("Exception caught setting the revive config, check your revive location config. Setting default value");
            return List.of(64);
        }
    }

    private static List<Integer> getReviveZs(Config config) {
        String reviveZ = config.getProperty(REVIVE_LOCATION_Z);
        if (reviveZ == null) reviveZ = "0";
        try {
            return Arrays.stream(reviveZ.split(",")).map(Integer::parseInt).toList();
        } catch (Exception e) {
            System.out.println("Exception caught setting the revive config, check your revive location config. Setting default value");
            return List.of(0);
        }
    }

    public static boolean isInsideReviveZone(Config config, Location location) {
        final List<Integer> reviveXs = getReviveXs(config);
        final List<Integer> reviveYs = getReviveYs(config);
        final List<Integer> reviveZs = getReviveZs(config);
        final int newPositionX = location.getBlockX();
        final int newPositionY = location.getBlockY();
        final int newPositionZ = location.getBlockZ();
        final int size = config.getProperty(REVIVE_LOCATION_SIZE, Defaults.REVIVE_LOCATION_SIZE);
        for (int i = 0; i < reviveXs.size(); i++) {
            final int reviveX = reviveXs.get(i);
            final int reviveY = reviveYs.get(i);
            final int reviveZ = reviveZs.get(i);
            final int minReviveX = reviveX - (reviveX < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            final int maxReviveX = reviveX + (reviveX < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            final int minReviveZ = reviveZ - (reviveZ < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            final int maxReviveZ = reviveZ + (reviveZ < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            if ((newPositionX >= minReviveX && newPositionX <= maxReviveX) &&
                    (newPositionZ >= minReviveZ && newPositionZ <= maxReviveZ) &&
                    (newPositionY <= reviveY + 2 && newPositionY >= reviveY - 2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNearReviveZone(Config config, Location location) {
        final List<Integer> reviveXs = getReviveXs(config);
        final List<Integer> reviveYs = getReviveYs(config);
        final List<Integer> reviveZs = getReviveZs(config);
        final int size = config.getProperty(REVIVE_LOCATION_SIZE, Defaults.REVIVE_LOCATION_SIZE);
        final int newPositionX = location.getBlockX();
        final int newPositionY = location.getBlockY();
        final int newPositionZ = location.getBlockZ();
        for (int i = 0; i < reviveXs.size(); i++) {
            final int reviveX = reviveXs.get(i);
            final int reviveY = reviveYs.get(i);
            final int reviveZ = reviveZs.get(i);
            final int minReviveX = reviveX - (reviveX < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            final int maxReviveX = reviveX + (reviveX < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            final int minReviveZ = reviveZ - (reviveZ < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            final int maxReviveZ = reviveZ + (reviveZ < 0 ? Math.floorDiv(size, 2) : Math.ceilDiv(size, 2));
            if ((newPositionX >= minReviveX - 8 && newPositionX <= maxReviveX + 8) &&
                    (newPositionZ >= minReviveZ - 8 && newPositionZ <= maxReviveZ + 8) &&
                    (newPositionY >= reviveY - 8 && newPositionY <= reviveY + 8)) {
                return true;
            }
        }
        return false;
    }
}
