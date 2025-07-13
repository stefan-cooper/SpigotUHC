import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.enchants.PrepareShieldEnchant;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.stream.Stream;

import static com.stefancooper.SpigotUHC.Defaults.WORLD_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PrepareShieldEnchantTest {

    private static ServerMock server;
    private static Plugin plugin;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Same offers are provided when the seed hasn't changed")
    void sameSeedEnchantTest() {
        int seed = 123456;

        PrepareShieldEnchant firstTime = new PrepareShieldEnchant(new ItemStack(Material.SHIELD), seed, 0);

        PrepareShieldEnchant secondTime = new PrepareShieldEnchant(new ItemStack(Material.SHIELD), seed, 0);

        assertEquals(firstTime.getOffers()[0].getEnchantment(), secondTime.getOffers()[0].getEnchantment());
        assertEquals(firstTime.getOffers()[0].getEnchantmentLevel(), secondTime.getOffers()[0].getEnchantmentLevel());
        assertEquals(firstTime.getOffers()[0].getCost(), secondTime.getOffers()[0].getCost());

        assertEquals(firstTime.getOffers()[1].getEnchantment(), secondTime.getOffers()[1].getEnchantment());
        assertEquals(firstTime.getOffers()[1].getEnchantmentLevel(), secondTime.getOffers()[1].getEnchantmentLevel());
        assertEquals(firstTime.getOffers()[1].getCost(), secondTime.getOffers()[1].getCost());

        assertEquals(firstTime.getOffers()[2].getEnchantment(), secondTime.getOffers()[2].getEnchantment());
        assertEquals(firstTime.getOffers()[2].getEnchantmentLevel(), secondTime.getOffers()[2].getEnchantmentLevel());
        assertEquals(firstTime.getOffers()[2].getCost(), secondTime.getOffers()[2].getCost());
    }

    @Test
    @DisplayName("Different offers are provided when the seed is different")
    void seededEnchantsTest() {
        int seed1 = 123456;
        int seed2 = 1;

        PrepareShieldEnchant firstTime = new PrepareShieldEnchant(new ItemStack(Material.SHIELD), seed1, 0);
        PrepareShieldEnchant secondTime = new PrepareShieldEnchant(new ItemStack(Material.SHIELD), seed2, 0);

        // level and cost may be same, but the enchant is what we care about

        assertNotEquals(firstTime.getOffers()[0].getEnchantment(), secondTime.getOffers()[0].getEnchantment());
        assertNotEquals(firstTime.getOffers()[1].getEnchantment(), secondTime.getOffers()[1].getEnchantment());
        assertNotEquals(firstTime.getOffers()[2].getEnchantment(), secondTime.getOffers()[2].getEnchantment());
    }

    @Test
    @DisplayName("Increasing the enchantment bonus means that we get better enchants")
    void higherEnchantmentBonusMeansBetterEnchants() {
        // 2 is effectively best
        int enchantmentBonus = 2;

        PrepareShieldEnchant enchant = new PrepareShieldEnchant(new ItemStack(Material.SHIELD), 0, enchantmentBonus);

        // assert that you never get an offer of an enchant of enchant level 1 from a higher bonus
        assertThat("enchantmentLevel", enchant.getOffers()[0].getEnchantmentLevel(), greaterThan(1));
    }
}
