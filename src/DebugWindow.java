import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Debug Window class.
 * 
 * This is a front end to help with testing HW2 of CSCE 4613, Artificial
 * Intelligence.
 * 
 * This class expects an ArrayList of strings representing the states your
 * program generates to solve the puzzle. Each string should be 100 characters
 * long, one character for each spot on the puzzle.
 * 
 * The characters expected to represent each piece are as follows: 
 * Piece 0 (red): (1,3) (2,3) (1,4) (2,4) 
 * Piece 1 (light green): (1,5) (1,6) (2,6) 
 * Piece 2 (lavender): (2,5) (3,5) (3,6) 
 * Piece 3 (yellow): (4,7) (5,7) (5,8)
 * Piece 4 (brown): (6,7) (7,7) (6,8) 
 * Piece 5 (pink): (3,7) (3,8) (4,8) 
 * Piece 6 (dark cyan): (5,4) (4,5) (5,5) (5,6) 
 * Piece 7 (dark green): (6,4) (6,5) (7,5) (6,6)
 * Piece 8 (light cyan): (8,5) (7,6) (8,6) 
 * Piece 9 (blue): (6,2) (5,3) (6,3)
 * Piece a (orange): (5,1) (6,1) (5,2)
 * 
 * I.E. the character 0 corresponds to a red square, 1 to light green and so on.
 * There is a main function in this file that shows the starting position of the
 * puzzle if you want a full example.
 * 
 * So all you need to do is translate each state in the sequence of states that
 * your program generates into a string as described above, throw them in an
 * ArrayList and this class should display a GUI that you can navigate with the
 * left and right arrows to see if any illegal moves have taken place.
 * 
 * DISCLAIMER: The DebugWindow class just makes it easier to see what is going
 * on. You should be careful in translating your state into the format
 * expected or you might accidently hide the errors that are going on. As long
 * as you translate correctly, however, this will help you see what is going on.
 * 
 * @author Matthew Voss, written 7-Feb-2015
 * 
 */
public class DebugWindow {

	JFrame frame;
	JPanel panel;
	JLabel label;

	BufferedImage img;
	ArrayList<String> states;
	final static int gridSquareLength = 30;
	int imgNbr = 0;

	/**
	 * Constructor.
	 * 
	 * @param states
	 */
	public DebugWindow(ArrayList<String> states) {
		frame = new JFrame("Search Debugger - Use arrow keys to navigate");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		panel = new JPanel();
		label = new JLabel();
		label.setIcon(new ImageIcon(new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_RGB)));
		new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

		panel.add(label);

		this.states = states;

		repaint();
		frame.add(panel);
		frame.pack();

		MyKeyListener keyListener = new MyKeyListener(this);
		frame.addKeyListener(keyListener);
	}

	/**
	 * Wax on, wax off.
	 */
	public void repaint() {
		createImageFromState(states.get(imgNbr));
		frame.pack();
	}

	/**
	 * Test Main Function. The input string is the starting position of the
	 * puzzle.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String input = "#############  aa#####   a9 ###00 #99  ##00##67  ##12266778##112 6788### 53344#####5534#############";
		ArrayList<String> list = new ArrayList<String>();
		list.add(input);
		new DebugWindow(list);
	}

	Color[] colors = {

	new Color(255, 0, 0), // 0
			new Color(111, 255, 0),// 1
			new Color(177, 173, 255),// 2
			new Color(231, 255, 158),// 3
			new Color(91, 107, 77),// 4
			new Color(255, 158, 163),// 5
			new Color(58, 128, 171),// 6
			new Color(78, 145, 45),// 7
			new Color(133, 247, 255),// 8
			new Color(43, 0, 255),// 9
			new Color(255, 136, 0), // a
			new Color(204, 255, 229), // default
			Color.BLACK // #
	};

	/**
	 * Parses the string state and paints the displayed image accordingly
	 * 
	 * @param s
	 */
	public void createImageFromState(String s) {
		
		if (s.length()!= 100){
			JOptionPane.showMessageDialog(null, "Error! The following string is not the expected 100 characters long:\n"+s);
			return;
		}
		img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
		initializeImage(img);
		for (int i = 0; i < s.length(); i++) {
			paintSquare(img, (i % 10), (i / 10), getColorFromChar(s.charAt(i)));

		}
		label.setIcon(new ImageIcon(img));
	}

	/**
	 * Translates the chars of the string to the color they represent
	 * 
	 * @param c
	 * @return
	 */
	public Color getColorFromChar(char c) {
		if (c == '0')
			return colors[0];
		else if (c == '1')
			return colors[1];
		else if (c == '2')
			return colors[2];
		else if (c == '3')
			return colors[3];
		else if (c == '4')
			return colors[4];
		else if (c == '5')
			return colors[5];
		else if (c == '6')
			return colors[6];
		else if (c == '7')
			return colors[7];
		else if (c == '8')
			return colors[8];
		else if (c == '9')
			return colors[9];
		else if (c == 'a')
			return colors[10];
		else if (c == ' ')
			return Color.WHITE;
		else if (c == '#')
			return Color.BLACK;
		else {
			JOptionPane.showMessageDialog(null, "Error! Expected one of the following chars: 0,1,2,3,4,5,6,7,8,9,a,[space],#, found : "+c);
			return null;
		}

	}

	public void initializeImage(BufferedImage img) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				paintSquare(img, i, j, Color.white);
			}
		}
	}

	/**
	 * Paints a 30x30 pixel square on the grid at the location dictated by
	 * gridX and gridY.
	 * @param img
	 * @param gridX
	 * @param gridY
	 * @param color
	 */
	public void paintSquare(BufferedImage img, int gridX, int gridY, Color color) {

		if (color == null)
			return;

		for (int x = 0; x < gridSquareLength; x++) {
			for (int y = 0; y < gridSquareLength; y++) {
				img.setRGB((gridX) * gridSquareLength + x, (gridY)
						* gridSquareLength + y, color.getRGB());
			}
		}
	}

}

/**
 * Simple listener class so we can navigate with arrow keys
 * 
 * @author Matthew
 * 
 */
class MyKeyListener implements KeyListener {

	DebugWindow debugWindow;

	public MyKeyListener(DebugWindow debugWindow) {
		this.debugWindow = debugWindow;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			debugWindow.imgNbr = (debugWindow.imgNbr - 1);
			if (debugWindow.imgNbr < 0)
				debugWindow.imgNbr = 0;
			debugWindow.repaint();

		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			debugWindow.imgNbr = (debugWindow.imgNbr + 1)
					% debugWindow.states.size();

			debugWindow.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
