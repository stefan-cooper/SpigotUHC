import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.bukkit.scoreboard.Scoreboard;
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
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

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

    @Test
    @DisplayName("Test set teams test")
    void testPlayerSetTeams() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock stefan = server.addPlayer();
        stefan.setName("stefan");
        PlayerMock jawad = server.addPlayer();
        jawad.setName("jawad");
        PlayerMock shurf = server.addPlayer();
        shurf.setName("shurf");
        PlayerMock sean = server.addPlayer();
        sean.setName("sean");
        PlayerMock pavey = server.addPlayer();
        pavey.setName("pavey");

        server.execute("uhc", admin, "set", "team.red=stefan,jawad");
        server.execute("uhc", admin, "set", "team.blue=shurf");
        server.execute("uhc", admin, "set", "team.green=sean");
        server.execute("uhc", admin, "set", "team.orange=pavey");

        Scoreboard scoreboard = admin.getScoreboard();

        Assertions.assertTrue(scoreboard.getEntityTeam(stefan).getName().equals("Red"));
        Assertions.assertTrue(scoreboard.getEntityTeam(jawad).getName().equals("Red"));
        Assertions.assertTrue(scoreboard.getEntityTeam(shurf).getName().equals("Blue"));
        Assertions.assertTrue(scoreboard.getEntityTeam(sean).getName().equals("Green"));
        Assertions.assertTrue(scoreboard.getEntityTeam(pavey).getName().equals("Orange"));

        server.execute("uhc", admin, "set", "team.yellow=jawad");
        Assertions.assertTrue(scoreboard.getEntityTeam(stefan).getName().equals("Red"));
        Assertions.assertTrue(scoreboard.getEntityTeam(jawad).getName().equals("Yellow"));
    }
}
