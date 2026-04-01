import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.stefancooper.EasyUHC.Plugin;
import com.stefancooper.EasyUHC.types.EvolvingShield;
import com.stefancooper.EasyUHC.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.ExperienceOrbMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import utils.TestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.stefancooper.EasyUHC.Defaults.END_WORLD_NAME;
import static com.stefancooper.EasyUHC.Defaults.NETHER_WORLD_NAME;
import static com.stefancooper.EasyUHC.Defaults.WORLD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.TestUtils.WorldAssertion;

public class EvolvingShieldTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static PlayerMock serverOp;

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        world = server.getWorld(WORLD_NAME);
        serverOp = server.addPlayer();
        serverOp.setOp(true);
    }

    @BeforeEach
    public void cleanUp() {
        TestUtils.executeCommand(plugin, serverOp, "cancel");
        plugin.getUHCConfig().resetToDefaults();
    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    private void assertUpdatedXP(final ItemStack shield, final Player player, final int newXP, final Map<Enchantment, Integer> enchantments) {
        assertEquals(List.of(
                Component.text(""),
                Component.text("Level this shield up by gaining XP,"),
                Component.text("dealing damage to players and getting kills!"),
                Component.text(""),
                Component.text(String.format("Current XP: %s", newXP))
        ), shield.getItemMeta().lore());
        assertEquals(enchantments, shield.getEnchantments());
        assertEquals(player.getName(), shield.getItemMeta().getPersistentDataContainer().get(plugin.getUHCConfig().getManagedResources().getEvolvingShieldUserKey(), PersistentDataType.STRING));
        assertEquals(newXP, shield.getItemMeta().getPersistentDataContainer().get(plugin.getUHCConfig().getManagedResources().getEvolvingShieldXPKey(), PersistentDataType.INTEGER));
    }

    private void addXPViaEvent(final Player player, final int addXP) {
        ExperienceOrb orb = world.spawn(player.getLocation(), ExperienceOrb.class);
        orb.setExperience(addXP);
        server.getPluginManager().callEvent(new PlayerPickupExperienceEvent(player, orb));
    }

    @Test
    @DisplayName("The shield evolves as players get more XP")
    void evolvingShieldsXPTest() {
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        TestUtils.executeCommand(plugin, admin, "set",
                "enable.evolving.shields=true"
        );

        PlayerMock player1 = server.addPlayer();
        player1.setName("stefan");

        TestUtils.executeCommand(plugin, admin, "start");

        assertTrue(EvolvingShield.isEvolvingShield(plugin.getUHCConfig(), player1.getInventory().getItemInMainHand()));
        assertUpdatedXP(
                player1.getInventory().getItemInMainHand(),
                player1,
                0,
                Map.of()
        );

        addXPViaEvent(player1, 10);
        assertUpdatedXP(
                player1.getInventory().getItemInMainHand(),
                player1,
                10,
                Map.of()
        );

        addXPViaEvent(player1, 90);
        assertUpdatedXP(
                player1.getInventory().getItemInMainHand(),
                player1,
                EvolvingShield.STAGE_1,
                Map.of(
                        Enchantment.KNOCKBACK, 1
                )
        );

        addXPViaEvent(player1, EvolvingShield.STAGE_2 - EvolvingShield.STAGE_1);
        assertUpdatedXP(
                player1.getInventory().getItemInMainHand(),
                player1,
                EvolvingShield.STAGE_2,
                Map.of(
                        Enchantment.KNOCKBACK, 2
                )
        );

        addXPViaEvent(player1, EvolvingShield.STAGE_3 - EvolvingShield.STAGE_2);
        assertUpdatedXP(
                player1.getInventory().getItemInMainHand(),
                player1,
                EvolvingShield.STAGE_3,
                Map.of(
                        Enchantment.KNOCKBACK, 2,
                        Enchantment.THORNS, 1
                )
        );

        addXPViaEvent(player1, EvolvingShield.STAGE_4 - EvolvingShield.STAGE_3);
        assertUpdatedXP(
                player1.getInventory().getItemInMainHand(),
                player1,
                EvolvingShield.STAGE_4,
                Map.of(
                        Enchantment.KNOCKBACK, 2,
                        Enchantment.THORNS, 2
                )
        );

        addXPViaEvent(player1, EvolvingShield.STAGE_5 - EvolvingShield.STAGE_4);
        assertUpdatedXP(
                player1.getInventory().getItemInMainHand(),
                player1,
                EvolvingShield.STAGE_5,
                Map.of(
                        Enchantment.KNOCKBACK, 2,
                        Enchantment.THORNS, 3
                )
        );

        TestUtils.executeCommand(plugin, admin, "cancel");
    }
}
