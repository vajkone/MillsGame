package mills.state;

import java.util.List;

/**
 * Class representing all possible mill combinations on the board
 */
public enum Mill {

    H1(List.of(new int[]{0, 0}, new int[]{0, 3},new int[]{0,6})),
    H2(List.of(new int[]{1, 1}, new int[]{1,3},new int[]{1,5})),
    H3(List.of(new int[]{2,2}, new int[]{2, 3},new int[]{2,4})),
    H4(List.of(new int[]{3, 0}, new int[]{3, 1},new int[]{3,2})),
    H5(List.of(new int[]{3,4}, new int[]{3, 5},new int[]{3,6})),
    H6(List.of(new int[]{4, 2}, new int[]{4, 3},new int[]{4,4})),
    H7(List.of(new int[]{5,1}, new int[]{5,3},new int[]{5,5})),
    H8(List.of(new int[]{6,0}, new int[]{6,3},new int[]{6,6})),

    V1(List.of(new int[]{0, 0}, new int[]{3,0},new int[]{6,0})),
    V2(List.of(new int[]{1,1}, new int[]{3,1},new int[]{5,1})),
    V3(List.of(new int[]{2,2}, new int[]{3,2},new int[]{4,2})),
    V4(List.of(new int[]{0, 3}, new int[]{1, 3},new int[]{2,3})),
    V5(List.of(new int[]{4,3}, new int[]{5, 3},new int[]{6,3})),
    V6(List.of(new int[]{2,4}, new int[]{3,4},new int[]{4,4})),
    V7(List.of(new int[]{1,5}, new int[]{3,5},new int[]{5,5})),
    V8(List.of(new int[]{0, 6}, new int[]{3,6},new int[]{6,6}));


    private List<int[]> arrayCoords;

    private Mill(List<int[]> arrayCoords) {
        this.arrayCoords=arrayCoords;
    }

    /**
     * Return whether the slot is forming a mill or not
     *
     * @param slot the x and y-coordinates of the slot
     * @param playerNum the {@code char} representation of the player, whose piece is on the slot
     * @param currentBoard array representing the current state of the board
     * @return {@code true} if the player is forming a mill on the given slot, {@code false} otherwise
     */

    public static boolean millTest(int[] slot,char playerNum,char[][] currentBoard){
        Mill test = null;
        int piececounter=0;
        for (Mill mill : values()){
            for (int i = 0; i < 3; i++) {
                if (mill.arrayCoords.get(i)[0] == slot[0] && mill.arrayCoords.get(i)[1] == slot[1]) {
                    test = mill;
                    break;
                }
            }
            if (test!=null) {
                for (int i = 0; i < 3; i++) {

                    int x = test.arrayCoords.get(i)[0];
                    int y = test.arrayCoords.get(i)[1];

                    if (currentBoard[x][y] == playerNum) {
                        piececounter++;
                    }
                }
                if (piececounter == 3) {
                    return true;
                } else {
                    piececounter = 0;
                    test=null;
                }

            }

        }
        return false;
    }
}
