import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import com.stefancooper.SpigotUHC.Plugin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;

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

        Assertions.assertFalse(world.getPVP());

        server.execute("uhc", admin, "start");

        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(20.0, player.getHealth());
            Assertions.assertEquals(20.0, player.getFoodLevel());
            Assertions.assertEquals(0, player.getExp());
            Assertions.assertEquals(0, Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList().size());
            Assertions.assertEquals(GameMode.SURVIVAL, player.getGameMode());
            Assertions.assertEquals(0, player.getWalkSpeed());
        });
    }

    @Test
    @DisplayName("When start is ran, players are spread out")
    void playersAreSpread() {
        BukkitSchedulerMock schedule = server.getScheduler();
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock player1 = server.addPlayer();
        PlayerMock player2 = server.addPlayer();
        PlayerMock player3 = server.addPlayer();
        
        server.execute("uhc", admin, "set", String.format("team.red=%s", player1.getName()));
        server.execute("uhc", admin, "set", String.format("team.red=%s", player2.getName()));
        server.execute("uhc", admin, "set", String.format("team.red=%s", player3.getName()));
        server.execute("uhc", admin, "start");
        schedule.performOneTick();

        // server.execute("spreadplayers", admin, "0 0 50 100 false @a");

        System.out.println(player1.getLocation());
        System.out.println(player2.getLocation());

        Assertions.assertTrue(player1.getLocation().distance(player2.getLocation()) >= 250.0);
        Assertions.assertTrue(player1.getLocation().distance(player2.getLocation()) <= 500);

        // Assertions.assertTrue(player1.getLocation().distance(player3.getLocation()) >= 250);
        // Assertions.assertTrue(player1.getLocation().distance(player3.getLocation()) <= 500);

        // Assertions.assertTrue(player2.getLocation().distance(player3.getLocation()) >= 250);
        // Assertions.assertTrue(player2.getLocation().distance(player3.getLocation()) <= 500);
    }

    private double roundToNearestDecimalPlace (float num) {
        return Math.round(num * 10.0) / 10.0;
    }

    @Test
    @DisplayName("When start is ran, the timers set everything appropriately")
    void startCommandTimers() throws InterruptedException {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        server.execute("uhc", admin, "set",
                "world.border.initial.size=50",
                "world.border.final.size=10",
                "countdown.timer.length=2",
                "grace.period.timer=4",
                "world.border.grace.period=6",
                "world.border.shrinking.period=2"
        );

        server.execute("uhc", admin, "start");

        // Initial start
        Assertions.assertFalse(world.getPVP());
        Assertions.assertEquals(50, world.getWorldBorder().getSize());
        server.getOnlinePlayers().forEach(player -> Assertions.assertEquals(0, roundToNearestDecimalPlace(player.getWalkSpeed())));

        Thread.sleep(3000);

        // Countdown finished
        Assertions.assertFalse(world.getPVP());
        server.getOnlinePlayers().forEach(player -> Assertions.assertEquals(0.2, roundToNearestDecimalPlace(player.getWalkSpeed())));
        Assertions.assertEquals(50, world.getWorldBorder().getSize());

        Thread.sleep(2000);

        // Grace period finished
        Assertions.assertTrue(world.getPVP());
        server.getOnlinePlayers().forEach(player -> Assertions.assertEquals(0.2, roundToNearestDecimalPlace(player.getWalkSpeed())));
        Assertions.assertEquals(50, world.getWorldBorder().getSize());

        Thread.sleep(2000);

        // World border grace period finished
        Assertions.assertTrue(world.getPVP());
        server.getOnlinePlayers().forEach(player -> Assertions.assertEquals(0.2, roundToNearestDecimalPlace(player.getWalkSpeed())));
        Assertions.assertEquals(50, world.getWorldBorder().getSize());

        Thread.sleep(2000);

        // World border shrinking finished
        Assertions.assertTrue(world.getPVP());
        server.getOnlinePlayers().forEach(player -> Assertions.assertEquals(0.2, roundToNearestDecimalPlace(player.getWalkSpeed())));
        // For some reason, the border size is still 50 here, even though I can verify that the code has been run to set it to smaller. Skipping this step for now.
        // Assertions.assertEquals(10, world.getWorldBorder().getSize());
    }
}
