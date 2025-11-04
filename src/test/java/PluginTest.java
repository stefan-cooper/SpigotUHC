import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.junit.jupiter.api.*;

import static com.stefancooper.SpigotUHC.Defaults.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PluginTest {

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
    @DisplayName("Test correct game rules were applied")
    void testGameRules() {
        PlayerMock player = server.addPlayer();
        assertNotNull(world);
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.DO_INSOMNIA));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.NATURAL_REGENERATION));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.LOCATOR_BAR));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.PVP));
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRule.REDUCED_DEBUG_INFO));
        assertEquals(GameMode.ADVENTURE, player.getGameMode());
    }

    @Test
    @DisplayName("Test player scoreboard objective is added")
    void testPlayerScoreboard() {
        PlayerMock player = server.addPlayer();
        assertNotNull(player.getScoreboard().getObjective(HEALTH_OBJECTIVE));
    }

    @Test
    @DisplayName("World border configs are set to the default")
    void testWorldBorderDefaults() {
        assertNotNull(world.getWorldBorder());
        assertEquals(0, world.getWorldBorder().getDamageAmount());
        assertEquals(5, world.getWorldBorder().getDamageBuffer());
        assertEquals(WORLD_BORDER_INITIAL_SIZE, world.getWorldBorder().getSize());
        assertEquals(WORLD_BORDER_CENTER_X, world.getWorldBorder().getCenter().getX());
        assertEquals(WORLD_BORDER_CENTER_Z, world.getWorldBorder().getCenter().getZ());
    }
}
