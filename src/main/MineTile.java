package main;

import javax.swing.JButton;

public class MineTile extends JButton {
	
	// 列、行
	public int row;
	public int col;
	
	public MineTile(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	// ボード上の何列目かを返す
	public int getRow() {
		return row;
	}
	
	// ボード上の何行目かを返す
	public int getCol() {
		return col;
	}

}
