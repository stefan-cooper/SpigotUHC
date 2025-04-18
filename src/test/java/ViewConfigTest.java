import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.junit.jupiter.api.*;

import static com.stefancooper.SpigotUHC.Defaults.*;

public class ViewConfigTest {

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
    @DisplayName("Test view full config")
    void testPlayerGetConfig() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "view", "config");
        player.assertSaid(String.format("""
                        countdown.timer.length=%s
                        difficulty=%s
                        end.world.name=%s
                        grace.period.timer=%s
                        nether.world.name=%s
                        on.death.action=%s
                        spread.min.distance=%s
                        world.border.center.x=%s
                        world.border.center.z=%s
                        world.border.final.size=%s
                        world.border.grace.period=%s
                        world.border.initial.size=%s
                        world.border.shrinking.period=%s
                        world.name=%s
                        """, COUNTDOWN_TIMER_LENGTH, DIFFICULTY, END_WORLD_NAME, GRACE_PERIOD_TIMER, NETHER_WORLD_NAME, ON_DEATH_ACTION, MIN_SPREAD_DISTANCE, WORLD_BORDER_CENTER_X, WORLD_BORDER_CENTER_Z,
                WORLD_BORDER_FINAL_SIZE, WORLD_BORDER_GRACE_PERIOD, WORLD_BORDER_INITIAL_SIZE, WORLD_BORDER_SHRINKING_PERIOD, WORLD_NAME
                        )
        );
    }

    @Test
    @DisplayName("Test view initial world border command")
    void testPlayerGetInitialWorldBorderSize() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "view", "world.border.initial.size");
        player.assertSaid("world.border.initial.size=" + WORLD_BORDER_INITIAL_SIZE);
    }

    @Test
    @DisplayName("Test view world border center x")
    void testPlayerGetWorldBorderCenterX() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "view", "world.border.center.x");
        player.assertSaid("world.border.center.x=" + WORLD_BORDER_CENTER_X);
    }

    @Test
    @DisplayName("Test view world border center z")
    void testPlayerGetWorldBorderCenterZ() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "view", "world.border.center.z");
        player.assertSaid("world.border.center.z=" + WORLD_BORDER_CENTER_Z);
    }
}
