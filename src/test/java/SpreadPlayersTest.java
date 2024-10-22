import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import mocks.servers.SpreadServerMock;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;

public class SpreadPlayersTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock(new SpreadServerMock());
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
    @DisplayName("When start is ran, correct spread command is sent to the server")
    void correctSpreadCommandParametersAreUsed() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        server.execute("uhc", admin, "set",
                "world.border.initial.size=500",
                "world.border.center.x=100",
                "world.border.center.z=150",
                "spread.min.distance=100"
        );

        server.execute("uhc", admin, "start");

        // center.x, center.z, min distance, initial border size / 2, true = respectTeams, @a = all players
        admin.assertSaid("spreadplayers 100.000000 150.000000 100.000000 250.000000 true @a");
    }
}
