import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.junit.jupiter.api.*;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;

public class SetConfigTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
    }

    @BeforeEach
    public void cleanUp() {
        plugin.getUHCConfig().resetToDefaults();
    }

    @AfterAll
    public static void unload() { MockBukkit.unmock(); }

    @Test
    @DisplayName("Test set initial world border command")
    void testPlayerSetInitialWorldBorderSize() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "world.border.initial.size=500");
        Assertions.assertEquals(Double.parseDouble("500"), world.getWorldBorder().getSize());
    }

    @Test
    @DisplayName("Test set world border center x")
    void testPlayerSetWorldBorderCenterX() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "world.border.center.x=25");
        Assertions.assertEquals(Double.parseDouble("25"), world.getWorldBorder().getCenter().getX());
    }

    @Test
    @DisplayName("Test set world border center z")
    void testPlayerSetWorldBorderCenterZ() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "world.border.center.z=25");
        Assertions.assertEquals(Double.parseDouble("25"), world.getWorldBorder().getCenter().getZ());
    }
}
