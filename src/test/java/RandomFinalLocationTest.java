import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomFinalLocationTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(WORLD_NAME);
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
    @DisplayName("When start is ran with random final location not set, the world center stays as configured")
    void playerRandomFinalLocationNotSet() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        server.execute("uhc", admin, "set",
                "world.border.center.x=0",
                "world.border.center.z=0",
                "random.final.location=false"
        );

        assertEquals(0, world.getWorldBorder().getCenter().getX());
        assertEquals(0, world.getWorldBorder().getCenter().getZ());

        server.execute("uhc", admin, "start");

        assertEquals(0, world.getWorldBorder().getCenter().getX());
        assertEquals(0, world.getWorldBorder().getCenter().getZ());

        server.getOnlinePlayers().forEach(player -> {
            assertNotEquals(player.getCompassTarget(), world.getWorldBorder().getCenter());
        });
    }

    @Test
    @DisplayName("When start is ran with random final location set, the world center changes to something non zero")
    void playerRandomFinalLocation() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        server.execute("uhc", admin, "set",
                "world.border.center.x=0",
                "world.border.center.z=0",
                "random.final.location=true"
        );

        assertEquals(0, world.getWorldBorder().getCenter().getX());
        assertEquals(0, world.getWorldBorder().getCenter().getZ());

        server.execute("uhc", admin, "start");

        assertTrue(world.getWorldBorder().getCenter().getX() != 0);
        assertTrue(world.getWorldBorder().getCenter().getZ() != 0);

        server.getOnlinePlayers().forEach(player -> {
            assertEquals(player.getCompassTarget(), world.getWorldBorder().getCenter());
        });
    }
}
