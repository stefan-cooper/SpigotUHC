import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import utils.TestUtils;

import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;

public class UnsetConfigTest {

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
    @DisplayName("Test set then unset config key")
    void testSetAndUnsetConfigKey() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        TestUtils.executeCommand(plugin, player, "set", "random.teams.pot.1=stefan");
        TestUtils.executeCommand(plugin, player, "view", "random.teams.pot.1");
        player.assertSaid("random.teams.pot.1=stefan");
        TestUtils.executeCommand(plugin, player, "unset", "random.teams.pot.1");
        TestUtils.executeCommand(plugin, player, "view", "random.teams.pot.1");
        player.assertSaid("Unknown config value requested or not set");
    }
}
