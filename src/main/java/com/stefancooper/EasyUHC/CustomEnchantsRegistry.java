package com.stefancooper.EasyUHC;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import static com.stefancooper.EasyUHC.utils.Constants.BLASTWAVE_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.NAMESPACE;
import static com.stefancooper.EasyUHC.utils.Constants.QUICKBOOM_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_FIRE_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_JUMP_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_SLOWNESS_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_STRENGTH_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_SWIFTNESS_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_THUNDER_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_WATER_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_WEAKNESS_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SHIELD_WIND_ENCHANTMENT;

public class CustomEnchantsRegistry {

    final BootstrapContext context;
    final TypedKey<Enchantment> quickboomKey;
    final TypedKey<Enchantment> blastwaveKey;
    final TypedKey<Enchantment> shieldSlownessKey;
    final TypedKey<Enchantment> shieldWeaknessKey;
    final TypedKey<Enchantment> shieldJumpKey;
    final TypedKey<Enchantment> shieldStrengthKey;
    final TypedKey<Enchantment> shieldSwiftKey;
    final TypedKey<Enchantment> shieldFireKey;
    final TypedKey<Enchantment> shieldWindKey;
    final TypedKey<Enchantment> shieldThunderKey;
    final TypedKey<Enchantment> shieldWaterKey;


    public CustomEnchantsRegistry(final BootstrapContext context) {
        this.context = context;
        quickboomKey = createEnchantmentKey(QUICKBOOM_ENCHANTMENT);
        blastwaveKey = createEnchantmentKey(BLASTWAVE_ENCHANTMENT);
        shieldSwiftKey = createEnchantmentKey(SHIELD_SWIFTNESS_ENCHANTMENT);
        shieldJumpKey = createEnchantmentKey(SHIELD_JUMP_ENCHANTMENT);
        shieldStrengthKey = createEnchantmentKey(SHIELD_STRENGTH_ENCHANTMENT);
        shieldSlownessKey = createEnchantmentKey(SHIELD_SLOWNESS_ENCHANTMENT);
        shieldWeaknessKey = createEnchantmentKey(SHIELD_WEAKNESS_ENCHANTMENT);

        shieldFireKey = createEnchantmentKey(SHIELD_FIRE_ENCHANTMENT);
        shieldWindKey = createEnchantmentKey(SHIELD_WIND_ENCHANTMENT);
        shieldThunderKey = createEnchantmentKey(SHIELD_THUNDER_ENCHANTMENT);
        shieldWaterKey = createEnchantmentKey(SHIELD_WATER_ENCHANTMENT);
    }

    private TypedKey<Enchantment> createEnchantmentKey(final String enchantmentConstant) {
        return EnchantmentKeys.create(Key.key(String.format("%s:%s", NAMESPACE, enchantmentConstant)));
    }

    private void registerTNTEnchantment(final RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event, final TypedKey<Enchantment> key, final String label, final int maxLevel) {
        event.registry().register(
                key,
                b -> b.description(Component.text(label))
                        .supportedItems(
                                RegistrySet.keySet(
                                        RegistryKey.ITEM,
                                        TypedKey.create(RegistryKey.ITEM, NamespacedKey.minecraft("tnt"))
                                )
                        )
                        .anvilCost(1)
                        .maxLevel(maxLevel)
                        .weight(10)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(maxLevel, 1))
                        .activeSlots(EquipmentSlotGroup.HAND)
        );
    }

    private void registerShieldEnchantment(final RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event, final TypedKey<Enchantment> key, final String label, final int maxLevel) {
        event.registry().register(
                key,
                b -> b.description(Component.text(label))
                        .supportedItems(
                                RegistrySet.keySet(
                                        RegistryKey.ITEM,
                                        TypedKey.create(RegistryKey.ITEM, NamespacedKey.minecraft("shield"))
                                )
                        )
                        .anvilCost(1)
                        .maxLevel(maxLevel)
                        .weight(10)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(maxLevel, 1))
                        .activeSlots(EquipmentSlotGroup.HAND)
        );
    }

    public void registerEnchantments(final RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event) {
            try {
                registerTNTEnchantment(event, quickboomKey, "Quickboom", 4);
                registerTNTEnchantment(event, blastwaveKey, "Blastwave", 4);
                registerShieldEnchantment(event, shieldJumpKey, "Leap Guard", 1);
                registerShieldEnchantment(event, shieldSwiftKey, "Swift Defense", 1);
                registerShieldEnchantment(event, shieldSlownessKey, "Snare Guard", 1);
                registerShieldEnchantment(event, shieldStrengthKey, "Counterforce", 1);
                registerShieldEnchantment(event, shieldWeaknessKey, "Sapping Guard", 1);
                registerShieldEnchantment(event, shieldFireKey, "Fire Elemental", 1);
                registerShieldEnchantment(event, shieldThunderKey, "Thunder Elemental", 1);
                registerShieldEnchantment(event, shieldWindKey, "Air Elemental", 1);
                registerShieldEnchantment(event, shieldWaterKey, "Water Elemental", 1);

                context.getLogger().debug("Custom enchantments registered successfully!");
            } catch (Throwable t) {
                context.getLogger().error("Failed to register custom enchantments", t);
            }
    }

}
