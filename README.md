# Chess

This project showcases an AI implementation for chess. The AI functions using a minimax algorithm as a baseline, with many further improvements added over time to improve its efficiency.

## Usage

#### Basic Controls

Upon launch, the game will request the user for a colour and an opponent type:

![new_game_dialog](../../Downloads/alpha-beta-chess-engine-master/gifs/new_game_dialog.gif)

This new game dialog can subsequently be accessed through the "File" menu item should the user desire to restart the match.



#### Preferences

Preferences for the user interface can be changed through the 'preferences' menu. Options such as piece style and tile colour can be altered according to the users taste:

![preferences_menu](../../Downloads/alpha-beta-chess-engine-master/gifs/preferences_menu.gif)



#### Options

Game/engine settings can be tweaked through the 'options' menu. Users can alter the game tree search depth, and number of threads utilized to optimize the engine for their hardware set up. It is recommended to use number of threads equal to the number of cores on the computer. Search depth can be altered to change the engine's difficulty/time taken to generate a move. The options menu also houses an undo button, in case a mistake is made:

![options_menu](../../Downloads/alpha-beta-chess-engine-master/gifs/options_menu.gif)

## Under the Hood

### User Interface

The GUI was built using JavaFX, and follows a typical MVC design pattern. The GUI interacts with a controller, which then modifies the engine's internal board represetation. Piece graphics were taken from the PyChess repository, which can be found [here](https://github.com/pychess/pychess/tree/master/pieces).

### Board Representation

The board is internally represented as a single length 64 array of piece squares. In the future, this will be changed to a magic bit-board representation in order to improve the engine's efficiency.

### Board Evaluation

The engine assigns a score to a particular board state using two simple heuristics:

##### Piece Score

Each piece is worth a pre-defined value:

Pawn: 100

Knight: 320

Bishop: 330

Rook: 500

Queen: 900

King: 20,000

##### Position Score

A positional score is assigned to each piece. In general, pawns are encouraged to advance forward, knights & bishops closer to the center are better, and the king should be tucked away in a corner early game, and closer to the center late game. The actual values used can be found in engine.ai.Evalulator.

### AI

The heart of the AI is a tweaked minimax algorithm with alpha-beta pruning. Improvements have been incrementally been added over time to improve the efficiency.

##### Transposition Table

As a particular board state can be reached through a variety of different move sequences, the minimax algorithm for chess repeatedly reevaluates scores for board states it has already seen. This can be optimized via dynammic programming. A hash is computed for each board state the algorithm evalulates, which is stored in the transposition table, along with the score said board state ended up recieving. This significantly reduces the computational load of the minimax algorithm, as it is no longer forced to recompute values to boards it has already seen.

##### Multi-Threading

As the majority of modern computers come with mutiple cores, these can be harnessed to parallelize the search algorithm. As the algorithm has already been tweaked to store results of states it has already evaluated, effective multi-threading can be achieved simply by starting the exact same search twice on two separate threads. When one thread encounters a node the other thread has already solved, its result will be conviniently stored in the transposition table for reuse.

##### Move Ordering

The alpha-beta enhancement to the minimax algorithm greatly benefits from finding stronger moves earlier in its search. By finding strong moves early, the search will prune unnessicary branches more aggressively, greatly improving search times. For this reason, its worth using some sort of heuristic to 

## References

The majority of techniques utilized in this chess engine were discovered on the [chess programming wiki](https://chessprogramming.wikispaces.com/).