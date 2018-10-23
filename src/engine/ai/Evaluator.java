package engine.ai;

import engine.Team;
import engine.board.Board;
import engine.board.Square;
import engine.pieces.Piece;

import static engine.Team.WHITE;

/**
 * This class score's a particular board state.
 * When white and black are perceived to be evenly matched, a score of 0 is returned. When white or black is ahead,
 * the score will increase, respective to who's turn it is.
 */
class Evaluator {
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20_000;
    private static final int CHECKMATE_VALUE = 100_000;

    private static final int[] PAWN_TABLE = {0,  0,  0,  0,  0,  0,  0,  0,
                                            50, 50, 50, 50, 50, 50, 50, 50,
                                            10, 10, 20, 30, 30, 20, 10, 10,
                                             5,  5, 10, 25, 25, 10,  5,  5,
                                             0,  0,  0, 20, 20,  0,  0,  0,
                                             5, -5,-10,  0,  0,-10, -5,  5,
                                             5, 10, 10,-20,-20, 10, 10,  5,
                                             0,  0,  0,  0,  0,  0,  0,  0};

    private static final int[] KNIGHT_TABLE = {-50,-40,-30,-30,-30,-30,-40,-50,
                                               -40,-20,  0,  0,  0,  0,-20,-40,
                                               -30,  0, 10, 15, 15, 10,  0,-30,
                                               -30,  5, 15, 20, 20, 15,  5,-30,
                                               -30,  0, 15, 20, 20, 15,  0,-30,
                                               -30,  5, 10, 15, 15, 10,  5,-30,
                                               -40,-20,  0,  5,  5,  0,-20,-40,
                                               -50,-40,-30,-30,-30,-30,-40,-50};

    private static final int[] BISHOP_TABLE = {-20,-10,-10,-10,-10,-10,-10,-20,
                                               -10,  0,  0,  0,  0,  0,  0,-10,
                                               -10,  0,  5, 10, 10,  5,  0,-10,
                                               -10,  5,  5, 10, 10,  5,  5,-10,
                                               -10,  0, 10, 10, 10, 10,  0,-10,
                                               -10, 10, 10, 10, 10, 10, 10,-10,
                                               -10,  5,  0,  0,  0,  0,  5,-10,
                                               -20,-10,-10,-10,-10,-10,-10,-20};

    private static final int[] ROOK_TABLE = {0,  0,  0,  0,  0,  0,  0,  0,
                                             5, 10, 10, 10, 10, 10, 10,  5,
                                            -5,  0,  0,  0,  0,  0,  0, -5,
                                            -5,  0,  0,  0,  0,  0,  0, -5,
                                            -5,  0,  0,  0,  0,  0,  0, -5,
                                            -5,  0,  0,  0,  0,  0,  0, -5,
                                            -5,  0,  0,  0,  0,  0,  0, -5,
                                             0,  0,  0,  5,  5,  0,  0,  0};

    private static final int[] QUEEN_TABLE = {-20,-10,-10, -5, -5,-10,-10,-20,
                                              -10,  0,  0,  0,  0,  0,  0,-10,
                                              -10,  0,  5,  5,  5,  5,  0,-10,
                                               -5,  0,  5,  5,  5,  5,  0, -5,
                                                0,  0,  5,  5,  5,  5,  0, -5,
                                              -10,  5,  5,  5,  5,  5,  0,-10,
                                              -10,  0,  5,  0,  0,  0,  0,-10,
                                              -20,-10,-10, -5, -5,-10,-10,-20};

    private static final int[] KING_TABLE_EARLY = {-30,-40,-40,-50,-50,-40,-40,-30,
                                                   -30,-40,-40,-50,-50,-40,-40,-30,
                                                   -30,-40,-40,-50,-50,-40,-40,-30,
                                                   -30,-40,-40,-50,-50,-40,-40,-30,
                                                   -20,-30,-30,-40,-40,-30,-30,-20,
                                                   -10,-20,-20,-20,-20,-20,-20,-10,
                                                    20, 20,  0,  0,  0,  0, 20, 20,
                                                    20, 30, 10,  0,  0, 10, 30, 20};


    /**
     * Returns a score relative to the current player's turn. Evaluates using a combination of piece value and piece
     * position.
     * @param board The board state to evaluate.
     * @return The score of the board.
     */
    public static int evaluate(final Board board) {
        int score = 0;
        for (int i = 0; i < 64; i++) {
            final Square square = board.getSquare(i);
            if(!square.isEmpty()) {
                final Piece pieceOnSquare = square.getPiece();
                final int piecePosition = pieceOnSquare.getPiecePosition();
                final Team pieceTeam = pieceOnSquare.getTeam();
                switch(pieceOnSquare.getPieceType()) {
                    case KNIGHT:
                        score += pieceTeam == WHITE ? KNIGHT_VALUE + KNIGHT_TABLE[piecePosition] :
                                                    -(KNIGHT_VALUE + KNIGHT_TABLE[63 - piecePosition]);
                        break;
                    case BISHOP:
                        score += pieceTeam == WHITE ? BISHOP_VALUE + BISHOP_TABLE[piecePosition] :
                                                    -(BISHOP_VALUE + BISHOP_TABLE[63 - piecePosition]);
                        break;
                    case ROOK:
                        score += pieceTeam == WHITE ? ROOK_VALUE + ROOK_TABLE[piecePosition] :
                                                    -(ROOK_VALUE + ROOK_TABLE[63 - piecePosition]);
                        break;
                    case QUEEN:
                        score += pieceTeam == WHITE ? QUEEN_VALUE + QUEEN_TABLE[piecePosition] :
                                                    -(QUEEN_VALUE + QUEEN_TABLE[63 - piecePosition]);
                        break;
                    case KING:
                        score += pieceTeam == WHITE ? KING_VALUE + KING_TABLE_EARLY[piecePosition] :
                                                    -(KING_VALUE + KING_TABLE_EARLY[63 - piecePosition]);
                        default:
                        score += pieceTeam == WHITE ? PAWN_VALUE + PAWN_TABLE[piecePosition] :
                                                    -(PAWN_VALUE + PAWN_TABLE[63 - piecePosition]);
                }
            }
        }

        return board.getTurn() == WHITE ? score : -score;
    }

    /**
     * If the player is in an end game scenario, returns 0 if they stalemated, and -100,000 if they are checkmated
     * @param board The current board
     * @return The score to return
     */
    public static int evaluateEndGame(final Board board) {
        if (board.isKingSafe()) {
            return 0;
        } else {
            return -CHECKMATE_VALUE;
        }
    }
}
