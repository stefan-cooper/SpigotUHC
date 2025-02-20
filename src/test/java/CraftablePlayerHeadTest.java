import mocks.servers.GetRecipeServerMock;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static com.stefancooper.SpigotUHC.utils.Constants.CRAFTABLE_PLAYER_HEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CraftablePlayerHeadTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static PlayerMock admin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(DEFAULT_WORLD_NAME);
        admin = server.addPlayer();
        admin.setOp(true);
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
    void craftablePlayerHeadNamespaced() {
        NamespacedKey key = new NamespacedKey(plugin, CRAFTABLE_PLAYER_HEAD);

        assertEquals(null, Bukkit.getRecipe(key));

        server.execute("uhc", admin, "set",
                "craftable.player.head=true"
        );

        assertEquals(new ItemStack(Material.PLAYER_HEAD), Bukkit.getRecipe(key).getResult());

        server.execute("uhc", admin, "set",
                "craftable.player.head=false"
        );

        assertEquals(null, Bukkit.getRecipe(key));

    }

    // Skip because `craftItem` does not exist in mockBukkit (yet)
    // @Test
    void craftablePlayerHeadTest() {
        PlayerMock player1 = server.addPlayer();

        final ItemStack off = Bukkit.craftItem(new ItemStack[]{
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND)
        }, world, player1);

        assertEquals(new ItemStack(Material.AIR), off);

        server.execute("uhc", admin, "set",
                "craftable.player.head=true"
        );

        final ItemStack on = Bukkit.craftItem(new ItemStack[]{
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND)
        }, world, player1);

        assertEquals(new ItemStack(Material.PLAYER_HEAD), on);

        server.execute("uhc", admin, "set",
                "craftable.player.head=false"
        );

        final ItemStack offAgain = Bukkit.craftItem(new ItemStack[]{
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND)
        }, world, player1);

        assertEquals(new ItemStack(Material.AIR), offAgain);

    }
}
