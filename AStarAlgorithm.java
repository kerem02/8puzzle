import java.util.*;

//A program that implements the A* algorithm to solve the 8 puzzle problem.
public class AStarAlgorithm {
	
	//Defines two types of heuristic functions that can be used
	enum HeuristicType {
		MANHATTAN,
		MISPLACED_TILES
	}
	
	// Solve the puzzle with both of the heuristics and returns the one with the shortest path
	public static PuzzleInfo solvePuzzle(int[][] initialstate, int[][] goalstate) {
	    List<String> manhattanPath = AStar(initialstate, goalstate, HeuristicType.MANHATTAN);
	    System.out.println("Manhattan : " + manhattanPath.size());
	    List<String> misplacedTilesPath = AStar(initialstate, goalstate, HeuristicType.MISPLACED_TILES);
	    System.out.println("MisplacedTiles : " + misplacedTilesPath.size());

	    if (manhattanPath.size() < misplacedTilesPath.size()) {
	        return new PuzzleInfo(manhattanPath,"(Manhattan)");
	    }
	    else if(manhattanPath.size() > misplacedTilesPath.size()) {
	    	return new PuzzleInfo(misplacedTilesPath,"(Misplaced Tiles)");
	    } else {
	    	return new PuzzleInfo(manhattanPath,"(Manhattan = Misplaced Tiles)");
	    }
	}
	
	// The A* search algorithm that finds the shortest path from the initial state to the goal state.
	public static List<String> AStar(int[][] initialstate,int[][] goalstate,HeuristicType heuristicType) {
		
		// Check if the puzzle is solvable if not, print a message and return an empty list.
		if(!isSolvable(initialstate)) {
			System.out.println("This puzzle is impossible to solve.");
			return new ArrayList<>();
		}
		// The set of nodes that have been discovered
		PriorityQueue<Node> openSet = new PriorityQueue<>();
		
		// The set of nodes already seen.
		HashSet<String> closedSet = new HashSet<>();
		
		// Checks the type of the heuristic
		int heuristic;
		if(heuristicType == HeuristicType.MANHATTAN) {
			heuristic = ManhattanDistance(initialstate,goalstate);
		} else {
			heuristic = misplacedTiles(initialstate,goalstate);
		}
		
		// The starting node.
		Node first = new Node(initialstate,heuristic,0,null,"",heuristicType);
		openSet.add(first);
		
		
		while(!openSet.isEmpty()) {
			Node current =openSet.poll(); // The current node with the lowest cost in the open set.
			
			// If the current state is the goal state, reconstruct the path and return it.
			if(Arrays.deepEquals(current.state, goalstate)) {
				LinkedList<String> moves = new LinkedList<>();
				Node pathNode = current;
				while(pathNode.parent != null) {
					moves.addFirst(pathNode.move);
					pathNode = pathNode.parent;
				}
				return moves;
			}
			
			 // Mark the current state as evaluated.
			closedSet.add(Arrays.deepToString(current.state));
			
			// Generate the possible successor states.
			List<Node> possibleStates = createStates(current,goalstate,heuristicType);
			
			// For each neighbor of the current state if a better path exists, update the current path.
			for(Node possibleState : possibleStates) {
				String stateStr = Arrays.deepToString(possibleState.state);
				if(!closedSet.contains(stateStr)) {
					boolean shouldAdd = true;
					Node existing = findNodeInOpenSet(openSet,possibleState.state);
					if(existing != null) {
						if(existing.cost + existing.heuristic <= possibleState.cost + possibleState.heuristic) {
							shouldAdd = false;
						} else {
							openSet.remove(existing);
						}
					}
					if(shouldAdd) {
						openSet.add(possibleState);
					}
				}
			}
		}
		// If the goal state is not reached, return an empty list.
		return new ArrayList<>();
	}
	
	// Represents a state in the puzzle including its heuristic cost, actual cost, and the move that led to it.
	static class Node implements Comparable<Node>{
		int[][] state;
		int heuristic;
		int cost;
		Node parent;
		String move;
		HeuristicType heuristicType;
		
		// Node constructor.
		Node(int[][] state,int heuristic,int cost,Node parent,String move,HeuristicType heuristicType){
			this.state = state;// The puzzle state
			this.heuristic = heuristic;// Heuristic cost to reach the goal from this state
			this.cost = cost;// Cost from the start to this state
			this.parent = parent;// Parent node in the path
			this.move = move;// Move made to reach this state
			this.heuristicType = heuristicType;
		}
		@Override
		public int compareTo(Node other) {
			// Comparison method for priority queue; nodes with lower cost are prioritized.
			return(this.cost + this.heuristic) - (other.cost + other.heuristic);
		}
	}
	
	// Checks if the puzzle is solvable. If the number of inversions are even then it is solvable, otherwise not.
	static boolean isSolvable(int[][]arr) {
		if(NumberofInversion(arr) % 2 == 0)
			return true;
		else
			return false;
	}
	
	// Creates all possible states from a given state.
	static List<Node> createStates(Node parent,int[][] goalstate,HeuristicType heuristicType){
		List<Node> possibleStates = new ArrayList<>();
		int[][] directions = {{-1,0},{1,0,},{0,-1,},{0,1,}}; // 2D representation of moves
		String[] moves = {"U","D","L","R"}; // moves
		int[] posBlank = findPos(0,parent.state); // Position of blank "0" tile.
		
		for(int i = 0; i < directions.length; i++) {
			int newRow = posBlank[0] + directions[i][0];
			int newCol = posBlank[1] + directions[i][1];
			
			if(newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) { // Checks if the move is legal.
				int[][] newState = new int[3][3];
				for(int j = 0; j < 3; j++) {
					System.arraycopy(parent.state[j], 0, newState[j], 0, 3); // Copy the state
				}
				
				// Swap the blank tile location.
				newState[posBlank[0]][posBlank[1]] = newState[newRow][newCol];
				newState[newRow][newCol] = 0;
				
				//Calculates the new cost and heuristic cost.
				int newCost = parent.cost + 1;
				int newHeuristic;
				if(heuristicType == HeuristicType.MANHATTAN) {
					newHeuristic = ManhattanDistance(newState,goalstate);
				} else {
					newHeuristic = misplacedTiles(newState,goalstate);
				}
				
				// Create a new node for this state with the new variables and add this to the list.
				Node newNode = new Node(newState,newHeuristic,newCost,parent,moves[i],heuristicType);
				possibleStates.add(newNode);
			}
		}
		return possibleStates;
	}
	
	// Finds a node with a specific state in the open set.
	private static Node findNodeInOpenSet(PriorityQueue<Node> openSet, int[][] state) {
	    for (Node node : openSet) {
	        if (Arrays.deepEquals(node.state, state)) {
	            return node;
	        }
	    }
	    return null; // return null if no matching node is found
	}
	
	// Calculates the number of inversions.
	static int NumberofInversion(int[][] arr) {
		int inversion = 0;
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				if(arr[row][col] != 0) {
					int tempcol = col;
					for(int temprow = row; temprow < 3; temprow++) {
						for(;tempcol < 3; tempcol++) {
							if(arr[temprow][tempcol] != 0 && arr[row][col] > arr[temprow][tempcol]) {
								inversion++;
							}
						}
						tempcol = 0; // Reset column index for the next row.
					}
				}
			}
		}
		return inversion; //Return the total number of inversions.
	}
	

	// Calculates the number of misplaced tiles.
	static int misplacedTiles(int[][] current, int[][] goal) {
	    int misplaced = 0;
	    for (int i = 0; i < current.length; i++) {
	        for (int j = 0; j < current[i].length; j++) {
	            if (current[i][j] != 0 && current[i][j] != goal[i][j]) {
	                misplaced++;
	            }
	        }
	    }
	    return misplaced;
	}
	
	//Calculates the Manhattan distance between the current state and goal state.
	static int ManhattanDistance(int[][] current,int[][] goal) {
		int result = 0;
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				if(current[row][col] != 0) {
				int[] arr = findPos(current[row][col],goal); // Find the position of the tile in the goal state
				// Add the distance from the current position to the goal position.
				result += Math.abs((row) - arr[0]) + Math.abs((col) - arr[1]);
				}
				
			}
		}
		return result; //Returns Manhattan distance.
	}
	
	// Finds the position (row and column) of a specific number.
	static int[] findPos(int number,int[][] array) {
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				if(number == array[row][col]) {
					return new int[] {row,col}; //Return the position as an array.
				}
			}
		}
		return null; // Return null if the number is not found.
	}
}
