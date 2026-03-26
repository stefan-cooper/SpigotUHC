package com.stefancooper.EasyUHC.events;

import com.stefancooper.EasyUHC.Config;
import com.stefancooper.EasyUHC.Defaults;
import com.stefancooper.EasyUHC.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;
import java.util.List;

import static com.stefancooper.EasyUHC.enums.ConfigKey.*;

public class ReviveEvents implements Listener {

    private final Config config;

    public ReviveEvents (Config config) {
        this.config = config;
    }

    // View docs for various events https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.ITEM) {
            Item item = (Item) event.getEntity();
            if (item.getItemStack().getType() == Material.PLAYER_HEAD) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onArmorStandEvent(PlayerArmorStandManipulateEvent event) {
        if (Utils.isReviveViaArmorStandEnabled(config)) {
            if (event.getSlot() == EquipmentSlot.HEAD && event.getPlayerItem().getType() == Material.PLAYER_HEAD) {
                ItemStack playerHead = event.getPlayerItem();
                if (config.getProperty(REVIVE_ANY_HEAD, Defaults.REVIVE_ANY_HEAD)) {
                    // Any head revive mode
                    Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(event.getPlayer().getDisplayName());
                    if (team != null) {
                        List<Player> teammates = team.getEntries().stream().map(Bukkit::getPlayer).toList();
                        List<Player> deadTeammates = teammates.stream().filter(player -> player != null && (player.isDead() || player.getGameMode() == GameMode.SPECTATOR)).toList();
                        if (!deadTeammates.isEmpty()) {
                            config.getManagedResources().instantRevive(event.getPlayer(), deadTeammates.getFirst().getName(), event.getRightClicked());
                            event.getRightClicked().setVisible(false);
                            event.getRightClicked().setHealth(0);
                            return;
                        }
                    }
                } else {
                    // Only teammates head revive mode
                    SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                    if (meta != null && meta.getOwningPlayer() != null && meta.getOwningPlayer() != null && meta.getOwningPlayer().getName() != null) {
                        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(meta.getOwningPlayer().getName());
                        if (team != null && team.hasEntry(event.getPlayer().getName())) {
                            config.getManagedResources().instantRevive(event.getPlayer(), meta.getOwningPlayer().getName(), event.getRightClicked());
                            return;
                        }
                    }
                }

            }
        }

    }

}
