package knox.sudoku;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.*;

/**
 * 
 * This is the MODEL class. This class knows all about the
 * underlying state of the Sudoku game. We can VIEW the data
 * stored in this class in a variety of ways, for example,
 * using a simple toString() method, or using a more complex
 * GUI (Graphical User Interface) such as the SudokuGUI 
 * class that is included.
 * 
 * @author jaimespacco
 *
 *
 *EDIT:
 *In order to make the save and load functions works as
 *intended please change the directory location (myDir)
 *and the newGameFile and currentFile vars as desired.
 *Also add colors to BackgroundColors if you would like
 *them to be in the program (ex. magenta and other ones
 *that java already knows about).
 *
 *@author airfishi
 */




public class Sudoku {
	
	String myDir = "B:\\CS 220 HOMEWORK\\Sudoku\\Sudoku";	
	
	//the file that will load when starting a new game
	String newGameFile = "easy1.txt";
	
	//the file that will load on startup, also changes
	//as files are navigated
	String currentFile = "easy1.txt";
	
	int[][] board = new int[9][9];
	
	int[][] originalboard = new int[9][9];
	
	
	public int get(int row, int col) {
		// TODO: check for out of bounds
			//Verify
		if((row > 8 || row < 0) || (col > 8 || col < 0)) {
			throw new ArrayIndexOutOfBoundsException("You're out of bounds");
		}
		return board[row][col];
	}
	
	public void set(int row, int col, int val) {
		// TODO: make sure val is legal
			//Verify
		if(val < 0) {
			throw new IndexOutOfBoundsException("Your value cannot be negative.");
		}
		if(val/10 != 0) {
			throw new IndexOutOfBoundsException("Your value is too large.");
		}
		board[row][col] = val;
	}
	
	public boolean isLegal(int row, int col, int val) {
		//returns true if the value can legally be placed in the current square
		
		//checks row
		for(int i = 0; i < 9; i ++) 
			if(board[row][i] == val && i != col) 
				return false;
		
		//checks column
		for(int i = 0; i < 9; i++)
			if(board[i][col] == val && i != row)
				return false;
		
		//Checks the squares
		int first;
		int second;
		
		if(row < 3)
			first = 0;
		else if(row < 6)
			first = 3;
		else
			first = 6;
		
		if(col < 3)
			second = 0;
		else if (col < 6)
			second = 3;
		else
			second = 6;
	
		for(int i = first; i < first + 3; i++)
			for(int j = second; j < second + 3; j++)
				if(board[i][j] == val && i != row && j != col)
					return false;

		
		return true;
	}
	
	public Collection<Integer> getLegalValues(int row, int col) {
		// TODO: return only the legal values that can be stored at the given row, col
		//System.out.println(row);
		//System.out.println(col);
		
		Collection<Integer> legalVals = new ArrayList<>();
		for(int i = 1; i < 10; i++)
			legalVals.add(i);
		
		//check row
		for(int i = 0; i < 9; i++) {
			if(!isBlank(row,i)) {
				legalVals.remove(board[row][i]);		
			}
		}
			

		//check col
		for(int i = 0; i < 9; i++)
			if(!isBlank(i,col))
				legalVals.remove(board[row][i]);
		
		//check squares
		int first;
		int second;
		
		if(row < 3)
			first = 0;
		else if(row < 6)
			first = 3;
		else
			first = 6;
		
		if(col < 3)
			second = 0;
		else if (col < 6)
			second = 3;
		else
			second = 6;
	
		for(int i = first; i < first + 3; i++)
			for(int j = second; j < second + 3; j++)
				if(!isBlank(i,j))
					legalVals.remove(board[i][j]);

		if(board[row][col]!=0) {
			legalVals.add(board[row][col]);
		}
		
		return legalVals;
	}

	public String legalValuesHelper(Collection<Integer> list) {
		String toRet = "The valid values are ";
		for(int i : list) {
			toRet += i + ", ";
		}
		toRet = toRet.substring(0,toRet.length()-2);
		toRet += ".";
		return toRet;
	}

	
	
	/**

_ _ _ 3 _ 4 _ 8 9
1 _ 3 2 _ _ _ _ _
etc


0 0 0 3 0 4 0 8 9

 */
	public void load(String filename) {
		try {
			Scanner scan = new Scanner(new FileInputStream(filename));
			// read the file
			for (int r=0; r<9; r++) {
				for (int c=0; c<9; c++) {
					int val = scan.nextInt();
					board[r][c] = val;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		originalboard = board;;
	}
	
	/**
	 * Return which 3x3 grid this row is contained in.
	 * 
	 * @param row
	 * @return
	 */
	public int get3x3row(int row) {
		return row / 3;
	}
	
	/**
	 * Convert this Sudoku board into a String
	 */
	public String toString() {
		String result = "";
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				int val = get(r, c);
				if (val == 0) {
					result += "_ ";
				} else {
					result += val + " ";
				}
			}
			result += "\n";
		}
		return result;
	}
	
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku();
		sudoku.load(sudoku.currentFile);
		System.out.println(sudoku);
		
		Scanner scan = new Scanner(System.in);
		while (!sudoku.gameOver()) {
			System.out.println("enter value r, c, v :");
			int r = scan.nextInt();
			int c = scan.nextInt();
			int v = scan.nextInt();
			sudoku.set(r, c, v);

			System.out.println(sudoku);
		}
	}

	public boolean gameOver() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(isBlank(i,j)) return false;
			}
		}
		return true;
	}

	public boolean isBlank(int row, int col) {
		return board[row][col] == 0;
	}

}
