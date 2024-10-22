import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.ItemEntityMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.Utils;
import mocks.types.RespawnPlayerMock;
import mocks.servers.RespawnPlayerServerMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_END_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_NETHER_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.WorldAssertion;


public class ReviveTest {

    private static RespawnPlayerServerMock server;
    private static Plugin plugin;
    private static World world;
    private static World nether;
    private static World end;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock(new RespawnPlayerServerMock());
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


    private void assertWorldValues(WorldAssertion assertion) {
        assertion.execute(world);
        assertion.execute(nether);
        assertion.execute(end);
    }

    @Test
    @DisplayName("When player dies, they can be revived")
    void revive() throws InterruptedException {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        RespawnPlayerMock player1 = server.addPlayer("pavey");
        RespawnPlayerMock player2 = server.addPlayer("luke");

        server.execute("uhc", admin, "set",
                "team.red=pavey,luke",
                "player.head.golden.apple=true",
                "revive.enabled=true",
                "revive.hp=4",
                "revive.lose.max.health=4",
                "revive.time=5",
                "revive.location.x=0",
                "revive.location.y=64",
                "revive.location.z=0",
                "revive.location.size=10"
        );

        player1.setHealth(10);
        player1.setFoodLevel(12);
        player1.setLevel(13);
        player1.setExp(0.9f);
        player1.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));

        assertEquals( 20, player1.getMaxHealth());
        assertEquals(10, player1.getHealth() );
        assertEquals(12, player1.getFoodLevel());
        assertEquals(13, player1.getLevel());
        assertEquals(0.9f, player1.getExp());
        assertEquals(1, Arrays.stream(player1.getInventory().getStorageContents()).filter(item -> item != null && !item.getType().equals(Material.AIR)).toList().size());
        assertEquals(0, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());

        player1.teleport(new Location(world, 100, 64, 100));
        player1.damage(20);

        schedule.performOneTick();

        assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertFalse(player2.getInventory().contains(Material.PLAYER_HEAD));

        player2.getInventory().addItem(((ItemEntityMock) world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().getFirst()).getItemStack());
        assertTrue(player2.getInventory().contains(Material.PLAYER_HEAD));
        player2.simulatePlayerMove(new Location(world, 0, 64, 0));
        player2.assertSoundHeard(Sound.BLOCK_END_PORTAL_SPAWN);
        assertTrue(player1.isDead());

        schedule.performTicks(Utils.secondsToTicks(5));

        assertFalse(player1.isDead());
        assertEquals( 16, player1.getMaxHealth());
        assertEquals(4, player1.getHealth());
        assertEquals(20, player1.getFoodLevel());
        assertEquals(0, player1.getLevel());
        assertEquals(0, player1.getExp());
        assertEquals(0, Arrays.stream(player1.getInventory().getStorageContents()).filter(item -> item != null && !item.getType().equals(Material.AIR)).toList().size());
    }
}
