# BFS_Puzzle
![alt tag](https://raw.githubusercontent.com/mpvoss/BFS_Puzzle/master/img/puzzle.png)


The goal of this puzzle is to move the red piece (0) to a position adjacent to the top. That is, move it to where the orange piece (10) is currently located. Black pieces cannot be moved. The pieces cannot overlap. The pieces cannot be rotated.  

My implementation used bitboards to handle all the movement of the pieces.  This code solves the puzzle in under 4 seconds, whereas other implementations using more OO approaches or representing the board in char arrays took several minutes.  The bitboard code is quite ugly and was just a proof of concept since this puzzle is easier to code up for bitboards compared to chess.

Credit: This puzzle a homework assignment from Dr. Mike Gashler's class at the University of Arkansas.
