import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.damage.DamageSourceMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.Utils;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.util.FileUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;

public class TimestampTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static PlayerMock admin;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
        admin = server.addPlayer();
        admin.setOp(true);
    }

    @BeforeEach
    public void cleanUp() {
        plugin.getUHCConfig().resetToDefaults();
        server.execute("uhc", admin, "cancel");
    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    private void assertFileContainsText(String match, boolean shouldFind) throws IOException {
        List<String> contents = Files.readAllLines(Path.of(Utils.getResourceLocation("timestamps.txt")));
        String allContents = contents.stream().reduce("", (acc, curr) -> String.format("%s\n%s", acc, curr));
        if (shouldFind) {
            Assertions.assertTrue(allContents.contains(match));
        } else {
            Assertions.assertFalse(allContents.contains(match));
        }
    }

    private void assertTotalLines(int size) throws IOException {
        List<String> contents = Files.readAllLines(Path.of(Utils.getResourceLocation("timestamps.txt")));
        Assertions.assertEquals(size, contents.size());
    }

    @Test
    @DisplayName("When start is run, a timestamp is added")
    void startTimestamp() throws IOException {
        server.execute("uhc", admin, "set", "enable.timestamps=true");
        server.execute("uhc", admin, "start");
        assertFileContainsText("UHC Started", true);
        assertTotalLines(1);
    }

    @Test
    @DisplayName("When start is run, and a player dies, the timestamps is updated")
    void deathTimestamp() throws IOException {
        PlayerMock player1 = server.addPlayer();
        PlayerMock player2 = server.addPlayer();
        PlayerMock player3 = server.addPlayer();
        player1.setDisplayName("jawad");
        player2.setDisplayName("stefan");
        player3.setDisplayName("sean");


        server.execute("uhc", admin, "set", "enable.timestamps=true");
        server.execute("uhc", admin, "start");

        player1.damage(100);

        assertFileContainsText("jawad dies", true);
        assertTotalLines(2); // uhc started and stefan dies

        player3.simulateDamage(100, player2);

        assertFileContainsText("stefan kills sean", true);
        assertTotalLines(3); // uhc started and stefan dies

    }

    @Test
    @DisplayName("When start is run, a timestamp is added")
    void timestampsResetOnStart() throws IOException {
        PlayerMock player = server.addPlayer();
        player.setDisplayName("stefan");

        server.execute("uhc", admin, "set", "enable.timestamps=true");
        server.execute("uhc", admin, "start");

        assertFileContainsText("UHC Started", true);
        assertTotalLines(1);

        player.damage(100);

        assertFileContainsText("stefan dies", true);
        assertTotalLines(2); // uhc started and stefan dies

        server.execute("uhc", admin, "start");

        assertFileContainsText("UHC Started", true);
        assertFileContainsText("stefan dies", false);
        assertTotalLines(1);
    }
}
