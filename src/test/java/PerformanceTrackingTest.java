import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.enums.PerformanceTrackingEvent;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.state.ChestStateMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import utils.TestUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static com.stefancooper.SpigotUHC.utils.Constants.PERFORMANCE_TRACKING_LOCATION;

public class PerformanceTrackingTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static PlayerMock admin;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(WORLD_NAME);
        admin = server.addPlayer();
        admin.setOp(true);
    }

    @AfterEach
    public void tearDown() throws IOException {
        MockBukkit.unmock();
        final FileWriter writer = new FileWriter(PERFORMANCE_TRACKING_LOCATION, false);
        writer.write("{}");
        writer.close();
    }

    private void assertFileContainsText(String match, boolean shouldFind) throws IOException {
        List<String> contents = Files.readAllLines(Path.of(PERFORMANCE_TRACKING_LOCATION));
        String allContents = contents.stream().reduce("", (acc, curr) -> String.format("%s\n%s", acc, curr));
        if (shouldFind) {
            Assertions.assertTrue(allContents.contains(match));
        } else {
            Assertions.assertFalse(allContents.contains(match));
        }
    }

    private void assertTotalLines(int size) throws IOException {
        List<String> contents = Files.readAllLines(Path.of(PERFORMANCE_TRACKING_LOCATION));
        Assertions.assertEquals(size, contents.size());
    }

    private void assertPerformanceValue(String player, PerformanceTrackingEvent stat, int expected) throws IOException {
        String content = new String(Files.readAllBytes(Path.of(PERFORMANCE_TRACKING_LOCATION)));
        JSONObject json = new JSONObject(content);
        Assertions.assertEquals(expected, json.getJSONObject(player).getInt(stat.name));
    }

    @Test
    @DisplayName("When start is run, the performance tracking file is created")
    void fileIsCreated() throws IOException {
        TestUtils.executeCommand(plugin, admin, "set", "enable.performance.tracking=true");


        TestUtils.executeCommand(plugin, admin, "start");
        assertFileContainsText("{}", true);
        assertTotalLines(1);
    }

    @Test
    @DisplayName("When start is run, and a player dies, the performance tracking is updated")
    void performanceTracking() throws IOException {
        BukkitSchedulerMock schedule = server.getScheduler();
        String player1Name = String.format("%s-%s", "jawad", UUID.randomUUID());
        String player2Name = String.format("%s-%s", "stefan", UUID.randomUUID());
        String player3Name = String.format("%s-%s", "sean", UUID.randomUUID());
        PlayerMock player1 = server.addPlayer();
        PlayerMock player2 = server.addPlayer();
        PlayerMock player3 = server.addPlayer();
        player1.setName(player1Name);
        player2.setName(player2Name);
        player3.setName(player3Name);

        TestUtils.executeCommand(plugin, admin, "set", "enable.performance.tracking=true");
        TestUtils.executeCommand(plugin, admin, "start");

        schedule.performOneTick();

        player1.simulateDamage(20, DamageSource.builder(DamageType.DROWN).build());

        schedule.performTicks(Utils.secondsToTicks(60));

        // player1 stats after dying
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.DEATH, 1);
        // 20 pve damage
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.PVE_DAMAGE, 20);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.REVIVE, 0);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.KILL, 0);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.DAMAGE_DEALT, 0);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.GOLD_ORE_MINED, 0);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.LOOT_CHEST_CLAIMED, 0);

        // player2 kills player3
        player3.simulateDamage(20, player2);

        schedule.performTicks(Utils.secondsToTicks(60));

        // player2 stats after killing
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.KILL, 1);
        // damage dealt
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.DAMAGE_DEALT, 20);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.REVIVE, 0);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.DEATH, 0);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.PVE_DAMAGE, 0);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.GOLD_ORE_MINED, 0);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.LOOT_CHEST_CLAIMED, 0);

        // player3 stats after dying
        assertPerformanceValue(player3Name, PerformanceTrackingEvent.DEATH, 1);
        // 0 pve damage
        assertPerformanceValue(player3Name, PerformanceTrackingEvent.PVE_DAMAGE, 0);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.REVIVE, 0);
        assertPerformanceValue(player3Name, PerformanceTrackingEvent.KILL, 0);
        assertPerformanceValue(player3Name, PerformanceTrackingEvent.DAMAGE_DEALT, 0);
        assertPerformanceValue(player3Name, PerformanceTrackingEvent.GOLD_ORE_MINED, 0);
        assertPerformanceValue(player3Name, PerformanceTrackingEvent.LOOT_CHEST_CLAIMED, 0);

        // player 2 mines gold ore
        world.getBlockAt(new Location(world, 0, 0, 0)).setType(Material.GOLD_ORE);
        world.getBlockAt(new Location(world, 1, 0, 0)).setType(Material.GOLD_ORE);
        world.getBlockAt(new Location(world, 2, 0, 0)).setType(Material.GOLD_ORE);
        world.getBlockAt(new Location(world, 3, 0, 0)).setType(Material.GOLD_ORE);
        world.getBlockAt(new Location(world, 4, 0, 0)).setType(Material.GOLD_ORE);
        world.getBlockAt(new Location(world, 5, 0, 0)).setType(Material.GOLD_ORE);
        player2.simulateBlockBreak(world.getBlockAt(new Location(world, 0, 0, 0)));
        player2.simulateBlockBreak(world.getBlockAt(new Location(world, 1, 0, 0)));
        player2.simulateBlockBreak(world.getBlockAt(new Location(world, 2, 0, 0)));
        player2.simulateBlockBreak(world.getBlockAt(new Location(world, 3, 0, 0)));
        player2.simulateBlockBreak(world.getBlockAt(new Location(world, 4, 0, 0)));
        player2.simulateBlockBreak(world.getBlockAt(new Location(world, 5, 0, 0)));

        schedule.performTicks(Utils.secondsToTicks(60));

        assertPerformanceValue(player2Name, PerformanceTrackingEvent.DEATH, 0);
        assertPerformanceValue(player1Name, PerformanceTrackingEvent.REVIVE, 0);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.PVE_DAMAGE, 0);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.KILL, 1);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.DAMAGE_DEALT, 20);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.GOLD_ORE_MINED, 6);
        assertPerformanceValue(player2Name, PerformanceTrackingEvent.LOOT_CHEST_CLAIMED, 0);
    }
}
