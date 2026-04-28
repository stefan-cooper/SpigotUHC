package com.stefancooper.EasyUHC;

import com.stefancooper.EasyUHC.enchants.CustomEnchantsRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.event.RegistryEvents;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrap implements PluginBootstrap {
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void bootstrap(final BootstrapContext context) {
        final CustomEnchantsRegistry registry = new CustomEnchantsRegistry(context);
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(registry::registerEnchantments));
    }
}
