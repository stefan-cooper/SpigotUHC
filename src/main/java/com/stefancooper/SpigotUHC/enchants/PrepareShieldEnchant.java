package com.stefancooper.SpigotUHC.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class PrepareShieldEnchant {

    private final EnchantmentOffer[] offers;

    final List<EnchantmentOffer> ZERO_BONUS_BOTTOM_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.KNOCKBACK, 1, 2)
    );

    final List<EnchantmentOffer> ZERO_BONUS_MIDDLE_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.THORNS, 1, 3),
            new EnchantmentOffer(Enchantment.KNOCKBACK, 1, 3)
    );

    final List<EnchantmentOffer> ZERO_BONUS_TOP_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.KNOCKBACK, 1, 4),
            new EnchantmentOffer(Enchantment.THORNS, 1, 4)

    );

    final List<EnchantmentOffer> ONE_BONUS_BOTTOM_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.KNOCKBACK, 1, 3),
            new EnchantmentOffer(Enchantment.THORNS, 1, 3)
    );

    final List<EnchantmentOffer> ONE_BONUS_MIDDLE_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.KNOCKBACK, 2, 7),
            new EnchantmentOffer(Enchantment.KNOCKBACK, 1, 7),
            new EnchantmentOffer(Enchantment.THORNS, 2, 7),
            new EnchantmentOffer(Enchantment.THORNS, 1, 7)
    );

    final List<EnchantmentOffer> ONE_BONUS_TOP_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.KNOCKBACK, 1, 8),
            new EnchantmentOffer(Enchantment.KNOCKBACK, 2, 8),
            new EnchantmentOffer(Enchantment.THORNS, 1, 8),
            new EnchantmentOffer(Enchantment.THORNS, 2, 8)
    );

    final List<EnchantmentOffer> TWO_BONUS_BOTTOM_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.KNOCKBACK, 2, 5),
            new EnchantmentOffer(Enchantment.THORNS, 2, 5)
    );

    final List<EnchantmentOffer> TWO_BONUS_MIDDLE_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.KNOCKBACK, 2, 8),
            new EnchantmentOffer(Enchantment.THORNS, 2, 8),
            new EnchantmentOffer(Enchantment.THORNS, 3, 8)
    );

    final List<EnchantmentOffer> TWO_BONUS_TOP_POSSIBLE_ENCHANTS = List.of(
            new EnchantmentOffer(Enchantment.THORNS, 2, 12),
            new EnchantmentOffer(Enchantment.THORNS, 3, 12),
            new EnchantmentOffer(Enchantment.KNOCKBACK, 2, 12)

    );

    public PrepareShieldEnchant (final ItemStack shield, final int seed, final int enchantmentBonus) {
        offers = new EnchantmentOffer[3];
        if (!shield.getEnchantments().isEmpty()) {
            return;
        }
        switch (enchantmentBonus) {
            case 0:
                offers[0] = getEnchantmentOfferFromSeed(seed, ZERO_BONUS_BOTTOM_POSSIBLE_ENCHANTS);
                offers[1] = getEnchantmentOfferFromSeed(seed, ZERO_BONUS_MIDDLE_POSSIBLE_ENCHANTS);
                offers[2] = getEnchantmentOfferFromSeed(seed, ZERO_BONUS_TOP_POSSIBLE_ENCHANTS);
                break;
            case 1:
                offers[0] = getEnchantmentOfferFromSeed(seed, ONE_BONUS_BOTTOM_POSSIBLE_ENCHANTS);
                offers[1] = getEnchantmentOfferFromSeed(seed, ONE_BONUS_MIDDLE_POSSIBLE_ENCHANTS);
                offers[2] = getEnchantmentOfferFromSeed(seed, ONE_BONUS_TOP_POSSIBLE_ENCHANTS);
                break;
            case 2:
            default:
                offers[0] = getEnchantmentOfferFromSeed(seed, TWO_BONUS_BOTTOM_POSSIBLE_ENCHANTS);
                offers[1] = getEnchantmentOfferFromSeed(seed, TWO_BONUS_MIDDLE_POSSIBLE_ENCHANTS);
                offers[2] = getEnchantmentOfferFromSeed(seed, TWO_BONUS_TOP_POSSIBLE_ENCHANTS);
        }
    }

    public EnchantmentOffer[] getOffers() {
        return offers;
    }

    private EnchantmentOffer getEnchantmentOfferFromSeed(final int seed, final List<EnchantmentOffer> offers) {
        // enchantment seeds are a 32-bit signed int, so lets convert it into an unsigned int so that we can get an element from our list
        long unsignedSeed = seed & 0xFFFFFFFFL;
        int index = (int) (unsignedSeed % offers.size());
        return offers.get(index);
    }

}
