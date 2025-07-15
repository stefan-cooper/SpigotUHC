package com.stefancooper.SpigotUHC.events;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.Optional;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.ENABLE_TIMESTAMPS;

public class TimestampEvents implements Listener {

    private final Config config;

    public TimestampEvents (Config config) {
        this.config = config;
    }

    boolean isTimestampsEnabled () {
        return Optional.ofNullable(config.<Boolean>getProperty(ENABLE_TIMESTAMPS)).orElse(false);
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
        if (isTimestampsEnabled() && config.getPlugin().getStarted() && Optional.ofNullable(event.getAdvancement().getDisplay()).isPresent()) {
            config.getManagedResources().addTimestamp(String.format("[Achievement] %s awarded achievement \"%s\"", event.getPlayer().getDisplayName(), event.getAdvancement().getDisplay().getTitle()));
        }
    }
}
