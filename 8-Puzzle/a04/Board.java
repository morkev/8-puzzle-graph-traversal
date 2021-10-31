package a04;

import edu.princeton.cs.algs4.Stack;

/**
 * Rearranges the blocks so that they are in order, using as few moves as possible. 
 * 
 * We define a search node of the game to be a board, the number 
 * of moves made to reach the board, and the previous search node. 
 * Repeat this procedure until the search node dequeued corresponds 
 * to a goal board. The success of this approach hinges on the 
 * choice of priority function for a search node. 
 * 
 * NOTE: You can slide blocks horizontally or vertically into the blank(0) square. 
 * 
 * @author Kevin Mora
 * @author Dawood Ahmed
 */
public class Board {
	private int N;
	private int hamming;
	private int manhattan;
	private int[] searchNode;
	private int initialValue;
	
   /**
    * Construct a board from an N-by-N array of blocks
    * (where blocks[i][j] = block in row i, column j).
    */
    public Board(int[][] blocks) {
    	if(blocks == null) {
    		throw new NullPointerException();
    	}
    	N = blocks.length;
    	searchNode = new int[N * N];
    	
    	int tile = 0;
	for (int i = 0; i < N; i++) {
	    for (int j = 0; j < N; j++) {
		if (blocks[i][j] == 0) initialValue = tile;
		searchNode[tile++] = blocks[i][j];
	    }
	}
    }
    
    /**
     * Creates a copy of the Object's immutable elements.
     */
    private Board(int[] block, int N, int initialValue) {
    	this.N = N;
    	this.initialValue = initialValue;
    	searchNode = new int[N * N];
    	System.arraycopy(block, 0, searchNode, 0, searchNode.length);
    }
               
    /**
     * Returns the board size N.
     */
    public int size() {
    	return N;
    }
    
    /**
     * Number of blocks out of place.
     * 
     * Evaluates the number of blocks in the wrong position, 
     * plus the number of moves made so far to get to the search node. 
     * 
     * Intuitively, a search node with a small number of blocks in 
     * the wrong position is close to the goal, and we prefer a search 
     * node that have been reached using a small number of moves.
     */
    public int hamming() {
    	if(hamming > 0) {
    		return hamming;
    	}
    	hamming = 0;
    	for(int i = 0; i < searchNode.length; i++){
    		if(searchNode[i] != (i+1) && searchNode[i] != 0){
			hamming++;
		}
    	}
    	return hamming;
    }
    
    /**
     * Sum of Manhattan distances between blocks and goal.
     * 
     * Evaluates the sum of the Manhattan distances:
     * (sum of the vertical and horizontal distance) from the blocks to their goal 
     * positions, plus the number of moves made so far to get to the search node.
     * 
     * e.g.:
     * The Hamming and Manhattan priorities of the initial search node below are 5 and 10:
     * 
     *  8  1  3        1  2  3     1  2  3  4  5  6  7  8    1  2  3  4  5  6  7  8
     *  4     2        4  5  6     ----------------------    ----------------------
     *  7  6  5        7  8        1  1  0  0  1  1  0  1    1  2  0  0  2  2  0  3
     *  
     *  initial          goal         Hamming = 5 + 0          Manhattan = 10 + 0
     */
    public int manhattan() {
    	if(manhattan > 0) {
    		return manhattan;
    	}
    	manhattan = 0;
    	for(int i = 0; i < searchNode.length; i++){
    		if(searchNode[i] == (i + 1) || i == initialValue) {
    			continue;
    		}
    	manhattan += Math.abs((i / N) - ((searchNode[i] - 1)) / N);
    	manhattan += Math.abs((i % N) - ((searchNode[i] - 1)) % N);
    	}
    	return manhattan;
    }
    
    /**
     * Is this board the goal board?
     * 
     * The root is the initial search node; the internal nodes have already been processed; 
     * the leaf nodes are maintained in a priority queue; at each step, the A* algorithm 
     * removes the node with the smallest priority from the priority queue and processes it 
     * (by adding its children to both the tree and the priority queue).
     */
    public boolean isGoal() {
    	if(searchNode.length - 1 != initialValue) {
    		return false;
    	}
    	for(int i = 0; i < searchNode.length - 1; i++){
    		if(searchNode[i]!= (i + 1)){
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Pushes an item onto the top of this stack. 
     * 
     * This has exactly the same effect as:
     * addElement(item).
     * 
     * @return the item argument
     */
    private void pushToStack(Stack<Board> board, int x) {
    	exchange(searchNode, initialValue, initialValue + x);
    	board.push(new Board(searchNode, N, initialValue + x));
    	exchange(searchNode, initialValue, initialValue + x);
    }
    
    /**
     * Exchanges specific elements in defined positions without 
     * impacting other elements in the data structure. 
     * 
     * The exchange() will give an output of the data structure 
     * with the elements in the indexes swapped.
     */
    private void exchange(int[] board, int x, int y) {
    	int exch = board[x];
    	board[x] = board[y];
    	board[y] = exch;
    }
    
    /**
     * Is this board solvable?
     * Given a board, an inversion is any pair of blocks x and y, where x < y 
     * but x appears after y when considering the board in row-major order.
     * 
     * i.e.: (row 0, followed by row 1, and so forth).
     */
    public boolean isSolvable() {
	int sum = inversions();
	boolean checkEven = size() % 2 == 0;
	if (checkEven) {
		sum += blankRow();
	}
	boolean checkInversions = (sum % 2) == 0;
	return checkEven != checkInversions;
    }
    
    /**
     * Locates and returns the row that the blank square is on.
     */
    private int blankRow() {
	for (int i = 0; i < searchNode.length - 1; i++) {
		int row = (searchNode[i] - 1) / size() - (i / size());
		if (searchNode[i] == 0) {
		return row;
		}
	}
	return -1;
    }
    
    /**
     * Counts and returns the number of inversions.
     */
    private int inversions() {
	int count = 0;
	for (int i = 0; i < searchNode.length; i++) {
		for (int j = i; j < searchNode.length; j++) {
			if (searchNode[j] < searchNode[i] && searchNode[j] != 0) {
			count++;
			}
		}
	}
	return count;
     }

    
    /**
     * All neighboring boards.
     * 
     * Condition 1: up neighbor.
     * Condition 2: down neighbor.
     * Condition 3: left neighbor.
     * Condition 4: right neighbor.
     * 
     * @return {@code Stack} data structure
     */
    public Iterable<Board> neighbors() {
    	Stack<Board> boards = new Stack<>();
    	
    	if(initialValue / N != 0) pushToStack(boards, - N);		
    	if(initialValue / N != N - 1) pushToStack(boards, N);	
    	if(initialValue % N != 0)pushToStack(boards, - 1);		
    	if(initialValue % N != N - 1) pushToStack(boards, 1);	
    	
    	return boards;
    }
    
    /**
     * Does this board equal y?
     * 
     * Compares the current Object with the blockNode 
     * elements from a new instance of immutable type Board.
     * 
     * Condition 1 && 2: base-cases preventing loitering,
     * 					 and extra memory utilization.
     */
    public boolean equals(Object y) {
    	if(y == this) {
    		return true;
    	}
    	if(y == null || this.getClass() != y.getClass()) {
    		return false;
    	}
    	Board other = (Board) y;
    	for (int i = 0; i < searchNode.length; i++) {
    		if (this.searchNode[i] != other.searchNode[i]) 
		return false;
    	}
	return true;
    }
    
    /**
     * String representation of the board.
     * -----------------------------------
     * e.g.:	0  1  3
     * 		4  2  5
     * 		7  8  6
     * -----------------------------------
     * 
     * Input and output format for a board is the 
     * board size N followed by the N-by-N initial 
     * board, using 0 to represent the blank square.
     */
    public String toString() {
    	StringBuilder s = new StringBuilder();
	s.append(N + "\n");
	for (int i = 0; i < searchNode.length; i++) {
        	s.append(String.format("%2d ", searchNode[i]));
        	if ((i + 1) % N == 0) {
        		s.append("\n");
        	}
	}
	return s.toString();
    }
}
