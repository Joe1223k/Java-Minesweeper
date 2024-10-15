package main;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
	
	// Screen settings
	private static final int TILE_SIZE = 50;
	private static final int NUM_ROWS = 8;
	private static final int NUM_COLS = 8;
	private static final int MINE_COUNT = 10;
	private static final int BOARD_WIDTH = NUM_COLS * TILE_SIZE;
	private static final int BOARD_HEIGHT = NUM_ROWS * TILE_SIZE + 50;
	
	// Setup UI
	private JFrame frame = new JFrame("Java Minesweeper");
	private JPanel infoPanel = new JPanel();
	private JPanel boardPanel = new JPanel();
	private JButton resetButton = new JButton();
	
	// Game settings
	private MineTile[][] board = new MineTile[NUM_ROWS][NUM_COLS];
	private ArrayList<MineTile> mineList = new ArrayList<MineTile>();
	private Random random = new Random();
	private int tilesClicked = 0;
	private boolean gameOver = false;
	
	// Setup images
	private ImageIcon resetNomal, resetFail, resetClear;
	private ImageIcon count, timer;
	private ImageIcon block, flag, theMine, mine;
	private ImageIcon tile0, tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8;
	
	public Minesweeper() {
		
		// Setup game objects
		importImg();
		setupFrame();
		createBoard();
		frame.setVisible(true);
	}
	
	private void importImg() {
		
		count = loadCounterImg("/mscount.png");
		timer = loadCounterImg("/mstimer.png");
		resetNomal = loadIcon("/resetbutton_nomal.png");
		resetFail = loadIcon("/resetbutton_fail.png");
		resetClear = loadIcon("/resetbutton_clear.png");
		block = loadIcon("/block.png");
		flag = loadIcon("/flag.png");
		theMine = loadIcon("/the_mine.png");
		mine = loadIcon("/mine.png");
		tile0 = loadIcon("/tile0.png");
		tile1 = loadIcon("/tile1.png");
		tile2 = loadIcon("/tile2.png");
		tile3 = loadIcon("/tile3.png");
		tile4 = loadIcon("/tile4.png");
		tile5 = loadIcon("/tile5.png");
		tile6 = loadIcon("/tile6.png");
		tile7 = loadIcon("/tile7.png");
		tile8 = loadIcon("/tile8.png");
	}
	
	private ImageIcon loadIcon(String path) {
		
		Image image = new ImageIcon(getClass().getResource(path)).getImage();
		return new ImageIcon(image.getScaledInstance(50, 50, 50));
	}
	
	private ImageIcon loadCounterImg(String path) {
		
		Image image = new ImageIcon(getClass().getResource(path)).getImage();
		return new ImageIcon(image.getScaledInstance(80, 40, 50));
	}
	
	private void setupFrame() {
		
		// Frame setup
		frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		// Info panel setup
		resetButton.setBackground(Color.LIGHT_GRAY);
		resetButton.setIcon(resetNomal);
		resetButton.setFocusable(false);
		resetButton.setBorder(BorderFactory.createEmptyBorder());
		resetButton.addActionListener(e -> resetGame());
		
		infoPanel.setLayout(new GridLayout(1, 3));
		infoPanel.setBackground(Color.LIGHT_GRAY);
		
		JLabel countLabel = new JLabel(count);
		JLabel timerLabel = new JLabel(timer);
		
		countLabel.setHorizontalAlignment(JLabel.CENTER);
		timerLabel.setHorizontalAlignment(JLabel.CENTER);
		
		Dimension labelSize = new Dimension(TILE_SIZE, TILE_SIZE);
		resetButton.setPreferredSize(labelSize);
		countLabel.setPreferredSize(labelSize);
		timerLabel.setPreferredSize(labelSize);
		
		infoPanel.add(countLabel);
		infoPanel.add(resetButton);
		infoPanel.add(timerLabel);
		
		frame.add(infoPanel, BorderLayout.NORTH);
		
		// Board panel setup
		boardPanel.setLayout(new GridLayout(NUM_ROWS, NUM_COLS)); // 8 x 8
		boardPanel.setBackground(Color.BLACK);
		frame.add(boardPanel);
	}
	
	private void createBoard() {
		
		// Game setup
		resetButton.setIcon(resetNomal);
		boardPanel.removeAll();
		mineList.clear();
		tilesClicked = 0;
		gameOver = false;
		
		for(int row = 0; row < NUM_ROWS; row++) {
			for(int col = 0; col < NUM_COLS; col++) {
				MineTile tile = new MineTile(row, col);
				board[row][col] = tile;
				
				tile.setIcon(block);
				tile.setBackground(Color.LIGHT_GRAY);
				tile.setFocusable(false);
				tile.setMargin(new Insets(0, 0, 0, 0));
				
				tile.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						
						if(gameOver) return;
						// Left click to reveal panel
						if(e.getButton() == MouseEvent.BUTTON1 && tile.getIcon() == block) {
							if(mineList.contains(tile)) {
								revealMines();
								tile.setIcon(theMine);
							}else {
								checkMine(tile.getRow(), tile.getCol());
							}
						}
						// Right click to put flag
						else if(e.getButton() == MouseEvent.BUTTON3) {
							setFlag(tile);
						}
					}
				});
				// Set Board
				boardPanel.add(tile);
			}
		}
		// Set mines
		setMines();
		
		boardPanel.revalidate();
		boardPanel.repaint();
	}
	
	private void setMines() {
		
		int mineLeft = MINE_COUNT;
		
		while(mineLeft > 0) {
			int row = random.nextInt(NUM_ROWS); // 0 - 7
			int col = random.nextInt(NUM_COLS); // 0 - 7
			
			MineTile tile = board[row][col];
			// Check if mines put on the same position
			if(!mineList.contains(tile)) {
				mineList.add(tile);
				mineLeft--;
			}
		}
	}
	
	private void revealMines() {
		
		for(MineTile tile : mineList) {
			tile.setIcon(mine);
			tile.setDisabledIcon(mine);
		}
		gameOver = true;
		resetButton.setIcon(resetFail);
	}
	
	private void checkMine(int row, int col) {
		
		if(row < 0 || row >= NUM_ROWS ||
		   col < 0 || col >= NUM_COLS) return;
		
		MineTile tile = board[row][col];
		if(!tile.isEnabled()) return;
		tile.setEnabled(false);
		tilesClicked++;
		
		int minesFound = countMinesAround(row, col);
		
		switch(minesFound) {
			case 1 -> tile.setIcon(tile1);
			case 2 -> tile.setIcon(tile2);
			case 3 -> tile.setIcon(tile3);
			case 4 -> tile.setIcon(tile4);
			case 5 -> tile.setIcon(tile5);
			case 6 -> tile.setIcon(tile6);
			case 7 -> tile.setIcon(tile7);
			case 8 -> tile.setIcon(tile8);
			default -> {
				tile.setIcon(tile0);
				revealAroundTiles(row, col);
			}
		}
		
		tile.setDisabledIcon(tile.getIcon());
		
		if(tilesClicked == NUM_ROWS * NUM_COLS - mineList.size()) {
			gameOver = true;
			resetButton.setIcon(resetClear);
		}
	}
	
	private void revealAroundTiles(int row, int col) {
		
		// Check top side
		checkMine(row - 1, col - 1);
		checkMine(row - 1, col);
		checkMine(row, col + 1);
		
		// Check left and right
		checkMine(row, col - 1);
		checkMine(row, col + 1);
		
		// Check bottom side
		checkMine(row + 1, col - 1);
		checkMine(row + 1, col);
		checkMine(row + 1, col + 1);
	}
	
	private int countMinesAround(int row, int col) {
		
		int count = 0;
		
		// Count mines on top side
		count += countMine(row - 1, col -1);
		count += countMine(row - 1, col);
		count += countMine(row - 1, col + 1);
		
		// Count mines on left and right
		count += countMine(row, col - 1);
		count += countMine(row, col + 1);
		
		// Count mines on bottom side
		count += countMine(row + 1, col - 1);
		count += countMine(row + 1, col);
		count += countMine(row + 1, col + 1);
		
		return count;
	}

	private int countMine(int row, int col) {
		
		if(row < 0 || row >= NUM_ROWS ||
		   col < 0 || col >= NUM_COLS) return 0;
		
		return mineList.contains(board[row][col]) ? 1 : 0;
	}
	
	private void setFlag(MineTile tile) {
		
		if(tile.getIcon() == block && tile.isEnabled()) {
			tile.setIcon(flag);
		}
		else if(tile.getIcon() == flag) {
			tile.setIcon(block);
		}
	}
	
	private void resetGame() {
		
		createBoard();
	}

}
