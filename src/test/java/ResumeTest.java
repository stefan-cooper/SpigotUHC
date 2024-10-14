import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.Utils;
import org.bukkit.Difficulty;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;


public class ResumeTest {

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
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);
        server.execute("uhc", admin, "cancel");
    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("When resume is ran, health, exp, hunger and inventories are persisted")
    void playerInitialValuesForUHCAfterResume() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock player1 = server.addPlayer();
        player1.setHealth(10);
        player1.setExp((float) 0.6);
        player1.setLevel(12);
        PlayerMock player2 = server.addPlayer();
        player2.getInventory().setItem(1, ItemStack.of(Material.DIAMOND_SWORD));
        PlayerMock player3 = server.addPlayer();
        player3.setFoodLevel(3);
        world.dropItem(new Location(world, 0, 100, 0), ItemStack.of(Material.DIAMOND_SWORD));

        Assertions.assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());

        server.execute("uhc", admin, "resume");

        Assertions.assertEquals(1, world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList().size());
        Assertions.assertEquals(10.0, player1.getHealth());
        Assertions.assertEquals(6, Math.round(player1.getExp() * 10));
        Assertions.assertEquals(12, player1.getLevel());
        Assertions.assertTrue(player2.getInventory().contains(ItemStack.of(Material.DIAMOND_SWORD)));
        Assertions.assertEquals(3, player3.getFoodLevel());
    }

    private static Stream<Arguments> resumeConfigGracePeriod() {
        return Stream.of(
                Arguments.of(0, 600, 3600, 3600),
                Arguments.of(300, 600, 3600, 3600),
                Arguments.of(700, 600, 3600, 3600)
        );
    }

    private static Stream<Arguments> worldBorderConfig() {
        return Stream.of(
                Arguments.of(0, 600, 3600, 3600),
                Arguments.of(1200, 600, 3600, 3600),
                Arguments.of(3700, 600, 3600, 3600),
                Arguments.of(7500, 600, 3600, 3600)
        );
    }

    @ParameterizedTest
    @MethodSource("resumeConfigGracePeriod")
    @DisplayName("When resume is ran, the timers set everything appropriately")
    void resumeCommandGracePeriod(int minutesProgressed, int gracePeriodTimer, int worldBorderGracePeriodTimer, int worldBorderShrinkingPeriod) throws InterruptedException {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        int initialSize = 100;

        server.execute("uhc", admin, "set",
                "world.border.initial.size=" + initialSize,
                "world.border.final.size=10",
                "countdown.timer.length=10",
                "grace.period.timer=" + gracePeriodTimer,
                "world.border.grace.period=" + worldBorderGracePeriodTimer,
                "world.border.shrinking.period=" + worldBorderShrinkingPeriod,
                "difficulty=HARD"
        );

        Assertions.assertEquals(0, world.getWorldBorder().getDamageAmount());
        Assertions.assertEquals(5, world.getWorldBorder().getDamageBuffer());

        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(GameMode.ADVENTURE, player.getGameMode());
        });

        server.execute("uhc", admin, "resume", Integer.toString(minutesProgressed));

        schedule.performOneTick();

        Assertions.assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(GameMode.SURVIVAL, player.getGameMode());
        });

        int secondsProgressed = minutesProgressed * 60;

        if (secondsProgressed > gracePeriodTimer) {
            Assertions.assertTrue(world.getPVP());
        } else {
            Assertions.assertFalse(world.getPVP());
            int difference = gracePeriodTimer - (minutesProgressed * 60);
            schedule.performTicks(Utils.secondsToTicks(difference));
            Assertions.assertTrue(world.getPVP());
        }
    }

    @ParameterizedTest
    @MethodSource("worldBorderConfig")
    @DisplayName("When resume is ran, the timers set the world border grace period and shrinking appropriately")
    void resumeCommandWorldBorderGracePeriod(int minutesProgressed, int gracePeriodTimer, int worldBorderGracePeriodTimer, int worldBorderShrinkingPeriod) throws InterruptedException {
        BukkitSchedulerMock schedule = server.getScheduler();

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        int initialSize = 100;
        int finalSize = 100;

        server.execute("uhc", admin, "set",
                "world.border.initial.size=" + initialSize,
                "world.border.final.size=" + finalSize,
                "countdown.timer.length=10",
                "grace.period.timer=" + gracePeriodTimer,
                "world.border.grace.period=" + worldBorderGracePeriodTimer,
                "world.border.shrinking.period=" + worldBorderShrinkingPeriod,
                "difficulty=HARD"
        );

        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(GameMode.ADVENTURE, player.getGameMode());
        });

        server.execute("uhc", admin, "resume", Integer.toString(minutesProgressed));

        schedule.performOneTick();

        Assertions.assertEquals(Difficulty.HARD, world.getDifficulty());
        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(GameMode.SURVIVAL, player.getGameMode());
        });

        int secondsProgressed = minutesProgressed * 60;

        Assertions.assertEquals(initialSize, world.getWorldBorder().getSize());

        if (secondsProgressed > worldBorderGracePeriodTimer + worldBorderShrinkingPeriod) {
            schedule.performTicks(Utils.secondsToTicks(secondsProgressed));
            Assertions.assertEquals(0.2, world.getWorldBorder().getDamageAmount());
            Assertions.assertEquals(5, world.getWorldBorder().getDamageBuffer());
            Assertions.assertEquals(finalSize, world.getWorldBorder().getSize());
        } else if (secondsProgressed > worldBorderGracePeriodTimer) {
            schedule.performTicks(Utils.secondsToTicks(secondsProgressed));
            Assertions.assertEquals(0.2, world.getWorldBorder().getDamageAmount());
            Assertions.assertEquals(5, world.getWorldBorder().getDamageBuffer());
            Assertions.assertTrue(world.getWorldBorder().getSize() < initialSize && world.getWorldBorder().getSize() > finalSize);
        } else {
            schedule.performTicks(Utils.secondsToTicks(secondsProgressed));
            Assertions.assertEquals(0, world.getWorldBorder().getDamageAmount());
            Assertions.assertEquals(5, world.getWorldBorder().getDamageBuffer());
            Assertions.assertEquals(initialSize, world.getWorldBorder().getSize());
        }
    }
}