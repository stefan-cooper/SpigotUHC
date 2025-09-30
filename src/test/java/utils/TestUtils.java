package utils;

import com.stefancooper.SpigotUHC.Plugin;
import com.stefancooper.SpigotUHC.commands.UHCCommand;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class TestUtils {

    public interface WorldAssertion {
        void execute(World world);
    }

    public static void executeCommand(Plugin plugin, CommandSender sender, String... args) {
        final UHCCommand uhc = new UHCCommand(plugin.getUHCConfig());
        uhc.execute(sender, "uhc", args);
    }
}
