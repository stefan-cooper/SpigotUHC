package com.stefancooper.SpigotUHC.enchants;

import com.stefancooper.SpigotUHC.ManagedResources;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnchantTNT {

    private final HashMap<Enchantment, Integer> enchantsToAdd;

    public EnchantTNT(final ItemStack tnt, final Map<Enchantment, Integer> enchantsToAdd, final ManagedResources managedResources, final Enchantment enchantmentHint, final int levelHint) throws Exception {
        if (tnt.getType() != Material.TNT || tnt.getItemMeta() == null || !tnt.getEnchantments().isEmpty()) {
            throw new Exception("Provided item to enchant is not tnt, does not contain metadata, or is already enchanted.");
        }
        final Random ran = new Random();
        final Enchantment quickboom = managedResources.getQuickboomEnchantment();
        final Enchantment blastwave = managedResources.getBlastwaveEnchantment();
        final boolean isQuickboom = enchantmentHint.key().equals(quickboom.key());
        final boolean isBlastwave = enchantmentHint.key().equals(blastwave.key());
        // 1/3 chance of getting an additional enchantment
        final boolean shouldAddAdditionalEnchantment = ran.nextInt(1,3 + 1) == 1;

        this.enchantsToAdd = new HashMap<>(enchantsToAdd);

        if (shouldAddAdditionalEnchantment) {
            if (isBlastwave) this.enchantsToAdd.put(quickboom,
                    ran.nextInt(Math.max(quickboom.getStartLevel(), levelHint - 2), Math.min(quickboom.getMaxLevel(), levelHint) + 1));
            if (isQuickboom) this.enchantsToAdd.put(blastwave,
                    ran.nextInt(Math.max(blastwave.getStartLevel(), levelHint - 2), Math.min(blastwave.getMaxLevel(), levelHint) + 1));
        }
    }

    public Map<Enchantment, Integer> getEnchantsToAdd () {
        return enchantsToAdd;
    }

}
