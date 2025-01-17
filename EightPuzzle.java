import java.awt.Color;
import java.awt.Font;
import java.util.*;
// A program that partially implements the 8 puzzle.
public class EightPuzzle {
   // The main method is the entry point where the program starts execution.
   public static void main(String[] args) {
      // StdDraw setup
      // -----------------------------------------------------------------------
      // set the size of the canvas (the drawing area) in pixels
      StdDraw.setCanvasSize(500, 500);
      // set the range of both x and y values for the drawing canvas
      StdDraw.setScale(0.5, 3.5);
      // enable double buffering to animate moving the tiles on the board
      StdDraw.enableDoubleBuffering();

      // create a random board for the 8 puzzle
      Board board = new Board();
      int[][] goalstate = {{1,2,3},{4,5,6},{7,8,0}}; // Initialize the goal state the algorithm will look for.
      int[][] initialState = board.getInitialState(); // Get the random initial state from the board class.
      boolean solvable = AStarAlgorithm.isSolvable(initialState); // Is the algorithm is solvable.
      PuzzleInfo puzzleInfo = AStarAlgorithm.solvePuzzle(initialState, goalstate);
      List<String> moves = puzzleInfo.getMoves();
      
      int moveIndex = 0; // Keep track of the moves.
      
      // The main animation and user interaction loop
      // -----------------------------------------------------------------------
      while (true) {
    	  
    	// Apply moves to the board if there are moves left.
          if (moveIndex < moves.size()) {
              String move = moves.get(moveIndex);
              switch (move) {
                  case "U": board.moveUp(); break; // Empty tile up
                  case "D": board.moveDown(); break; // Empty tile down
                  case "L": board.moveLeft(); break; // Empty tile left
                  case "R": board.moveRight(); break; // Empty tile right
              }
              moveIndex++; // Go to the next move
          }
          
          board.draw();
          StdDraw.show();
          
         // If the puzzle is not solvable, display a message overlay on the board.
          if(!solvable) {
        	  Color transparentRed = new Color(250, 100, 100, 125); // Semi-transparent color for the overlay.
        	  Font font = new Font("Arial", Font.BOLD, 25); // Font for the text.
        	  StdDraw.setPenColor(transparentRed);
              StdDraw.filledRectangle(2, 2, 1.5, 0.5); // Draws the semi-transperent rectangle for the text.

              StdDraw.setPenColor(StdDraw.WHITE);
              StdDraw.setFont(font);
              StdDraw.text(2, 2, "This puzzle is impossible to solve.");
              StdDraw.show();
          }
          
       // Once all moves have been applied and the goal state is reached, display a completion message.
          if(moveIndex == moves.size() && moves.size() != 0) {
        	  Font font = new Font("Arial", Font.BOLD, 15);
        	  StdDraw.setPenColor(StdDraw.WHITE);
        	  StdDraw.setFont(font);
        	  StdDraw.text(2, 1.7, "Goal State Reached in " + moves.size() + " moves. " + puzzleInfo.getHeuristicUsed());
        	  StdDraw.show();
          }
          
          // Pauses the loop for better looking animation.
          StdDraw.pause(400);
      }
   }
}