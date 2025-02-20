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

import java.util.ArrayList;
import java.util.List;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;
import static com.stefancooper.SpigotUHC.utils.Constants.NOTCH_APPLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NotchAppleTest {

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
    void notchAppleTestNamespaced() {
        NamespacedKey key = new NamespacedKey(plugin, NOTCH_APPLE);

        assertEquals(null, Bukkit.getRecipe(key));

        server.execute("uhc", admin, "set",
                "craftable.notch.apple=true"
        );

        assertEquals(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), Bukkit.getRecipe(key).getResult());

        server.execute("uhc", admin, "set",
                "craftable.notch.apple=false"
        );

        assertEquals(null, Bukkit.getRecipe(key));

    }

    // Skip because `craftItem` does not exist in mockBukkit (yet)
    // @Test
    void notchAppleTest() {
        PlayerMock player1 = server.addPlayer();

        final ItemStack off = Bukkit.craftItem(new ItemStack[]{
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.APPLE), new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK)
        }, world, player1);

        assertEquals(new ItemStack(Material.AIR), off);

        server.execute("uhc", admin, "set",
                "craftable.notch.apple=true"
        );

        final ItemStack on = Bukkit.craftItem(new ItemStack[]{
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.APPLE), new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK)
        }, world, player1);

        assertEquals(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), on);

        server.execute("uhc", admin, "set",
                "craftable.notch.apple=false"
        );

        final ItemStack offAgain = Bukkit.craftItem(new ItemStack[]{
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.APPLE), new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_BLOCK)
        }, world, player1);

        assertEquals(new ItemStack(Material.AIR), offAgain);

    }
}
