package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.Optional;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_HP;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_SIZE;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_X;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_Y;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOCATION_Z;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_LOSE_MAX_HEALTH;
import static com.stefancooper.SpigotUHC.resources.ConfigKey.REVIVE_TIME;


public class Revive {

    public interface ReviveCallback {
        void callback();
    }

    public final Player reviver;
    public final Player revivee;
    public final ItemStack playerHead;
    private final BukkitTask reviveTask;
    private final ReviveCallback reviveCallback;

    private final int reviveHealth;
    private final int reviveLoseMaxHealth;
    private final int reviveX;
    private final int reviveY;
    private final int reviveZ;
    private final World world;

    public Revive(Config config, Player reviver, String revivee, ItemStack playerHead, ReviveCallback reviveCallback, boolean playSound) {
        this.reviver = reviver;
        this.revivee = Bukkit.getPlayer(revivee);
        this.world = config.getWorlds().getOverworld();
        this.reviveHealth = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_HP.configName)).orElse("2"));
        this.reviveX = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_X.configName)).orElse("0"));
        this.reviveY = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_Y.configName)).orElse("64"));
        this.reviveZ = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_Z.configName)).orElse("0"));
        this.reviveLoseMaxHealth = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOSE_MAX_HEALTH.configName)).orElse("2"));
        this.playerHead = playerHead;
        int reviveTime = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_TIME.configName)).orElse("5"));

        this.reviveTask = config.getManagedResources().runTaskLater(revivePlayer(), reviveTime);
        this.reviveCallback = reviveCallback;

        if (revivee == null) {
            reviver.sendMessage(String.format("%s is offline, so cannot be revived", revivee));
            config.getManagedResources().cancelRevive();
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
            // double check that the reviver still has the player head
            if (reviver.getInventory().contains(playerHead)) {
                Bukkit.broadcastMessage(String.format("%s has been revived!", revivee.getDisplayName()));
                reviveCallback.callback();
                // TODO - q's:
                // - only revivable if death was non-pvp (?)
                // - only revivable one time (?)

                // Revivee effects
                revivee.getInventory().clear();
                revivee.spigot().respawn();
                revivee.teleport(new Location(world, reviveX, reviveY, reviveZ));
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

                // reviver effects
                reviver.getInventory().remove(playerHead);
            }
        };
    }

    public void cancelRevive() {
        reviveTask.cancel();
    }

    public static boolean isInsideReviveZone(Config config, Location location) {
        int reviveX = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_X.configName)).orElse("0"));
        int reviveY = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_Y.configName)).orElse("100"));
        int reviveZ = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_Z.configName)).orElse("0"));
        int size = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_SIZE.configName)).orElse("10"));
        int minReviveX = reviveX - Math.round((float) size / 2);
        int maxReviveX = reviveX + Math.round((float) size / 2);;
        int minReviveZ = reviveZ - Math.round((float) size / 2);;
        int maxReviveZ = reviveZ + Math.round((float) size / 2);;
        long newPositionX = Math.round(location.getX());
        long newPositionY = Math.round(location.getY());
        long newPositionZ = Math.round(location.getZ());
        return (newPositionX >= minReviveX && newPositionX <= maxReviveX) &&
               (newPositionZ >= minReviveZ && newPositionZ <= maxReviveZ) &&
               (newPositionY <= reviveY + 2 && newPositionY >= reviveY - 2);
    }

    public static boolean isNearReviveZone(Config config, Location location) {
        int reviveX = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_X.configName)).orElse("0"));
        int reviveY = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_Y.configName)).orElse("100"));
        int reviveZ = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_Z.configName)).orElse("0"));
        int size = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOCATION_SIZE.configName)).orElse("10"));
        int minReviveX = reviveX - Math.round((float) size / 2);
        int maxReviveX = reviveX + Math.round((float) size / 2);;
        int minReviveZ = reviveZ - Math.round((float) size / 2);;
        int maxReviveZ = reviveZ + Math.round((float) size / 2);;
        long newPositionX = Math.round(location.getX());
        long newPositionY = Math.round(location.getY());
        long newPositionZ = Math.round(location.getZ());
        return (newPositionX >= minReviveX - 8 && newPositionX <= maxReviveX + 8) &&
                (newPositionZ >= minReviveZ - 8 && newPositionZ <= maxReviveZ + 8) &&
                (newPositionY >= reviveY - 8 && newPositionY <= reviveY + 8);
    }

}
