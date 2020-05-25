package mills.state;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

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
     * Creates a {@code GameState} object that represents the initial state of the board.
     */
    public GameState() {
        this.Board=INITIAL;
    }


    /**
     * Checks whether the player can place one of his pieces on the clicked slot.
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
     * Checks whether the player is trying to remove one of his/her opponent's pieces from the clicked slot.
     *
     * @param slot the id of the clicked slot
     *
     * @return {@code true} if the clicked slot has the opponent's piece on it, {@code false} otherwise
     */
    public boolean isValidRemoval(String slot,char playerNum){
        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        return Board[row][col] != playerNum && Board[row][col] != '0';


    }


    /**
     * Places the player's piece in the array representing the board.
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

    /**
     * Removes the (other) player's piece from the clicked slot.
     *
     * @param slot the id of the clicked slot, which determines the slot's place on the board
     */
    public void removePieceFromBoard(String slot){
        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        Board[row][col]='0';

    }

    public ArrayList<String> checkForValidMovement(String slot){
        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        ArrayList<String> canMoveTo=new ArrayList<String>();



        if (row==0){
            for (int i = 1; i < 4; i++) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2'){
                    break;
                }

            }
        }else if(row<6){
            for (int i = row+1; i < 7; i++) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2'){
                    break;
                }
            }
            for (int i = row-1; i >= 0; i--) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2'){
                    break;
                }
            }
        }else{
            for (int i = 5; i >= 3; i--) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2'){
                    break;
                }
            }
        }

        if (col==0){
            for (int i = 1; i < 4; i++) {
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2'){
                    break;
                }

            }
        }else if(col < 6){
            for (int i = col+1; i < 7; i++) {
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2'){
                    break;
                }
            }
            for (int i = col-1; i >= 0; i--){
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2'){
                    break;
                }
            }
        }else{
            for (int i = 5; i >= 3; i--) {
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2'){
                    break;
                }
            }
        }


        return canMoveTo;


    }

    public boolean isMill(String slot,char playerNum,char[][] board){

        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        return Mill.millTest(new int[]{row,col},playerNum,board);

    }

}
