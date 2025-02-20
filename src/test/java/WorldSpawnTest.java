import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class WorldSpawnTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static PlayerMock admin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
        admin = server.addPlayer();
        admin.setOp(true);
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
    void worldSpawnTest() {
        int newX = 1234;
        int newY = 123;
        int newZ = -1234;

        // not equal to the new world spawn on join
        PlayerMock player1 = server.addPlayer();
        assertNotEquals(newX, player1.getLocation().getX());
        assertNotEquals(newY, player1.getLocation().getY());
        assertNotEquals(newZ, player1.getLocation().getZ());

        // not equal to the new world spawn on respawn
        player1.respawn();
        assertNotEquals(newX, player1.getLocation().getX());
        assertNotEquals(newY, player1.getLocation().getY());
        assertNotEquals(newZ, player1.getLocation().getZ());

        // set world spawn
        server.execute("uhc", admin, "set",
                String.format("world.spawn.x=%s", newX),
                String.format("world.spawn.y=%s", newY),
                String.format("world.spawn.z=%s", newZ)
        );

        // new player joins and is at the new world spawn
        PlayerMock player2 = server.addPlayer();
        assertEquals(newX, player2.getLocation().getX());
        assertEquals(newY, player2.getLocation().getY());
        assertEquals(newZ, player2.getLocation().getZ());

        // old player respawns and is at the new world spawn
        player1.respawn();
        assertEquals(newX, player1.getLocation().getX());
        assertEquals(newY, player1.getLocation().getY());
        assertEquals(newZ, player1.getLocation().getZ());

        // start uhc (so the world spawn should now be ignored)
        server.execute("uhc", admin, "start");

        // new player joins and is not at the new world spawn
        PlayerMock player3 = server.addPlayer();
        assertNotEquals(newX, player3.getLocation().getX());
        assertNotEquals(newY, player3.getLocation().getY());
        assertNotEquals(newZ, player3.getLocation().getZ());

        // old player respawns and is not at the new world spawn
        player1.respawn();
        assertNotEquals(newX, player1.getLocation().getX());
        assertNotEquals(newY, player1.getLocation().getY());
        assertNotEquals(newZ, player1.getLocation().getZ());
    }
}
