import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameRules;
import org.bukkit.potion.PotionEffect;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.utils.Utils;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.TestUtils;
import static com.stefancooper.SpigotUHC.Defaults.END_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.NETHER_WORLD_NAME;
import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static utils.TestUtils.WorldAssertion;

public class StartTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static World nether;
    private static World end;
    private static PlayerMock serverOp;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(WORLD_NAME);
        nether = server.getWorld(NETHER_WORLD_NAME);
        end = server.getWorld(END_WORLD_NAME);
        serverOp = server.addPlayer();
        serverOp.setOp(true);
    }

    @BeforeEach
    public void cleanUp() {
        TestUtils.executeCommand(plugin, serverOp, "cancel");
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

        world.setGameRule(GameRules.FALL_DAMAGE, false);

        PlayerMock player1 = server.addPlayer();
        player1.setHealth(10);
        player1.giveExp(100);
        player1.setLevel(12);
        player1.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 3));
        PlayerMock player2 = server.addPlayer();
        player2.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));
        PlayerMock player3 = server.addPlayer();
        player3.setFoodLevel(3);
        world.dropItem(new Location(world, 0, 100, 0), ItemStack.of(Material.DIAMOND_SWORD));

        assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.PVP));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.FALL_DAMAGE));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.REDUCED_DEBUG_INFO));
        assertEquals(3, player1.getPotionEffect(PotionEffectType.JUMP_BOOST).getAmplifier());

        TestUtils.executeCommand(plugin, admin, "start");

        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.PVP));
        assertEquals(0, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.FALL_DAMAGE));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.REDUCED_DEBUG_INFO));

        server.getOnlinePlayers().forEach(player -> {
            assertEquals(20.0, player.getHealth());
            assertEquals(20.0, player.getFoodLevel());
            assertEquals(0, player.getExp());
            assertEquals(0, player.getLevel());
            assertEquals(0, Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList().size());
            assertEquals(GameMode.SURVIVAL, player.getGameMode());
            assertNull(player.getPotionEffect(PotionEffectType.JUMP_BOOST));
            assertEquals(3, player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier());
        });
    }

    @Test
    @DisplayName("When start is run with debug (f3) disabled, their action bar shows their coordinates")
    void playerHasCoordinatesInActionBarWhenDebugInfoDisabled() {
        BukkitSchedulerMock schedule = server.getScheduler();
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        world.setGameRule(GameRules.FALL_DAMAGE, false);

        TestUtils.executeCommand(plugin, admin, "set",
                "disable.debug.info=true"
        );

        PlayerMock player1 = server.addPlayer();
        player1.setHealth(10);
        player1.giveExp(100);
        player1.setLevel(12);
        player1.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 3));
        PlayerMock player2 = server.addPlayer();
        player2.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));
        PlayerMock player3 = server.addPlayer();
        player3.setFoodLevel(3);
        world.dropItem(new Location(world, 0, 100, 0), ItemStack.of(Material.DIAMOND_SWORD));

        assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.PVP));
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.FALL_DAMAGE));
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.REDUCED_DEBUG_INFO));
        assertEquals(3, player1.getPotionEffect(PotionEffectType.JUMP_BOOST).getAmplifier());

        TestUtils.executeCommand(plugin, admin, "start");

        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.PVP));
        assertEquals(0, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.FALL_DAMAGE));
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.REDUCED_DEBUG_INFO));

        schedule.performOneTick();

        server.getOnlinePlayers().forEach(player -> {
            assertEquals(20.0, player.getHealth());
            assertEquals(20.0, player.getFoodLevel());
            assertEquals(0, player.getExp());
            assertEquals(0, player.getLevel());
            assertEquals(0, Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList().size());
            assertEquals(GameMode.SURVIVAL, player.getGameMode());
            assertNull(player.getPotionEffect(PotionEffectType.JUMP_BOOST));
            assertEquals(3, player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier());
            assertEquals(NamedTextColor.AQUA + "X: 0 Y: 5 Z: 0", PlainTextComponentSerializer.plainText().serialize(player.nextActionBar()));
        });

        schedule.performOneTick();

        // havent moved yet so still all the same
        server.getOnlinePlayers().forEach(player -> {
            // they havent moved so all players still in default position
            assertEquals(NamedTextColor.AQUA + "X: 0 Y: 5 Z: 0", PlainTextComponentSerializer.plainText().serialize(player.nextActionBar()));
        });

        // 2 players move
        player1.teleport(new Location(world, 10, 5, 10));
        player2.teleport(new Location(world, -5, 5, 20));

        schedule.performOneTick();

        // players positions have updated
        assertEquals(NamedTextColor.AQUA + "X: 10 Y: 5 Z: 10", PlainTextComponentSerializer.plainText().serialize(player1.nextActionBar()));
        assertEquals(NamedTextColor.AQUA + "X: -5 Y: 5 Z: 20", PlainTextComponentSerializer.plainText().serialize(player2.nextActionBar()));
        assertEquals(NamedTextColor.AQUA + "X: 0 Y: 5 Z: 0", PlainTextComponentSerializer.plainText().serialize(player3.nextActionBar()));

        schedule.performTicks(Utils.secondsToTicks(10));
        TestUtils.executeCommand(plugin, admin, "cancel");
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
                "disable.debug.info=false",
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
            assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.PVP));
        });
        assertEquals(Difficulty.PEACEFUL, world.getDifficulty());
        assertEquals(50, world.getWorldBorder().getSize());
        server.getOnlinePlayers().forEach(player -> {
            assertEquals(GameMode.SURVIVAL, player.getGameMode());
            assertEquals(3, player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier());
            assertEquals(3, player.getPotionEffect(PotionEffectType.REGENERATION).getAmplifier());
        });

        admin.assertSaid("UHC: Countdown starting now. Don't forget to record your POV if you can. GLHF!");

        schedule.performTicks(Utils.secondsToTicks(10));

        // Countdown finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
            assertEquals(3, player.getPotionEffect(PotionEffectType.REGENERATION).getAmplifier());
        });
        assertWorldValues((world) -> {
            assertEquals(0, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals(50, world.getWorldBorder().getSize());
            assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRules.PVP));
        });

        schedule.performTicks(Utils.secondsToTicks(20));
        admin.assertSaid("UHC: PVP grace period is now over.");

        // Grace period finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.PVP));
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
            assertEquals(3, player.getPotionEffect(PotionEffectType.REGENERATION).getAmplifier());
        });
        assertWorldValues((world) -> {
            assertEquals(0, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals(50, world.getWorldBorder().getSize());
            assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.PVP));
        });

        schedule.performTicks(Utils.secondsToTicks(10)); // advance ticks for potion effect
        admin.assertSaid("UHC: World Border shrink grace period is now over.");

        // World border grace period finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
            assertNull(player.getPotionEffect(PotionEffectType.REGENERATION));
        });
        assertWorldValues((world) -> {
            assertEquals(0.2, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals(49, Math.round(world.getWorldBorder().getSize()));
            assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.PVP));
        });

        schedule.performTicks(Utils.secondsToTicks(30)); // advance ticks for potion effect

        // World border shrinking finished
        assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            assertNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE));
            assertNull(player.getPotionEffect(PotionEffectType.REGENERATION));
        });
        assertWorldValues((world) -> {
            assertEquals(0.2, world.getWorldBorder().getDamageAmount());
            assertEquals(5, world.getWorldBorder().getDamageBuffer());
            assertEquals( 10, Math.round(world.getWorldBorder().getSize()));
            assertEquals(Boolean.TRUE, world.getGameRuleValue(GameRules.PVP));
        });
        admin.assertNoMoreSaid();
    }

    @Test
    @DisplayName("When start is ran, including a mob grace period will affect when the difficulty changes")
    void startCommandTimersMobGracePeriod() throws InterruptedException {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        TestUtils.executeCommand(plugin, admin, "set",
                "world.border.initial.size=50",
                "world.border.final.size=10",
                "disable.debug.info=false",
                "countdown.timer.length=10",
                "mob.grace.period=5",
                "grace.period.timer=20",
                "world.border.grace.period=30",
                "world.border.shrinking.period=30",
                "difficulty=HARD"
        );

        TestUtils.executeCommand(plugin, admin, "start");

        schedule.performOneTick();

        // Initial start
        assertEquals(Difficulty.PEACEFUL, world.getDifficulty());

        admin.assertSaid("UHC: Countdown starting now. Don't forget to record your POV if you can. GLHF!");

        schedule.performTicks(Utils.secondsToTicks(10));

        // Countdown finished
        assertEquals(Difficulty.PEACEFUL, world.getDifficulty());

        schedule.performTicks(Utils.secondsToTicks(5));
        admin.assertSaid("UHC: Mob grace period is over");
        assertEquals(Difficulty.HARD, world.getDifficulty());
    }
}
