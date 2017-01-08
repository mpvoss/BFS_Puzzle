import java.awt.Point;
import java.util.ArrayList;

/**
 * State Class.  I'm using the concept of chess bitboards here for efficiency's sake.
 * Java Longs are 64 bits long, and the board is 10x10.  The outer rim is all
 * illegal squares anyway so we can safely ignore those and treat the board as an 8x8,
 * which would mean it has 64 positions.  I use one long for every piece, and if there is a 1 
 * in the bit string that means that part of the piece is in that location.
 * 
 * I also use illegal move bitmasks, so if a piece is already on the left edge I can just
 * do a bitwise AND with it and the left move bitmask and if it is anything but 0 then
 * there's part of the square in a position that can't move left, so I can stop computing
 * there.
 * 
 * All the piece movement itself is just bitwise ANDs, ORs, and bitshifting, which is incredibly
 * fast on 64 bit processors.  This implementation can solve the puzzle in about 2 seconds.
 * @author Matthew
 *
 */
public class State {

	int cost = 0;
	State parent = null;
	String moveBefore = "";
	boolean useHeuristic = true;
	
	// Bitmaps for squares where it is illegal to move left, right, down, or up
	static long leftMoveBorder = 0xE0C088888080C0E0L;
	static long rightMoveBorder = 0x0703214101010307L;
	static long downMoveBorder = 0xC39130300081C3FFL;
	static long upMoveBorder = 0xFFC39130300081C3L;

	// Initial location of top left corner of square
	Point squareLocation = new Point(0, 2);
	static final Point finalSquareLocation = new Point(4, 0);

	// Bitmaps for all pieces. If there is a 1 in the binary string for a spot,
	// one of the piece's parts is on that square
	private long[] pieces = { 
			0x0000C0C000000000L, // Red square
			0x0C08000000000000L, // Orange L
			0x00040C0000000000L, // Blue L
			0x0000000080C00000L, // Bright green L
			0x0000000060200000L, // Purple L
			0x0000000818080000L, // Green Blue T
			0x0000000406040000L, // Green T
			0x0000000001030000L, // Weird bright Blue L
			0x0000000000002030L, // Pink L
			0x0000000000001808L, // Yellow L
			0x0000000000000604L  // Brwon L
	};
	
	// Human readable names for debugging
//	static String[] names = {
//		"Red Square",
//		"OrangeL",
//		"Blue L",
//		"Bright Green L",
//		"Purple L",
//		"Green Blue T",
//		"Green T",
//		"Weird bright Blue L",
//		"Pink L",
//		"Yellow L",
//		"Brown L"		
//	};

	// Default Constructor
	public State(boolean useHeuristic) {
		this.useHeuristic = useHeuristic;
	}

	// Copy constructor
	public State(State otherState) {
		System.arraycopy(otherState.getPieces(), 0, this.getPieces(), 0,
				otherState.getPieces().length);
		
		this.parent = otherState;
		this.cost = otherState.getCost();
		this.useHeuristic = otherState.useHeuristic;
		
		this.squareLocation = new Point(otherState.getSquareLocation());
	}


	// Returns an arraylist of legal children states of the current state
	public void addMoves(ArrayList<State> moves) {

		// For each piece
		for (int i = 0; i < this.getPieces().length; i++) {
			long everyOtherPiece = this.orEverythingBut(i);
			long piece = this.getPieces()[i];

			// Check if piece in question can move in the cardinal directions
			addMove(moves, everyOtherPiece, piece, i, State.leftMoveBorder);
			addMove(moves, everyOtherPiece, piece, i, State.rightMoveBorder);
			addMove(moves, everyOtherPiece, piece, i, State.upMoveBorder);
			addMove(moves, everyOtherPiece, piece, i, State.downMoveBorder);

		}
	}

	// Checks for a move of piece piece in direction direction
	public void addMove(ArrayList<State> moves, long everyOtherPiece,
			long piece, int i, long direction) {
		// Direction shows the squares where it is not legal to move in that direction.
		// If any parts of the piece are on one of those squares, we can't move that
		// direction. If they share no squares, we can
		if ((piece & direction) == 0) {
			long newLocation;

			//String move;
			
			// For whatever direction we were passed, shift the piece in question accordingly
			if (direction == State.leftMoveBorder){
				newLocation = State.moveLeft(this.getPieces()[i]);
				//move = "left";
			}
			else if (direction == State.rightMoveBorder){
				newLocation = State.moveRight(this.getPieces()[i]);
				//move = "right";
			}else if (direction == State.upMoveBorder){
				newLocation = State.moveUp(this.getPieces()[i]);
				//move = "up";
			}else{
				newLocation = State.moveDown(this.getPieces()[i]);
				//move = "down";
			}
			
			// Check to see if the piece's new location overlaps any other piece.
			// If not, it's a valid move
			if ((newLocation & everyOtherPiece) == 0) {
				State s = new State(this);
				
				// If we moved square, update location
				if (i == 0){
					if (direction == State.leftMoveBorder)
						s.squareLocation.x--;
					else if (direction == State.rightMoveBorder)
						s.squareLocation.x++;
					else if (direction == State.upMoveBorder)
						s.squareLocation.y--;
					else
						s.squareLocation.y++;
				
				}
				
				// Save the new location of moved piece into the new state.
				s.getPieces()[i] = newLocation;
				
				moves.add(s);
				
				// Human readable description of move for debugging
				//s.moveBefore = this.names[i] + " " + move;
				
			}
		}
	}
	
	// Since all the pieces are stored as longs, we can add up all the longs to get a
	// very unique ID for the position
	public long getID(){
		long result = 0;
		for (int i = 0; i < pieces.length; i++)
			result += pieces[i];
		return  result;
	}
	

	@Override
	public int hashCode(){
		return (int) this.getID();
	}
	
	// Manhattan distance
	public int getHeuristic() {
		
		if(useHeuristic)
			return Math.abs(this.getSquareLocation().x
					- this.getFinalSquareLocation().x)
					+ Math.abs(this.getSquareLocation().y
							- this.getFinalSquareLocation().y);
		else
			return 0;
	}

	public long orEverythingBut(int index) {
		long result = 0x0L;
		for (int i = 0; i < this.getPieces().length ; i++) {
			if (i != index)
			result = result ^ this.getPieces()[i];
		}
		return result;
	}
	public boolean isGoalState() {
		return squareLocation.x == finalSquareLocation.x
				&& squareLocation.y == finalSquareLocation.y;
	}
	
	
	//-----------------------------------------------------
	// Bit twiddling
	//-----------------------------------------------------
	
	static long moveLeft(long l) {
		return l << 1;
	}
	
	static long moveRight(long l) {
		return l >>> 1;
	}
	
	static long moveUp(long l) {
		return l << 8;
	}
	
	static long moveDown(long l) {
		return l >>> 8;
	}

	//-----------------------------------------------------
	// Debugging functions, getters and setters
	//-----------------------------------------------------
//	public void printColors(){
//		
//		for (int i = 0; i <this.pieces.length;i++){
//			System.out.println(this.names[i]);
//			print(this.pieces[i]);
//		}
//	}

	public Point getSquareLocation() {
		return this.squareLocation;
	}

	public Point getFinalSquareLocation() {
		return State.finalSquareLocation;
	}
	
	static void movementBorderDriver() {
		print(upMoveBorder);
	}

	static void getMoves() {

	}
	
	static String getBinary(long l) {
		return String.format("%64s", Long.toBinaryString(l)).replace(' ', '0');
	}

	static void printBoard(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i % 8 == 0 && i != 0)
				System.out.println();
			System.out.print(s.charAt(i));
		}
		System.out.println();
	}

	static void print(long l) {
		printBoard(getBinary(l));
		System.out.println("------------------------");

	}

	public long[] getPieces() {
		return this.pieces;
	}

	public int getCost() {
		// TODO Auto-generated method stub
		return this.cost;
	}

}