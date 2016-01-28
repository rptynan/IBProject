package uk.ac.cam.quebec.userapi;

public class UserAPIOuter {

    /**
     * Validates a chess move.
     *
     * <p>Use {@link #doMove(int theFromFile, int theFromRank, int theToFile,
     * int theToRank)} to move a piece.
     *
     * @param theFromFile file from which a piece is being moved
     * @param theFromRank rank from which a piece is being moved
     * @param theToFile   file to which a piece is being moved
     * @param theToRank   rank to which a piece is being moved
     * @return            true if the move is valid, otherwise false
     */
    boolean isValidMove(int theFromFile, int theFromRank, int theToFile, int theToRank) {
        // ...body
        return true;
    }

    /**
     * Moves a chess piece.
     *
     * @see java.math.RoundingMode
     */
    void doMove(int theFromFile, int theFromRank, int theToFile, int theToRank)  {
        // ...body
    }

}
