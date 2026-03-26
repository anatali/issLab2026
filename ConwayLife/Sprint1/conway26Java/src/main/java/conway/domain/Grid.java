package main.java.conway.domain;
import java.util.Arrays;
import java.util.stream.Collectors;
import unibo.basicomm23.utils.CommUtils;

public class Grid implements IGrid{

	private Cell[][] gridrep;
	private int rows, cols;
	
	public Grid( int rowsNum, int colsNum) {
		this.rows = rowsNum;
		this.cols = rowsNum;
		gridrep = new Cell[rowsNum][colsNum];	
		initGrid();
	}
	

	  protected void initGrid() {
		  CommUtils.outyellow("Grid | initGrid rows=" + rows + " cols=" + cols);
		  for (int i = 0; i < rows; i++) {
		     for (int j = 0; j < cols; j++) {
		    	 gridrep[i][j] = new Cell();
		     }
		  }
		  //CommUtils.outyellow("Grid | initGrid done");
	  }	
	  
	  @Override
	  public int getRowsNum() {
		  return rows;
	  }
	  @Override
	  public int getColsNum() {
		  return cols;
	  }
	  @Override
	  public Cell getCell(int x, int y) {
		  return gridrep[x][y] ;
	  }
	  @Override
	  public void setCellValue(int x, int y, boolean state) {
		  gridrep[x][y].setStatus(state);
	  }
	  @Override
	  public boolean getCellValue(int x, int y) {
		  return gridrep[x][y].isAlive();
	  }
	  @Override
	  public void reset() {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					gridrep[i][j].setStatus(false);
				}
			}
	  }
	  
	  /*
	   * Grid non deve sapere di I/O
	   */
	  
	  public String toString() {
		    return Arrays.stream( gridrep ) // Stream di Cell[] (le righe)
	        .map(row -> {
	            // Trasformiamo ogni riga in una stringa di . e O
	            StringBuilder sb = new StringBuilder();
	            for (Cell cell : row) {
	                sb.append(cell.isAlive() ? "O " : ". ");
	            }
	            return sb.toString();
	        })
	        .collect(Collectors.joining("\n")); // Uniamo le righe con un a capo  
	  }
	   
/*
 * UTILITY METHODS (for gui canvas)
 */
		public boolean[][] repAsBoolArray( ) {
			boolean[][] gridasboool = new boolean[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					gridasboool[i][j] = getCell(i, j).isAlive();
				}
			}
			return gridasboool;
		}
		
}

