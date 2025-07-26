import java.util.*;

abstract public class Piece {
    public static HashMap<Character, PieceFactory> pieceMap = new HashMap<>();

    public Color m_color;

    public Piece(Color c) {m_color = c;}

    public static void registerPiece(PieceFactory pf) {
        pieceMap.put(pf.symbol(), pf);
    }
    public static Piece createPiece(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if(name.charAt(0) == 'b') {
            return pieceMap.get(name.charAt(1)).create(Color.BLACK);
        }
        if(name.charAt(0) == 'w') {
            return pieceMap.get(name.charAt(1)).create(Color.WHITE);
        }
        throw new IllegalArgumentException("SB");
    }

    public Color color() {
        return m_color;
    }

    public static String Color2Name(Color c) {
        switch (c) {
            case WHITE: return "w";
            case BLACK: return "b";
            // Add more cases for other colors if necessary (?)
            default: throw new IllegalArgumentException("Unknown color");
        }
    }

    public void deltaMoves(Board b, int rowMul, int colMul, int row, int col, List<String> validMoves) {
        for (int delta = 1; delta < Board.DIMENSION; delta++) {
            String nextPos = Board.Coord2Loc(row + delta * rowMul, col + delta * colMul);
            if (!Board.inBounds(nextPos)) {
                break;
            }
            Piece p = b.getPiece(nextPos);
            if (p == null)
            {
                validMoves.add(nextPos);
                continue;
            }
            if (!p.color().equals(this.color())) {
                validMoves.add(nextPos);
                break;
            }
        }
    }

    abstract public String toString();

    abstract public List<String> moves(Board b, String loc);
}