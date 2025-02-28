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

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_COUNTDOWN_TIMER_LENGTH;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_DIFFICULTY;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_END_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_GRACE_PERIOD_TIMER;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_MIN_SPREAD_DISTANCE;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_NETHER_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_ON_DEATH_ACTION;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_BORDER_CENTER_X;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_BORDER_CENTER_Z;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_BORDER_FINAL_SIZE;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_BORDER_GRACE_PERIOD;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_BORDER_INITIAL_SIZE;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_BORDER_SHRINKING_PERIOD;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;

public class UnsetConfigTest {

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
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test set then unset config key")
    void testSetAndUnsetConfigKey() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "random.teams.pot.1=stefan");
        server.execute("uhc", player, "view", "random.teams.pot.1");
        player.assertSaid("random.teams.pot.1=stefan");
        server.execute("uhc", player, "unset", "random.teams.pot.1");
        server.execute("uhc", player, "view", "random.teams.pot.1");
        player.assertSaid("Unknown config value requested or not set");
    }
}
