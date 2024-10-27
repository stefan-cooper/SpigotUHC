import com.stefancooper.SpigotUHC.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UtilsTest {


    private static Stream<Arguments> worldBorderProgressTestParameters() {
        return Stream.of(
                // Not moved
                Arguments.of(2000, 1000, 2000, 1.0),
                // Half way
                Arguments.of(2000, 1000, 1500, 0.5),
                // Complete
                Arguments.of(2000, 1000, 1000, 0.0),
                // Misc
                Arguments.of(2000, 1000, 1350, 0.35),
                Arguments.of(2000, 1000, 1249, 0.249),
                Arguments.of(2000, 1000, 1100, 0.1)

        );
    }

    @ParameterizedTest
    @MethodSource("worldBorderProgressTestParameters")
    @DisplayName("Correct progress percentage is calculated given the parameters")
    void worldBorderProgressTest(int initialSize, int finalSize, int current, double expected) {
        Assertions.assertEquals(expected, Utils.calculateWorldBorderProgress(initialSize, finalSize, current));
    }
}
