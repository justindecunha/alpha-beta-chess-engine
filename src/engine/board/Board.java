package engine.board;

import engine.Team;
import engine.pieces.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A chess board represented as a list of 64 engine.squares
 */
public class Board {

    private Zobrist zobrist;
    private Team turn; // Who's turn it is currently
    private final List<Square> gameBoard; // Internal game board represented as a list of squares
    private final Stack<Move> moveHistory; // A history of previous moves. Used for undoing board moves and enpassent moves.
    private final Piece whiteKing;
    private final Piece blackKing;
    private long hashCode;

    /**
     * The default constructor creates a standard chess board setup
     */
    public Board() {
        final List<Square> standardBoard = new ArrayList<>();

        // Black's pieces
        standardBoard.add(new Square(new Rook(Team.BLACK, 0, true)));
        standardBoard.add(new Square(new Knight(Team.BLACK, 1, true)));
        standardBoard.add(new Square(new Bishop(Team.BLACK, 2, true)));
        standardBoard.add(new Square(new Queen(Team.BLACK, 3, true)));
        standardBoard.add(new Square(new King(Team.BLACK, 4, true)));
        standardBoard.add(new Square(new Bishop(Team.BLACK, 5, true)));
        standardBoard.add(new Square(new Knight(Team.BLACK, 6, true)));
        standardBoard.add(new Square(new Rook(Team.BLACK, 7, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 8, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 9, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 10, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 11, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 12, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 13, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 14, true)));
        standardBoard.add(new Square(new Pawn(Team.BLACK, 15, true)));

        // Fills in the 32 blank squares between white and black's opposing armies
        for (int i = 16; i < 48; i++)
            standardBoard.add(new Square(null));

        // White's pieces
        standardBoard.add(new Square(new Pawn(Team.WHITE, 48, true)));
        standardBoard.add(new Square(new Pawn(Team.WHITE, 49, true)));
        standardBoard.add(new Square(new Pawn(Team.WHITE, 50, true)));
        standardBoard.add(new Square(new Pawn(Team.WHITE, 51, true)));
        standardBoard.add(new Square(new Pawn(Team.WHITE, 52, true)));
        standardBoard.add(new Square(new Pawn(Team.WHITE, 53, true)));
        standardBoard.add(new Square(new Pawn(Team.WHITE, 54, true)));
        standardBoard.add(new Square(new Pawn(Team.WHITE, 55, true)));
        standardBoard.add(new Square(new Rook(Team.WHITE, 56, true)));
        standardBoard.add(new Square(new Knight(Team.WHITE, 57, true)));
        standardBoard.add(new Square(new Bishop(Team.WHITE, 58, true)));
        standardBoard.add(new Square(new Queen(Team.WHITE, 59, true)));
        standardBoard.add(new Square(new King(Team.WHITE, 60, true)));
        standardBoard.add(new Square(new Bishop(Team.WHITE, 61, true)));
        standardBoard.add(new Square(new Knight(Team.WHITE, 62, true)));
        standardBoard.add(new Square(new Rook(Team.WHITE, 63, true)));

        this.turn = Team.WHITE;
        this.moveHistory = new Stack<>();
        this.gameBoard = standardBoard;
        this.whiteKing = this.getSquare(Utility.WHITE_KING_START_POSITION).getPiece();
        this.blackKing = this.getSquare(Utility.BLACK_KING_START_POSITION).getPiece();
        zobrist = Zobrist.getInstance();
        hashCode = zobrist.getFullHash(this);
    }

    /**
     * Getter method for a square on the board
     *
     * @param position The position of the square on the board to return. Top down, left to right, 0 - 63.
     * @return A reference to the square at the specified position
     */
    public Square getSquare(final int position) {
        return gameBoard.get(position);
    }


    /**
     * Since the gui has no concept of 'moves' and operates solely on X/Y grid coordinates,
     * the move the user is requesting needs to be checked against the list of
     * If the requested X/Y coordinates corresponds with that of a move in the list, the move is passed to
     * the makeMove method to be executed on the board.
     *
     * This has the added benefit of ensuring the user cannot make any illegal moves.
     *
     * @param startPosition       The start position.
     * @param destinationPosition The destination position.
     * @return If the move was successful or not.
     */
    public boolean guiRequestMove(final int startPosition, final int destinationPosition) {
        final List<Move> legalMoves = getLegalMoves(getTurn());
        for (final Move move : legalMoves)
            if (move.getPiece().getPiecePosition() == startPosition && move.getEndPosition() == destinationPosition) {
                makeMove(move);
                return true;
            }
        return false;
    }

    /**
     * A helper method that moves a piece from one square to another
     * @param startPosition The start position as an integer from 0 - 63.
     * @param endPosition The end position as an integer from 0 - 63.
     * @param isFirstMove Whether its the piece's first move or not.
     */
    private void movePiece(final int startPosition, final int endPosition, final boolean isFirstMove) {
        final Square startSquare = gameBoard.get(startPosition);
        final Square endSquare = gameBoard.get(endPosition);
        final Piece pieceToMove;
        if (!startSquare.isEmpty()) {
            pieceToMove = startSquare.getPiece();
            pieceToMove.setIsFirstMove(isFirstMove);
            pieceToMove.setPiecePosition(endPosition); // Sets internal piece position to the end square's position
        } else {
            pieceToMove = null;
        }

        startSquare.setPiece(null); // Empties start square
        endSquare.setPiece(pieceToMove); // Places piece at end square
    }

    /**
     * Preforms the specified move on the board.
     * @param move The move to make on the board.
     */
    public void makeMove(final Move move) {
        toggleTurn();
        movePiece(move.getStartPosition(), move.getEndPosition(), false);

        switch(move.getType()) {
            case ENPASSENT: // Special case due to the attacked piece not being in its usual position
                final int offset = getTurn() == Team.WHITE ? -8 : 8;
                final int attackedPawnPosition = move.getEndPosition() + offset;
                this.getSquare(attackedPawnPosition).setPiece(null);
                break;
            case SHORT_CASTLE: // Handles rook movement for the short castle
                int rookStartPosition = move.getPiece().getPiecePosition() + 1;
                int rookEndPosition = move.getPiece().getPiecePosition() - 1;
                movePiece(rookStartPosition, rookEndPosition, false);
                break;
            case LONG_CASTLE: // Handles rook movement for the long castle
                rookStartPosition = move.getPiece().getPiecePosition() - 2;
                rookEndPosition = move.getPiece().getPiecePosition() + 1;
                movePiece(rookStartPosition, rookEndPosition, false);
                break;
            case PAWN: // Handles pawn promotion
            case PAWN_ATTACK:
                if(Utility.isBackRank(this.getSquare(move.getEndPosition()).getPiece())) {
                    final Piece promotion = new Queen(move.getPiece().getTeam(), move.getEndPosition(), false);
                    this.getSquare(move.getPiece().getPiecePosition()).setPiece(promotion);
                }
        }

        moveHistory.push(move);
        hashCode = zobrist.updateHash(hashCode, move, this);
    }

    /**
     * Pops the last move from the moveHistory stack, and undoes it on the board.
     */
    public void undoMove() {
        final Move move = moveHistory.pop();

        switch (move.getType()) {
            case ENPASSENT: // Handles adding the attackedPawn on a different square then destinationPosition
                final int offset = getTurn() == Team.WHITE ? -8 : 8;
                final Piece attackedPawn = new Pawn(getTurn(), move.getEndPosition() + offset, false);
                final int attackedPawnPosition = attackedPawn.getPiecePosition();
                this.getSquare(attackedPawnPosition).setPiece(attackedPawn);
                break;
            case SHORT_CASTLE: // Handles returning the rook from the short castle
                int rookStartPosition = move.getPiece().getPiecePosition() + 1;
                int rookEndPosition =  move.getPiece().getPiecePosition() - 1;
                movePiece(rookEndPosition, rookStartPosition, true);
                break;
            case LONG_CASTLE: // Handles returning the rook from the long castle
                rookStartPosition = move.getPiece().getPiecePosition() - 2;
                rookEndPosition = move.getPiece().getPiecePosition() + 1;
                movePiece(rookEndPosition, rookStartPosition, true);
                break;
            case PAWN: // Handles replacing the promoted piece with a pawn
            case PAWN_ATTACK:
                if(Utility.isBackRank(this.getSquare(move.getEndPosition()).getPiece())) {
                    this.getSquare(move.getPiece().getPiecePosition()).setPiece(move.getPiece());
                }
        }

        movePiece(move.getEndPosition(), move.getStartPosition(), move.isFirstMove()); // Moves the original piece back
        gameBoard.get(move.getEndPosition()).setPiece(move.getAttackedPiece()); // Puts attacked piece back in original position
        toggleTurn();
        hashCode = zobrist.updateHash(hashCode, move, this);
    }

    public boolean isKingSafe() {
        Piece king = getTurn() == Team.WHITE ? whiteKing : blackKing;
        return isSquareSafe(king.getPiecePosition(), getTurn() == Team.WHITE ? Team.BLACK : Team.WHITE);
    }

    public boolean isEndGameScenario() {
        return getLegalMoves(getTurn()).isEmpty();
    }

    /**
     * Toggles the turn from white to black, or black to white
     */
    private void toggleTurn() {
        turn = turn == Team.WHITE ? Team.BLACK : Team.WHITE;
    }

    /**
     * Returns the current turn
     * @return The current turn
     */
    public Team getTurn() {
        return turn;
    }

    /**
     * Peeks last move from the moveHistory stack
     * @return lastMove of the moveHistory stack, without removing it
     */
    public Move peekLastMove() {
        return moveHistory.isEmpty() ? null : moveHistory.peek();
    }

    /**
     * Gets all the possible moves that can be made on this particular board, respective to who's turn it is.
     *
     * @param team The team to get the moves for.
     * @return The list of possible moves.
     */
    private List<Move> getAllPossibleMoves(final Team team) {
        final List<Move> moveList = new ArrayList<>();

        for (final Square square : gameBoard)
            if (!square.isEmpty() && square.getPiece().getTeam() == team)
                moveList.addAll(square.getPiece().generatePossibleMoves(this));

        return moveList;
    }

    /**
     * Checks to see if the specified square is being attacked by any of the opposing teams pieces.
     *
     * @param position The square's position to preform the check on
     * @return If the square is safe or not
     */
    private boolean isSquareSafe(final int position, final Team team) {
        for (final Move move : getAllPossibleMoves(team))
            if (move.getEndPosition() == position)
                return false;

        return true;
    }

    /**
     * Checks each of the moves in the getAllPossibleMoves list for king safety. If the king is put in check, its not a
     * valid move.
     * @param team The team to check legal moves for.
     * @return A list of possible moves, that don't result in the king being put in check.
     */
    public List<Move> getLegalMoves(final Team team) {
        final List<Move> feasibleMoves = new ArrayList<>();

        final Piece king = (team == Team.WHITE) ? this.whiteKing : this.blackKing;

        for (final Move possibleMove : this.getAllPossibleMoves(team)) {
            this.makeMove(possibleMove);
            if (this.isSquareSafe(king.getPiecePosition(), getTurn())) feasibleMoves.add(possibleMove);
            undoMove();
        }

        // Castle Moves
        if (this.canShortCastle(team)) {
            feasibleMoves.add(new Move(king, king.getPiecePosition() + 2, Move.moveType.SHORT_CASTLE));
        }
        if (this.canLongCastle(team)) {
            feasibleMoves.add(new Move(king, king.getPiecePosition() - 2, Move.moveType.LONG_CASTLE));
        }

        return feasibleMoves;
    }


    /**
     * Checks if the specified team has the ability to king side castle. It accomplishes this by the following procedure:
     * 1. Checks if the king is in its start position, and has yet to move.
     * 2. Checks if the king-side rook is in its start position, and has yet to move.
     * 3. Checks if the two squares between the king and the rook are empty.
     * 4. Checks if the three squares the king traverses are 'safe' (have no opposing pieces attacking them)
     *
     * @param team The team to preform the check for.
     * @return If the four conditions listed above are all true, returns true. Otherwise, false.
     */
    boolean canShortCastle(final Team team) {
        final int kingStartPosition = (team == Team.WHITE) ? Utility.WHITE_KING_START_POSITION : Utility.BLACK_KING_START_POSITION;
        final int rookStartPosition = kingStartPosition + 3;

        if (getSquare(kingStartPosition).isEmpty() || getSquare(rookStartPosition).isEmpty()) return false;
        final Piece shouldBeKing = getSquare(kingStartPosition).getPiece();
        final Piece shouldBeRook = getSquare(rookStartPosition).getPiece();

        if (shouldBeKing instanceof King &&
                shouldBeKing.isFirstMove() &&
                shouldBeRook instanceof Rook &&
                shouldBeRook.isFirstMove() &&
                getSquare(kingStartPosition + 1).isEmpty() &&
                getSquare(kingStartPosition + 2).isEmpty()) {
            for (int i = 0; i < 3; i++)
                if (!isSquareSafe(kingStartPosition + i, (getTurn() == Team.WHITE) ? Team.BLACK : Team.WHITE))
                    return false;

            return true;
        }
        return false;
    }

    /**
     * Checks if the specified team has the ability to queen side castle. It accomplishes this by the following procedure:
     * 1. Checks if the king is in its start position, and has yet to move.
     * 2. Checks if the queen-side rook is in its start position, and has yet to move.
     * 3. Checks if the three squares between the king and the rook are empty.
     * 4. Checks if the three squares the king traverses are 'safe' (have no opposing pieces attacking them)
     *
     * @param team The team to preform the check for.
     * @return If the four conditions listed above are all true, returns true. Otherwise, false.
     */
    boolean canLongCastle(final Team team) {
        final int kingStartPosition = (team == Team.WHITE) ? Utility.WHITE_KING_START_POSITION : Utility.BLACK_KING_START_POSITION;
        final int rookStartPosition = kingStartPosition - 4;

        if (getSquare(kingStartPosition).isEmpty() || getSquare(rookStartPosition).isEmpty()) return false;
        final Piece shouldBeKing = getSquare(kingStartPosition).getPiece();
        final Piece shouldBeRook = getSquare(rookStartPosition).getPiece();

        if (shouldBeKing instanceof King &&
                shouldBeKing.isFirstMove() &&
                shouldBeRook instanceof Rook &&
                shouldBeRook.isFirstMove() &&
                getSquare(kingStartPosition - 1).isEmpty() &&
                getSquare(kingStartPosition - 2).isEmpty() &&
                getSquare(kingStartPosition - 3).isEmpty()) {
            for (int i = 0; i < 3; i++)
                if (!isSquareSafe(kingStartPosition - i, (getTurn() == Team.WHITE) ? Team.BLACK : Team.WHITE))
                    return false;

            return true;
        }
        return false;
    }

    /**
     * Helper method for the toString method, converts black's pieces to lower case for differentiation.
     *
     * @param square The square to convert to ascii.
     * @return The ascii representation of said square.
     */
    private static String prettyPrint(final Square square) {
        if (!square.isEmpty()) {
            return square.getPiece().getTeam() == Team.BLACK ?
                    square.toString().toLowerCase() : square.toString();
        }
        return square.toString();
    }

    /**
     * Prints board in a human readable format
     * @return The human readable ascii string
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Utility.BOARD_SQUARE_COUNT; i++) {
            final String tileText = prettyPrint(this.gameBoard.get(i));
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % Utility.RANK_SQUARE_COUNT == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public long getZobristHash() {
        return hashCode;
    }
}