import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.junit.jupiter.api.*;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.HEALTH_OBJECTIVE;

public class PluginTest {

    private static ServerMock server;
    private static Plugin plugin;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
    }

    @AfterAll
    public static void unload()
    {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test correct game rules were applied")
    void testGameRules() {
        World world = server.getWorld(DEFAULT_WORLD_NAME);
        Assertions.assertNotNull(world);
        Assertions.assertFalse(world.getGameRuleValue(GameRule.DO_INSOMNIA));
        Assertions.assertFalse(world.getGameRuleValue(GameRule.NATURAL_REGENERATION));
    }

    @Test
    @DisplayName("Test player scoreboard objective is added")
    void testPlayerScoreboard() {
        server.setPlayers(20);
        PlayerMock player = server.addPlayer();
        Assertions.assertNotNull(player.getScoreboard().getObjective(HEALTH_OBJECTIVE));
    }


}
