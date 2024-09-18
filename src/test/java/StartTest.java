import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.Difficulty;
import java.util.Arrays;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;


public class StartTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
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
    @DisplayName("When start is ran, health, exp, hunger and inventories are rest")
    void playerInitialValuesForUHC() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock player1 = server.addPlayer();
        player1.setHealth(10);
        player1.giveExp(100);
        PlayerMock player2 = server.addPlayer();
        player2.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));
        PlayerMock player3 = server.addPlayer();
        player3.setFoodLevel(3);
        world.dropItem(new Location(world, 0, 100, 0), ItemStack.of(Material.DIAMOND_SWORD));

        Assertions.assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        Assertions.assertFalse(world.getPVP());

        server.execute("uhc", admin, "start");

        Assertions.assertFalse(world.getPVP());
        Assertions.assertEquals(0, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());

        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(20.0, player.getHealth());
            Assertions.assertEquals(20.0, player.getFoodLevel());
            Assertions.assertEquals(0, player.getExp());
            Assertions.assertEquals(0, Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList().size());
            Assertions.assertEquals(GameMode.SURVIVAL, player.getGameMode());
            Assertions.assertEquals(128, player.getPotionEffect(PotionEffectType.SLOWNESS).getAmplifier());
            Assertions.assertEquals(128, player.getPotionEffect(PotionEffectType.JUMP_BOOST).getAmplifier());
            Assertions.assertEquals(3, player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier());
        });
    }

    @Test
    @DisplayName("When start is ran, the timers set everything appropriately")
    void startCommandTimers() throws InterruptedException {
        BukkitSchedulerMock schedule = server.getScheduler();
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        server.execute("uhc", admin, "set",
                "world.border.initial.size=50",
                "world.border.final.size=10",
                "countdown.timer.length=2",
                "grace.period.timer=4",
                "world.border.grace.period=6",
                "world.border.shrinking.period=2",
                "difficulty=HARD"
        );

        server.execute("uhc", admin, "start");

        // Initial start
        Assertions.assertFalse(world.getPVP());
        Assertions.assertEquals(Difficulty.PEACEFUL, world.getDifficulty());
        Assertions.assertEquals(50, world.getWorldBorder().getSize());
        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(128, player.getPotionEffect(PotionEffectType.SLOWNESS).getAmplifier());
            Assertions.assertEquals(128, player.getPotionEffect(PotionEffectType.JUMP_BOOST).getAmplifier());
            Assertions.assertEquals(3, player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier());
        });

        Thread.sleep(3000);
        schedule.performTicks(41L); // advance ticks for potion effect

        // Countdown finished
        Assertions.assertEquals(Difficulty.HARD, world.getDifficulty());
        Assertions.assertFalse(world.getPVP());
        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.SLOWNESS));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.JUMP_BOOST));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        Assertions.assertEquals(50, world.getWorldBorder().getSize());

        Thread.sleep(2000);

        // Grace period finished
        Assertions.assertEquals(Difficulty.HARD, world.getDifficulty());
        Assertions.assertTrue(world.getPVP());
        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.SLOWNESS));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.JUMP_BOOST));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        Assertions.assertEquals(50, world.getWorldBorder().getSize());

        Thread.sleep(2000);

        // World border grace period finished
        Assertions.assertEquals(Difficulty.HARD, world.getDifficulty());
        Assertions.assertTrue(world.getPVP());
        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.SLOWNESS));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.JUMP_BOOST));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        Assertions.assertEquals(50, world.getWorldBorder().getSize());

        Thread.sleep(2000);
        schedule.performTicks(41L); // advance ticks for shrinking border

        // World border shrinking finished
        Assertions.assertEquals(Difficulty.HARD, world.getDifficulty());
        Assertions.assertTrue(world.getPVP());
        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.SLOWNESS));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.JUMP_BOOST));
            Assertions.assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        Assertions.assertEquals(10, world.getWorldBorder().getSize());
    }
}
