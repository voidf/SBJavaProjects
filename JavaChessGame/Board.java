import java.util.HashMap;

public class Board {

    public static final int DIMENSION = 8;
    public static Board instance = new Board();

    private Piece[][] pieces = new Piece[DIMENSION][DIMENSION];

    private Board() { }

    public static java.util.Map.Entry<Integer, Integer> Loc2Coord(String loc) {
        int row = Character.getNumericValue(loc.charAt(1)) - 1;
        int col = loc.charAt(0) - 'a';
        return java.util.Map.entry(row, col);
    }

    public static String Coord2Loc(int row, int col) {
        return "" + (char)(col + 'a') + (row + 1);
    }

    public static Board theBoard() {
        return instance;
    }
    public static boolean inBounds(String loc) {
        var coord = Loc2Coord(loc);
        return coord.getKey() >= 0 && coord.getKey() < DIMENSION && coord.getValue() >= 0 && coord.getValue() < DIMENSION;
    }

    public static boolean inBounds(int row, int col) {
        return row >= 0 && row < DIMENSION && col >= 0 && col < DIMENSION;
    }

    // Returns piece at given loc or null if no such piece
    // exists
    public Piece getPiece(String loc) {
        var coord = Loc2Coord(loc);
        return pieces[coord.getKey()][coord.getValue()];
    }
    public Piece getPiece(int r, int c) {
        return pieces[r][c];
    }

    public void addPiece(Piece p, String loc) {
        if (!Board.inBounds(loc)) {
            throw new IllegalArgumentException("Invalid location: " + loc);
        }
        var coord = Loc2Coord(loc);
        if (pieces[coord.getKey()][coord.getValue()] != null) {
            throw new IllegalStateException("Location is occupied: " + loc);
        }
        pieces[coord.getKey()][coord.getValue()] = p;
    }

    public void movePiece(String from, String to) {
        var fromCoord = Loc2Coord(from);
        var toCoord = Loc2Coord(to);
        if (!Board.inBounds(from)) {
            throw new IllegalArgumentException("Invalid starting location: " + from);
        }
        if (!Board.inBounds(to)) {
            throw new IllegalArgumentException("Invalid ending location: " + to);
        }
        
        Piece piece = pieces[fromCoord.getKey()][fromCoord.getValue()];
        if (piece == null) {
            throw new IllegalStateException("No piece at starting location: " + from);
        }
        
        if (!piece.moves(this, from).contains(to)) {
            throw new IllegalArgumentException("Invalid move for the piece");
        }

        Piece toPiece = pieces[toCoord.getKey()][toCoord.getValue()];
        if (toPiece != null) {
            for (var pair: eventHashMap.entrySet()) {
                pair.getValue().onCapture(piece, toPiece);
            }
        }
        for (var pair: eventHashMap.entrySet()) {
            pair.getValue().onMove(from, to, piece);
        }
        
        pieces[fromCoord.getKey()][fromCoord.getValue()] = null;
        pieces[toCoord.getKey()][toCoord.getValue()] = piece;
    }

    public void clear() {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                pieces[i][j] = null;
            }
        }
    }

    public static HashMap<Integer, BoardListener> eventHashMap = new HashMap<>();
    public void registerListener(BoardListener bl) {
        var hash = bl.hashCode();
        eventHashMap.put(hash, bl);
    }

    public void removeListener(BoardListener bl) {
        var hash = bl.hashCode();
        eventHashMap.remove(hash);
    }

    public void removeAllListeners() {
        eventHashMap.clear();
    }

    public void iterate(BoardInternalIterator bi) {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                bi.visit(Coord2Loc(i, j) , pieces[i][j]);
            }
        }
    }
}