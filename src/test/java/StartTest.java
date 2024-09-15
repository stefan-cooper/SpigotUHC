import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import java.util.Arrays;

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
    public static void unload() { MockBukkit.unmock(); }

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

        server.execute("uhc", admin, "start");

        server.getOnlinePlayers().forEach(player -> {
            Assertions.assertEquals(20.0, player.getHealth());
            Assertions.assertEquals(20.0, player.getFoodLevel());
            Assertions.assertEquals(0, player.getExp());
            Assertions.assertEquals(0, Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList().size());
        });
    }

}
