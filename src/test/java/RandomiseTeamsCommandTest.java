import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static com.stefancooper.SpigotUHC.Defaults.END_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.NETHER_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomiseTeamsCommandTest {

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

    @Test
    @DisplayName("When randomise teams (teamSize of 3) command is run, teams are randomised")
    void randomiseTeamsThreesCommandEqualPots() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        List<String> pot1 = List.of("one1,one2,one3");
        List<String> pot2 = List.of("two1,two2,two3");
        List<String> pot3 = List.of("three1,three2,three3");

        server.execute("uhc", admin, "set", String.format("random.teams.pot.1=%s", String.join(",", pot1)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.2=%s", String.join(",", pot2)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.3=%s", String.join(",", pot3)));

        server.execute("uhc", admin, "randomise", "3");

        final Set<String> redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Red").getEntries();
        assertEquals(3, redTeam.size()); // 1, 2, 3
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> orangeTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Orange").getEntries();
        assertEquals(3, orangeTeam.size());
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Blue").getEntries();
        assertEquals(3, blueTeam.size()); // 1, 2, 3
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));
    }

    @Test
    @DisplayName("When randomise teams (teamSize of 3) command is run with unequal pots, teams are randomised")
    void randomiseTeamsThreesCommandUnequalPotsMiddle() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        List<String> pot1 = List.of("one1,one2");
        List<String> pot2 = List.of("two1,two2,two3,two4,two5");
        List<String> pot3 = List.of("three1,three2");

        server.execute("uhc", admin, "set", String.format("random.teams.pot.1=%s", String.join(",", pot1)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.2=%s", String.join(",", pot2)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.3=%s", String.join(",", pot3)));

        server.execute("uhc", admin, "randomise", "3");

        final Set<String> redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Red").getEntries();
        assertEquals(3, redTeam.size()); // 1, 2, 3
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> orangeTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Orange").getEntries();
        assertEquals(3, orangeTeam.size()); // 1, 2, 3
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Blue").getEntries();
        assertEquals(3, blueTeam.size()); // 2, 2, 2
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));
    }

    @Test
    @DisplayName("When randomise teams (teamSize of 2) command is run with equal pots, teams are randomised")
    void randomiseTeamsTwosCommandEqualPots() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        List<String> pot1 = List.of("one1,one2,one3,one4");
        List<String> pot2 = List.of("two1,two2,two3,two4");
        List<String> pot3 = List.of("three1,three2,three3,three4");

        server.execute("uhc", admin, "set", String.format("random.teams.pot.1=%s", String.join(",", pot1)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.2=%s", String.join(",", pot2)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.3=%s", String.join(",", pot3)));

        server.execute("uhc", admin, "randomise", "2");

        final Set<String> redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Red").getEntries();
        assertEquals(2, redTeam.size()); // 1, 3
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertFalse(redTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> orangeTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Orange").getEntries();
        assertEquals(2, orangeTeam.size()); // 1, 3
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertFalse(orangeTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Blue").getEntries();
        assertEquals(2, blueTeam.size()); // 1, 3
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> greenTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Green").getEntries();
        assertEquals(2, greenTeam.size()); // 1, 3
        assertTrue(greenTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertFalse(greenTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(greenTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> yellowTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Yellow").getEntries();
        assertEquals(2, yellowTeam.size()); // 2, 2
        assertFalse(yellowTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(yellowTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(yellowTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> pinkTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Pink").getEntries();
        assertEquals(2, pinkTeam.size()); // 2, 2
        assertFalse(pinkTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(pinkTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(pinkTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));
    }

    @Test
    @DisplayName("When randomise teams (teamSize of 2) command is run with unequal pots, teams are randomised")
    void randomiseTeamsTwosCommandUnequalPots() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        List<String> pot1 = List.of("one1,one2");
        List<String> pot2 = List.of("two1,two2,two3,two4,two5,two6,two7,two8");
        List<String> pot3 = List.of("three1,three2");

        server.execute("uhc", admin, "set", String.format("random.teams.pot.1=%s", String.join(",", pot1)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.2=%s", String.join(",", pot2)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.3=%s", String.join(",", pot3)));

        server.execute("uhc", admin, "randomise", "2");

        final Set<String> redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Red").getEntries();
        assertEquals(2, redTeam.size()); // 1, 3
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertFalse(redTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> orangeTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Orange").getEntries();
        assertEquals(2, orangeTeam.size()); // 1, 3
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertFalse(orangeTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Blue").getEntries();
        assertEquals(2, blueTeam.size()); // 2, 2
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> greenTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Green").getEntries();
        assertEquals(2, greenTeam.size()); // 2, 2
        assertFalse(greenTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(greenTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(greenTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> yellowTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Yellow").getEntries();
        assertEquals(2, yellowTeam.size()); // 2, 2
        assertFalse(yellowTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(yellowTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(yellowTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> pinkTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Pink").getEntries();
        assertEquals(2, pinkTeam.size()); // 2, 2
        assertFalse(pinkTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(pinkTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(pinkTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));
    }

    @Test
    @DisplayName("When randomise teams (teamSize of 4) command is run with equal pots, teams are randomised")
    void randomiseTeamsFoursCommandEqualPots() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        List<String> pot1 = List.of("one1,one2,one3,one4");
        List<String> pot2 = List.of("two1,two2,two3,two4");
        List<String> pot3 = List.of("three1,three2,three3,three4");

        server.execute("uhc", admin, "set", String.format("random.teams.pot.1=%s", String.join(",", pot1)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.2=%s", String.join(",", pot2)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.3=%s", String.join(",", pot3)));

        server.execute("uhc", admin, "randomise", "4");

        final Set<String> redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Red").getEntries();
        assertEquals(4, redTeam.size()); // 1,2,2,3
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> orangeTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Orange").getEntries();
        assertEquals(4, orangeTeam.size()); // 1,2,2,3
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Blue").getEntries();
        assertEquals(4, blueTeam.size()); // 1,1,3,3
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));
    }

    @Test
    @DisplayName("When randomise teams (teamSize of 4) command is run with unequal pots, teams are randomised")
    void randomiseTeamsFoursCommandUnequalPots() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        List<String> pot1 = List.of("one1,one2");
        List<String> pot2 = List.of("two1,two2,two3,two4,two5,two6,two7,two8");
        List<String> pot3 = List.of("three1,three2");

        server.execute("uhc", admin, "set", String.format("random.teams.pot.1=%s", String.join(",", pot1)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.2=%s", String.join(",", pot2)));
        server.execute("uhc", admin, "set", String.format("random.teams.pot.3=%s", String.join(",", pot3)));

        server.execute("uhc", admin, "randomise", "4");

        final Set<String> redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Red").getEntries();
        assertEquals(4, redTeam.size()); // 1,2,2,3
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(redTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> orangeTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Orange").getEntries();
        assertEquals(4, orangeTeam.size()); // 1,2,2,3
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertTrue(orangeTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));

        final Set<String> blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("Blue").getEntries();
        assertEquals(4, blueTeam.size()); // 2,2,2,2
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^one(.+)$")));
        assertTrue(blueTeam.stream().anyMatch(player -> player.matches("^two(.+)$")));
        assertFalse(blueTeam.stream().anyMatch(player -> player.matches("^three(.+)$")));
    }
}
