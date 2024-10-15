package utils;

import org.bukkit.World;

public class TestUtils {

    public interface WorldAssertion {
        void execute(World world);
    }
}
