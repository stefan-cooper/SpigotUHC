package com.stefancooper.SpigotUHC;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.stefancooper.SpigotUHC.resources.ConfigKey.*;

public class Events implements Listener {

    private final Config config;

    public Events(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(config.getProp(ON_DEATH_ACTION.configName).equalsIgnoreCase("SPECTATOR")) {
            event.getEntity().setGameMode(GameMode.SPECTATOR);
        } else if (config.getProp(ON_DEATH_ACTION.configName).equalsIgnoreCase("KICK")) {
            event.getEntity().kickPlayer("Get rekt");
        }
    }
}


// View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

