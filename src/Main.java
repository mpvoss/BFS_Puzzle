import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import javax.swing.ImageIcon;

public class Main {

	public static void main(String[] arg) {

		boolean aStarSearch = true;

		State finalState = SolvePuzzle(aStarSearch);

		ArrayList<State> list = new ArrayList<State>();
		if (finalState == null){
			System.out.println("No solution found");
		} else {
			System.out.println("Solution found.");

			State tmp = finalState;

			while (tmp != null) {
				list.add(0, tmp);
				tmp = tmp.parent;
			}

			System.out.println("Num moves: " + finalState.cost);
			DebugWindow debugWindow = new DebugWindow(buildStringArray(list));

		}

	}


	static State SolvePuzzle(boolean useHeuristic) {

		// Data structures
		PriorityQueue<State> queue = new PriorityQueue<State>(100000,
				new StateComparator());
		HashMap<Long, State> beenThere = new HashMap<Long, State>();

		// Initialize queue/hashmap
		State initial = new State(useHeuristic);
		beenThere.put(initial.getID(), initial);
		queue.add(initial);

		int counter = 0;
		while (!queue.isEmpty()) {
			ArrayList<State> kids = new ArrayList<State>();
			State s = queue.remove();
			counter++;

			if (s.isGoalState()) {

				if (useHeuristic)
					System.out.println("astar2=" + counter);
				else
					System.out.println("bfs2=" + counter);
				return s;
			}

			// Find legal following moves
			s.addMoves(kids);

			for (State child : kids) {
				if (beenThere.containsKey(child.getID())) {

					// We've found a cheaper way to get to child
					if (s.cost + 1 < child.cost) {
						child.cost = s.cost + 1;
						child.parent = s;
						queue.add(child);
					}

				} else {
					child.cost = s.cost + 1;
					child.parent = s;
					queue.add(child);
					beenThere.put(child.getID(), child);
				}
			}

		}
		return null;
	}

	// RGB Color values of pieces of puzzle, 1:1 with colors array below
	// red square = (255,0,0)
	// Orange: 255,136,0
	// Dark blue L = (43,0,255);
	// Lime green: 111,255,0 // 1
	// Light purpole: 177,173,255 // 2
	// blue green: 58,128,171 //6
	// dark green T: 78,145,45 ///7
	// light blue: 133,247,255 // 8
	// washed out pink l: 255,158,163 // 5
	// yella: 231,255,158 // 3
	// brown: 91 107 77 // 4

	static	char[] colors = {
	'0', 'a', '9', '1', '2', '6', '7', '8', '5', '3', '4', };

	public static char[] setBoundary() {
		char[] charArray = new char[100];
		for (int i = 0; i < 100; i++)
			charArray[i] = ' ';

		for (int i = 0; i < 10; i++)
			charArray[i] = '#';

		for (int i = 0; i < 10; i++)
			charArray[i * 10] = '#';

		for (int i = 9; i < 100; i = i + 10)
			charArray[i] = '#';

		for (int i = 90; i < 100; i++)
			charArray[i] = '#';

		charArray[11] = '#';
		charArray[12] = '#';
		charArray[21] = '#';

		charArray[17] = '#';
		charArray[18] = '#';
		charArray[28] = '#';

		charArray[71] = '#';
		charArray[81] = '#';
		charArray[82] = '#';

		charArray[78] = '#';
		charArray[87] = '#';
		charArray[88] = '#';

		charArray[34] = '#';
		charArray[44] = '#';
		charArray[43] = '#';
		return charArray;
	}


	public static int computeIndex(int i) {

		int row = i / 8;
		int col = i % 8;

		return row * 10 + col + 11;

	}

	public static char computeColor(int i) {
		return colors[i];
	}

	static ArrayList<String> buildStringArray(ArrayList<State> stateList) {

		ArrayList<String> strList = new ArrayList<String>();
		for (State s : stateList) {

			char[] charArray = setBoundary();
			for (int i = 0; i < 64; i++) {

				for (int j = 0; j < 11; j++) {

					String binary = State.getBinary(s.getPieces()[j]);
					for (int z = 0; z < binary.length(); z++) {
						if (binary.charAt(z) == '1')
							charArray[computeIndex(z)] = computeColor(j);
					}

				}

			}
			strList.add(new String(charArray));

		}

		return strList;
	}

	
}

class StateComparator implements Comparator<State> {

	@Override
	public int compare(State s1, State s2) {
		return (s1.getHeuristic() + s1.getCost())
				- (s2.getHeuristic() + s2.getCost());
	}

}