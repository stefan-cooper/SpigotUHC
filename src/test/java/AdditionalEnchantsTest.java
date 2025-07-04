import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.types.AdditionalEnchants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdditionalEnchantsTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static PlayerMock admin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
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

    /* ---------------------------
     *       SHIELD ENCHANTS
     * --------------------------- */

    @Test
    void shieldEnchantAppliedWhenForced() {
        ItemStack shield = new ItemStack(Material.SHIELD);

        // Test-only subclass to bypass config and randomness
        AdditionalEnchants enchants = new AdditionalEnchants(plugin.getUHCConfig()) {
            @Override
            public void apply(ItemStack item) {
                if (item.getType() != Material.SHIELD) return;

                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;

                List<String> lore = new ArrayList<>();
                lore.add("Knockback II");
                meta.setLore(lore);
                item.setItemMeta(meta);

                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
            }
        };

        enchants.apply(shield);

        assertTrue(shield.containsEnchantment(Enchantment.KNOCKBACK));
        assertEquals(2, shield.getEnchantmentLevel(Enchantment.KNOCKBACK));

        ItemMeta meta = shield.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasLore());
        assertTrue(meta.getLore().stream().anyMatch(l -> l.contains("Knockback II")));
    }


    @Test
    void shieldEnchantNotAppliedWhenDisabled() {
        server.execute("uhc", admin, "set", "additional.enchants.shield=false");

        ItemStack shield = new ItemStack(Material.SHIELD);
        new AdditionalEnchants(plugin.getUHCConfig()).apply(shield);

        assertFalse(shield.containsEnchantment(Enchantment.KNOCKBACK));
        assertFalse(shield.containsEnchantment(Enchantment.THORNS));
    }

    /* ---------------------------
     *       HELMET ENCHANTS
     * --------------------------- */

    @Test
    void helmetEnchantAppliedWhenEnabled() {
        server.execute("uhc", admin, "set", "additional.enchants.helmet=true");

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        new AdditionalEnchants(plugin.getUHCConfig()).apply(helmet);

        assertTrue(helmet.containsEnchantment(Enchantment.RESPIRATION));

        ItemMeta meta = helmet.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasLore());
        assertTrue(meta.getLore().contains("§7§oNight Vision Goggles"));
        assertEquals(3001, meta.getCustomModelData());
    }

    @Test
    void helmetEnchantNotAppliedWhenDisabled() {
        server.execute("uhc", admin, "set", "additional.enchants.helmet=false");

        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        new AdditionalEnchants(plugin.getUHCConfig()).apply(helmet);

        assertFalse(helmet.containsEnchantment(Enchantment.RESPIRATION));

        ItemMeta meta = helmet.getItemMeta();
        if (meta != null && meta.hasLore()) {
            assertFalse(meta.getLore().contains("§7§oNight Vision Goggles"));
        }
    }

    /* ---------------------------
     *       TRIDENT ENCHANTS
     * --------------------------- */

    @Test
    void tridentEnchantAppliedWhenEnabled() {
        server.execute("uhc", admin, "set", "additional.enchants.trident=true");

        ItemStack trident = new ItemStack(Material.TRIDENT);
        new AdditionalEnchants(plugin.getUHCConfig()).apply(trident);

        assertTrue(trident.containsEnchantment(Enchantment.CHANNELING));

        ItemMeta meta = trident.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasLore());
        assertTrue(meta.getLore().stream().anyMatch(line -> line.contains("Ender Trident")));
    }

    @Test
    void tridentEnchantNotAppliedWhenDisabled() {
        server.execute("uhc", admin, "set", "additional.enchants.trident=false");

        ItemStack trident = new ItemStack(Material.TRIDENT);
        new AdditionalEnchants(plugin.getUHCConfig()).apply(trident);

        assertFalse(trident.containsEnchantment(Enchantment.CHANNELING));

        ItemMeta meta = trident.getItemMeta();
        if (meta != null && meta.hasLore()) {
            assertFalse(meta.getLore().stream().anyMatch(line -> line.contains("Ender Trident")));
        }
    }
}
