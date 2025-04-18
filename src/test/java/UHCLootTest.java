import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.state.ChestStateMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
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
        world = server.getWorld(WORLD_NAME);
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

    private List<ItemStack> getLatestChestContents(ChestStateMock chest) {
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

        schedule.performOneTick();

        // start uhc (so the world spawn should now be ignored)
        final List<ItemStack> firstGeneration = getLatestChestContents((ChestStateMock) world.getBlockAt(new Location(world, x, y, z)).getState());
        assertNotEquals(0, firstGeneration.size());
        assertEquals(firstGeneration, firstGeneration);

        schedule.performTicks(100);

        final List<ItemStack> secondGeneration = getLatestChestContents((ChestStateMock) world.getBlockAt(new Location(world, x, y, z)).getState());

        assertNotEquals(0, secondGeneration.size());
        assertNotEquals(firstGeneration, secondGeneration);
    }

    @Test
    void dynamicLootChestTest() {
        BukkitSchedulerMock schedule = server.getScheduler();
        String x = "750,1000";
        String z = "250,500";
        int lootFrequency = 5; // 100 ticks

        // set world spawn
        server.execute("uhc", admin, "set",
                String.format("loot.chest.x.range=%s", x),
                String.format("loot.chest.z.range=%s", z),
                String.format("loot.chest.frequency=%s", lootFrequency),
                String.format("loot.chest.enabled=%s", "true")
        );

        server.execute("uhc", admin, "start");

        schedule.performOneTick();

        Block firstGenerationChest = plugin.getUHCConfig().getManagedResources().getDynamicLootChestLocation();
        assertEquals(Material.CHEST, firstGenerationChest.getType());

        // start uhc (so the world spawn should now be ignored)
        final List<ItemStack> firstGeneration = getLatestChestContents((ChestStateMock) world.getBlockAt(firstGenerationChest.getLocation()).getState());
        assertNotEquals(0, firstGeneration.size());
        assertEquals(firstGeneration, firstGeneration);

        schedule.performTicks(100);

        Block secondGenerationChest = plugin.getUHCConfig().getManagedResources().getDynamicLootChestLocation();
        assertEquals(Material.AIR, firstGenerationChest.getType());
        assertEquals(Material.CHEST, secondGenerationChest.getType());

        final List<ItemStack> secondGeneration = getLatestChestContents((ChestStateMock) world.getBlockAt(secondGenerationChest.getLocation()).getState());
        assertNotEquals(0, secondGeneration.size());
        assertNotEquals(firstGeneration, secondGeneration);
    }
}
