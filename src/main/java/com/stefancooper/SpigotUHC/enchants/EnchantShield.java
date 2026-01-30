package com.stefancooper.SpigotUHC.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

public class EnchantShield {

    private final Map<Enchantment, Integer> enchantsToAdd;

    public EnchantShield (final ItemStack shield, final Map<Enchantment, Integer> enchantsToAdd) throws Exception {
        if (shield.getType() != Material.SHIELD || shield.getItemMeta() == null || !shield.getEnchantments().isEmpty()) {
            throw new Exception("Provided item to enchant is not a shield, does not contain metadata, or is already enchanted.");
        }

        /* -- Additional logic for adding more than just the 'hinted' enchants would go here -- */

        this.enchantsToAdd = enchantsToAdd;
    }

    public Map<Enchantment, Integer> getEnchantsToAdd () {
        return enchantsToAdd;
    }

}
