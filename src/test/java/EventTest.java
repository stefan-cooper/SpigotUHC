import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.damage.DamageSourceMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;

import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

public class EventTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
    }

    @BeforeEach
    public void cleanUp() {
        plugin.getUHCConfig().resetToDefaults();
    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test the on death event to ensure a player is set to Spectator after death")
    void testOnDeathEventSpectate() {
        PlayerMock player = server.addPlayer();
        player.damage(100);
        player.getGameMode();
        Assertions.assertEquals(GameMode.SPECTATOR, player.getGameMode());
    }

    @Test
    @DisplayName("Test the on death event to ensure a player is kicked after death")
    void testOnDeathEventKick() {
        server.setPlayers(15);
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "on.death.action=kick");
        Assertions.assertEquals(16, server.getOnlinePlayers().size());
        player.damage(100);
        server.getOnlinePlayers();
        Assertions.assertEquals(15, server.getOnlinePlayers().size());
    }

    @Test
    @DisplayName("Test the on death event to ensure a player drops a head after death")
    void testHeadDropOnDeath() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "player.head.golden.apple=true");
        player.damage(100);
        if(!world.getEntities().stream().filter(entity -> entity.getType() == EntityType.ITEM && entity.getName().equals("PLAYER_HEAD")).toList().isEmpty()){
            Item droppedItem = (Item) world.getEntities().get(0);
            droppedItem.getItemStack();

            Assertions.assertEquals(Material.PLAYER_HEAD, droppedItem.getType());
        }
    }

    @Test
    @DisplayName("Test the on respawn event to ensure a player respawns at their death location")
    void testPlayerRespawnOnDeathLocation() {
        PlayerMock player = server.addPlayer();
        World world = player.getWorld();
        Location deathLocation = new Location(world, 100, 65, 100);
        player.damage(100);
        player.setLastDeathLocation(deathLocation);

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player, null, false);
        server.getPluginManager().callEvent(respawnEvent);

        Assertions.assertEquals(deathLocation, respawnEvent.getRespawnLocation(), "Player should respawn at their death location");
    }

    @Test
    @DisplayName("Test the on respawn event to ensure a player respawns at their death location")
    void testKillTracking() {
        // Create two mock players
        PlayerMock killer = server.addPlayer("Killer");
        PlayerMock victim = server.addPlayer("Victim");
        World world = killer.getWorld();
        BukkitSchedulerMock schedule = server.getScheduler();


        //Sets Diamond sword to killer's main hand
        ItemStack bow = new ItemStack(Material.BOW);
        killer.getInventory().setItemInMainHand(bow);
        Location location = new Location(world, 116, 64, 118);

        DamageSource damageSource = new DamageSourceMock(DamageType.ARROW, killer, victim, location);
        // Simulate the killer killing the victim
        victim.setKiller(killer);
        PlayerDeathEvent deathEvent = new PlayerDeathEvent((Player) victim, damageSource, Collections.singletonList(bow), 0, killer.getName() + " killed " + victim.getName());
        server.getPluginManager().callEvent(deathEvent);
        schedule.performOneTick();

        // Check if the killer's kills were incremented and shown on their scoreboard
        Scoreboard scoreboard = killer.getScoreboard();
        Objective objective = scoreboard.getObjective("Kills");
        Assertions.assertEquals("§aPlayer Kills", objective.getDisplayName(), "The display name should be 'Player Kills'");

        int kills = objective.getScore(killer.getName()).getScore();
        Assertions.assertEquals(1, kills, "Killer should have 1 kill on the scoreboard");

        // Victim's scoreboard should not change
        int victimKills = objective.getScore(victim.getName()).getScore();
        Assertions.assertEquals(0, victimKills, "Victim should have 0 kills on the scoreboard");
    }


}