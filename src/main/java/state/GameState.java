package state;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Class representing the state of the game.
 */
public class GameState {

    /**
     * The array representing the initial configuration of the board.
     */
    public static final char[][] INITIAL = {
            {'0', '-', '-', '0', '-', '-','0'},
            {'|', '0', '-', '0', '-', '0','|'},
            {'|', '|', '0', '0', '0', '|','|'},
            {'0', '0', '0', '*', '0', '0','0'},
            {'|', '|', '0', '0', '0', '|','|'},
            {'|', '0', '-', '0', '-', '0','|'},
            {'0', '-', '-', '0', '-', '-','0'}

    };

    /**
     * The array storing the current configuration of the board.
     */

    @Getter
    private char[][] Board;



    /**
     * Creates a {@code GameState} object that represents the initial state of the board
     */
    public GameState() {
        this.Board=INITIAL;
    }


    /**
     * Checks whether the player can place one of his pieces on the clicked slot
     *
     * @param slot the id of the clicked slot, which determines the slot's place on the board
     *
     * @return {@code true} if the clicked slot is empty, {@code false} otherwise
     */
    public boolean isValidPlacement(String slot){
        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        return Board[row][col] == '0';
    }


    /**
     * Places the player's piece in the array representing the board
     *
     * @param slot the id of the clicked slot, which determines the slot's place on the board
     *
     * @param playerNum the {@code char} representation of the player's number, which will be placed on the board.
     */
    public void placePieceOnBoard(String slot,char playerNum){
        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        Board[row][col]=playerNum;

    }

}
