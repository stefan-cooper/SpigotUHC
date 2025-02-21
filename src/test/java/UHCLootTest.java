import com.stefancooper.SpigotUHC.utils.Utils;
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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UHCLootTest {

    private static ServerMock server;
    private static World world;
    private static PlayerMock admin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        MockBukkit.load(Plugin.class);
        server.addSimpleWorld("world");
        world = server.getWorld("world");
        admin = server.addPlayer();
        admin.setOp(true);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private List<ItemStack> getLatestChestContents(ChestStateMock chest) {
        return Arrays.stream(chest.getBlockInventory().getStorageContents()).filter(item -> item != null && item.getType() != Material.AIR).toList();
    }

    @Test
    void lootChestTest() {
        BukkitSchedulerMock schedule = server.getScheduler();
        int x = 0;
        int y = 100;
        int z = 0;

        server.execute("uhc", admin, "start");

        // start uhc (so the world spawn should now be ignored)
        Block chestBlock = world.getBlockAt(new Location(world, x, y, z));
        ChestStateMock chest = (ChestStateMock) chestBlock.getState();
        final List<ItemStack> initialContents = getLatestChestContents(chest);
        assertEquals(0, initialContents.size());

        schedule.performOneTick();
        schedule.performTicks(Utils.secondsToTicks(5));

        final List<ItemStack> firstGeneration = getLatestChestContents(chest);
        assertNotEquals(0, firstGeneration.size());
        assertNotEquals(initialContents, firstGeneration);
    }
}
