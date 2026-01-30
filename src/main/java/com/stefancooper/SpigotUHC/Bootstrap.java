package com.stefancooper.SpigotUHC;

import com.stefancooper.SpigotUHC.utils.Constants;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrap implements PluginBootstrap {
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
        RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            try {
                final TypedKey<Enchantment> quickboomKey = EnchantmentKeys.create(Key.key(String.format("%s:%s", Constants.SPIGOT_NAMESPACE, Constants.QUICKBOOM_ENCHANTMENT)));
                final TypedKey<Enchantment> blastwaveKey = EnchantmentKeys.create(Key.key(String.format("%s:%s", Constants.SPIGOT_NAMESPACE, Constants.BLASTWAVE_ENCHANTMENT)));
                registerTNTEnchantment(event, quickboomKey, "Quickboom", 4);
                registerTNTEnchantment(event, blastwaveKey, "Blastwave", 4);
                context.getLogger().debug("Custom enchantments registered successfully!");
            } catch (Throwable t) {
                context.getLogger().error("Failed to register custom enchantments", t);
            }
        }));
    }

    private void registerTNTEnchantment(final RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event, final TypedKey<Enchantment> key, final String label, final int maxLevel) {
        event.registry().register(
                key,
                b -> b.description(Component.text(label))
                        .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.NOTEBLOCK_TOP_INSTRUMENTS))
                        .anvilCost(1)
                        .maxLevel(maxLevel)
                        .weight(10)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(maxLevel, 1))
                        .activeSlots(EquipmentSlotGroup.HAND)
        );
    }
}
