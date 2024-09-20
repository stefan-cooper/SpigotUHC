package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;

import java.util.Timer;

public class ManagedResources {

    final Config config;
    final BossBarBorder bossBarBorder;
    Timer timer;

    public ManagedResources(Config config) {
        this.config = config;
        this.bossBarBorder = new BossBarBorder(config);
        this.timer = new Timer();
    }

    public BossBarBorder getBossBarBorder() {
        return bossBarBorder;
    }

    public Timer getTimer() {
        return timer;
    }

    public void cancelTimer() {
        timer.cancel();
        timer = new Timer();
    }

}
