package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_HP;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.REVIVE_LOSE_MAX_HEALTH;


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
        this.reviveHealth = config.getProperty(REVIVE_HP, Defaults.REVIVE_HP);
        this.reviveLoseMaxHealth = config.getProperty(REVIVE_LOSE_MAX_HEALTH, Defaults.REVIVE_LOSE_MAX_HEALTH);

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
