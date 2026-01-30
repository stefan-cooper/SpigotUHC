import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import org.mockito.Mockito;
import utils.TestUtils;
import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AutoSmeltTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static PlayerMock admin;
    private static BlockBreakListener listener;

    @BeforeAll
    public static void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(WORLD_NAME);
        admin = server.addPlayer();
        admin.setOp(true);
    }

    @BeforeEach
    public void cleanUp() {
        plugin.getUHCConfig().resetToDefaults();
    }

    @AfterEach
    public void teardown() {
        Mockito.reset(listener);
        listener = null;
        server.getPluginManager().unregisterPluginEvents(plugin);
    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("If autosmelt is disabled, ores are not smelted into ingots")
    void disabledTest() {

        BukkitSchedulerMock schedule = server.getScheduler();
        TestUtils.executeCommand(plugin, admin, "set", "enable.autosmelt=false");

        PlayerMock player1 = server.addPlayer();
        player1.setName("stefan");

        TestUtils.executeCommand(plugin, admin, "start");

        listener = spy(new BlockBreakListener(false));
        server.getPluginManager().registerEvents(listener, plugin);

        schedule.performOneTick();

        world.getBlockAt(new Location(world, 0, 0, 0)).setType(Material.GOLD_ORE);
        world.getBlockAt(new Location(world, 1, 0, 0)).setType(Material.DEEPSLATE_GOLD_ORE);
        world.getBlockAt(new Location(world, 2, 0, 0)).setType(Material.IRON_ORE);
        world.getBlockAt(new Location(world, 3, 0, 0)).setType(Material.DEEPSLATE_IRON_ORE);
        world.getBlockAt(new Location(world, 4, 0, 0)).setType(Material.COPPER_ORE);
        world.getBlockAt(new Location(world, 5, 0, 0)).setType(Material.DEEPSLATE_COPPER_ORE);

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 0, 0, 0)));
        verify(listener, times(1)).onBlockBreak(any());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 1, 0, 0)));
        verify(listener, times(2)).onBlockBreak(any());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 2, 0, 0)));
        verify(listener, times(3)).onBlockBreak(any());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 3, 0, 0)));
        verify(listener, times(4)).onBlockBreak(any());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 4, 0, 0)));
        verify(listener, times(5)).onBlockBreak(any());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 5, 0, 0)));
        verify(listener, times(6)).onBlockBreak(any());
    }

    @Test
    @DisplayName("If autosmelt is enabled, ores are smelted into ingots")
    void enabledTest() {

        BukkitSchedulerMock schedule = server.getScheduler();
        TestUtils.executeCommand(plugin, admin, "set", "enable.autosmelt=true");

        PlayerMock player1 = server.addPlayer();
        player1.setName("stefan");

        TestUtils.executeCommand(plugin, admin, "start");

        listener = spy(new BlockBreakListener(true));
        server.getPluginManager().registerEvents(listener, plugin);

        schedule.performOneTick();

        world.getBlockAt(new Location(world, 0, 0, 0)).setType(Material.GOLD_ORE);
        world.getBlockAt(new Location(world, 1, 0, 0)).setType(Material.DEEPSLATE_GOLD_ORE);
        world.getBlockAt(new Location(world, 2, 0, 0)).setType(Material.IRON_ORE);
        world.getBlockAt(new Location(world, 3, 0, 0)).setType(Material.DEEPSLATE_IRON_ORE);
        world.getBlockAt(new Location(world, 4, 0, 0)).setType(Material.COPPER_ORE);
        world.getBlockAt(new Location(world, 5, 0, 0)).setType(Material.DEEPSLATE_COPPER_ORE);

        schedule.performOneTick();

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 0, 0, 0)));
        verify(listener, times(1)).onBlockBreak(any());

        assertEquals(1, world.getEntities().stream().filter(entity -> entity instanceof Item item && item.getItemStack().getType().equals(Material.GOLD_INGOT)).toList().size());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 1, 0, 0)));
        verify(listener, times(2)).onBlockBreak(any());

        assertEquals(2, world.getEntities().stream().filter(entity -> entity instanceof Item item && item.getItemStack().getType().equals(Material.GOLD_INGOT)).toList().size());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 2, 0, 0)));
        verify(listener, times(3)).onBlockBreak(any());

        assertEquals(1, world.getEntities().stream().filter(entity -> entity instanceof Item item && item.getItemStack().getType().equals(Material.IRON_INGOT)).toList().size());

        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 3, 0, 0)));
        verify(listener, times(4)).onBlockBreak(any());

        assertEquals(2, world.getEntities().stream().filter(entity -> entity instanceof Item item && item.getItemStack().getType().equals(Material.IRON_INGOT)).toList().size());


        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 4, 0, 0)));
        verify(listener, times(5)).onBlockBreak(any());

        assertEquals(1, world.getEntities().stream().filter(entity -> entity instanceof Item item && item.getItemStack().getType().equals(Material.COPPER_INGOT)).toList().size());


        player1.simulateBlockBreak(world.getBlockAt(new Location(world, 5, 0, 0)));
        verify(listener, times(6)).onBlockBreak(any());

        assertEquals(2, world.getEntities().stream().filter(entity -> entity instanceof Item item && item.getItemStack().getType().equals(Material.COPPER_INGOT)).toList().size());

    }

    public static class BlockBreakListener implements Listener
    {
        private final boolean shouldOverrideDrop;

        public BlockBreakListener(final boolean shouldOverrideDrop) {
            this.shouldOverrideDrop = shouldOverrideDrop;
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            assertEquals(!shouldOverrideDrop, event.isDropItems());
        }
    }

}
