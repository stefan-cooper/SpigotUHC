import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.utils.Utils;
import mocks.servers.DispatchCommandServerMock;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.stefancooper.SpigotUHC.Defaults.END_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.NETHER_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.TestUtils.WorldAssertion;


public class ShrinkYBorderTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static World nether;
    private static World end;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock(new DispatchCommandServerMock());
        WorldMock mockWorld = new WorldMock(Material.GRASS_BLOCK, Biome.PLAINS, -64, 128, 4);
        mockWorld.setName(WORLD_NAME);
        server.addWorld(mockWorld);
        plugin = MockBukkit.load(Plugin.class);
        world = mockWorld;
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

    private void assertWorldValues(WorldAssertion assertion) {
        assertion.execute(world);
        assertion.execute(nether);
        assertion.execute(end);
    }

    @Test
    @DisplayName("When start is ran, the y borders shrink appropriately and the fill command is dispatched as expected")
    void timersForYBorder() throws InterruptedException {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setName("admin");
        admin.setOp(true);

        server.execute("uhc", admin, "set",
                "world.border.initial.size=50",
                "world.border.final.size=25",
                "countdown.timer.length=10",
                "grace.period.timer=20",
                "world.border.grace.period=30",
                "world.border.final.y=-50", // will shrink 14 blocks from -64 -> -50
                "world.border.y.shrinking.period=28", // should shrink 1 block every 2 seconds
                "world.border.shrinking.period=30",
                "difficulty=HARD"
        );

        assertWorldValues((world) -> {
            assertEquals(0, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
        });

        server.execute("uhc", admin, "start");

        schedule.performOneTick();

         admin.assertSaid("spreadplayers 0.0 0.0 250 25 true @a");

        // Initial start
        schedule.performTicks(Utils.secondsToTicks(10));

        // Countdown finished
        schedule.performTicks(Utils.secondsToTicks(10));

        // Grace period finished
        schedule.performTicks(Utils.secondsToTicks(10)); // advance ticks for potion effect

        // World border grace period finished
        schedule.performTicks(Utils.secondsToTicks(30)); // advance ticks for potion effect

        // xz World border shrinking finished
        // y border start shrinking
        admin.assertSaid("fill 12 -63 12 -12 -63 -12 minecraft:bedrock");
        admin.assertNoMoreSaid();

        schedule.performTicks(Utils.secondsToTicks(10));
        admin.assertSaid("fill 12 -62 12 -12 -62 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -61 12 -12 -61 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -60 12 -12 -60 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -59 12 -12 -59 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -58 12 -12 -58 -12 minecraft:bedrock");
        admin.assertNoMoreSaid();

        schedule.performTicks(Utils.secondsToTicks(10));
        admin.assertSaid("fill 12 -57 12 -12 -57 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -56 12 -12 -56 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -55 12 -12 -55 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -54 12 -12 -54 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -53 12 -12 -53 -12 minecraft:bedrock");
        admin.assertNoMoreSaid();

        schedule.performTicks(Utils.secondsToTicks(10));
        admin.assertSaid("fill 12 -52 12 -12 -52 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -51 12 -12 -51 -12 minecraft:bedrock");
        admin.assertSaid("fill 12 -50 12 -12 -50 -12 minecraft:bedrock");
        admin.assertNoMoreSaid();
    }
}
