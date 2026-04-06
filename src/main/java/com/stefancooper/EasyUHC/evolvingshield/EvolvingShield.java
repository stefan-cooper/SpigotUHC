package com.stefancooper.EasyUHC.evolvingshield;

import com.stefancooper.EasyUHC.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        updateLore(shieldMeta, 0, false);
        shieldMeta.setEnchantmentGlintOverride(true);
        shieldMeta.setUnbreakable(true);
        shieldMeta.setFireResistant(true);
        shieldMeta.getPersistentDataContainer().set(config.getManagedResources().getKeys().getEvolvingShieldUserKey(), PersistentDataType.STRING, player.getName());
        shieldMeta.getPersistentDataContainer().set(config.getManagedResources().getKeys().getEvolvingShieldXPKey(), PersistentDataType.INTEGER, 0);
        shieldMeta.getPersistentDataContainer().set(config.getManagedResources().getKeys().getEvolvingShieldUpgradeReadyKey(), PersistentDataType.BOOLEAN, false);
        shieldMeta.getPersistentDataContainer().set(config.getManagedResources().getKeys().getEvolvingShieldUpgradeStageKey(), PersistentDataType.INTEGER, 0);

        try {
            shieldMeta.setRarity(ItemRarity.EPIC);
        } catch (Exception e) {
            // noop
            // for some reason, this function is not implemented in MockBukkit but it is not worth us mocking
        }
        shield.setItemMeta(shieldMeta);
        player.getInventory().addItem(shield);
    }

    public static void updateLore(final Config config, final ItemMeta shieldMeta, final boolean isUpgradeAvailable) {
        final NamespacedKey xpKey = config.getManagedResources().getKeys().getEvolvingShieldXPKey();
        final int xp = shieldMeta.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
        updateLore(shieldMeta, xp, isUpgradeAvailable);
    }

    public static ItemMeta updateLore(final ItemMeta shieldMeta, final int xp, final boolean isUpgradeAvailable) {
        final List<Component> lore = new ArrayList<>(List.of(
                Component.text(""),
                Component.text("Level this shield up by gaining XP,"),
                Component.text("dealing damage to players and getting kills!"),
                Component.text(""))
        );
        if (isUpgradeAvailable) {
            lore.addAll(List.of(
                Component.text("§6SHIELD UPGRADE AVAILABLE!"),
                Component.text("§6Shift-click the shield to upgrade"),
                Component.text("")
            ));
        }
        lore.add(Component.text(String.format("Current XP: %s", xp)));

        shieldMeta.lore(lore);
        return shieldMeta;
    }

    public static void setUpgradeAvailable(final Config config, final ItemMeta shieldMeta, final boolean set) {
        final NamespacedKey upgradeAvailable = config.getManagedResources().getKeys().getEvolvingShieldUpgradeReadyKey();
        shieldMeta.getPersistentDataContainer().set(upgradeAvailable, PersistentDataType.BOOLEAN, set);
    }

    public static void incrementUpgradeStage(final Config config, final ItemMeta shieldMeta) {
        final NamespacedKey upgradeStage = config.getManagedResources().getKeys().getEvolvingShieldUpgradeStageKey();
        shieldMeta.getPersistentDataContainer().set(upgradeStage, PersistentDataType.INTEGER,
                shieldMeta.getPersistentDataContainer().get(upgradeStage, PersistentDataType.INTEGER) + 1);
    }

    public static Optional<ItemStack> getEvolvingShieldFromPlayer(final Config config, final Player player) {
        final NamespacedKey xpKey = config.getManagedResources().getKeys().getEvolvingShieldXPKey();
        final NamespacedKey userKey = config.getManagedResources().getKeys().getEvolvingShieldUserKey();
        final List<ItemStack> items = new ArrayList<>(Arrays.stream(player.getInventory().getStorageContents()).filter(Objects::nonNull).toList());
        items.add(player.getInventory().getItemInOffHand());
        final List<ItemStack> shields = items.stream().filter(Objects::nonNull).filter(item -> item.getType().equals(Material.SHIELD)).toList();
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

    public static boolean isUpgradeAvailable(final Config config, final ItemStack shield) {
        final NamespacedKey upgradeAvailable = config.getManagedResources().getKeys().getEvolvingShieldUpgradeReadyKey();
        final ItemMeta shieldMeta = shield.getItemMeta();
        return Boolean.TRUE.equals(shieldMeta.getPersistentDataContainer().get(upgradeAvailable, PersistentDataType.BOOLEAN));
    }

    public static void updateXP(final Config config, final ItemStack shield, final int addXp, final Player player) {
        final NamespacedKey xpKey = config.getManagedResources().getKeys().getEvolvingShieldXPKey();
        final NamespacedKey upgradeAvailableKey = config.getManagedResources().getKeys().getEvolvingShieldUpgradeReadyKey();
        final NamespacedKey currentUpgradeStageKey = config.getManagedResources().getKeys().getEvolvingShieldUpgradeStageKey();
        final ItemMeta shieldMeta = shield.getItemMeta();
        final int currentXP = shieldMeta.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
        final int updatedXP = currentXP + addXp;
        boolean setUpgrade = Boolean.TRUE.equals(shieldMeta.getPersistentDataContainer().get(upgradeAvailableKey, PersistentDataType.BOOLEAN));
        final int currentUpgradeStage = shieldMeta.getPersistentDataContainer().get(currentUpgradeStageKey, PersistentDataType.INTEGER);
        shieldMeta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, updatedXP);
        if (updatedXP >= STAGE_1 && updatedXP < STAGE_2 && currentUpgradeStage == 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            setUpgrade = true;
        } else if (updatedXP >= STAGE_2 && updatedXP < STAGE_3 && currentUpgradeStage == 1) {
            setUpgrade = true;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        } else if (updatedXP >= STAGE_3 && updatedXP < STAGE_4 && currentUpgradeStage == 2) {
            setUpgrade = true;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        } else if (updatedXP >= STAGE_4 && updatedXP < STAGE_5 && currentUpgradeStage == 3) {
            setUpgrade = true;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        } else if (updatedXP >= STAGE_5 && updatedXP < STAGE_6 && currentUpgradeStage == 4) {
            setUpgrade = true;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        }
        if (setUpgrade) {
            setUpgradeAvailable(config, shieldMeta, true);
            updateLore(shieldMeta, updatedXP, true);
        } else {
            updateLore(shieldMeta, updatedXP, false);
        }
        shield.setItemMeta(shieldMeta);
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

        return meta.getPersistentDataContainer().has(config.getManagedResources().getKeys().getEvolvingShieldUserKey());
    }
}
