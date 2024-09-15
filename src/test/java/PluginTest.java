import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.junit.jupiter.api.*;

import static com.stefancooper.SpigotUHC.Defaults.*;

public class PluginTest {

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
    @DisplayName("Test correct game rules were applied")
    void testGameRules() {
        Assertions.assertNotNull(world);
        Assertions.assertFalse(world.getGameRuleValue(GameRule.DO_INSOMNIA));
        Assertions.assertFalse(world.getGameRuleValue(GameRule.NATURAL_REGENERATION));
        Assertions.assertFalse(world.getPVP());
    }

    @Test
    @DisplayName("Test player scoreboard objective is added")
    void testPlayerScoreboard() {
        PlayerMock player = server.addPlayer();
        Assertions.assertNotNull(player.getScoreboard().getObjective(HEALTH_OBJECTIVE));
    }

    @Test
    @DisplayName("World border configs are set to the default")
    void testWorldBorderDefaults() {
        Assertions.assertNotNull(world.getWorldBorder());
        Assertions.assertEquals(Double.parseDouble(DEFAULT_WORLD_BORDER_INITIAL_SIZE), world.getWorldBorder().getSize());
        Assertions.assertEquals(Double.parseDouble(DEFAULT_WORLD_BORDER_CENTER_X), world.getWorldBorder().getCenter().getX());
        Assertions.assertEquals(Double.parseDouble(DEFAULT_WORLD_BORDER_CENTER_Z), world.getWorldBorder().getCenter().getZ());
    }
}
