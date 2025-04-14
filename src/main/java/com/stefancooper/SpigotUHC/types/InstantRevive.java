package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
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


public class InstantRevive {

    public interface ReviveCallback {
        void callback();
    }
    public final Config config;
    public final Player reviver;
    public final Player revivee;
    public final ArmorStand armorStand;

    private final int reviveHealth;
    private final int reviveLoseMaxHealth;

    public InstantRevive(Config config, Player reviver, String revivee, boolean playSound, ArmorStand armorStand) {
        this.config = config;
        this.reviver = reviver;
        this.armorStand = armorStand;
        this.revivee = Bukkit.getPlayer(revivee);
        this.reviveHealth = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_HP.configName)).orElse("2"));
        this.reviveLoseMaxHealth = Integer.parseInt(Optional.ofNullable(config.getProp(REVIVE_LOSE_MAX_HEALTH.configName)).orElse("2"));

        if (this.revivee == null) {
            reviver.sendMessage(String.format("%s is offline, so cannot be revived", revivee));
            return;
        }

        if (playSound) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
            });
        }

        revivePlayer();

    }

    void revivePlayer () {
        // double check that the reviver still has the player head
        Bukkit.broadcastMessage(String.format("%s has been revived!", revivee.getDisplayName()));

        // Revivee effects
        revivee.getInventory().clear();
        revivee.spigot().respawn();
        revivee.teleport(armorStand.getLocation());
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

        armorStand.setVisible(false);
        armorStand.setHealth(0);
    }


}
