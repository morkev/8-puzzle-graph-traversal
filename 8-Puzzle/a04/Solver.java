package a04;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Solves a Board and returns the shortest amount 
 * of moves and the boards to get there. To do so,
 * it runs the {@code A* algorithm} on two puzzle instances. 
 * 
 * @author Kevin Mora
 * @author Dawood Ahmed
 */
public class Solver {
	private Stack<Board> solution;
	private MinPQ<Move> priorityQueue;
	private int moves;
	
	/**
	 * Find a solution to the board using the A* algorithm.
	 * Determines whether a Board is solvable that relies on a parity argument.
	 */
	public Solver(Board startingBoard){
		if (!startingBoard.isSolvable()) {
			throw new IllegalArgumentException("Constructor argument cannot be solved.");
		}
		if (startingBoard.equals(null)) {
			throw new NullPointerException("Board provided cannot be null.");
		}
		// Create an instance of both data structures
		solution = new Stack<>();
		priorityQueue = new MinPQ<>();
		
		// Insert the initial search node into a priority queue:
		// (the initial board, 0 moves, and a null previous search node)
		priorityQueue.insert(new Move(startingBoard, 0, null));
		
		while(true){
			Move move = priorityQueue.delMin();
			// Goal has been reached; populate fields.
			if(move.board.isGoal()){ 
				this.moves = move.moves;
				do{
					solution.push(move.board);
					move = move.node;
				} while(move != null);
				return;
			}
			
			for(Board next : move.board.neighbors()){
				// Go back 1 move to avoid looping
				if(move.node == null || !next.equals(move.node.board)) {
					priorityQueue.insert(new Move(next, move.moves + 1, move));
				}
			}
		}
	}
	
	private class Move implements Comparable<Move>{
		private Board board;
		private int moves;
		private Move node;
		
		public Move(Board board, int moves, Move node){
			this.board = board;
			this.moves = moves;
			this.node = node;
		}

		@Override
		public int compareTo(Move node) {
			int difference = this.board.manhattan() + this.moves 
					- node.board.manhattan() - node.moves;
			// Return the normal difference of priority functions
			if (difference != 0) {
				return difference; 
			}
			// If priority is the same, then give preference maximum moves
			if (this.moves > node.moves) {
				return -1; 
			}
			return 1;
		}
	}
	
	/**
	 * Returns the minimum number of moves to solve the initial board.
     	 */
    	public int moves(){
		return moves;
    	}
	
	/**
	 * Returns an Iterable sequence of boards for the shortest solution.
	 */
	public Iterable<Board> solution(){
		return solution;
	}
	
	/**
	 * Client application.
	 * 
	 * Reads a puzzle from a (.txt file), 
	 * printing the solution in standard output.
	 */
	public static void main(String[] args) {
	    // Create initial board from file
	    String fileLocation = "puzzle01.txt";
	    In in = new In("src/puzzles/" + fileLocation);

	    int N = in.readInt();
	    int[][] blocks = new int[N][N];
	    for (int i = 0; i < N; i++)
	    	for (int j = 0; j < N; j++)
	    	    blocks[i][j] = in.readInt();
	    Board initial = new Board(blocks);
		
	    // Check if puzzle is solvable; if so, solve it and output solution
	    if (initial.isSolvable()) {
	    	Stopwatch stopwatch = new Stopwatch();
	        Solver solver = new Solver(initial);
	        StdOut.println("% java Solver " + fileLocation);
	        StdOut.println("Minimum number of moves = " + solver.moves());

	        for (Board board : solver.solution())
	            StdOut.println(board.toString());
	        StdOut.println("Solved in " + stopwatch.elapsedTime() + " seconds.");
	    }

	    // If not, report unsolvable
	    else {
	        StdOut.println("Unsolvable puzzle");
	    }
	}
}
