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
     * Checks whether the player is trying to remove a valid piece from the board
     *
     * @param slot the id of the clicked slot, where the player wants to remove a piece from
     * @param playerNum the {@code char} representation of the currently acting player's number
     *
     * @return {@code true} if the opponent's piece can be legally removed from the clicked slot, {@code false} otherwise
     */
    public boolean isValidRemoval(String slot,char playerNum){

        String slotPruned=slot.substring(1,3);

        return getRemovables(playerNum).contains(slotPruned);



    }
    /**
     * Collects all of the opponents legally removable piece from the board
     *
     *
     * @param actingPlayer the {@code char} representation of the currently acting player's number
     *
     * @return the {@code List} of the opponent's legally removable pieces
     */

    public ArrayList<String> getRemovables(char actingPlayer){
        ArrayList<String> removables = new ArrayList<>();
        char otherPlayer;
        if (actingPlayer=='1'){
            otherPlayer='2';
        }else{
            otherPlayer='1';
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (Board[i][j]==otherPlayer && !Mill.millTest(new int[]{i,j},otherPlayer,Board)){
                    removables.add(String.valueOf(i)+j);
                }
            }
        }

        if (removables.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    if (Board[i][j] == otherPlayer) {
                        removables.add(String.valueOf(i) + j);
                    }
                }
            }
        }
        return removables;


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

    /**
     * Checks all possible moves of the clicked piece.
     *
     * @param slot the id of the clicked slot.
     *
     * @return the {@code List} of coordinates (in {@code String} format) the clicked piece can move to
     */
    public ArrayList<String> checkForValidMovement(String slot){
        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        ArrayList<String> canMoveTo=new ArrayList<>();



        if (row==0){
            for (int i = 1; i < 4; i++) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2'  ){
                    break;
                }

            }
        }else if(row<6){
            for (int i = row+1; i < 7; i++) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2' ||getBoard()[i][col]=='*'){
                    break;
                }
            }
            for (int i = row-1; i >= 0; i--) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2'  ||getBoard()[i][col]=='*'){
                    break;
                }
            }
        }else{
            for (int i = 5; i >= 3; i--) {
                if (getBoard()[i][col]=='0'){
                    canMoveTo.add(String.valueOf(i)+col);
                    break;
                }else if(getBoard()[i][col]=='1' || getBoard()[i][col]=='2'  ){
                    break;
                }
            }
        }

        if (col==0){
            for (int i = 1; i < 4; i++) {
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2' ){
                    break;
                }

            }
        }else if(col < 6){
            for (int i = col+1; i < 7; i++) {
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2' ||getBoard()[row][i]=='*'){
                    break;
                }
            }
            for (int i = col-1; i >= 0; i--){
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2' ||getBoard()[row][i]=='*'){
                    break;
                }
            }
        }else{
            for (int i = 5; i >= 3; i--) {
                if (getBoard()[row][i]=='0'){
                    canMoveTo.add(String.valueOf(row)+i);
                    break;
                }else if(getBoard()[row][i]=='1' || getBoard()[row][i]=='2' ){
                    break;
                }
            }
        }


        return canMoveTo;


    }

    /**
     * Gets the player's corresponding number of the clicked slot
     *
     * @param slot the id of the clicked slot.
     *
     * @return the {@code char} representation of the player's number whose man is on the clicked slot, the {@code char} '0' if the slot is empty
     */
    public char getPlayerOfPiece(String slot){
        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        return Board[row][col];

    }

    public int countPlayerPieces(char playerNum){
        int count=0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (Board[i][j]==playerNum){
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isMill(String slot,char playerNum,char[][] board){


        int row = Integer.parseInt(slot.substring(1,2));
        int col = Integer.parseInt(slot.substring(2,3));

        return Mill.millTest(new int[]{row,col},playerNum,board);

    }

    public ArrayList<String> getVacantPoints() {

        ArrayList<String> vacantPoints=new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (Board[i][j]=='0'){
                    vacantPoints.add(String.valueOf(i)+j);
                }
            }
        }


        return vacantPoints;

    }
}
