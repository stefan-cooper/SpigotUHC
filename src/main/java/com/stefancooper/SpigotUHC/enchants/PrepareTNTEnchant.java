package com.stefancooper.SpigotUHC.enchants;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class PrepareTNTEnchant {
    private final EnchantmentOffer[] offers;

    public PrepareTNTEnchant(final Config config, final ItemStack tnt, final int seed, final int enchantmentBonus) {
        offers = new EnchantmentOffer[3];
        if (!tnt.getEnchantments().isEmpty()) {
            return;
        }
        final Optional<Enchantment> quickboomEnchantment = Optional.ofNullable(config.getManagedResources().getQuickboomEnchantment());
        final Optional<Enchantment> blastwaveEnchantment = Optional.ofNullable(config.getManagedResources().getBlastwaveEnchantment());

        if (quickboomEnchantment.isPresent() && blastwaveEnchantment.isPresent()) {
            Random ran = new Random();
            final Enchantment quickboom = quickboomEnchantment.get();
            final Enchantment blastwave = blastwaveEnchantment.get();
            final List<EnchantmentOffer> ZERO_BONUS_BOTTOM_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 1, ran.nextInt(1,2)),
                    new EnchantmentOffer(blastwave, 1, ran.nextInt(1,2))
            );

            final List<EnchantmentOffer> ZERO_BONUS_MIDDLE_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 1, ran.nextInt(2,3)),
                    new EnchantmentOffer(quickboom, 2, ran.nextInt(2,4)),
                    new EnchantmentOffer(blastwave, 1, ran.nextInt(2,3)),
                    new EnchantmentOffer(blastwave, 2, ran.nextInt(2,4))
            );

            final List<EnchantmentOffer> ZERO_BONUS_TOP_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 2, ran.nextInt(4,6)),
                    new EnchantmentOffer(blastwave, 2, ran.nextInt(4,6))

            );

            final List<EnchantmentOffer> ONE_BONUS_BOTTOM_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 1, ran.nextInt(2,3)),
                    new EnchantmentOffer(quickboom, 2, ran.nextInt(2,4)),
                    new EnchantmentOffer(blastwave, 1, ran.nextInt(2,3)),
                    new EnchantmentOffer(blastwave, 2, ran.nextInt(2,4))
            );

            final List<EnchantmentOffer> ONE_BONUS_MIDDLE_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 2, ran.nextInt(4,6)),
                    new EnchantmentOffer(blastwave, 2, ran.nextInt(4,6))
            );

            final List<EnchantmentOffer> ONE_BONUS_TOP_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 2, ran.nextInt(6,8)),
                    new EnchantmentOffer(quickboom, 3, ran.nextInt(6,10)),
                    new EnchantmentOffer(blastwave, 2, ran.nextInt(6,8)),
                    new EnchantmentOffer(blastwave, 3, ran.nextInt(6,10))
            );

            final List<EnchantmentOffer> TWO_BONUS_BOTTOM_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 2, ran.nextInt(3,4)),
                    new EnchantmentOffer(blastwave, 2, ran.nextInt(3,4))
            );

            final List<EnchantmentOffer> TWO_BONUS_MIDDLE_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 2, ran.nextInt(4,6)),
                    new EnchantmentOffer(quickboom, 3, ran.nextInt(5,8)),
                    new EnchantmentOffer(blastwave, 2, ran.nextInt(4,6)),
                    new EnchantmentOffer(blastwave, 3, ran.nextInt(5,8))
            );

            final List<EnchantmentOffer> TWO_BONUS_TOP_POSSIBLE_ENCHANTS = List.of(
                    new EnchantmentOffer(quickboom, 3, ran.nextInt(8,10)),
                    new EnchantmentOffer(quickboom, 4, ran.nextInt(8,12)),
                    new EnchantmentOffer(blastwave, 3, ran.nextInt(8,10)),
                    new EnchantmentOffer(blastwave, 4, ran.nextInt(8,12))

            );

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
