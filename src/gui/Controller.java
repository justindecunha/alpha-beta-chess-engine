package gui;

import engine.Team;
import engine.ai.AIThinkTank;
import engine.board.Board;
import engine.board.Move;
import engine.board.Square;
import engine.board.Utility;
import engine.pieces.Piece;
import gui.dialogs.InformationDialog;
import gui.dialogs.NewGameDialog;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.Optional;

/**
 * The controller handles actions preformed by the utilization of the gui
 */

public class Controller {
    // Each FXML tag initializes the subsequent object to its corresponding FXML item on the gui
    @FXML
    ProgressBar progressBar;

    @FXML
    ColorPicker lightTileColourPicker;

    @FXML
    ColorPicker darkTileColourPicker;

    @FXML
    Slider depthSlider;

    @FXML
    GridPane gPane; // The grid pane enforces the basic checkerboard pattern

    @FXML
    ToggleGroup pieceStyleGroup;

    private static final int MENU_BAR_HEIGHT = 30;
    private int startPosition; // The starting position used when the user selects a piece
    private Point2D draggedPieceCoordinate; // The position of the selected piece while dragging
    private Board board; // The internal board representation
    private AIThinkTank aiThinkTank; // The AI service, handles threading for negamax algorithm.
    private NewGameDialog newGameDialog; // The new game dialog
    private String pieceStyle;
    /**
     * Initializes a position map used to easily obtain a reference to a piece's graphics
     * object based on its position on the board
     */
    @FXML
    private void initialize() {
        pieceStyle = "standard"; // sets default piece style to standard
        newGameDialog = new NewGameDialog();
        showNewGameDialog();
    }

    /**
     * Initializes a new instance of a game. The board is reset and redrawn, and the ai is set as the player specifies
     * @param playerIsWhite Decide's if AI makes the first move or not
     * @param aiPlayer Decide's if the opponent type is ai or human
     */
    private void initializeGame(final boolean playerIsWhite, final boolean aiPlayer) {

        board = new Board(); // A new internal board representation is initialized
        resetBoard(); // The board is redrawn
        if(aiPlayer) {
            aiThinkTank = new AIThinkTank(board);
            progressBar.setVisible(true);
            progressBar.progressProperty().bind(aiThinkTank.progressProperty());
            aiThinkTank.setOnSucceeded(event -> {
                Move nextMove = aiThinkTank.getValue();

                board.makeMove(nextMove);
                resetBoard();
                if(board.isEndGameScenario())
                    endGameDialog(board.isKingSafe());
            });

            if (!playerIsWhite)
                aiThinkTank.start((int) depthSlider.getValue());
        } else {
            progressBar.setVisible(false);
            aiThinkTank = null;
        }
    }

    /**
     * Displays the new game dialog, and passes the result to the game initialization function if OK is pressed.
     * If cancel is pressed, the program will exit.
     */
    private void showNewGameDialog() {
        Optional<Boolean[]> dialogResponse = newGameDialog.showAndWait();
        if (dialogResponse.isPresent()) {
            Boolean[] result = dialogResponse.get();
            initializeGame(result[0], result[1]);
        } else {
            System.exit(0);
        }
    }

    /**
     * Simply checks if the AI is in the ready state. Used to block user input while AI is thinking.
     * @return Boolean if AI is ready or not.
     */
    private boolean aiReady() {
        return aiThinkTank == null || aiThinkTank.getState() == Worker.State.READY;
    }

    /**
     * Checks if a piece's mid-point is contained within the boards bounds.
     * @param pieceCenter The center-point of the gui piece
     * @return If the piece is inside the bounds or not
     */
    private boolean inBounds(final Point2D pieceCenter) {
        return (int) pieceCenter.getX() >= 0 && pieceCenter.getX() <= 7 &&
                pieceCenter.getY() >= 0 && pieceCenter.getY() <= 7;
    }

    /**
     * Draws the internal board representation on the gui's board.
     */
    private void drawBoard() {
        for (int j = 0; j < Utility.BOARD_SQUARE_COUNT; j++) {
            final Square squareToDraw = board.getSquare(j); // The internal board square we desire to draw on the gui
            if (!squareToDraw.isEmpty()) {
                final Piece pieceOnSquare = squareToDraw.getPiece(); // The piece on the internal board square

                final Piece.PieceType typeOnSquare = pieceOnSquare.getPieceType(); // The type of piece it is
                final String pieceTeam = pieceOnSquare.getTeam() == Team.WHITE ? "W" : "B"; // The team of the piece

                // The corresponding GridPane coordinates to the squares position on the gui-board
                final int row = j / Utility.RANK_SQUARE_COUNT;
                final int column = j % Utility.RANK_SQUARE_COUNT;

                // Generates a piece graphics object
                final Node guiPiece = new ImageView(new Image("gui/pieceimages/" + pieceStyle + "/" + pieceTeam + typeOnSquare + ".png"));
                guiPiece.setOnMousePressed(this::pressed);
                guiPiece.setOnMouseReleased(this::release);
                guiPiece.setOnMouseDragged(this::dragged);
                guiPiece.setOnMouseEntered(this::entered);

                // Draws the new graphics object to the board
                gPane.add(guiPiece, column, row);
            }
        }
    }

    /**
     * Clears the gui board of all chess pieces.
     */
    private void clearBoard() {
        gPane.getChildren().removeIf(child -> child instanceof ImageView);
    }

    /**
     * Resets the drawn board, by clearing all pieces from it and redrawing it.
     */
    private void resetBoard() {
        clearBoard();
        drawBoard();
    }

    /**
     * Highlights the possible movements the selected piece can make. Doesn't take into account advanced calculations,
     * such as if the king will be safe, etc. Extremely useful for debugging, ensuring pieces are only generating moves
     * that should be allowed.
     *
     * @param pos The position of the piece for move highlighting.
     */
    private void highlightMoves(final int pos) {
        final Square startSquare = board.getSquare(pos);

        if (!startSquare.isEmpty()) {
            final Piece pieceOnSquare = startSquare.getPiece();
            final List<Move> moves = pieceOnSquare.generatePossibleMoves(board);

            final InnerShadow innerShadow = new InnerShadow(BlurType.THREE_PASS_BOX, Color.GREEN, 10, 0.7, 0, 0);
            if (moves != null && !moves.isEmpty()) {
                for (final Move move : moves) {
                    final int dest = move.getEndPosition();

                    gPane.getChildren().get(dest).setEffect(innerShadow);
                }
            }
        }
    }

    /**
     * Removes the move highlighting effect
     * @param pos The position of the piece for move highlighting removal.
     */
    private void unhighlightMoves(final int pos) {
        final Square startSquare = board.getSquare(pos);
        if (!startSquare.isEmpty()) {
            final Piece pieceOnSquare = startSquare.getPiece();
            final List<Move> moves = pieceOnSquare.generatePossibleMoves(board);

            for (final Move move : moves) {
                final int dest = move.getEndPosition();
                if (gPane.getChildren().get(dest).getEffect() != null)
                    gPane.getChildren().get(dest).setEffect(null);
            }
        }
    }

    /**
     * Triggered when mouse button is released
     */
    private void release(final MouseEvent t) {
        if(aiReady()) {
            unhighlightMoves(startPosition);

            final Node selectedPiece = ((Node) t.getSource());

            // The calculated center-point of the selected gui piece
            final long xMidPoint = Math.round(selectedPiece.getBoundsInParent().getMinX() +
                    selectedPiece.getBoundsInParent().getMaxX()) / 200;
            final long yMidPoint = Math.round(selectedPiece.getBoundsInParent().getMinY() +
                    selectedPiece.getBoundsInParent().getMaxY() - MENU_BAR_HEIGHT) / 200;
            final Point2D pieceCenter = new Point2D(xMidPoint, yMidPoint);

            // Calculates the square on the chessboard the midpoint of the gui piece is contained within (0 - 63)
            final int releasePosition = (int) pieceCenter.getX() + (int) pieceCenter.getY() * 8;

            // Checks if move was successful, by first checking if mouse was clicked inside the board bounds,
            // then requesting the move on the internal board. Variable holds the result if the move was succesful.
            final boolean moveSuccessful = inBounds(pieceCenter) && board.guiRequestMove(startPosition, releasePosition);
            if (moveSuccessful) {
                gPane.getChildren().remove(selectedPiece); // Removes the piece the player is dragging
                resetBoard();

                // Check for end game scenario
                if(board.isEndGameScenario())
                    endGameDialog(board.isKingSafe());

                if (aiThinkTank != null) // The ai only makes a move if AI was set during game setup
                    aiThinkTank.start((int) depthSlider.getValue());
            } else {
                // If the move failed, reset the piece to its initial position
                selectedPiece.setTranslateX(0.0);
                selectedPiece.setTranslateY(0.0);
            }

            selectedPiece.setCursor(Cursor.HAND);
        }
    }

    /**
     * Triggered when mouse button is pressed down initially
     */
    private void pressed(final MouseEvent t) {
        if(aiReady()) {
            startPosition = ((int) t.getSceneX() / 100) + ((int) (t.getSceneY() - MENU_BAR_HEIGHT) / 100 * 8);

            highlightMoves(startPosition);

            final Node guiPiece = ((Node) t.getSource());
            draggedPieceCoordinate = new Point2D(guiPiece.getTranslateX() - t.getSceneX(),
                    guiPiece.getTranslateY() - t.getSceneY());
            guiPiece.setCursor(Cursor.MOVE);
        }
    }

    /**
     * Any time the cursor clicks and drags the mouse across the screen.
     */
    private void dragged(final MouseEvent t) {
        if(aiReady()) {
            final Node guiPiece = ((Node) t.getSource());
            guiPiece.setTranslateX(t.getSceneX() + draggedPieceCoordinate.getX());
            guiPiece.setTranslateY(t.getSceneY() + draggedPieceCoordinate.getY());
        }
    }

    /**
     * When the cursor 'enters' the space of a chess piece (IE hovers over it).
     */
    private void entered(final MouseEvent t) {
        if(aiReady()) {
            final Node piece = ((Node) t.getSource());
            piece.setCursor(Cursor.HAND);
        }
    }

    /**
     * Displays the end game dialog.
     */
    private void endGameDialog(final boolean kingSafe) {
        String message;
        if(!kingSafe) {
            message = board.getTurn() == Team.WHITE ? "Black Wins!" : "White Wins!";
        } else {
            message = "Stalemate!";
        }
        new InformationDialog("The game is over!", message);
    }

    /**
     * Repaints all the tiles on the chess board according to the colours specified by the user.
     */
    public void setTileColour(Event e) {
        for(Node node : gPane.getChildrenUnmodifiable()) {
            if (node instanceof Rectangle) {
                Color fillColour;
                if((GridPane.getRowIndex(node) + GridPane.getColumnIndex(node)) % 2 == 0) {
                    fillColour = lightTileColourPicker.getValue();
                } else {
                    fillColour = darkTileColourPicker.getValue();
                }
                ((Shape) node).setFill(fillColour);
            }
        }
    }

    public void selectStyle(Event e) {
        MenuItem selectedMenuItem = (MenuItem) pieceStyleGroup.getSelectedToggle();
        pieceStyle = selectedMenuItem.getText().toLowerCase();
        resetBoard();
    }
    /**
     * Undoes a board move and resets the board.
     */
    public void undo(final Event e) {
        if(aiReady() && board.peekLastMove() != null) {
            board.undoMove();
            resetBoard();
        }
    }

    /**
     * Displays the event dialog.
     */
    public void about(final Event e) {
        new InformationDialog("Author", "Created by Justin DeCunha");
    }

    /**
     * JavFX menuItem's method that shows the new game dialog
     */
    public void showNewGameDialog(Event e) { showNewGameDialog(); }

    /**
     * JavaFX menuItem's method that closes the game
     */
    public void close(Event e) { System.exit(0); }
}