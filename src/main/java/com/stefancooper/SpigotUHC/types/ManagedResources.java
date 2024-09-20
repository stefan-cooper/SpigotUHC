package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.NamespacedKey;

import java.util.Timer;

import static com.stefancooper.SpigotUHC.resources.Constants.PLAYER_HEAD;

public class ManagedResources {

    final Config config;
    final BossBarBorder bossBarBorder;
    Timer timer;
    final NamespacedKey playerHead;

    public ManagedResources(Config config) {
        this.config = config;
        this.bossBarBorder = new BossBarBorder(config);
        this.timer = new Timer();
        this.playerHead = new NamespacedKey(config.getPlugin(), PLAYER_HEAD);
    }

    public BossBarBorder getBossBarBorder() {
        return bossBarBorder;
    }

    public Timer getTimer() {
        return timer;
    }

    public NamespacedKey getPlayerHeadKey() {
        return playerHead;
    }

    public void cancelTimer() {
        timer.cancel();
        timer = new Timer();
    }

}
