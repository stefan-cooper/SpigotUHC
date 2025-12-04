package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.utils.Utils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.ENABLE_TIMESTAMPS;

public class TimestampEvents implements Listener {

    private final Config config;
    private final Map<UUID, Set<NamespacedKey>> completed = new HashMap<>();

    public TimestampEvents (Config config) {
        this.config = config;
    }

    boolean isTimestampsEnabled () {
        return config.getProperty(ENABLE_TIMESTAMPS, Defaults.ENABLE_TIMESTAMPS);
    }

    @EventHandler
    public void onDeath (PlayerDeathEvent event) {
        if (isTimestampsEnabled()) {
            if (event.getEntity().getLastDamageCause() != null &&
                    event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity() != null &&
                    event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity().getType() == EntityType.PLAYER
            ) {
                Player player = (Player) event.getEntity().getLastDamageCause().getDamageSource().getDirectEntity();
                config.getManagedResources().addTimestamp(String.format("[Death] %s killed by %s | Death message: \"%s\"", event.getEntity().getDisplayName(), player.getDisplayName(), event.getDeathMessage()));
            } else {
                config.getManagedResources().addTimestamp(String.format("[Death] %s dies | Death message: \"%s\"", event.getEntity().getDisplayName(), event.getDeathMessage()));
            }
        }
    }

    @EventHandler
    public void onAchievement (PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        NamespacedKey key = event.getAdvancement().getKey();

        completed.putIfAbsent(player.getUniqueId(), new HashSet<>());
        Set<NamespacedKey> done = completed.get(player.getUniqueId());
        if (isTimestampsEnabled() && config.getPlugin().getStarted() && done.add(key) && event.getAdvancement().getDisplay() != null && !Utils.testMode()) {
            config.getManagedResources().addTimestamp(String.format("[Achievement] %s awarded achievement \"%s\"", event.getPlayer().getDisplayName(), PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().displayName())));
        }
    }
}
