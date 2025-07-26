import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Chess {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java Chess layout moves");
		}
		Piece.registerPiece(new KingFactory());
		Piece.registerPiece(new QueenFactory());
		Piece.registerPiece(new KnightFactory());
		Piece.registerPiece(new BishopFactory());
		Piece.registerPiece(new RookFactory());
		Piece.registerPiece(new PawnFactory());
		Board.theBoard().registerListener(new Logger());
		// args[0] is the layout file name
		// args[1] is the moves file name
		try (BufferedReader layoutReader = new BufferedReader(new FileReader(args[0]));
				BufferedReader movesReader = new BufferedReader(new FileReader(args[1]))) {
			String line;
			while ((line = layoutReader.readLine()) != null) {
				// Process the layout file line by line character by character
				var chararr = line.trim().toCharArray();
				if (chararr.length == 0 || chararr[0] == '#') {
					continue;
				}
				String[] parts = line.split("=");
				if (parts.length != 2 || parts[0].length() != 2 || parts[1].length() != 2) {
					throw new IllegalArgumentException("Invalid line format: " + line);
				}
				String loc = parts[0].trim();
				if (!Board.inBounds(loc)) {
					throw new IllegalArgumentException("Invalid location: " + loc);
				}
				String pieceType = parts[1].trim();
				Piece piece = Piece.createPiece(pieceType);
				Board.theBoard().addPiece(piece, loc);
			}
			while ((line = movesReader.readLine()) != null) {
				var chararr = line.trim().toCharArray();
				if (chararr.length == 0 || chararr[0] == '#') {
					continue;
				}
				String[] parts = line.split("-");
				if (parts.length != 2 || parts[0].length() != 2 || parts[1].length() != 2) {
					throw new IllegalArgumentException("Invalid line format: " + line);
				}
				String from = parts[0].trim();
				String to = parts[1].trim();
				if (!Board.inBounds(from) || !Board.inBounds(to)) {
					throw new IllegalArgumentException("Invalid location: " + from + " or " + to);
				}
				Board.theBoard().movePiece(from, to);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Leave the following code at the end of the simulation:
		System.out.println("Final board:");
		Board.theBoard().iterate(new BoardPrinter());
	}
}