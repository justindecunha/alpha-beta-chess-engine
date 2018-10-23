# Alpha Beta Chess Engine

This project showcases an AI implementation for chess, although it can also be used to play chess against your friends. The AI functions using the [alpha beta pruning algorithm](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning) as a baseline, with many further improvements listed below.

## Getting Started
### Requirements

* Windows, Linux, or Mac OS
* Java version 8, 9 or 10 (11 is unsupported)

### Installation
The simplest way to get the program up and running is to double-click the [executable JAR file](https://github.com/justindecunha/alpha-beta-chess-engine/blob/master/out/artifacts/JustinDeCunha_ChessAI_jar/JustinDeCunha_ChessAI.jar), or calling it via command line:

`java -jar JustinDeCunha_ChessAI.jar`

More advanced users can also compile the program from the sources provided.

## Demo

![demo](gifs/demo.gif)

## Features

### User Interface

The GUI was built using JavaFX, and follows a typical MVC design pattern. The GUI interacts with a controller, which then modifies the engine's internal board representation.

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

As a particular board state can be reached through a variety of different move sequences, the minimax algorithm for chess repeatedly reevaluates scores for board states it has already seen. This can be optimized via dynamic programming. A hash is computed for each board state the algorithm evaluates, which is stored in the transposition table, along with the score said board state ended up receiving. This significantly reduces the computational load of the minimax algorithm, as it is no longer forced to recompute values to boards it has already seen.

##### Move Ordering

The alpha-beta enhancement to the minimax algorithm greatly benefits from finding stronger moves earlier in its search. By finding strong moves early, the search will prune unnecessary branches more aggressively, greatly improving search times. For this reason, its worth using some sort of heuristic to



## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgements

The majority of techniques utilized in this chess engine were discovered on the [chess programming wiki](https://chessprogramming.wikispaces.com/).

Piece graphics were taken from the PyChess repository, which can be found [here](https://github.com/pychess/pychess/tree/master/pieces).
