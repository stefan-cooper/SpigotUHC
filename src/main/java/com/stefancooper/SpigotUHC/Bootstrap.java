package com.stefancooper.SpigotUHC;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrap implements PluginBootstrap {
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void bootstrap(BootstrapContext context) {
        context.getLogger().info(" ------  EYE CATCHER ----");
        context.getLifecycleManager().registerEventHandler(
        RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            try {
                // Create your custom enchantment key in your namespace
                var enchantKey = EnchantmentKeys.create(Key.key("spigotuhc:quickboom"));

                event.registry().register(
                        enchantKey,
                        b -> b.description(Component.text("Quickboom"))
                                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.NOTEBLOCK_TOP_INSTRUMENTS))
                                .anvilCost(1)
                                .maxLevel(3)
                                .weight(10)
                                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                                .activeSlots(EquipmentSlotGroup.HAND)
                );

                context.getLogger().info("Quickboom enchantment registered successfully!");
            } catch (Throwable t) {
                context.getLogger().error("Failed to register custom enchantment", t);
            }
        })
);

        context.getLogger().info(" ------  EYE CATCHER  2 ----");
    }
}
