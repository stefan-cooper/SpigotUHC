package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;
import com.stefancooper.SpigotUHC.resources.ConfigKey;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.TimerTask;

public class BossBarBorder {

    private final BossBar bossBar;
    private final Config config;

    public BossBarBorder (Config config) {
        this.bossBar = Bukkit.createBossBar("World Border", BarColor.WHITE, BarStyle.SOLID);;
        this.config = config;
    }

    public TimerTask updateProgress() {
        return new TimerTask() {
            @Override
            public void run() {
                int finalBorder = Integer.parseInt(config.getProp(ConfigKey.WORLD_BORDER_FINAL_SIZE.configName));
                int initialBorder = Integer.parseInt(config.getProp(ConfigKey.WORLD_BORDER_INITIAL_SIZE.configName));
                int currentSize = (int) Math.round(Utils.getWorld(config.getProp(ConfigKey.WORLD_NAME.configName)).getWorldBorder().getSize());
                bossBar.setProgress(Utils.calculateWorldBorderProgress(finalBorder, initialBorder, currentSize));
            }
        };
    }

    public BossBar getBossBar() {
        return bossBar;
    }


}
