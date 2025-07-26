import java.util.*;

public class Bishop extends Piece {
    public Bishop(Color c) { 
        super(c);
    }

    public String toString() {
        return Piece.Color2Name(m_color) + "b";
    }

    public List<String> moves(Board b, String loc) {
        var coord = Board.Loc2Coord(loc);
        int row = coord.getKey();
        int col = coord.getValue();


        List<String> validMoves = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (!(i == j || i == -j)) continue;
                deltaMoves(b, i, j, row, col, validMoves);
            }
        }
        
        return validMoves;
    }
}