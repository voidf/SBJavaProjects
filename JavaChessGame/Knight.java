import java.util.*;

public class Knight extends Piece {
    public Knight(Color c) {
        super(c);
    }
    // implement appropriate methods

    public String toString() {
        return Piece.Color2Name(m_color) + "n";
    }

    public List<String> moves(Board b, String loc) {
        var coord = Board.Loc2Coord(loc);
        int row = coord.getKey();
        int col = coord.getValue();

        List<String> validMoves = new ArrayList<>();
        int[][] knightMoves = {
                { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 },
                { 1, -2 }, { 1, 2 }, { 2, -1 }, { 2, 1 }
        };

        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (newRow >= 0 && newRow < Board.DIMENSION && newCol >= 0 && newCol < Board.DIMENSION) {
                String newLoc = "" + (char) (newCol + 97) + (newRow + 1);
                Piece targetPiece = b.getPiece(newLoc);

                if (targetPiece == null || targetPiece.color() != color()) {
                    validMoves.add(newLoc);
                }
            }
        }

        return validMoves;
    }
}