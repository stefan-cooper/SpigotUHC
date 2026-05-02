import com.stefancooper.EasyUHC.base.records.Coordinate;
import com.stefancooper.EasyUHC.base.SpreadPlayers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpreadPlayersTest {


    @Test
    void singleCoordTest () {
        final List<Coordinate> coords = SpreadPlayers.splitEvenly(0, 0, 100, 1);

        assertEquals(1, coords.size());
        assertEquals(0.0, coords.getFirst().x());
        assertEquals(0.0, coords.getFirst().z());
    }

    @Test
    void twoCoordTest () {
        final List<Coordinate> coords = SpreadPlayers.splitEvenly(0, 0, 100, 2);

        assertEquals(2, coords.size());
        assertEquals(-50, coords.getFirst().x());
        assertEquals(-50, coords.getFirst().z());
        assertEquals(50, coords.get(1).x());
        assertEquals(-50, coords.get(1).z());
    }

    @Test
    void fourCoordTest () {
        final List<Coordinate> coords = SpreadPlayers.splitEvenly(0, 0, 100, 4);

        assertEquals(4, coords.size());
        // bottom left
        assertEquals(-50, coords.getFirst().x());
        assertEquals(-50, coords.getFirst().z());
        // bottom right
        assertEquals(50, coords.get(1).x());
        assertEquals(-50, coords.get(1).z());
        // top left
        assertEquals(-50, coords.get(2).x());
        assertEquals(50, coords.get(2).z());
        // top right
        assertEquals(50, coords.get(3).x());
        assertEquals(50, coords.get(3).z());

    }

    @Test
    void fiveCoordTest () {
        final List<Coordinate> coords = SpreadPlayers.splitEvenly(0, 0, 100, 5);

        assertEquals(5, coords.size());
        // bottom left
        assertEquals(-50, coords.getFirst().x());
        assertEquals(-50, coords.getFirst().z());
        // bottom middle
        assertEquals(0, coords.get(1).x());
        assertEquals(-50, coords.get(1).z());
        // bottom right
        assertEquals(50, coords.get(2).x());
        assertEquals(-50, coords.get(2).z());
        // top left
        assertEquals(-50, coords.get(3).x());
        assertEquals(50, coords.get(3).z());
        // top middle
        assertEquals(0, coords.get(4).x());
        assertEquals(50, coords.get(4).z());

    }

    @Test
    void nineCoordTest () {
        final List<Coordinate> coords = SpreadPlayers.splitEvenly(0, 0, 100, 9);

        assertEquals(9, coords.size());
        // bottom left
        assertEquals(-50, coords.getFirst().x());
        assertEquals(-50, coords.getFirst().z());
        // bottom middle
        assertEquals(0, coords.get(1).x());
        assertEquals(-50, coords.get(1).z());
        // bottom right
        assertEquals(50, coords.get(2).x());
        assertEquals(-50, coords.get(2).z());
        // middle left
        assertEquals(-50, coords.get(3).x());
        assertEquals(0, coords.get(3).z());
        // middle middle
        assertEquals(0, coords.get(4).x());
        assertEquals(0, coords.get(4).z());
        // middle left
        assertEquals(50, coords.get(5).x());
        assertEquals(0, coords.get(5).z());
        // top left
        assertEquals(-50, coords.get(6).x());
        assertEquals(50, coords.get(6).z());
        // top middle
        assertEquals(0, coords.get(7).x());
        assertEquals(50, coords.get(7).z());
        // top right
        assertEquals(50, coords.get(8).x());
        assertEquals(50, coords.get(8).z());
    }

    @Test
    void twentyCoordTest () {
        final List<Coordinate> coords = SpreadPlayers.splitEvenly(0, 0, 1000, 20);

        assertEquals(20, coords.size());
        // bottom row
        assertEquals(-500, coords.getFirst().x());
        assertEquals(-500, coords.getFirst().z());
        assertEquals(-250, coords.get(1).x());
        assertEquals(-500, coords.get(1).z());
        assertEquals(0, coords.get(2).x());
        assertEquals(-500, coords.get(2).z());
        assertEquals(250, coords.get(3).x());
        assertEquals(-500, coords.get(3).z());
        assertEquals(500, coords.get(4).x());
        assertEquals(-500, coords.get(4).z());
        // bottom middle row
        assertEquals(-500, coords.get(5).x());
        assertEquals(-166, (int) coords.get(5).z());
        assertEquals(-250, coords.get(6).x());
        assertEquals(-166, (int) coords.get(6).z());
        assertEquals(0, coords.get(7).x());
        assertEquals(-166, (int) coords.get(7).z());
        assertEquals(250, coords.get(8).x());
        assertEquals(-166, (int) coords.get(8).z());
        assertEquals(500, coords.get(9).x());
        assertEquals(-166, (int) coords.get(9).z());
        // top middle row
        assertEquals(-500, coords.get(10).x());
        assertEquals(166, (int) coords.get(10).z());
        assertEquals(-250, coords.get(11).x());
        assertEquals(166, (int) coords.get(11).z());
        assertEquals(0, coords.get(12).x());
        assertEquals(166, (int) coords.get(12).z());
        assertEquals(250, coords.get(13).x());
        assertEquals(166, (int) coords.get(13).z());
        assertEquals(500, coords.get(14).x());
        assertEquals(166, (int) coords.get(14).z());
        // top row
        assertEquals(-500, coords.get(15).x());
        assertEquals(500, coords.get(15).z());
        assertEquals(-250, coords.get(16).x());
        assertEquals(500, coords.get(16).z());
        assertEquals(0, coords.get(17).x());
        assertEquals(500, coords.get(17).z());
        assertEquals(250, coords.get(18).x());
        assertEquals(500, coords.get(18).z());
        assertEquals(500, coords.get(19).x());
        assertEquals(500, coords.get(19).z());
    }

    @Test
    void bigCoordTest () {
        final List<Coordinate> coords = SpreadPlayers.splitEvenly(0, 0, 1000, 100);

        assertEquals(100, coords.size());
        for (final Coordinate coord : coords) {
            assertTrue(coord.x() >= -500 && coord.x() <= 500);
            assertTrue(coord.z() >= -500 && coord.z() <= 500);
        }
    }

    @Test
    void badDiameterTest () {
        assertEquals(List.of(), SpreadPlayers.splitEvenly(0, 0, -1, 3));
    }

    @Test
    void badGroupsCoordTest () {
        assertEquals(List.of(), SpreadPlayers.splitEvenly(0, 0, 100, -1));
        assertEquals(List.of(), SpreadPlayers.splitEvenly(0, 0, 100, 0));
    }
}
