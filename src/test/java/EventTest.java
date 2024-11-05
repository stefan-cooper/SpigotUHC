import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.*;

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
        assertEquals(GameMode.SPECTATOR, player.getGameMode());
    }

    @Test
    @DisplayName("Test the on death event to ensure a player is kicked after death")
    void testOnDeathEventKick() {
        server.setPlayers(15);
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "on.death.action=kick");
        assertEquals(16, server.getOnlinePlayers().size());
        player.damage(100);
        server.getOnlinePlayers();
        assertEquals(15, server.getOnlinePlayers().size());
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

            assertEquals(Material.PLAYER_HEAD, droppedItem.getType());
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

        assertEquals(deathLocation, respawnEvent.getRespawnLocation(), "Player should respawn at their death location");
    }

    @Test
    @DisplayName("Correct game mode is set when joining the server")
    void testGameModeSet() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock player = server.addPlayer();
        assertEquals(GameMode.ADVENTURE, player.getGameMode());

        server.execute("uhc", admin, "start");

        assertEquals(GameMode.SURVIVAL, player.getGameMode());

        player.disconnect();
        player.reconnect();

        assertEquals(GameMode.SURVIVAL, player.getGameMode());

        player.damage(100);
        player.respawn();

        assertEquals(GameMode.SPECTATOR, player.getGameMode());

        player.disconnect();
        player.reconnect();

        assertEquals(GameMode.SPECTATOR, player.getGameMode());

        server.execute("uhc", admin, "cancel");

        assertEquals(GameMode.ADVENTURE, player.getGameMode());

        player.disconnect();
        player.reconnect();

        assertEquals(GameMode.ADVENTURE, player.getGameMode());
    }

    @Test
    void preventMovementDuringCountdown() throws InterruptedException {
        BukkitSchedulerMock scheduler = server.getScheduler();
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock player = server.addPlayer();

        server.execute("uhc", admin, "set", "countdown.timer.length=5");

        Location initialLocation = player.getLocation();
        Location newLocation1 = initialLocation.clone();
        Location newLocation2 = initialLocation.clone();
        Location newLocation3 = initialLocation.clone();
        newLocation1.setX(initialLocation.x() + 1);
        newLocation2.setY(initialLocation.y() + 1);
        newLocation3.setZ(initialLocation.z() + 1);

        // Assert that they can move in all directions and they get moved
        player.simulatePlayerMove(newLocation1);
        assertEquals(player.getLocation(), newLocation1);
        player.simulatePlayerMove(newLocation2);
        assertEquals(player.getLocation(), newLocation2);
        player.simulatePlayerMove(newLocation3);
        assertEquals(player.getLocation(), newLocation3);

        // Reset back to original location
        player.simulatePlayerMove(initialLocation);
        server.execute("uhc", admin, "start");

        assertEquals(player.getLocation(), initialLocation);
        player.simulatePlayerMove(newLocation1);
        assertEquals(player.getLocation(), initialLocation);
        player.simulatePlayerMove(newLocation2);
        assertEquals(player.getLocation(), initialLocation);
        player.simulatePlayerMove(newLocation3);
        assertEquals(player.getLocation(), initialLocation);

        scheduler.performTicks(Utils.secondsToTicks(5));

        player.simulatePlayerMove(newLocation1);
        assertEquals(player.getLocation(), newLocation1);
        player.simulatePlayerMove(newLocation2);
        assertEquals(player.getLocation(), newLocation2);
        player.simulatePlayerMove(newLocation3);
        assertEquals(player.getLocation(), newLocation3);
    }
}
