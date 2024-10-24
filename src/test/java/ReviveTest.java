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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_END_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_NETHER_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.WorldAssertion;


public class ReviveTest {

    private static RespawnPlayerServerMock server;
    private static Plugin plugin;
    private static World world;
    private static RespawnPlayerMock revivee;
    private static RespawnPlayerMock reviver;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock(new RespawnPlayerServerMock());
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
        nether = server.getWorld(DEFAULT_NETHER_WORLD_NAME);
        end = server.getWorld(DEFAULT_END_WORLD_NAME);
        revivee = server.addPlayer("pavey");
        reviver = server.addPlayer("luke");
    }

    @BeforeEach
    public void cleanUp() {
        plugin.getUHCConfig().resetToDefaults();
        world.getEntities().forEach(entity -> {
            if (!(entity instanceof PlayerMock)) {
                entity.remove();
            }
        } );
        revivee.setMaxHealth(20);
        reviver.getInventory().clear();
    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("When a player dies, they can be revived")
    void revive() {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);
        
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

        revivee.setHealth(10);
        revivee.setFoodLevel(12);
        revivee.setLevel(13);
        revivee.setExp(0.9f);
        revivee.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));

        assertEquals( 20, revivee.getMaxHealth());
        assertEquals(10, revivee.getHealth() );
        assertEquals(12, revivee.getFoodLevel());
        assertEquals(13, revivee.getLevel());
        assertEquals(0.9f, revivee.getExp());
        assertEquals(1, Arrays.stream(revivee.getInventory().getStorageContents()).filter(item -> item != null && !item.getType().equals(Material.AIR)).toList().size());
        assertEquals(0, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());

        revivee.teleport(new Location(world, 100, 64, 100));
        revivee.damage(20);

        schedule.performOneTick();

        assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertFalse(reviver.getInventory().contains(Material.PLAYER_HEAD));

        reviver.getInventory().addItem(((ItemEntityMock) world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().getFirst()).getItemStack());
        assertTrue(reviver.getInventory().contains(Material.PLAYER_HEAD));
        reviver.simulatePlayerMove(new Location(world, 0, 64, 0));
        reviver.assertSoundHeard(Sound.BLOCK_END_PORTAL_SPAWN);
        assertTrue(revivee.isDead());

        schedule.performTicks(Utils.secondsToTicks(5));

        assertFalse(revivee.isDead());
        assertEquals( 16, revivee.getMaxHealth());
        assertEquals(4, revivee.getHealth());
        assertEquals(20, revivee.getFoodLevel());
        assertEquals(0, revivee.getLevel());
        assertEquals(0, revivee.getExp());
        assertEquals(0, Arrays.stream(revivee.getInventory().getStorageContents()).filter(item -> item != null && !item.getType().equals(Material.AIR)).toList().size());

        assertFalse(reviver.getInventory().contains(Material.PLAYER_HEAD));
    }

    public interface CancelCallback {
        void doCancelAction(RespawnPlayerMock reviver, RespawnPlayerMock revivee, BukkitSchedulerMock schedule);
    }

    private static final CancelCallback byMovingAway = (reviver, revivee, schedule) -> {
        schedule.performTicks(Utils.secondsToTicks(3));
        reviver.simulatePlayerMove(new Location(world, 100, 64, 0));
        schedule.performTicks(Utils.secondsToTicks(2));
        assertTrue(reviver.getInventory().contains(Material.PLAYER_HEAD));
    };

    private static final CancelCallback byReviverDisconnecting = (reviver, revivee, schedule) -> {
        reviver.disconnect();
        schedule.performTicks(Utils.secondsToTicks(5));
        reviver.reconnect();
        assertTrue(reviver.getInventory().contains(Material.PLAYER_HEAD));
    };

    private static final CancelCallback byReviveeDisconnecting = (reviver, revivee, schedule) -> {
        revivee.disconnect();
        schedule.performTicks(Utils.secondsToTicks(5));
        revivee.reconnect();
        assertTrue(reviver.getInventory().contains(Material.PLAYER_HEAD));
    };

    private static final CancelCallback byReviverDying = (reviver, revivee, schedule) -> {
        reviver.damage(20);
        schedule.performTicks(Utils.secondsToTicks(5));
        assertFalse(reviver.getInventory().contains(Material.PLAYER_HEAD));
    };

    private static Stream<Arguments> cancelArguments() {
        return Stream.of(
                Arguments.of("moveAway", byMovingAway),
                Arguments.of("reviverDisconnects", byReviverDisconnecting),
                Arguments.of("reviveeDisconnects", byReviveeDisconnecting),
                Arguments.of("reviverDying", byReviverDying)
        );
    }

    @MethodSource("cancelArguments")
    @ParameterizedTest
    @DisplayName("When a player dies, if someone starts reviving, it can be cancelled")
    void reviveCancel(String id, CancelCallback callback) {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

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

        revivee.setHealth(10);
        revivee.setFoodLevel(12);
        revivee.setLevel(13);
        revivee.setExp(0.9f);
        revivee.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));

        assertEquals( 20, revivee.getMaxHealth());
        assertEquals(10, revivee.getHealth() );
        assertEquals(12, revivee.getFoodLevel());
        assertEquals(13, revivee.getLevel());
        assertEquals(0.9f, revivee.getExp());
        assertEquals(1, Arrays.stream(revivee.getInventory().getStorageContents()).filter(item -> item != null && !item.getType().equals(Material.AIR)).toList().size());
        assertEquals(0, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());

        revivee.teleport(new Location(world, 100, 64, 100));
        revivee.damage(20);

        schedule.performOneTick();

        assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertFalse(reviver.getInventory().contains(Material.PLAYER_HEAD));

        reviver.getInventory().addItem(((ItemEntityMock) world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().getFirst()).getItemStack());
        assertTrue(reviver.getInventory().contains(Material.PLAYER_HEAD));
        reviver.simulatePlayerMove(new Location(world, 0, 64, 0));
        assertTrue(revivee.isDead());

        callback.doCancelAction(reviver, revivee, schedule);

        assertTrue(revivee.isDead());
    }
}
