package com.stefancooper.SpigotUHC.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantTNT {

    private final Map<Enchantment, Integer> enchantsToAdd;

    public EnchantTNT(final ItemStack tnt, final Map<Enchantment, Integer> enchantsToAdd) throws Exception {
        if (tnt.getType() != Material.TNT || tnt.getItemMeta() == null || !tnt.getEnchantments().isEmpty()) {
            throw new Exception("Provided item to enchant is not tnt, does not contain metadata, or is already enchanted.");
        }

        /* -- Additional logic for adding more than just the 'hinted' enchants would go here -- */

        this.enchantsToAdd = enchantsToAdd;
    }

    public Map<Enchantment, Integer> getEnchantsToAdd () {
        return enchantsToAdd;
    }

}
