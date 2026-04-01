package com.stefancooper.EasyUHC.types;

import com.stefancooper.EasyUHC.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class EvolvingShield {

    public static int STAGE_1 = 100;
    public static int STAGE_2 = 400;
    public static int STAGE_3 = 700;
    public static int STAGE_4 = 1000;
    public static int STAGE_5 = 1300;
    public static int STAGE_6 = 1600;

    public static void createEvolvingShield(final Config config, final Player player) {
        final ItemStack shield = new ItemStack(Material.SHIELD, 1);
        ItemMeta shieldMeta = shield.getItemMeta();
        assert shieldMeta != null;
        shieldMeta.displayName(Component.text(String.format("%s's shield", player.getName())));
        updateLore(shieldMeta, 0);
        shieldMeta.setEnchantmentGlintOverride(true);
        shieldMeta.setUnbreakable(true);
        shieldMeta.setFireResistant(true);
        shieldMeta.getPersistentDataContainer().set(config.getManagedResources().getEvolvingShieldUserKey(), PersistentDataType.STRING, player.getName());
        shieldMeta.getPersistentDataContainer().set(config.getManagedResources().getEvolvingShieldXPKey(), PersistentDataType.INTEGER, 0);
        try {
            shieldMeta.setRarity(ItemRarity.EPIC);
        } catch (Exception e) {
            // noop
            // for some reason, this function is not implemented in MockBukkit but it is not worth us mocking
        }
        shield.setItemMeta(shieldMeta);
        player.getInventory().addItem(shield);
    }

    public static void updateLore(final ItemMeta shieldMeta, final int xp) {
        final List<Component> lore = new ArrayList<>(List.of(
                Component.text(""),
                Component.text("Level this shield up by gaining XP,"),
                Component.text("dealing damage to players and getting kills!"),
                Component.text(""),
                Component.text(String.format("Current XP: %s", xp))
        ));
        shieldMeta.lore(lore);
    }

    public static Optional<ItemStack> getEvolvingShieldFromPlayer(final Config config, final Player player) {
        final NamespacedKey xpKey = config.getManagedResources().getEvolvingShieldXPKey();
        final NamespacedKey userKey = config.getManagedResources().getEvolvingShieldUserKey();
        final List<ItemStack> shields = Arrays.stream(player.getInventory().getStorageContents()).filter(Objects::nonNull).filter(item -> item.getType().equals(Material.SHIELD)).toList();
        final List<ItemStack> evolvingShields = shields.stream().filter(item ->
                item.getItemMeta().getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER) != null &&
                        item.getItemMeta().getPersistentDataContainer().get(userKey, PersistentDataType.STRING) != null &&
                        Objects.equals(item.getItemMeta().getPersistentDataContainer().get(userKey, PersistentDataType.STRING), player.getName())
        ).toList();

        if (evolvingShields.isEmpty()) {
            config.getPlugin().getLogger().log(Level.FINE, String.format("Could not find player %s shield to add XP to", player.getName()));
            return Optional.empty();
        }
        if (shields.size() > 1) {
            config.getPlugin().getLogger().log(Level.FINE, String.format("Player %s has too many evolving shields", player.getName()));
            return Optional.empty();
        }
        return Optional.ofNullable(evolvingShields.getFirst());
    }

    public static void updateXP(final Config config, final ItemStack shield, final int addXp, final Player player) {
        final NamespacedKey xpKey = config.getManagedResources().getEvolvingShieldXPKey();
        final ItemMeta shieldMeta = shield.getItemMeta();
        final Map<Enchantment, Integer> enchantments = shield.getEnchantments();
        final int currentXP = shieldMeta.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
        final int updatedXP = currentXP + addXp;
        shieldMeta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, updatedXP);
        updateLore(shieldMeta, updatedXP);
        shield.setItemMeta(shieldMeta);
        if (updatedXP >= STAGE_1 && updatedXP < STAGE_2) {
            if (enchantments.isEmpty()) player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            shield.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        } else if (updatedXP >= STAGE_2 && updatedXP < STAGE_3) {
            if (enchantments.get(Enchantment.KNOCKBACK).equals(1)) player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            shield.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        } else if (updatedXP >= STAGE_3 && updatedXP < STAGE_4) {
            if (enchantments.get(Enchantment.THORNS) == null) player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            shield.addUnsafeEnchantment(Enchantment.THORNS, 1);
        } else if (updatedXP >= STAGE_4 && updatedXP < STAGE_5) {
            if (enchantments.get(Enchantment.THORNS).equals(1)) player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            shield.addUnsafeEnchantment(Enchantment.THORNS, 2);
        } else if (updatedXP >= STAGE_5 && updatedXP < STAGE_6) {
            if (enchantments.get(Enchantment.THORNS).equals(2)) player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            shield.addUnsafeEnchantment(Enchantment.THORNS, 3);
        }
    }

    public enum EvolvingShieldXPType {
        EXPERIENCE,
        DAMAGE_HEARTS,
        KILL
    }

    public static int calculateXP(final EvolvingShieldXPType type, final int toAdd) {
        return switch (type) {
            case KILL:
                yield 250;
            case DAMAGE_HEARTS:
                yield toAdd * 200;
            case EXPERIENCE:
                yield toAdd;
        };
    }

    public static boolean isEvolvingShield(final Config config, final ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();

        return meta.getPersistentDataContainer().has(config.getManagedResources().getEvolvingShieldUserKey());
    }

}
