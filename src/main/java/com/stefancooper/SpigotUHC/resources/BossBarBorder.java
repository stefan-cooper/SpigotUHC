package com.stefancooper.SpigotUHC.resources;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class BossBarBorder {

    private BossBar bossBar;
    private Config config;

    public BossBarBorder (Config config) {

        this.bossBar = Bukkit.createBossBar("World Border", BarColor.WHITE, BarStyle.SOLID);;
        this.config = config;
        int finalBorder = Integer.parseInt(config.getProp(ConfigKey.WORLD_BORDER_FINAL_SIZE.configName));
        int initialBorder = Integer.parseInt(config.getProp(ConfigKey.WORLD_BORDER_INITIAL_SIZE.configName));
        int shrinkTime = Integer.parseInt(config.getProp(ConfigKey.WORLD_BORDER_SHRINKING_PERIOD.configName));
//        blocksPerSecond = (double) (initialBorder - finalBorder) / shrinkTime;
    }

    public void updateProgress () {

        // initial 2000
        // final 250
        // shrink gap 1750
        // current border 1500

        // prog = initial - current == 500
        // perc = (shrink gap - prog) / shrink gap

//        double progress = secondsPassed * blocksPerSecond;
        int finalBorder = Integer.parseInt(config.getProp(ConfigKey.WORLD_BORDER_FINAL_SIZE.configName));
        int initialBorder = Integer.parseInt(config.getProp(ConfigKey.WORLD_BORDER_INITIAL_SIZE.configName));
        int distanceToShrink = initialBorder - finalBorder;
        int currentSize = (int) Math.round(Utils.getWorld(config.getProp(ConfigKey.WORLD_NAME.configName)).getWorldBorder().getSize());
        int progress = initialBorder - currentSize;
        float dec = ((float) (distanceToShrink - progress) / distanceToShrink);
        bossBar.setProgress(dec);
    }

    public BossBar getBossBar() {
        return bossBar;
    }


}
