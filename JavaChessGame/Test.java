public class Test {

    // Run "java -ea Test" to run with assertions enabled (If you run
    // with assertions disabled, the default, then assert statements
    // will not execute!)

    public static void test1() {
        Board b = Board.theBoard();
        Piece.registerPiece(new PawnFactory());
        Piece p = Piece.createPiece("bp");
        b.addPiece(p, "a3");
        assert b.getPiece("a3") == p;
        System.out.println("OK");
    }

    public static void test14() {
        Board b = Board.theBoard();
        var qf = new QueenFactory();
        var q = qf.create(Color.BLACK);
        // b.addPiece(q, "d4");
        var res = q.moves(b, "d4");
        res.sort(null);
        for (var m : res) System.out.println(m);
    }
    public static void test27() {
        Board b = Board.theBoard();
        var qf = new PawnFactory();
        var q = qf.create(Color.BLACK);
        // b.addPiece(q, "d4");
        var res = q.moves(b, "g7");
        res.sort(null);
        for (var m : res) System.out.println(m);
    }
    
    public static void main(String[] args) {
        test27();
    }

}