import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.EasyUHC.Plugin;
import mocks.types.RespawnPlayerMock;
import mocks.servers.RespawnPlayerServerMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import utils.TestUtils;
import java.util.Arrays;
import java.util.List;
import static com.stefancooper.EasyUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.*;

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
        world = server.getWorld(WORLD_NAME);
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

    // Skip because I can't find a way to interact with the armor stand to trigger the event in MockBukkit
    // @Test
    @DisplayName("When a player dies, they can be revived with any head via an armor stand")
    void reviveViaArmorStand() {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        TestUtils.executeCommand(plugin, admin, "set",
                "team.red=pavey,luke",
                "player.head.golden.apple=true",
                "revive.enabled=true",
                "revive.hp=4",
                "revive.lose.max.health=4",
                "revive.time=5",
                "revive.location.x=0",
                "revive.location.y=64",
                "revive.location.z=0",
                "revive.location.size=10",
                "revive.any.head=true",
                "revive.via.armor.stand=true"
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

        reviver.getInventory().addItem(new ItemStack(Material.PLAYER_HEAD)); // random player head
        assertTrue(reviver.getInventory().contains(Material.PLAYER_HEAD));
        world.spawn(new Location(world, 500,64,500), ArmorStand.class);
        List<ArmorStand> armorStands = world.getNearbyEntitiesByType(ArmorStand.class, new Location(world, 500,64,500), 100).stream().toList();
        assertEquals(1, armorStands.size());
        reviver.simulatePlayerMove(new Location(world, 499, 64, 499));
//        armorStands.getFirst().setItem(EquipmentSlot.HEAD, new ItemStack(Material.PLAYER_HEAD));
        reviver.sendEquipmentChange(armorStands.getFirst(), EquipmentSlot.HEAD, new ItemStack(Material.PLAYER_HEAD));


//        reviver.assertSoundHeard(Sound.BLOCK_END_PORTAL_SPAWN);
//        assertFalse(revivee.isDead());
//        schedule.performTicks(Utils.secondsToTicks(5));
//        assertFalse(revivee.isDead());
//        assertEquals( 16, revivee.getMaxHealth());
//        assertEquals(4, revivee.getHealth());
//        assertEquals(20, revivee.getFoodLevel());
//        assertEquals(0, revivee.getLevel());
//        assertEquals(0, revivee.getExp());
//        assertEquals(0, Arrays.stream(revivee.getInventory().getStorageContents()).filter(item -> item != null && !item.getType().equals(Material.AIR)).toList().size());
//
//        assertFalse(reviver.getInventory().contains(Material.PLAYER_HEAD));
    }
}
