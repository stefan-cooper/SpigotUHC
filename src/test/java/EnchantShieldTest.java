import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.enchants.EnchantShield;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import java.util.Map;
import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnchantShieldTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(WORLD_NAME);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Same offers are provided when the seed hasn't changed")
    void correctEnchantsProvidedBackBasedOnSupplied() throws Exception {
        Map<Enchantment, Integer> enchants = Map.of(Enchantment.KNOCKBACK, 2);


        EnchantShield enchant = new EnchantShield(new ItemStack(Material.SHIELD), enchants);

        // at the moment this just returns what we fed in, but in the future, we may want to test more for additional enchants
        assertEquals(enchant.getEnchantsToAdd(), enchants);
    }
}
