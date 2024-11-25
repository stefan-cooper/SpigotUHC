import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static com.stefancooper.SpigotUHC.Defaults.*;
import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.WorldAssertion;


public class WinEventTest {

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
        world = server.getWorld(DEFAULT_WORLD_NAME);
        nether = server.getWorld(DEFAULT_NETHER_WORLD_NAME);
        end = server.getWorld(DEFAULT_END_WORLD_NAME);
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
    @DisplayName("When a team wins UHC, everyone sees title")
    void titleSent() {

        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock winner = server.addPlayer("reece");
        PlayerMock loser1 = server.addPlayer("jawad");
        PlayerMock loser2 = server.addPlayer("pavey");

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        int newX = 870;
        int newY = 64;
        int newZ = 7586;

        server.execute("uhc", admin, "set",
                "world.border.initial.size=50",
                "world.border.final.size=10",
                "countdown.timer.length=5",
                "grace.period.timer=5",
                "world.border.grace.period=10",
                "world.border.shrinking.period=30",
                "difficulty=HARD",
                "team.red=reece",
                "team.yellow=pavey",
                "team.blue=jawad",
                String.format("world.spawn.x=%s", newX),
                String.format("world.spawn.y=%s", newY),
                String.format("world.spawn.z=%s", newZ)
        );

        assertNull(winner.nextTitle());
        assertNull(loser1.nextTitle());
        assertNull(loser2.nextTitle());

        server.execute("uhc", admin, "start");

        schedule.performTicks(200);

        for (int titleCount = 0; titleCount < 8; titleCount++) {
            // 5, 4, 3, 2, 1, Start, End Grace Period
            winner.nextTitle();
            loser1.nextTitle();
            loser2.nextTitle();
        }

        assertNull(winner.nextTitle());
        assertNull(loser1.nextTitle());
        assertNull(loser2.nextTitle());

        loser1.damage(20);

        assertNull(winner.nextTitle());
        assertNull(loser1.nextTitle());
        assertNull(loser2.nextTitle());

        assertEquals(new Location(world, 0, 5, 0), winner.getLocation());

        loser2.damage(20);

        assertEquals(ChatColor.GOLD + "Congratulations to Team Red!", winner.nextTitle());
        assertEquals(ChatColor.GOLD + "Congratulations to Team Red!", loser1.nextTitle());
        assertEquals(ChatColor.GOLD + "Congratulations to Team Red!", loser2.nextTitle());

        schedule.performTicks(100);

        assertEquals(new Location(world, newX, newY, newZ), winner.getLocation());

    }

    @Test
    @DisplayName("When a team wins UHC, everyone sees title")
    void titleSentManyMen() {

        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock winner = server.addPlayer("reece");
        PlayerMock loser1 = server.addPlayer("jawad");
        PlayerMock loser2 = server.addPlayer("pavey");

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        int newX = 870;
        int newY = 64;
        int newZ = 7586;

        server.execute("uhc", admin, "set",
                "world.border.initial.size=50",
                "world.border.final.size=10",
                "countdown.timer.length=5",
                "grace.period.timer=5",
                "world.border.grace.period=10",
                "world.border.shrinking.period=30",
                "difficulty=HARD",
                "team.red=reece",
                "team.yellow=pavey",
                "team.blue=jawad",
                String.format("world.spawn.x=%s", newX),
                String.format("world.spawn.y=%s", newY),
                String.format("world.spawn.z=%s", newZ)
        );

        assertNull(winner.nextTitle());
        assertNull(loser1.nextTitle());
        assertNull(loser2.nextTitle());

        server.execute("uhc", admin, "start");

        schedule.performTicks(200);

        for (int titleCount = 0; titleCount < 8; titleCount++) {
            // 5, 4, 3, 2, 1, Start, End Grace Period
            winner.nextTitle();
            loser1.nextTitle();
            loser2.nextTitle();
        }

        assertNull(winner.nextTitle());
        assertNull(loser1.nextTitle());
        assertNull(loser2.nextTitle());

        loser1.damage(20);

        assertNull(winner.nextTitle());
        assertNull(loser1.nextTitle());
        assertNull(loser2.nextTitle());

        assertEquals(new Location(world, 0, 5, 0), winner.getLocation());

        loser2.damage(20);

        assertEquals(ChatColor.GOLD + "Congratulations to Team Red!", winner.nextTitle());
        assertEquals(ChatColor.GOLD + "Congratulations to Team Red!", loser1.nextTitle());
        assertEquals(ChatColor.GOLD + "Congratulations to Team Red!", loser2.nextTitle());

        schedule.performTicks(100);

        assertEquals(new Location(world, newX, newY, newZ), winner.getLocation());

    }

    private void assertWorldValues(WorldAssertion assertion) {
        assertion.execute(world);
        assertion.execute(nether);
        assertion.execute(end);
    }
}
