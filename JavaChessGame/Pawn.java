import java.util.*;

public class Pawn extends Piece {
    public Pawn(Color c) { super(c);}
    // implement appropriate methods

    public String toString() {
        return Piece.Color2Name(m_color) + "p";
    }

    public List<String> moves(Board b, String loc) {
        var coord = Board.Loc2Coord(loc);
        int row = coord.getKey();
        int col = coord.getValue();

        List<String> validMoves = new ArrayList<>();
        int direction = (m_color == Color.WHITE) ? 1 : -1;

        // Check one step forward
        String oneStepForward = Board.Coord2Loc(row + direction, col);
        if (Board.inBounds(oneStepForward)) {
            validMoves.add(oneStepForward);
        }

        // Check two steps forward from starting position
        String twoStepsForward = Board.Coord2Loc(row + 2 * direction, col);
        if ((m_color == Color.WHITE && row == 1) || (m_color == Color.BLACK && row == 6)) {
            if (Board.inBounds(twoStepsForward) && b.getPiece(twoStepsForward) == null) {
                validMoves.add(twoStepsForward);
            }
        }

        // Check diagonal captures
        String[] diagonals = {Board.Coord2Loc(row + direction, col - 1), Board.Coord2Loc(row + direction, col + 1)};
        for (String diag : diagonals) {
            if (Board.inBounds(diag)) {
                Piece targetPiece = b.getPiece(diag);
                if (targetPiece != null && targetPiece.m_color != m_color) {
                    validMoves.add(diag);
                }
            }
        }

        return validMoves;
    }
}