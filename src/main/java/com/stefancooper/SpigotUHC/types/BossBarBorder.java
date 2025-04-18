package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Defaults;
import com.stefancooper.SpigotUHC.utils.Utils;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.WORLD_BORDER_FINAL_SIZE;

public class BossBarBorder {

    private final BossBar bossBar;
    private final Config config;

    public BossBarBorder (Config config) {
        this.bossBar = Bukkit.createBossBar("World Border", BarColor.WHITE, BarStyle.SOLID);;
        this.config = config;
    }

    public Runnable updateProgress() {
        return () -> {
            int finalBorder = config.getProperty(WORLD_BORDER_FINAL_SIZE, Defaults.WORLD_BORDER_FINAL_SIZE);
            int initialBorder = config.getProperty(ConfigKey.WORLD_BORDER_INITIAL_SIZE, Defaults.WORLD_BORDER_INITIAL_SIZE);
            int currentSize = (int) Math.round(config.getWorlds().getOverworld().getWorldBorder().getSize());
            bossBar.setProgress(Utils.calculateWorldBorderProgress(initialBorder, finalBorder, currentSize));
        };
    }

    public BossBar getBossBar() {
        return bossBar;
    }


}
