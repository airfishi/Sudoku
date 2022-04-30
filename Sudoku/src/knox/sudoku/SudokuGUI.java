package knox.sudoku;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * 
 * This is the GUI (Graphical User Interface) for Sudoku.
 * 
 * It extends JFrame, which means that it is a subclass of JFrame.
 * The JFrame is the main class, and owns the JMenuBar, which is 
 * the menu bar at the top of the screen with the File and Help 
 * and other menus.
 * 
 * 
 * One of the most important instance variables is a JCanvas, which is 
 * kind of like the canvas that we will paint all of the grid squared onto.
 * 
 * @author jaimespacco
 *
 */
public class SudokuGUI extends JFrame {
	
	private Sudoku sudoku;
	
	private Sudoku originalsudoku;
    
	private static final long serialVersionUID = 1L;
	
	// Sudoku boards have 9 rows and 9 columns
    private int numRows = 9;
    private int numCols = 9;
    
    // the current row and column we are potentially putting values into
    private int currentRow = -1;
    private int currentCol = -1;
    
    //the current hint row and col
    //private int hintRow = -1;
    //private int hintCol = -1;    
    
    // the current guessed number for currentRow and currentCol
    //private int guess = -1;
    //Not sure what this was for but Spacco included it so I didn't delete it
    
    // figuring out how big to make each button
    // honestly not sure how much detail is needed here with margins
	protected final int MARGIN_SIZE = 5;
    protected final int DOUBLE_MARGIN_SIZE = MARGIN_SIZE*2;
    protected int squareSize = 90;
    private int width = DOUBLE_MARGIN_SIZE + squareSize * numCols;    		
    private int height = DOUBLE_MARGIN_SIZE + squareSize * numRows;  
    
    private static Font FONT = new Font("Verdana", Font.BOLD, 40);
    private static Color FONT_COLOR = Color.WHITE;
    private static Color BACKGROUND_COLOR = Color.BLUE;
    
    // the canvas is a panel that gets drawn on
    private JPanel panel;

    // this is the menu bar at the top that owns all of the buttons
    private JMenuBar menuBar;
    
    // 2D array of buttons; each sudoku square is a button
    private JButton[][] buttons = new JButton[numRows][numCols];
    
    String beta = "Click a box then try this again to find out the legal values!";
    
    
    
    private class MyKeyListener extends KeyAdapter {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	
        
    	MyKeyListener(int row, int col, Sudoku sudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    		
    	}
    	
    	public void keyTyped(KeyEvent e) {
			char key = e.getKeyChar();
			//System.out.println(key);
			if (Character.isDigit(key)) {
				// use ascii values to convert chars to ints
				int digit = key - '0';
				
				if(!sudoku.isLegal(row,col,digit)) {
					JOptionPane.showMessageDialog(null, "This value is not a legal value here.");
					return;
				}
				System.out.println(key);
				if (currentRow == row && currentCol == col) {
					sudoku.set(row, col, digit);
				}
				
				int n = 0;
				for(int i = 0; i < 9; i++) {
					for(int j = 0; j < 9; j++) {
						if(sudoku.isBlank(i,j)) {
							n++;
						}
					}
				}
				if(n == 0)
					JOptionPane.showMessageDialog(null, "You Win! ");
				

				
				for(int i = 0; i < 9; i++) {
					for(int j = 0; j < 9; j++) {
						if(sudoku.isBlank(i,j) && sudoku.getLegalValues(i,j).isEmpty()) {
							JOptionPane.showMessageDialog(null, "You Lose! You put a value in a box which stopped another\n"
									+ "box from having any possible values");
						}
					}
				}

				
				
				update();
			}
		}
    }
    
    private class ButtonListener implements ActionListener {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	public final Sudoku originalsudoku;
    	
    	ButtonListener(int row, int col, Sudoku sudoku,Sudoku originalsudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    		this.originalsudoku = originalsudoku;
    		
    	}
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.printf("row %d, col %d, %s\n", row, col, e);
			JButton button = (JButton)e.getSource();
			
			
			if (sudoku.isBlank(row,col) && row == currentRow && col == currentCol) {
				currentRow = -1;
				currentCol = -1;
			} else if (originalsudoku.isBlank(row, col)) {
				// we can try to enter a value in a 
				currentRow = row;
				currentCol = col;
				
				beta = sudoku.legalValuesHelper(sudoku.getLegalValues(row,col));

			} else {
				currentRow = row;
				currentCol = col;
				buttons[currentRow][currentCol].setBackground(BACKGROUND_COLOR);
				currentRow = -1;
				currentCol = -1;
				update();
				JOptionPane.showMessageDialog(null, "Can't enter a value here");
			}
			
			update();
		}
		
		
    }
    
    /**
     * Put text into the given JButton
     * 
     * @param row
     * @param col
     * @param text
     */
    private void setText(int row, int col, String text) {
    	buttons[row][col].setText(text);
    }
    
    /**
     * This is a private helper method that updates the GUI/view
     * to match any changes to the model
     */
    private void update() {
    	for (int row=0; row<numRows; row++) {
    		for (int col=0; col<numCols; col++) {
    			Color highlightColor = Color.CYAN;
    			if (row == currentRow && col == currentCol && sudoku.isBlank(row, col)) {
    				// draw this grid square special!
    				// this is the grid square we are trying to enter value into
    				buttons[row][col].setForeground(highlightColor);
    				// I can't figure out how to change the background color of a grid square, ugh
    				// Maybe I should have used JLabel instead of JButton?
    				buttons[row][col].setBackground(highlightColor);
    				setText(row, col, "_");
    			} else if (row == currentRow && col == currentCol && originalsudoku.isBlank(row, col)) {
    				buttons[row][col].setForeground(FONT_COLOR);
    				buttons[row][col].setBackground(highlightColor);
	    			int val = sudoku.get(row, col);
	    			if (val == 0) {
	    				setText(row, col, "");
	    			} else {
	    				setText(row, col, val+"");
	    			}
    				
    			} else {
    				buttons[row][col].setForeground(FONT_COLOR);
    				buttons[row][col].setBackground(BACKGROUND_COLOR);
	    			int val = sudoku.get(row, col);
	    			if (val == 0) {
	    				setText(row, col, "");
	    			} else {
	    				setText(row, col, val+"");
	    			}
    			}
    		}
    	}
    	repaint();
    }
    
	
    private void createMenuBar(){
    	menuBar = new JMenuBar();
        
    	//
    	// File menu
    	//
    	JMenu file = new JMenu("File");
        menuBar.add(file);
        
        addToMenu(file, "New Game", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.load(sudoku.newGameFile);
                originalsudoku.load(sudoku.newGameFile);
                repaint();
                update();
            }
        });
        
        addToMenu(file, "Save", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO: save the current game to a file!
            	// HINT: Check the Util.java class for helpful methods
            	// HINT: check out JFileChooser
            	// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
            	String result = "";
        		for (int r=0; r<9; r++) {
        			for (int c=0; c<9; c++) {
        				int val = sudoku.get(r,c);
        					result += val + " ";
    				}
        			result = result.substring(0,result.length()-1);
        			result += "\n";
        		}
        		result = result.substring(0,result.length()-1);
            	String toSave = result;
            	
            	System.out.println(toSave);
            	
            	JFileChooser chooser = new JFileChooser();
            	FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Sudoku games", "txt");
                chooser.setFileFilter(filter);
            	chooser.setCurrentDirectory(new File(sudoku.myDir));
                int retVal = chooser.showSaveDialog(null);
                if (retVal == JFileChooser.APPROVE_OPTION) {
					try {
						FileWriter writer;
						
						//handles user manually typing .txt at the end so it doesn't double
						if(chooser.getSelectedFile().getName().endsWith(".txt")) {
							writer = new FileWriter(sudoku.myDir + chooser.getSelectedFile().getName());
						} else
						 writer = new FileWriter(sudoku.myDir + chooser.getSelectedFile().getName() + ".txt");
							
						//writer stuff to write the string to the file		
						for(int i = 0; i < toSave.length(); i++)
								writer.write(toSave.charAt(i));
						writer.close();
						
					} catch (IOException e1) {
						//e1.printStackTrace();
					}
					//help
                	
                }
                repaint();
            }
        });

        
        addToMenu(file, "Load", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Sudoku games", "txt");
                chooser.setFileFilter(filter);
                chooser.setCurrentDirectory(new File(sudoku.myDir));
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   sudoku.load(chooser.getSelectedFile().getName());
                }
                sudoku.currentFile = sudoku.myDir + chooser.getSelectedFile().getName();
                update();
                repaint();
            }
        });
        
        addToMenu(file, "Change Background Color", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	List<BackgroundColors> initOptions = new ArrayList<>();
            	
            	for(BackgroundColors i: BackgroundColors.values()) {
            		initOptions.add(i);
            	}
            	
            	Object[] possibleValues = new Object[initOptions.size()];
            	
            	for(int i = 0; i <initOptions.size(); i++) {
            		possibleValues[i] = initOptions.get(i);
            	}
            	
            	Object selectedValue = JOptionPane.showInputDialog(null,"Choose a color!", "Colors",
            			JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
            	
            	//this was tricky
            	Color color;
            	for(BackgroundColors i : initOptions) {
            		if(selectedValue.equals(i)) {
	                	try {
	                	    java.lang.reflect.Field field = Class.forName("java.awt.Color")
	                	    		.getField(i.toString());
	                	    color = (Color) field.get(null);
	                	} catch (Exception e1) {
	                	    color = null; // Not defined
	                	}
	                	BACKGROUND_COLOR = color;
            		}
                	
            	}

            	update();
            	repaint();
            }
        });
        
        addToMenu(file, "Change Font Color", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	Object[] possibleValues = { "Black","White" };
            	Object selectedValue = JOptionPane.showInputDialog(null,"Choose a color!", "Colors",
            			JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
            	
            	if(selectedValue.equals("Black")) {
            		FONT_COLOR = Color.BLACK;
            	} else if(selectedValue.equals("White")) 
            		FONT_COLOR = Color.WHITE;
            	
            	
            	update();
            	repaint();
            }
        });
        
        //
        // Help menu
        //
        JMenu help = new JMenu("Help");
        menuBar.add(help);
        
        addToMenu(help, "Hint", new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent e) {
				/*
				for(int r = 0; r < 9; r++) {
					for(int c = 0; c < 9; c++) {
						if(sudoku.isBlank(r,c) && sudoku.getLegalValues(r, c).size() ==4) {
							buttons[r][c].setForeground(Color.PINK);
							hintRow = r;
							hintCol = c;
							update();
							return;
						}
					}
				}
				*/
				
				int n = 0;
				
				while(n != -1) {
					n++;
					for(int i = 0; i < 9; i++) {
						for(int j = 0; j < 9; j++) {
							if(sudoku.isBlank(i,j)) {
								if(sudoku.getLegalValues(i,j).size() == n) {
									buttons[i][j].setBackground(Color.PINK);
									n = -1;
								}
							}
						}
					}
				}
				
				JOptionPane.showMessageDialog(null, "Check out the pink square, it has the least values that can go there!");
			}
		});
        
        addToMenu(help, "Possible Values", new ActionListener() {
			@Override
			
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, beta);
			}
		});
        
        this.setJMenuBar(menuBar);
    }
    
    
    /**
     * Private helper method to put 
     * 
     * @param menu
     * @param title
     * @param listener
     */
    private void addToMenu(JMenu menu, String title, ActionListener listener) {
    	JMenuItem menuItem = new JMenuItem(title);
    	menu.add(menuItem);
    	menuItem.addActionListener(listener);
    }
    
    private void createMouseHandler() {
    	MouseAdapter a = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.printf("%s\n", e.getButton());
			}
    		
    	};
        this.addMouseMotionListener(a);
        this.addMouseListener(a);
    }
    
    
    private void createKeyboardHandlers() {
    	for (int r=0; r<buttons.length; r++) {
    		for (int c=0; c<buttons[r].length; c++) {
    			buttons[r][c].addKeyListener(new MyKeyListener(r, c, sudoku));
    			/*
    			buttons[r][c].addKeyListener(new KeyAdapter() {
    				@Override
    				public void keyTyped(KeyEvent e) {
    					char key = e.getKeyChar();
    					System.out.println(key);
    					if (Character.isDigit(key)) {
    						System.out.println(key);
    						if (currentRow > -1 && currentCol > -1) {
    							guess = Integer.parseInt(key + "");
    						}
    					}
    					
    				}
    			});
    			*/
    		}
    	}
    	/*
    	this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char key = e.getKeyChar();
				System.out.println(key);
				if (Character.isDigit(key)) {
					System.out.println(key);
					if (currentRow > -1 && currentCol > -1) {
						guess = Integer.parseInt(key + "");
					}
				}
				
			}
		});
		*/
    }
    
    public SudokuGUI() {
        sudoku = new Sudoku();
        // load a puzzle from a text file
        // right now we only have 1 puzzle, but we could always add more!
        sudoku.load(sudoku.currentFile);
        
        originalsudoku = new Sudoku();
        originalsudoku.load(sudoku.currentFile);
        
        
        
        setTitle("Sudoku!");

        this.setSize(width, height);
        
        // the JPanel where everything gets painted
        panel = new JPanel();
        // set up a 9x9 grid layout, since sudoku boards are 9x9
        panel.setLayout(new GridLayout(9, 9));
        // set the preferred size
        // If we don't do this, often the window will be minimized
        // This is a weird quirk of Java GUIs
        panel.setPreferredSize(new Dimension(width, height));
        
        // This sets up 81 JButtons (9 rows * 9 columns)
        for (int r=0; r<numRows; r++) {
        	for (int c=0; c<numCols; c++) {
        		JButton b = new JButton();
        		b.setPreferredSize(new Dimension(squareSize, squareSize));
        		
        		b.setFont(FONT);
        		b.setForeground(FONT_COLOR);
        		b.setBackground(BACKGROUND_COLOR);
        		buttons[r][c] = b;
        		// add the button to the canvas
        		// the layout manager (the 9x9 GridLayout from a few lines earlier)
        		// will make sure we get a 9x9 grid of these buttons
        		panel.add(b);

        		// thicker borders in some places
        		// sudoku boards use 3x3 sub-grids
        		int top = 1;
        		int left = 1;
        		int right = 1;
        		int bottom = 1;
        		if (r % 3 == 2) {
        			bottom = 5;
        		}
        		if (c % 3 == 2) {
        			right = 5;
        		}
        		if (r == 0) {
        			top = 5;
        		}
        		if (c == 9) {
        			bottom = 5;
        		}
        		b.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
        		
        		//
        		// button handlers!
        		//
        		// check the ButtonListener class to see what this does
        		//
        		b.addActionListener(new ButtonListener(r, c, sudoku, originalsudoku));
        	}
        }
        
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(width, height));
        this.setResizable(false);
        this.pack();
        this.setLocation(100,100);
        this.setFocusable(true);
        
        createMenuBar();
        createKeyboardHandlers();
        createMouseHandler();
        
        // close the GUI application when people click the X to close the window
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        update();
        repaint();
    }
    
    public static void main(String[] args) {
        SudokuGUI g = new SudokuGUI();
        g.setVisible(true);
    }

}
