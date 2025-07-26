import java.util.*;

public class Rook extends Piece {
    public Rook(Color c) { super(c); }
    // implement appropriate methods

    public String toString() {
        return Piece.Color2Name(m_color) + "r";
    }

    public List<String> moves(Board b, String loc) {
        var coord = Board.Loc2Coord(loc);
        int row = coord.getKey();
        int col = coord.getValue();

        List<String> validMoves = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == j || i == -j) continue;
                deltaMoves(b, i, j, row, col, validMoves);
            }
        }
        return validMoves;
    }

}