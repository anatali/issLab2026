package main.java.conway.domain;
/*
 * Contratto definito dalla business logic
 */
public interface IOutDev {
	public void display(String msg);      //For HMI
	public void displayCell(Cell cell, Grid grid);   
	public void close();
	public void displayGrid(Grid grid);

}
