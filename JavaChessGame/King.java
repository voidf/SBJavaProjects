import java.util.*;

public class King extends Piece {
    public King(Color c) { super(c);}
    // implement appropriate methods

    public String toString() {
        return Piece.Color2Name(m_color) + "k";
    }

    public List<String> moves(Board b, String loc) {
        var coord = Board.Loc2Coord(loc);
        int row = coord.getKey();
        int col = coord.getValue();
        ArrayList<String> result = new ArrayList<>();
        for (int i=-1; i<=1; i++) {
            for (int j=-1; j<=1; j++) {
                if (i == 0 && j == 0) continue; // Skip the current location
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < Board.DIMENSION && newCol >= 0 && newCol < Board.DIMENSION) {
                    String newLoc = "" + (char)(newCol + 97) + (newRow + 1);
                    if (b.getPiece(newLoc) == null || b.getPiece(newLoc).m_color != m_color) {
                        result.add(newLoc);
                    }
                }
            }
        }
        return result;
    }

}