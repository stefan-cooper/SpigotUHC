import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Difficulty;
import java.util.Arrays;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import static com.stefancooper.SpigotUHC.Defaults.END_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.NETHER_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.TestUtils.WorldAssertion;


public class StartTest {

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
        world = server.getWorld(WORLD_NAME);
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

    @Test
    @DisplayName("When start is ran, health, exp, hunger and inventories are reset")
    void playerInitialValuesForUHC() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        world.setGameRule(GameRule.FALL_DAMAGE, false);

        PlayerMock player1 = server.addPlayer();
        player1.setHealth(10);
        player1.giveExp(100);
        player1.setLevel(12);
        PlayerMock player2 = server.addPlayer();
        player2.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));
        PlayerMock player3 = server.addPlayer();
        player3.setFoodLevel(3);
        world.dropItem(new Location(world, 0, 100, 0), ItemStack.of(Material.DIAMOND_SWORD));

        assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.PVP));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.FALL_DAMAGE));

        TestUtils.executeCommand(plugin, admin, "start");

        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.PVP));
        assertEquals(0, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRule.FALL_DAMAGE));

        server.getOnlinePlayers().forEach(player -> {
            assertEquals(20.0, player.getHealth());
            assertEquals(20.0, player.getFoodLevel());
            assertEquals(0, player.getExp());
            assertEquals(0, player.getLevel());
            assertEquals(0, Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList().size());
            assertEquals(GameMode.SURVIVAL, player.getGameMode());
            assertEquals(3, player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier());
        });
    }

    private void assertWorldValues(WorldAssertion assertion) {
        assertion.execute(world);
        assertion.execute(nether);
        assertion.execute(end);
    }

    @Test
    @DisplayName("When start is ran, the timers set everything appropriately")
    void startCommandTimers() throws InterruptedException {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        TestUtils.executeCommand(plugin, admin, "set",
                "world.border.initial.size=50",
                "world.border.final.size=10",
                "countdown.timer.length=10",
                "grace.period.timer=20",
                "world.border.grace.period=30",
                "world.border.shrinking.period=30",
                "difficulty=HARD"
        );

        server.getOnlinePlayers().forEach(player -> {
            assertEquals(GameMode.ADVENTURE, player.getGameMode());
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });

        assertWorldValues((world) -> {
            assertEquals(0, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
        });

        TestUtils.executeCommand(plugin, admin, "start");

        schedule.performOneTick();

        // Initial start
        assertWorldValues((world) -> {
            assertEquals(0, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.PVP));
        });
        assertEquals(Difficulty.PEACEFUL, world.getDifficulty());
        assertEquals(50, world.getWorldBorder().getSize());
        server.getOnlinePlayers().forEach(player -> {
            assertEquals(GameMode.SURVIVAL, player.getGameMode());
            assertEquals(3, player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier());
        });

        admin.assertSaid("UHC: Countdown starting now. Don't forget to record your POV if you can. GLHF!");

        schedule.performTicks(Utils.secondsToTicks(10));

        // Countdown finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        assertWorldValues((world) -> {
            assertEquals(0, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals(50, world.getWorldBorder().getSize());
            assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.PVP));
        });

        schedule.performTicks(Utils.secondsToTicks(20));
        admin.assertSaid("UHC: PVP grace period is now over.");

        // Grace period finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRule.PVP));
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        assertWorldValues((world) -> {
            assertEquals(0, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals(50, world.getWorldBorder().getSize());
            assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRule.PVP));
        });

        schedule.performTicks(Utils.secondsToTicks(10)); // advance ticks for potion effect
        admin.assertSaid("UHC: World Border shrink grace period is now over.");

        // World border grace period finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        assertWorldValues((world) -> {
            assertEquals(0.2, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals(50, Math.round(world.getWorldBorder().getSize()));
            assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRule.PVP));
        });

        schedule.performTicks(Utils.secondsToTicks(30)); // advance ticks for potion effect

        // World border shrinking finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
        });
        assertWorldValues((world) -> {
            assertEquals(0.2, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals( 10, Math.round(world.getWorldBorder().getSize()));
            assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRule.PVP));
        });
        admin.assertNoMoreSaid();
    }
}
