import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utils.TestUtils;

import java.util.stream.Stream;

import static com.stefancooper.SpigotUHC.Defaults.*;

public class PvpCommandTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static World nether;
    private static World end;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(WORLD_NAME);
        nether = server.getWorld(NETHER_WORLD_NAME);
        end = server.getWorld(END_WORLD_NAME);
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

    private static Stream<Arguments> pvpCommand() {
        return Stream.of(
                Arguments.of("true", true),
                Arguments.of("TRUE", true),
                Arguments.of("tRUe", true),
                Arguments.of("false", false),
                Arguments.of("trueeee", false),
                Arguments.of("tru", false),
                Arguments.of("fAlse", false)
        );
    }

    @ParameterizedTest
    @MethodSource("pvpCommand")
    @DisplayName("When pvp command is run")
    void manualPvpCommandSetsAllOfTheWorldPvps(String commandArg, boolean expected) throws InterruptedException {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        // should be false before UHC starts
        Assertions.assertEquals(false, world.getPVP());
        Assertions.assertEquals(false, nether.getPVP());
        Assertions.assertEquals(false, end.getPVP());

        // manually set uhc pvp
        TestUtils.executeCommand(plugin, admin, "pvp", commandArg);

        // assert the value is the expected value
        Assertions.assertEquals(expected, world.getPVP());
        Assertions.assertEquals(expected, nether.getPVP());
        Assertions.assertEquals(expected, end.getPVP());
    }
}
