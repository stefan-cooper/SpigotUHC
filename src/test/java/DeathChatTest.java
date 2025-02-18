import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import static com.stefancooper.SpigotUHC.Defaults.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeathChatTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static World world;
    private static ChatListener listener;
    private final static String jawadsMessage = "foobar";

    @BeforeAll
    public static void load()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
        listener = spy(new ChatListener());
        server.getPluginManager().registerEvents(listener, plugin);
    }

    @BeforeEach
    public void cleanUp() {
        plugin.getUHCConfig().resetToDefaults();
        Mockito.reset(listener);

    }

    @AfterAll
    public static void unload() {
        plugin.getUHCConfig().resetToDefaults();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Death chat works as expected as non spectator")
    void deathChatTest()  {
        BukkitSchedulerMock schedule = server.getScheduler();
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock player1 = server.addPlayer();
        PlayerMock player2 = server.addPlayer();
        PlayerMock player3 = server.addPlayer();
        PlayerMock player4 = server.addPlayer();
        player1.setName("stefan");
        player2.setName("jawad");
        player3.setName("shurf");
        player4.setName("sean");

        server.execute("uhc", admin, "set",
                "team.red=stefan",
                "team.blue=jawad",
                "team.yellow=shurf",
                "team.pink=sean",
                "enable.death.chat=true"
        );

        server.execute("uhc", admin, "start");

        schedule.performOneTick();

        player2.chat(jawadsMessage);
        schedule.waitAsyncEventsFinished();
        verify(listener).onAsyncChat(any());
    }

    @Test
    @DisplayName("Death chat works as expected as spectator")
    void deathChatTestAsSpectator()  {
        BukkitSchedulerMock schedule = server.getScheduler();
        PlayerMock admin = server.addPlayer();
        admin.setOp(true);

        PlayerMock player1 = server.addPlayer();
        PlayerMock player2 = server.addPlayer();
        PlayerMock player3 = server.addPlayer();
        PlayerMock player4 = server.addPlayer();
        player1.setName("stefan");
        player2.setName("jawad");
        player3.setName("shurf");
        player4.setName("sean");

        server.execute("uhc", admin, "set",
                "team.red=stefan",
                "team.blue=jawad",
                "team.yellow=shurf",
                "team.pink=sean",
                "enable.death.chat=true"
        );

        server.execute("uhc", admin, "start");

        schedule.performOneTick();

        player2.setGameMode(GameMode.SPECTATOR);
        player2.chat(jawadsMessage);
        schedule.waitAsyncEventsFinished();
        verify(listener).onAsyncChat(any());
    }

    public static class ChatListener implements Listener
    {
        @EventHandler
        public void onAsyncChat(AsyncPlayerChatEvent event) {
            if (event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
                assertEquals(String.format("(Death Chat) %s", jawadsMessage), event.getMessage());
            } else {
                assertEquals(jawadsMessage, event.getMessage());
            }

        }
    }
}
