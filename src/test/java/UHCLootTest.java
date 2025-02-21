import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.block.state.ChestMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UHCLootTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static PlayerMock admin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
        admin = server.addPlayer();
        admin.setOp(true);
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

    private List<ItemStack> getLatestChestContents(ChestMock chest) {
        return Arrays.stream(chest.getBlockInventory().getStorageContents()).filter(item -> item != null && item.getType() != Material.AIR).toList();
    }

    @Test
    void lootChestTest() {
        BukkitSchedulerMock schedule = server.getScheduler();
        int x = 1234;
        int y = 123;
        int z = -1234;
        int lootFrequency = 5; // 100 ticks

        // set world spawn
        server.execute("uhc", admin, "set",
                String.format("loot.chest.x=%s", x),
                String.format("loot.chest.y=%s", y),
                String.format("loot.chest.z=%s", z),
                String.format("loot.chest.frequency=%s", lootFrequency),
                String.format("loot.chest.enabled=%s", "true")
        );

        server.execute("uhc", admin, "start");

        // start uhc (so the world spawn should now be ignored)
        Block chestBlock = world.getBlockAt(new Location(world, x, y, z));
        ChestMock chest = (ChestMock) chestBlock.getState();
        final List<ItemStack> initialContents = getLatestChestContents(chest);
        assertEquals(0, initialContents.size());

        schedule.performOneTick();

        final List<ItemStack> firstGeneration = getLatestChestContents(chest);

        assertNotEquals(0, firstGeneration.size());
        assertNotEquals(initialContents, firstGeneration);

        schedule.performTicks(100);

        final List<ItemStack> secondGeneration = getLatestChestContents(chest);

        assertNotEquals(0, secondGeneration.size());
        assertNotEquals(firstGeneration, secondGeneration);
        assertNotEquals(initialContents, secondGeneration);
    }
}
