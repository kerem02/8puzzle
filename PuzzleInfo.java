import java.util.*;

// A new class to store the information of the moves and which heuristic is used
public class PuzzleInfo {
	private List<String> moves;
	private String heuristicUsed;
	
	public PuzzleInfo(List<String> moves, String heuristicUsed) {
		this.moves = moves;
		this.heuristicUsed = heuristicUsed;
	}

	public List<String> getMoves() {
		return moves;
	}

	public String getHeuristicUsed() {
		return heuristicUsed;
	}
}
