package mocks.types;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RespawnPlayerMock extends PlayerMock {
    private final PlayerSpigotMock playerSpigotMock = new PlayerSpigotMock();

    public RespawnPlayerMock(@NotNull ServerMock server, @NotNull String name, @NotNull UUID uuid) {
        super(server, name, uuid);
    }

    @Override
    public Player.@NotNull Spigot spigot()
    {
        return playerSpigotMock;
    }

    public class PlayerSpigotMock extends Player.Spigot
    {
        @Override
        public void respawn()
        {
            setHealth(getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            setLocation(new Location(getWorld(), 0, 64, 0));
            alive = true;
        }
    }
}
