import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.enchants.PrepareTNTEnchant;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import utils.TestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PrepareTNTEnchantTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static PlayerMock admin;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        admin = server.addPlayer();
        admin.setOp(true);
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

        TestUtils.executeCommand(plugin, admin, "set", "additional.enchants.tnt=true");

        PrepareTNTEnchant firstTime = new PrepareTNTEnchant(plugin.getUHCConfig(), new ItemStack(Material.TNT), seed, 0);

        PrepareTNTEnchant secondTime = new PrepareTNTEnchant(plugin.getUHCConfig(), new ItemStack(Material.TNT), seed, 0);

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
        int seed2 = 5;

        TestUtils.executeCommand(plugin, admin, "set", "additional.enchants.tnt=true");

        PrepareTNTEnchant firstTime = new PrepareTNTEnchant(plugin.getUHCConfig(), new ItemStack(Material.SHIELD), seed1, 2);
        PrepareTNTEnchant secondTime = new PrepareTNTEnchant(plugin.getUHCConfig(), new ItemStack(Material.SHIELD), seed2, 2);

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

        TestUtils.executeCommand(plugin, admin, "set", "additional.enchants.tnt=true");

        PrepareTNTEnchant enchant = new PrepareTNTEnchant(plugin.getUHCConfig(), new ItemStack(Material.SHIELD), 0, enchantmentBonus);

        // assert that you never get an offer of an enchant of enchant level 1 from a higher bonus
        assertThat("enchantmentLevel", enchant.getOffers()[0].getEnchantmentLevel(), greaterThan(1));
    }
}
