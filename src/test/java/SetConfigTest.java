import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.InventoryMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.*;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;

public class SetConfigTest {

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
    @DisplayName("Test set initial world border command")
    void testPlayerSetInitialWorldBorderSize() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "world.border.initial.size=500");
        Assertions.assertEquals(Double.parseDouble("500"), world.getWorldBorder().getSize());
    }

    @Test
    @DisplayName("Test set world border center x")
    void testPlayerSetWorldBorderCenterX() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "world.border.center.x=25");
        Assertions.assertEquals(Double.parseDouble("25"), world.getWorldBorder().getCenter().getX());
    }

    @Test
    @DisplayName("Test set world border center z")
    void testPlayerSetWorldBorderCenterZ() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "world.border.center.z=25");
        Assertions.assertEquals(Double.parseDouble("25"), world.getWorldBorder().getCenter().getZ());
    }

    @Test
    @DisplayName("Test set teams test")
    void testPlayerSetTeams() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock stefan = server.addPlayer();
        stefan.setName("stefan");
        PlayerMock jawad = server.addPlayer();
        jawad.setName("jawad");
        PlayerMock shurf = server.addPlayer();
        shurf.setName("shurf");
        PlayerMock sean = server.addPlayer();
        sean.setName("sean");
        PlayerMock pavey = server.addPlayer();
        pavey.setName("pavey");

        server.execute("uhc", admin, "set", "team.red=stefan,jawad");
        server.execute("uhc", admin, "set", "team.blue=shurf");
        server.execute("uhc", admin, "set", "team.green=sean");
        server.execute("uhc", admin, "set", "team.orange=pavey");

        Scoreboard scoreboard = admin.getScoreboard();

        Assertions.assertTrue(scoreboard.getEntityTeam(stefan).getName().equals("Red"));
        Assertions.assertTrue(scoreboard.getEntityTeam(jawad).getName().equals("Red"));
        Assertions.assertTrue(scoreboard.getEntityTeam(shurf).getName().equals("Blue"));
        Assertions.assertTrue(scoreboard.getEntityTeam(sean).getName().equals("Green"));
        Assertions.assertTrue(scoreboard.getEntityTeam(pavey).getName().equals("Orange"));

        server.execute("uhc", admin, "set", "team.yellow=jawad");
        Assertions.assertTrue(scoreboard.getEntityTeam(stefan).getName().equals("Red"));
        Assertions.assertTrue(scoreboard.getEntityTeam(jawad).getName().equals("Yellow"));
    }

    @Test
    @DisplayName("Test that the recipe for creating a golden apple from a player head works")
    void testPlayerCanCraftGoldenAppleFromPlayerHead() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "player.head.golden.apple=true");
        ItemStack apple = new ItemStack(Material.GOLDEN_APPLE);
        NamespacedKey key = new NamespacedKey(plugin, "golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, apple)
                .shape("   ", " X ", "   ")
                .setIngredient('X', Material.PLAYER_HEAD);
        Bukkit.addRecipe(recipe);
        InventoryMock craftingInventory = server.createInventory(null, 9, "Crafting");
        craftingInventory.setItem(4, new ItemStack(Material.PLAYER_HEAD));

        Assertions.assertTrue(isMatchingRecipe(craftingInventory, recipe));
        Assertions.assertEquals(apple.getType(), recipe.getResult().getType());
    }

    private boolean isMatchingRecipe(InventoryMock inventory, ShapedRecipe recipe) {
        String[] shape = recipe.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length(); col++) {
                int slotIndex = row * 3 + col;
                char ingredientChar = shape[row].charAt(col);
                ItemStack expected = recipe.getIngredientMap().get(ingredientChar);
                ItemStack actual = inventory.getItem(slotIndex);

                if (ingredientChar == ' ' && actual != null) return false;
                if (expected != null && (actual == null || expected.getType() != actual.getType())) return false;
            }
        }
        return true;
    }
}
