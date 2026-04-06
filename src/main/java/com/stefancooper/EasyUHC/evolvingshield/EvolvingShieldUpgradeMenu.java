package com.stefancooper.EasyUHC.evolvingshield;

import com.stefancooper.EasyUHC.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class EvolvingShieldUpgradeMenu {

    final Config config;
    final Player player;

    public EvolvingShieldUpgradeMenu(final Config config, final Player player) {
        this.config = config;
        this.player = player;
        final Optional<ItemStack> shield = EvolvingShield.getEvolvingShieldFromPlayer(config, player);
        if (shield.isPresent()) {
            final NamespacedKey stageKey = config.getManagedResources().getKeys().getEvolvingShieldUpgradeStageKey();
            final ItemMeta shieldMeta = shield.get().getItemMeta();
            final int stage = shieldMeta.getPersistentDataContainer().get(stageKey, PersistentDataType.INTEGER);
            final Inventory inv = Bukkit.createInventory(null, 27, "§8Evolve your shield!");

            switch (stage) {
                case 0:
                    inv.setItem(12, createUpgradeItem(
                            Material.PISTON,
                            "§6Knockback",
                            "§7Apply knockback upon blocking attacks",
                            Constants.KNOCKBACK
                    ));

                    inv.setItem(14, createUpgradeItem(
                            Material.CACTUS,
                            "§6Thorns",
                            "§7Apply thorns upon blocking attacks",
                            Constants.THORNS
                    ));
                case 1:
                    break;
            }


            player.openInventory(inv);
        }
    }

    private ItemStack createUpgradeItem(final Material mat, final String name, final String desc, final String key) {
        final ItemStack item = new ItemStack(mat);
        final ItemMeta meta = item.getItemMeta();

        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(config.getManagedResources().getKeys().getEvolvingShieldUpgradeTypeKey(), PersistentDataType.STRING, key);
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add("§7" + desc);
        lore.add("");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static void applyEnchantments(final Config config, final Player player, final ItemStack selectedEnchantment) {
        final String type = selectedEnchantment.getItemMeta().getPersistentDataContainer().get(config.getManagedResources().getKeys().getEvolvingShieldUpgradeTypeKey(), PersistentDataType.STRING);
        final Optional<ItemStack> getShield = EvolvingShield.getEvolvingShieldFromPlayer(config, player);
        if (getShield.isPresent()) {
            final ItemStack shield = getShield.get();
            switch (type) {
                case Constants.THORNS:
                    shield.addUnsafeEnchantment(Enchantment.THORNS, 1);
                    break;
                case Constants.KNOCKBACK:
                    shield.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                    break;
                case null:
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            // enchantment applied so upgrade no longer available
            ItemMeta shieldMeta = shield.getItemMeta();
            EvolvingShield.incrementUpgradeStage(config, shieldMeta);
            EvolvingShield.setUpgradeAvailable(config, shieldMeta, false);
            EvolvingShield.updateLore(config, shieldMeta, false);
            shield.setItemMeta(shieldMeta);
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        } else {
            config.getPlugin().getLogger().log(Level.WARNING, "Could not find shield to evolve");
        }
    }
}
