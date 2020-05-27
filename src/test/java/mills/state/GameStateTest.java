package mills.state;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.hamcrest.collection.IsEmptyCollection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

class GameStateTest {

    private GameState gs;

    @BeforeEach
    public void init(){
        gs=new GameState();
    }

    @AfterEach
    public void tearDown(){
        gs=null;
    }






    @Test
    void isValidPlacementTest() {

        assertTrue(gs.isValidPlacement("c00"));
        assertTrue(gs.isValidPlacement("c32"));
        gs.placePieceOnBoard("c32",'1');
        assertFalse(gs.isValidPlacement("c32"));


    }

    @Test
    void isValidRemovalTest() {

         assertFalse(gs.isValidRemoval("c00",'1'));
         gs.placePieceOnBoard("c32",'1');
         assertTrue(gs.isValidRemoval("c32",'2'));
         gs.placePieceOnBoard("c30",'1');
         gs.placePieceOnBoard("c31",'1');
         gs.placePieceOnBoard("c66",'1');
        assertFalse(gs.isValidRemoval("c32",'2'));


    }

    @Test
    void getRemovablesTest() {

        assertThat(gs.getRemovables('1'),IsEmptyCollection.empty());
        gs.placePieceOnBoard("c30",'1');
        assertThat(gs.getRemovables('2'),hasItem("30"));
        gs.placePieceOnBoard("c00",'1');
        gs.placePieceOnBoard("c30",'1');
        gs.placePieceOnBoard("c60",'1');
        List<String> expected = Arrays.asList("00", "30", "60");
        assertThat(gs.getRemovables('2'),is(expected));


    }

    @Test
    void placePieceOnBoardTest() {

        gs.placePieceOnBoard("c55",'2');
        assertEquals('2',gs.getBoard()[5][5]);
        assertNotEquals('1',gs.getBoard()[3][0]);
        gs.placePieceOnBoard("c30",'2');
        assertEquals('2',gs.getBoard()[3][0]);

    }

    @Test
    void removePieceFromBoardTest() {

        gs.placePieceOnBoard("c55",'2');
        assertEquals('2',gs.getBoard()[5][5]);
        gs.removePieceFromBoard("c55");
        assertEquals('0',gs.getBoard()[5][5]);

    }



    @Test
    void checkForValidMovementTest() {

        gs.placePieceOnBoard("c00",'2');
        gs.placePieceOnBoard("c60",'2');
        List<String> expected = Collections.singletonList("31");
        assertThat(gs.checkForValidMovement("c30"),is(expected));
        assertThat(gs.checkForValidMovement("c31"),hasItems("51","11","32","30"));
        gs.placePieceOnBoard("c31",'1');
        assertThat(gs.checkForValidMovement("c30"),IsEmptyCollection.empty());

    }

    @Test
    void getPlayerOfPieceTest() {

        gs.placePieceOnBoard("c30",'1');
        assertEquals('1',gs.getPlayerOfPiece("c30"));
        assertEquals('0',gs.getPlayerOfPiece("c31"));
        gs.placePieceOnBoard("c66",'2');
        assertEquals('2',gs.getPlayerOfPiece("c66"));

    }

    @Test
    void countPlayerPiecesTest() {

        gs.placePieceOnBoard("c30",'1');
        gs.placePieceOnBoard("c60",'1');
        gs.placePieceOnBoard("c00",'1');
        gs.placePieceOnBoard("c31",'1');
        assertEquals(4,gs.countPlayerPieces('1'));
        gs.placePieceOnBoard("c32",'2');
        gs.placePieceOnBoard("c63",'2');
        gs.placePieceOnBoard("c03",'2');
        gs.placePieceOnBoard("c55",'2');
        gs.placePieceOnBoard("c51",'2');
        gs.placePieceOnBoard("c42",'2');
        assertEquals(6,gs.countPlayerPieces('2'));

    }

    @Test
    void isMillTest() {

        assertFalse(gs.isMill("c00",'1',gs.getBoard()));
        gs.placePieceOnBoard("c30",'1');
        gs.placePieceOnBoard("c60",'1');
        gs.placePieceOnBoard("c00",'1');
        assertTrue(gs.isMill("c00",'1',gs.getBoard()));
        assertFalse(gs.isMill("c00",'2',gs.getBoard()));
        assertFalse(gs.isMill("c66",'2',gs.getBoard()));
        gs.placePieceOnBoard("c06",'2');
        gs.placePieceOnBoard("c36",'2');
        gs.placePieceOnBoard("c66",'2');
        assertTrue(gs.isMill("c66",'2',gs.getBoard()));


    }

    @Test
    void getVacantPointsTest() {

        assertThat(gs.getVacantPoints(),hasSize(24));
        gs.placePieceOnBoard("c06",'2');
        gs.placePieceOnBoard("c36",'1');
        gs.placePieceOnBoard("c66",'2');
        gs.placePieceOnBoard("c60",'1');
        gs.placePieceOnBoard("c00",'2');
        assertThat(gs.getVacantPoints(),hasSize(19));
        assertThat(gs.getVacantPoints(),hasItem("31"));
        gs.placePieceOnBoard("c31",'2');
        assertThat(gs.getVacantPoints(),not(hasItem("31")));


    }
}