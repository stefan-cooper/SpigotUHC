package mocks.servers;

import be.seeseemelk.mockbukkit.AsyncCatcher;
import be.seeseemelk.mockbukkit.ServerMock;
import mocks.types.RespawnPlayerMock;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RespawnPlayerServerMock extends ServerMock  {

    @Override
    public @NotNull RespawnPlayerMock addPlayer(@NotNull String name)
    {
        AsyncCatcher.catchOp("player add");
        RespawnPlayerMock player = new RespawnPlayerMock(this, name, UUID.randomUUID());
        addPlayer(player);
        return player;
    }
}
