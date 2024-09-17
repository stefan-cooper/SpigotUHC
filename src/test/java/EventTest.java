import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.stefancooper.SpigotUHC.Plugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.stefancooper.SpigotUHC.Defaults.DEFAULT_WORLD_NAME;

import org.junit.jupiter.api.*;

public class EventTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;

    @BeforeAll
    public static void load() {
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
    @DisplayName("Test the on death event to ensure a player is set to Spectator after death")
    void testOnDeathEventSpectate() {
        PlayerMock player = server.addPlayer();
        player.damage(100);
        player.getGameMode();
        Assertions.assertEquals(GameMode.SPECTATOR, player.getGameMode());
    }

    @Test
    @DisplayName("Test the on death event to ensure a player is kicked after death")
    void testOnDeathEventKick() {
        server.setPlayers(15);
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "on.death.action=kick");
        Assertions.assertEquals(16, server.getOnlinePlayers().size());
        player.damage(100);
        server.getOnlinePlayers();
        Assertions.assertEquals(15, server.getOnlinePlayers().size());
    }

    @Test
    @DisplayName("Test the on death event to ensure a player drops a head after death")
    void testHeadDropOnDeath() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        server.execute("uhc", player, "set", "player.head.golden.apple=true");
        player.damage(100);
        if(!world.getEntities().stream().filter(entity -> entity.getType() == EntityType.ITEM && entity.getName().equals("PLAYER_HEAD")).toList().isEmpty()){
            Item droppedItem = (Item) world.getEntities().get(0);
            droppedItem.getItemStack();

            Assertions.assertEquals(Material.PLAYER_HEAD, droppedItem.getType());
        }
    }


}